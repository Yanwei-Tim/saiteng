#include <jni.h>
#include <stdio.h>
#include "librtp.h"
#include <BVCU.h>
#include "pjmedia/rtcp.h"
#include <pjmedia/rtp.h>
#include "PUConfig.h"
#include "PUConfig.pb.h"
extern "C" {
#include <pjmedia-codec/h264_packetizer.h>
}
;
#include "RTCPRetranBuild.h"
#include <android/log.h>
#undef	LOG_TAG
#define LOG_TAG "RTP"
#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

using namespace BVCU::PUConfig;

INIT_PLUGIN(RTPModule);

RTPModule::RTPModule() :
		Module("RTP", "misc") {
	m_sendThread = NULL;
}

RTPModule::~RTPModule() {
	Output("Unload module RTP");
}

RTPFactory* RTPModule::getRTPFactory() {
	return &m_rtpFactory;
}

void RTPModule::startupSendThread() {
	if (m_sendThread != NULL)
		return;
	m_sendThread = new SendThread;
	m_sendThread->startup();
}

void RTPModule::stopSendThread() {
	if (m_sendThread == NULL)
		return;
	m_sendThread->exit();
}

void RTPModule::initialize() {
	Output("Initializing module RTP");
	Engine::install(new MessageRelay("engine.start", this, Private, 150));
	Engine::install(new MPURegisterHandler);
	Engine::install(new MPUBroadcastInviteHandler);
	Engine::install(new MPUGetRTPHandler);
	Engine::install(new AudioPacketMountPoint);
	Engine::install(new MPUNotifyInviteHandler);
	installRelay(Halt);
	installRelay(Private + 1, "engine.stop");
	Engine::install(new MessageRelay("mpu.restart", this, Private + 2));
}

bool RTPModule::received(Message& msg, int id) {
	if (msg == "engine.start") {
		{
			Message msg0("mpu.mount.audio.enc");
			msg0.userData(
					(RefObject*) m_rtpFactory.getAudioTransmit()->getObject(
							"RefObject"));
			Engine::dispatch(msg0);
		}

		{
			Message msg1("mpu.mount.video.enc");
			msg1.userData(
					(RefObject*) m_rtpFactory.getVideoTransmit()->getObject(
							"RefObject"));
			Engine::dispatch(msg1);
		}

		{
			Message msg1("mpu.mount.gps.raw");
			msg1.userData(
					(RefObject*) m_rtpFactory.getGPSTransmit()->getObject(
							"RefObject"));
			Engine::dispatch(msg1);
		}

		{
			Message data("net.socket");
			data.addParam("type", "udp");
			data.addParam("port", String(0));
			Engine::dispatch(data);

			int dataSocket = data.getIntValue("socketid");
			RTPFactory *pRTPFactory = __plugin.getRTPFactory();
			pRTPFactory->setRTPSocketId(dataSocket);

			Message recvFrom("net.recvfrom");
			recvFrom.addParam("socketid", String(dataSocket));
			recvFrom.userData((RefObject*) pRTPFactory->getObject("RefObject"));
			Engine::dispatch(recvFrom);

			Message sendTo("net.send");
			sendTo.addParam("socketid", String(dataSocket));
			Engine::dispatch(sendTo);

			RefObject *pObj = sendTo.userData();
			pRTPFactory->setRTPIOBuf((IOBuf *) pObj->getObject("IOBuf"));
		}
	} else if (msg == "engine.stop") {
		stopSendThread();
		LOGI("stop");
	} else if (msg == "mpu.restart") {
		LOGI("restart");
	}
	return Module::received(msg, id);
}

void VideoTransmit::transmit(BigData *pData) {
	RTPFactory *pRTPFactory = __plugin.getRTPFactory();
	android::Mutex *mutex = pRTPFactory->getAudioSendIOBufMutex();
	mutex->lock();
	IOBuf * pVideoSendIOBuf = pRTPFactory->getVideoSendIOBuf();
	if (pVideoSendIOBuf) {
		IOBuf::IOUnit *pUnit = pVideoSendIOBuf->produceBegin(
				pData->packet.iDataSize);
		if (pUnit) {
			pUnit->data.packet.iDataSize = pData->packet.iDataSize/* + 4*/;
			pUnit->data.packet.pData = (SAV_TYPE_UINT8*) pUnit->pBuf;
			pUnit->data.packet.iPTS = pData->packet.iPTS;
			/*char * data = (char*) pUnit->data.packet.pData;
			 data[0] = 0;
			 data[1] = 0;
			 data[2] = 0;
			 data[3] = 1;*/
			memcpy(pUnit->data.packet.pData/* + 4*/, pData->packet.pData,
					pUnit->data.packet.iDataSize);
			pVideoSendIOBuf->produceEnd(pUnit);
		}
	}
	mutex->unlock();
}

void AudioTransmit::transmit(BigData *pData) {
	RTPFactory *pRTPFactory = __plugin.getRTPFactory();
	android::Mutex *mutex = pRTPFactory->getAudioSendIOBufMutex();
	mutex->lock();
	IOBuf * pAudioSendIOBuf = pRTPFactory->getAudioSendIOBuf();
	if (pAudioSendIOBuf) {
		IOBuf::IOUnit *pUnit = pAudioSendIOBuf->produceBegin(
				pData->packet.iDataSize);
		if (pUnit) {
			pUnit->data.packet.iDataSize = pData->packet.iDataSize;
			pUnit->data.packet.pData = (SAV_TYPE_UINT8*) pUnit->pBuf;
			pUnit->data.packet.iPTS = pData->packet.iPTS;

			memcpy(pUnit->data.packet.pData, pData->packet.pData,
					pUnit->data.packet.iDataSize);
			pAudioSendIOBuf->produceEnd(pUnit);
		}
	}
	mutex->unlock();
}

void GPSTransmit::transmit(BigData *pData) {
	RTPFactory *pRTPFactory = __plugin.getRTPFactory();
	android::Mutex *mutex = pRTPFactory->getAudioSendIOBufMutex();
	mutex->lock();
	IOBuf * pGPSSendIOBuf = pRTPFactory->getGPSSendIOBuf();
	if (pGPSSendIOBuf) {
		BVCU::PUConfig::GPSData gpsdata;
		BVCU_PUCFG_GPSData *data = (BVCU_PUCFG_GPSData *) pData->frame.ppData[0];
		{
			gpsdata.set_bantennastate(data->bAntennaState);
			gpsdata.set_borientationstate(data->bOrientationState);
			gpsdata.set_iangle(data->iAngle);
			gpsdata.set_iheight(data->iHeight);
			gpsdata.set_ilatitude(data->iLatitude);
			gpsdata.set_ilongitude(data->iLongitude);
			gpsdata.set_ispeed(data->iSpeed);
			gpsdata.set_istarcount(data->iStarCount);
			WallTime* walltime = gpsdata.mutable_sttime();
			walltime->set_iyear(data->stTime.iYear);
			walltime->set_imonth(data->stTime.iMonth);
			walltime->set_iday(data->stTime.iDay);
			walltime->set_ihour(data->stTime.iHour);
			walltime->set_iminute(data->stTime.iMinute);
			walltime->set_isecond(data->stTime.iSecond);
		}
		IOBuf::IOUnit *pUnit = pGPSSendIOBuf->produceBegin(gpsdata.ByteSize());
		if (pUnit) {
			pUnit->data.packet.iDataSize = gpsdata.ByteSize();
			pUnit->data.packet.pData = (SAV_TYPE_UINT8*) pUnit->pBuf;

			gpsdata.SerializeToArray(pUnit->data.packet.pData,
					pUnit->data.packet.iDataSize);
			pGPSSendIOBuf->produceEnd(pUnit);
		}
	}
	mutex->unlock();
}

SendThread::SendThread() {
	m_exit = 0;
	m_videoNTP = m_audioNTP = 0;
	m_videoTS = 0;
	m_audioTS = 0;
}

void SendThread::run() {
	pjmedia_h264_packetizer_cfg cfg0;
	memset(&cfg0, 0, sizeof(cfg0));
	cfg0.mode = PJMEDIA_H264_PACKETIZER_MODE_NON_INTERLEAVED;
	cfg0.mtu = 968;
	pjmedia_h264_packetizer *h264;
	pjmedia_h264_packetizer_create(NULL, &cfg0, &h264);
	time_t c1 = time(NULL);
	int64_t videoNTP, audioNTP;
	int videoTS = 0, audioTS = 0;
	while (!m_exit) {
		IOBuf *videoSendIOBuf = __plugin.getRTPFactory()->getVideoSendIOBuf();
		IOBuf *audioSendIOBuf = __plugin.getRTPFactory()->getAudioSendIOBuf();
		IOBuf *gpsSendIOBuf = __plugin.getRTPFactory()->getGPSSendIOBuf();
		if (videoSendIOBuf != NULL) {
			IOBuf::IOUnit *pUnit = videoSendIOBuf->consumeBegin();
			if (pUnit && pUnit->data.packet.iDataSize > 0) {
				{
					videoNTP = pUnit->data.packet.iPTS;
					if (m_videoNTP == 0)
						m_videoNTP = videoNTP;

					int64_t diff = pUnit->data.packet.iPTS - m_videoNTP;
					diff *= 9;
					diff /= 100;
					videoTS += 320;
					diff += videoTS;
					m_videoTS = diff;
					pj_size_t frameSize = pUnit->data.packet.iDataSize;
					pj_size_t iPos = 0;
					pj_uint8_t* pPayLoad = NULL;
					pj_size_t iPayLoadLen = 0;
					while (iPos < frameSize) {
						pj_status_t rc = pjmedia_h264_packetize(h264,
								(pj_uint8_t*) pUnit->data.packet.pData,
								frameSize, &iPos,
								(const pj_uint8_t **) &pPayLoad, &iPayLoadLen);
						if (iPos >= frameSize)
							sendVideo((char*) pPayLoad, iPayLoadLen, diff, 1,
									pUnit->data.packet.iPTS);
						else
							sendVideo((char*) pPayLoad, iPayLoadLen, diff, 0,
									pUnit->data.packet.iPTS);
					}
				}
				videoSendIOBuf->consumeEnd(pUnit);
			}
		}
		if (audioSendIOBuf != NULL) {
			IOBuf::IOUnit *pUnit = audioSendIOBuf->consumeBegin();
			if (pUnit && pUnit->data.packet.iDataSize > 0) {
				{
					audioNTP = pUnit->data.packet.iPTS;
					if (m_audioNTP == 0)
						m_audioNTP = audioNTP;

					int64_t diff = pUnit->data.packet.iPTS - m_audioNTP;
					diff *= 8;
					diff /= 1000;
					audioTS += 320;
					diff += audioTS;
					m_audioTS = diff;
					IOBuf::IOUnit *pSendUnit =
							__plugin.getRTPFactory()->getRTPIOBuf()->produceBegin(
									pUnit->data.packet.iDataSize
											+ sizeof(pjmedia_rtp_hdr)
											+ sizeof(pjmedia_rtp_ext_hdr) + 8);
					if (pSendUnit) {
						pSendUnit->data.net.len = pUnit->data.packet.iDataSize
								+ sizeof(pjmedia_rtp_hdr)
								+ sizeof(pjmedia_rtp_ext_hdr) + 8;
						pSendUnit->data.net.flags = 0;
						pSendUnit->data.net.buf = (char*) pSendUnit->pBuf;

						pjmedia_rtp_hdr* hdr =
								(pjmedia_rtp_hdr*) pSendUnit->data.net.buf;
						pjmedia_rtp_ext_hdr *ext =
								(pjmedia_rtp_ext_hdr*) (pSendUnit->data.net.buf
										+ sizeof(pjmedia_rtp_hdr));
						ext->profile_data = htons(0xcc);
						ext->length = htons(2);
						int64_t *pts = (int64_t*) (pSendUnit->data.net.buf
								+ sizeof(pjmedia_rtp_ext_hdr)
								+ sizeof(pjmedia_rtp_hdr));
						*pts = pUnit->data.packet.iPTS;
						memset(hdr, 0, sizeof(pjmedia_rtp_hdr));
						hdr->m = 1;
						hdr->pt = 96;
						hdr->x = 1;
						static pj_uint16_t seq = 12345;
						hdr->seq = htons(seq);
						hdr->ssrc = htonl(
								__plugin.getRTPFactory()->getStreamIdMap(
										STREAM_DIR_AUDIO_SEND)->send);
						hdr->ts = htonl((int)diff);
						hdr->v = 2;

						SocketAddr lAddr(AF_INET);
						lAddr.host(__plugin.getRTPFactory()->getServerAddr());
						lAddr.port(__plugin.getRTPFactory()->getRTPDataPort());
						pSendUnit->data.net.addr =
								*(sockaddr_in*) lAddr.address();

						memcpy(
								pSendUnit->data.net.buf
										+ sizeof(pjmedia_rtp_hdr)
										+ sizeof(pjmedia_rtp_ext_hdr) + 8,
								pUnit->data.packet.pData,
								pUnit->data.packet.iDataSize);

						__plugin.getRTPFactory()->copyAudio(
								pSendUnit->data.net.buf,
								pSendUnit->data.net.len, seq);
						seq++;
						__plugin.getRTPFactory()->getRTPIOBuf()->produceEnd(
								pSendUnit);
					} else {
						LOGI("audio send error");
					}
				}
				audioSendIOBuf->consumeEnd(pUnit);
			}
		}
		if (gpsSendIOBuf != NULL) {
			IOBuf::IOUnit *pUnit = gpsSendIOBuf->consumeBegin();
			if (pUnit && pUnit->data.packet.iDataSize > 0) {

				IOBuf::IOUnit *pSendUnit =
						__plugin.getRTPFactory()->getRTPIOBuf()->produceBegin(
								pUnit->data.packet.iDataSize
										+ sizeof(pjmedia_rtp_hdr));
				if (pSendUnit) {
					pSendUnit->data.net.len = pUnit->data.packet.iDataSize
							+ sizeof(pjmedia_rtp_hdr);
					pSendUnit->data.net.flags = 0;
					pSendUnit->data.net.buf = (char*) pSendUnit->pBuf;

					pjmedia_rtp_hdr* hdr =
							(pjmedia_rtp_hdr*) pSendUnit->data.net.buf;
					memset(hdr, 0, sizeof(pjmedia_rtp_hdr));
					hdr->m = 1;
					hdr->pt = 98;
					static pj_uint16_t seq = 7536;
					hdr->seq = htons(seq);
					hdr->ssrc = htonl(
							__plugin.getRTPFactory()->getStreamIdMap(
									STREAM_DIR_GPS_SEND)->send);
					static int ts = 320;
					hdr->ts = htonl(ts);
					ts += 320;
					hdr->v = 2;

					SocketAddr lAddr(AF_INET);
					lAddr.host(__plugin.getRTPFactory()->getServerAddr());
					lAddr.port(__plugin.getRTPFactory()->getRTPDataPort());
					pSendUnit->data.net.addr = *(sockaddr_in*) lAddr.address();

					memcpy(pSendUnit->data.net.buf + sizeof(pjmedia_rtp_hdr),
							pUnit->data.packet.pData,
							pUnit->data.packet.iDataSize);

					seq++;
					__plugin.getRTPFactory()->getRTPIOBuf()->produceEnd(
							pSendUnit);
				}

				gpsSendIOBuf->consumeEnd(pUnit);
			}
		}
		time_t c2 = time(NULL);
		time_t c3 = c2 - c1;
		if (c3 >= 3) {
			c1 = c2;
			m_audioNTP = audioNTP;
			m_videoNTP = videoNTP;
			sendRTCP();
		}
	}
}

void SendThread::sendRTCP() {
	IOBuf::IOUnit *pSendUnit =
			__plugin.getRTPFactory()->getRTPIOBuf()->produceBegin(
					sizeof(pjmedia_rtcp_common) + sizeof(pjmedia_rtcp_rr));
	if (pSendUnit) {
		pSendUnit->data.net.len = sizeof(pjmedia_rtcp_common)
				+ sizeof(pjmedia_rtcp_rr);
		pSendUnit->data.net.flags = 0;
		pSendUnit->data.net.buf = (char*) pSendUnit->pBuf;

		SocketAddr lAddr(AF_INET);
		lAddr.host(__plugin.getRTPFactory()->getServerAddr());
		lAddr.port(__plugin.getRTPFactory()->getRTPDataPort());
		pSendUnit->data.net.addr = *(sockaddr_in*) lAddr.address();

		pjmedia_rtcp_common *common =
				(pjmedia_rtcp_common*) pSendUnit->data.net.buf;
		common->version = 2;
		common->p = 0;
		common->ssrc =
				pj_ntohl(
						__plugin.getRTPFactory()->getStreamIdMap(
								STREAM_DIR_AUDIO_RECV)->send);
		common->count = 1;
		common->pt = 201;
		common->length = pj_ntohs(7);
		pjmedia_rtcp_rr *p_rr =
				(pjmedia_rtcp_rr*) ((char*) pSendUnit->data.net.buf
						+ sizeof(pjmedia_rtcp_common));

		pjmedia_rtcp_rr rr;
		pj_bzero(&rr, sizeof(rr));
		rr.ssrc =
				pj_ntohl(
						__plugin.getRTPFactory()->getStreamIdMap(
								STREAM_DIR_AUDIO_RECV)->recv);
		*p_rr = rr;

		__plugin.getRTPFactory()->getRTPIOBuf()->produceEnd(pSendUnit);
	}
	pSendUnit = __plugin.getRTPFactory()->getRTPIOBuf()->produceBegin(
			sizeof(pjmedia_rtcp_common) + sizeof(pjmedia_rtcp_sr));
	if (pSendUnit) {

		pSendUnit->data.net.len = sizeof(pjmedia_rtcp_common)
				+ sizeof(pjmedia_rtcp_sr);
		pSendUnit->data.net.flags = 0;
		pSendUnit->data.net.buf = (char*) pSendUnit->pBuf;

		SocketAddr lAddr(AF_INET);
		lAddr.host(__plugin.getRTPFactory()->getServerAddr());
		lAddr.port(__plugin.getRTPFactory()->getRTPDataPort());
		pSendUnit->data.net.addr = *(sockaddr_in*) lAddr.address();

		pjmedia_rtcp_common *common =
				(pjmedia_rtcp_common*) pSendUnit->data.net.buf;
		common->version = 2;
		common->p = 0;
		common->ssrc =
				pj_ntohl(
						__plugin.getRTPFactory()->getStreamIdMap(
								STREAM_DIR_VIDEO_SEND)->send);
		common->count = 1;
		common->pt = 200;
		common->length = pj_ntohs(7);
		pjmedia_rtcp_sr *p_sr =
				(pjmedia_rtcp_sr*) ((char*) pSendUnit->data.net.buf
						+ sizeof(pjmedia_rtcp_common));

		pjmedia_rtcp_sr sr;
		pj_bzero(&sr, sizeof(sr));
		sr.rtp_ts = pj_ntohl(m_videoTS);
		//sr.ntp_sec =
		int64_t sec = 0, usec = 0;
		sec = m_videoNTP / 1000000;
		usec = m_videoNTP % 1000000;
		sr.ntp_frac = pj_ntohl((usec * 4294967296.0) / 1000000);
		sr.ntp_sec = pj_ntohl(sec + 2208988800UL);
		*p_sr = sr;

		__plugin.getRTPFactory()->getRTPIOBuf()->produceEnd(pSendUnit);
	}
	pSendUnit = __plugin.getRTPFactory()->getRTPIOBuf()->produceBegin(
			sizeof(pjmedia_rtcp_common) + sizeof(pjmedia_rtcp_sr));
	if (pSendUnit) {

		pSendUnit->data.net.len = sizeof(pjmedia_rtcp_common)
				+ sizeof(pjmedia_rtcp_sr);
		pSendUnit->data.net.flags = 0;
		pSendUnit->data.net.buf = (char*) pSendUnit->pBuf;

		SocketAddr lAddr(AF_INET);
		lAddr.host(__plugin.getRTPFactory()->getServerAddr());
		lAddr.port(__plugin.getRTPFactory()->getRTPDataPort());
		pSendUnit->data.net.addr = *(sockaddr_in*) lAddr.address();

		pjmedia_rtcp_common *common =
				(pjmedia_rtcp_common*) pSendUnit->data.net.buf;
		common->version = 2;
		common->p = 0;
		common->ssrc =
				pj_ntohl(
						__plugin.getRTPFactory()->getStreamIdMap(
								STREAM_DIR_AUDIO_SEND)->send);
		common->count = 1;
		common->pt = 200;
		common->length = pj_ntohs(7);
		pjmedia_rtcp_sr *p_sr =
				(pjmedia_rtcp_sr*) ((char*) pSendUnit->data.net.buf
						+ sizeof(pjmedia_rtcp_common));

		pjmedia_rtcp_sr sr;
		pj_bzero(&sr, sizeof(sr));
		sr.rtp_ts = pj_ntohl(m_audioTS);
		int64_t sec = 0, usec = 0;
		sec = m_audioNTP / 1000000;
		usec = m_audioNTP % 1000000;
		sr.ntp_frac = pj_ntohl((usec * 4294967296.0) / 1000000);
		sr.ntp_sec = pj_ntohl(sec + 2208988800UL);
		//sr.ntp_sec =
		*p_sr = sr;

		__plugin.getRTPFactory()->getRTPIOBuf()->produceEnd(pSendUnit);
	}
}

void SendThread::sendVideo(char* payload, int len, int ts, int m, int64_t pts) {
	static pj_uint16_t seq = 12345;
	char buffer[1024];
	pjmedia_rtp_hdr *hdr = (pjmedia_rtp_hdr*) buffer;
	pjmedia_rtp_ext_hdr *ext = (pjmedia_rtp_ext_hdr*) (buffer
			+ sizeof(pjmedia_rtp_hdr));
	int64_t *pts0 = (int64_t*) (buffer + sizeof(pjmedia_rtp_hdr)
			+ sizeof(pjmedia_rtp_ext_hdr));
	*pts0 = pts;
	ext->length = htons(2);
	ext->profile_data = htons(0xcc);

	memset(hdr, 0, sizeof(pjmedia_rtp_hdr));
	hdr->m = m;
	hdr->pt = 96;
	hdr->seq = htons(seq);
	hdr->ssrc =
			htonl(__plugin.getRTPFactory()->getStreamIdMap(STREAM_DIR_VIDEO_SEND)->send);
	hdr->ts = htonl(ts);
	hdr->v = 2;
	hdr->x = 1;

	memcpy(buffer + sizeof(pjmedia_rtp_hdr) + sizeof(pjmedia_rtp_ext_hdr) + 8,
			payload, len);
	int rtp_len = len + sizeof(pjmedia_rtp_hdr) + sizeof(pjmedia_rtp_ext_hdr)
			+ 8;

	__plugin.getRTPFactory()->copyVideo(buffer, rtp_len, seq);

	IOBuf::IOUnit *pUnit =
			__plugin.getRTPFactory()->getRTPIOBuf()->produceBegin(rtp_len);
	if (pUnit != NULL) {
		pUnit->data.net.len = rtp_len;
		pUnit->data.net.buf = (char*) pUnit->pBuf;
		pUnit->data.net.flags = 0;
		SocketAddr lAddr(AF_INET);
		lAddr.host(__plugin.getRTPFactory()->getServerAddr());
		lAddr.port(__plugin.getRTPFactory()->getRTPDataPort());
		pUnit->data.net.addr = *(sockaddr_in*) lAddr.address();
		memcpy(pUnit->data.net.buf, buffer, rtp_len);

		__plugin.getRTPFactory()->getRTPIOBuf()->produceEnd(pUnit);
	}
	seq++;
}

void SendThread::cleanup() {
	delete this;
}

void SendThread::exit() {
	m_exit = 1;
}

RTPFactory::RTPFactory() {
	m_rtpDataPort = 0;
	memset(m_streamIdMap, 0, sizeof(m_streamIdMap));
	srand(time(NULL));
	m_streamIdMap[STREAM_DIR_VIDEO_SEND].recv = rand();
	m_streamIdMap[STREAM_DIR_AUDIO_SEND].recv = rand();
	m_streamIdMap[STREAM_DIR_GPS_SEND].recv = rand();
	m_streamIdMap[STREAM_DIR_AUDIO_RECV].recv = rand();
	m_videoSendIOBuf = m_audioSendIOBuf = NULL;
	m_currentMediaDir = 0;
	memset(m_audioBuffer, 0, sizeof(m_audioBuffer));
	memset(m_videoBuffer, 0, sizeof(m_videoBuffer));
}

RTPFactory::~RTPFactory() {

}

/* 0£ºÎ´ÖªÀàÐÍ£¬1£ºRTP£¬2£ºRTCP*/
static int pt_type[] = {
/*		0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15											*/
/*0*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*16*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*32*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*48*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*64*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*80*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*96*/1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
/*112*/1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
/*128*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*144*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*160*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*176*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*192*/0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0,
/*208*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*224*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*240*/0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
/*256*/0, };

enum {
	STREAM_TYPE_NONE = 0, STREAM_TYPE_RTP, STREAM_TYPE_RTCP,
};
int IsRTPorRTCP(char *data, int len, int* ssrc) {
	if (len < 12) {
		*ssrc = 0;
		return STREAM_TYPE_NONE;
	}
	pjmedia_rtp_hdr *rtp_hdr = (pjmedia_rtp_hdr*) data;
	int rtp_pt = rtp_hdr->pt & 0xff;
	if (pt_type[rtp_pt] == STREAM_TYPE_RTP) {
		*ssrc = pj_ntohl(rtp_hdr->ssrc);
		return STREAM_TYPE_RTP;
	}

	pjmedia_rtcp_common *rtcp_hdr = (pjmedia_rtcp_common*) data;
	int rtcp_pt = rtcp_hdr->pt & 0xff;
	if (pt_type[rtcp_pt] == STREAM_TYPE_RTCP) {
		/*if (rtcp_pt == RTCP_RR)
		 {
		 pjmedia_rtcp_rr *rtcp_rr = (pjmedia_rtcp_rr*) (data + sizeof(pjmedia_rtcp_common));
		 *ssrc = pj_ntohl(rtcp_rr->ssrc);
		 }
		 else if (rtcp_pt == RTCP_SR)
		 {
		 *ssrc = pj_ntohl(rtcp_hdr->ssrc);
		 }*/
		*ssrc = pj_ntohl(rtcp_hdr->ssrc);
		return STREAM_TYPE_RTCP;
	}
	*ssrc = 0;
	return STREAM_TYPE_NONE;
}

void RTPFactory::onRecvFrom(char* data, int size, sockaddr *src_addr,
		int addr_len, status_t status) {
	int ssrc = 0;
	int type = IsRTPorRTCP((char*) data, size, &ssrc);
	if (type == STREAM_TYPE_RTCP) {
		pjmedia_rtcp_common *common = (pjmedia_rtcp_common*) data;
		if (common->pt == 204) {
			RTCPRetranBuilder builder((char*) data, size);
			if (ssrc
					== __plugin.getRTPFactory()->getStreamIdMap(
							STREAM_DIR_AUDIO_SEND)->recv) {
				while (rtcp_retran_unit* unit = builder.GetNextPacket()) {
					int* retran_seq = RTCPRetranBuilder::GetBitMask(unit);
					__plugin.getRTPFactory()->sendAudio(unit->seq);
					for (int i = 0; i < LOSS_LENGTH; i++) {
						if (retran_seq[i] != -1) {
							__plugin.getRTPFactory()->sendAudio(retran_seq[i]);
						}
					}
				}
			} else if (ssrc
					== __plugin.getRTPFactory()->getStreamIdMap(
							STREAM_DIR_VIDEO_SEND)->recv) {
				while (rtcp_retran_unit* unit = builder.GetNextPacket()) {
					int* retran_seq = RTCPRetranBuilder::GetBitMask(unit);
					__plugin.getRTPFactory()->sendVideo(unit->seq);
					for (int i = 0; i < LOSS_LENGTH; i++) {
						if (retran_seq[i] != -1) {
							__plugin.getRTPFactory()->sendVideo(retran_seq[i]);
						}
					}
				}
			}

		}
	} else if (type == STREAM_TYPE_RTP) {
		if (ssrc
				== __plugin.getRTPFactory()->getStreamIdMap(
						STREAM_DIR_AUDIO_RECV)->recv) {
			inputAudioData(data, size);
		}
	}
}


void RTPFactory::inputAudioData(char* data, int size) {
	BigData audioDec;
	pjmedia_rtp_hdr *hdr = (pjmedia_rtp_hdr*) data;
	memset(&audioDec.packet, 0, sizeof(SAV_Packet));
	audioDec.packet.iDataSize = size - sizeof(pjmedia_rtp_hdr);
	audioDec.packet.iPTS = ntohl(hdr->ts);
	audioDec.packet.pData = (SAV_TYPE_UINT8*) (data + sizeof(pjmedia_rtp_hdr));
	for (int i = 0; i < m_audioPacketTransmit.size(); i++) {
		m_audioPacketTransmit[i]->transmit(&audioDec);
	}
}


AudioTransmit* RTPFactory::getAudioTransmit() {
	return &m_audioTransmit;
}

VideoTransmit* RTPFactory::getVideoTransmit() {
	return &m_videoTransmit;
}

GPSTransmit* RTPFactory::getGPSTransmit() {
	return &m_gpsTransmit;
}

void RTPFactory::setRTPIOBuf(IOBuf* pIOBuf) {
	m_rtpIOBuf = pIOBuf;
}

IOBuf* RTPFactory::getRTPIOBuf() {
	return m_rtpIOBuf;
}

void RTPFactory::setRTPSocketId(int id) {
	m_rtpSocketId = id;
}

int RTPFactory::getRTPSocketId() {
	return m_rtpSocketId;
}

int RTPFactory::getRTPDataPort() {
	return m_rtpDataPort;
}

void RTPFactory::setRTPDataPort(int port) {
	m_rtpDataPort = port;
}

char* RTPFactory::getServerAddr() {
	return m_serverAddr;
}

void RTPFactory::setServerAddr(char* addr) {
	sprintf(m_serverAddr, "%s", addr);
}

StreamIdMap* RTPFactory::getStreamIdMap(int index) {
	return &m_streamIdMap[index];
}

void RTPFactory::addAudioPacketTransmit(Transmit* t) {
	m_audioPacketTransmit.push_back(t);
}

IOBuf* RTPFactory::getVideoSendIOBuf() {
	return m_videoSendIOBuf;
}

IOBuf* RTPFactory::getAudioSendIOBuf() {
	return m_audioSendIOBuf;
}

IOBuf* RTPFactory::getGPSSendIOBuf() {
	return m_gpsSendIOBuf;
}

void RTPFactory::setVideoSendIOBuf(IOBuf* b) {
	m_videoSendIOBuf = b;
}

void RTPFactory::setAudioSendIOBuf(IOBuf* b) {
	m_audioSendIOBuf = b;
}

void RTPFactory::setGPSSendIOBuf(IOBuf* b) {
	m_gpsSendIOBuf = b;
}

int RTPFactory::getCurrentMediaDir() {
	return m_currentMediaDir;
}

void RTPFactory::setCurrentMediaDir(int d) {
	m_currentMediaDir = d;
}

android::Mutex* RTPFactory::getVideoSendIOBufMutex() {
	return &m_videoSendIOBufMutex;
}

android::Mutex* RTPFactory::getAudioSendIOBufMutex() {
	return &m_audioSendIOBufMutex;
}

android::Mutex* RTPFactory::getGPSSendIOBufMutex() {
	return &m_gpsSendIOBufMutex;
}

void RTPFactory::sendVideo(int seq) {
	int index = seq % 512;
	if (m_videoBuffer[index].seq == seq) {
		IOBuf::IOUnit *pUnit = getRTPIOBuf()->produceBegin(
				m_videoBuffer[index].length);
		if (pUnit != NULL) {
			pUnit->data.net.len = m_videoBuffer[index].length;
			pUnit->data.net.buf = (char*) pUnit->pBuf;
			pUnit->data.net.flags = 0;
			SocketAddr lAddr(AF_INET);
			lAddr.host(getServerAddr());
			lAddr.port(getRTPDataPort());
			pUnit->data.net.addr = *(sockaddr_in*) lAddr.address();
			memcpy(pUnit->data.net.buf, m_videoBuffer[index].buffer,
					m_videoBuffer[index].length);

			__plugin.getRTPFactory()->getRTPIOBuf()->produceEnd(pUnit);
		}
	}
}

void RTPFactory::sendAudio(int seq) {
	int index = seq % 512;
	if (m_audioBuffer[index].seq == seq) {
		IOBuf::IOUnit *pUnit = getRTPIOBuf()->produceBegin(
				m_audioBuffer[index].length);
		if (pUnit != NULL) {
			pUnit->data.net.len = m_audioBuffer[index].length;
			pUnit->data.net.buf = (char*) pUnit->pBuf;
			pUnit->data.net.flags = 0;
			SocketAddr lAddr(AF_INET);
			lAddr.host(getServerAddr());
			lAddr.port(getRTPDataPort());
			pUnit->data.net.addr = *(sockaddr_in*) lAddr.address();
			memcpy(pUnit->data.net.buf, m_audioBuffer[index].buffer,
					m_audioBuffer[index].length);

			getRTPIOBuf()->produceEnd(pUnit);
		}
	}
}

void RTPFactory::copyVideo(char* data, int length, int seq) {
	int index = seq % 512;
	m_videoBuffer[index].seq = seq;
	m_videoBuffer[index].length = length;
	memcpy(m_videoBuffer[index].buffer, data, length);
}

void RTPFactory::copyAudio(char* data, int length, int seq) {
	int index = seq % 512;
	m_audioBuffer[index].seq = seq;
	m_audioBuffer[index].length = length;
	memcpy(m_audioBuffer[index].buffer, data, length);
}

MPURegisterHandler::MPURegisterHandler(unsigned int prio /* = 100 */) :
		MessageHandler("mpu.register", prio, __plugin.name()) {

}

bool MPURegisterHandler::received(Message& msg) {
	const String& serverAddr = msg["serveraddr"];
	int serverPort = msg.getIntValue("serverport");
	int deviceId = msg.getIntValue("deviceid");
	const String& deviceName = msg["devicename"];

	RTPFactory *pFactory = __plugin.getRTPFactory();
	pFactory->setServerAddr((char*) serverAddr.c_str());
	return false;
}

MPUBroadcastInviteHandler::MPUBroadcastInviteHandler(
		unsigned int prio /* = 100 */) :
		MessageHandler("mpu.broadcast.invite", prio, __plugin.name()) {

}

bool MPUBroadcastInviteHandler::received(Message& msg) {
	int rtpPort = msg.getIntValue("rtpport");
	RTPFactory *pFactory = __plugin.getRTPFactory();
	pFactory->setRTPDataPort(rtpPort);
	return false;
}

MPUGetRTPHandler::MPUGetRTPHandler(unsigned int prio /* = 100 */) :
		MessageHandler("mpu.get.rtp", prio, __plugin.name()) {

}

bool MPUGetRTPHandler::received(Message& msg) {
	const String& videoSSRCMap = msg["videosendonly"];
	const String& audioSendSSRCMap = msg["audiosendonly"];
	const String& audioRecvSSRCMap = msg["audiorecvonly"];
	const String& gpsSendSSRCMap = msg["gpssendonly"];
	unsigned int d1, d2, d3, d4;
	RTPFactory *pRTP = __plugin.getRTPFactory();
	if (!videoSSRCMap.null() && !videoSSRCMap.empty()) {
		sscanf(videoSSRCMap.c_str(), "send-ssrc=%d,%d;recv-ssrc=%d,%d", &d1,
				&d2, &d3, &d4);
		d4 = pRTP->getStreamIdMap(STREAM_DIR_VIDEO_SEND)->recv;
		pRTP->getStreamIdMap(STREAM_DIR_VIDEO_SEND)->send = d3;
		char buffer[128];
		sprintf(buffer, "send-ssrc=%d,%d;recv-ssrc=%d,%d", d3, d4, d1, d2);
		msg.setParam("videosendonly", buffer);
	}
	if (!audioSendSSRCMap.null() && !audioSendSSRCMap.empty()) {
		sscanf(audioSendSSRCMap.c_str(), "send-ssrc=%d,%d;recv-ssrc=%d,%d", &d1,
				&d2, &d3, &d4);
		d4 = pRTP->getStreamIdMap(STREAM_DIR_AUDIO_SEND)->recv;
		pRTP->getStreamIdMap(STREAM_DIR_AUDIO_SEND)->send = d3;
		char buffer[128];
		sprintf(buffer, "send-ssrc=%d,%d;recv-ssrc=%d,%d", d3, d4, d1, d2);
		msg.setParam("audiosendonly", buffer);
	}
	if (!audioRecvSSRCMap.null() && !audioRecvSSRCMap.empty()) {
		sscanf(audioRecvSSRCMap.c_str(), "send-ssrc=%d,%d;recv-ssrc=%d,%d", &d1,
				&d2, &d3, &d4);
		LOGI("%s", audioRecvSSRCMap.c_str());
		d1 = pRTP->getStreamIdMap(STREAM_DIR_AUDIO_RECV)->recv;
		pRTP->getStreamIdMap(STREAM_DIR_AUDIO_RECV)->send = d2;
		char buffer[128];
		sprintf(buffer, "send-ssrc=%d,%d;recv-ssrc=%d,%d", d3, d4, d1, d2);
		LOGI("%s", buffer);
		msg.setParam("audiorecvonly", buffer);
	}
	if (!gpsSendSSRCMap.null() && !gpsSendSSRCMap.empty()) {
		sscanf(gpsSendSSRCMap.c_str(), "send-ssrc=%d,%d;recv-ssrc=%d,%d", &d1,
				&d2, &d3, &d4);
		LOGI("%s", gpsSendSSRCMap.c_str());
		d4 = pRTP->getStreamIdMap(STREAM_DIR_GPS_SEND)->recv;
		pRTP->getStreamIdMap(STREAM_DIR_GPS_SEND)->send = d3;
		char buffer[128];
		sprintf(buffer, "send-ssrc=%d,%d;recv-ssrc=%d,%d", d3, d4, d1, d2);
		msg.setParam("gpssendonly", buffer);
	}
	return true;
}

AudioPacketMountPoint::AudioPacketMountPoint(unsigned int prio /* = 100 */) :
		MessageHandler("mpu.mount.audio.packet", prio, __plugin.name()) {

}

bool AudioPacketMountPoint::received(Message& msg) {
	RTPFactory *pFactory = __plugin.getRTPFactory();
	RefObject *obj = (RefObject*) msg.userData();
	Transmit *t = (Transmit*) obj->getObject("Transmit");
	pFactory->addAudioPacketTransmit(t);
	return true;
}

MPUNotifyInviteHandler::MPUNotifyInviteHandler(unsigned int prio /* = 100 */) :
		MessageHandler("mpu.notify.invite", prio, __plugin.name()) {

}

bool MPUNotifyInviteHandler::received(Message& msg) {
	int mediaDir = msg.getIntValue("mediadir");
	if ((mediaDir & BVCU_MEDIADIR_AUDIOSEND) == BVCU_MEDIADIR_AUDIOSEND) {
		if ((__plugin.getRTPFactory()->getCurrentMediaDir()
				& BVCU_MEDIADIR_AUDIOSEND) != BVCU_MEDIADIR_AUDIOSEND) {
			if (!__plugin.getRTPFactory()->getAudioSendIOBuf()) {
				IOBuf *pAudioSendIOBuf = new IOBuf(80 * 64, 32);
				__plugin.getRTPFactory()->setAudioSendIOBuf(pAudioSendIOBuf);
			}
			android::Mutex *mutex =
					__plugin.getRTPFactory()->getAudioSendIOBufMutex();
			mutex->lock();
			__plugin.getRTPFactory()->getAudioSendIOBuf()->reset();
			mutex->unlock();
		}
	} else {
		//__plugin.getRTPFactory()->setAudioSendIOBuf(NULL);
	}

	if ((mediaDir & BVCU_MEDIADIR_VIDEOSEND) == BVCU_MEDIADIR_VIDEOSEND) {
		if ((__plugin.getRTPFactory()->getCurrentMediaDir()
				& BVCU_MEDIADIR_VIDEOSEND) != BVCU_MEDIADIR_VIDEOSEND) {
			if (!__plugin.getRTPFactory()->getVideoSendIOBuf()) {
				IOBuf *pVideoSendIOBuf = new IOBuf(1024 * 64, 32);
				__plugin.getRTPFactory()->setVideoSendIOBuf(pVideoSendIOBuf);
			}
			android::Mutex *mutex =
					__plugin.getRTPFactory()->getVideoSendIOBufMutex();
			mutex->lock();
			__plugin.getRTPFactory()->getVideoSendIOBuf()->reset();
			mutex->unlock();
		}
	} else {
		//__plugin.getRTPFactory()->setVideoSendIOBuf(NULL);
	}

	if ((mediaDir & BVCU_MEDIADIR_DATASEND) == BVCU_MEDIADIR_DATASEND) {
		if ((__plugin.getRTPFactory()->getCurrentMediaDir()
				& BVCU_MEDIADIR_DATASEND) != BVCU_MEDIADIR_DATASEND) {
			if (!__plugin.getRTPFactory()->getGPSSendIOBuf()) {
				IOBuf *pGPSSendIOBuf = new IOBuf(512, 32);
				__plugin.getRTPFactory()->setGPSSendIOBuf(pGPSSendIOBuf);
			}
			android::Mutex *mutex =
					__plugin.getRTPFactory()->getGPSSendIOBufMutex();
			mutex->lock();
			__plugin.getRTPFactory()->getGPSSendIOBuf()->reset();
			mutex->unlock();
		}
	} else {
		//__plugin.getRTPFactory()->setGPSSendIOBuf(NULL);
	}

	__plugin.getRTPFactory()->setCurrentMediaDir(mediaDir);
	if (mediaDir != 0) {
		__plugin.startupSendThread();
	} else {
		//__plugin.stopSendThread();
	}
	return false;
}
