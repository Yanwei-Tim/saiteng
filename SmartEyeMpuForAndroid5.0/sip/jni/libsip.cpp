#include <jni.h>
#include "libsip.h"
#include "pjlib.h"
#include "pjlib-util.h"
#include "pjlib-util/string.h"
#include <android/log.h>
#include <sys/system_properties.h>
#undef	LOG_TAG
#define LOG_TAG "SIP"
#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

INIT_PLUGIN(SIPModule);

#define FOUR_CHARS_TO_INT(a, b, c, d) (a<<24 | b<<16 | c<<8 | d)

SIPModule::SIPModule() :
		Module("SIP", "misc") {
	SEProto_GlobalParam cb;
	memset(&cb, 0, sizeof(cb));
	cb.OnCommand = SIPModule::sip_on_recv_command;
	cb.OnInvite = SIPModule::sip_on_recv_invite;
	cb.OnReInvite = SIPModule::sip_on_recv_reinvite;
	SEProto_Init(&cb);

	m_mpuSIPFactory = new MPUSIPFactory;

	m_sipThread.startup();
}

SIPModule::~SIPModule() {
	Output("Unload module SIP");
	delete m_mpuSIPFactory;
	m_sipThread.exit();
}

MPUSIPFactory* SIPModule::getMPUSIPFactory() {
	return m_mpuSIPFactory;
}

void SIPModule::sip_on_recv_command(SEProto_Transaction* pOperation,
		SEProto_Msg* pMsg) {

}

void SIPModule::sip_on_recv_invite(SEProto_Transaction* pOperation,
		SEProto_Msg* pMsg) {
	if (pMsg->stMsgHeader.iApplierID
			== __plugin.getMPUSIPFactory()->getApplierId()) {
		LOGI("%s", pMsg->stMsgHeader.line.szTargetID);
		sdp_session offer;
		bool rc = offer.ParseFromArray(pMsg->stMsgContent.pData,
				pMsg->stMsgContent.iDataLength);
		int puId = 0, channelIndex = 0, streamIndex = 0;
		if (sscanf(pMsg->stMsgHeader.line.szTargetID, "sip:PU_%X_%d_%d", &puId,
				&channelIndex, &streamIndex) != 3) {
			LOGI("sscanf err!");
			return;
		}
		if (channelIndex >= BVCU_SUBDEV_INDEXMAJOR_MIN_CHANNEL
				&& channelIndex <= BVCU_SUBDEV_INDEXMAJOR_MAX_CHANNEL) {
			__plugin.getMPUSIPFactory()->processInvite(&offer);
		} else if (channelIndex >= BVCU_SUBDEV_INDEXMAJOR_MIN_GPS
				&& channelIndex <= BVCU_SUBDEV_INDEXMAJOR_MAX_GPS) {
			__plugin.getMPUSIPFactory()->processInviteGPS(&offer);
		}

		SEProto_MsgContent stContent;
		char buf[1024] = { 0 };
		offer.SerializeToArray(buf, sizeof(buf));
		stContent.pData = buf;
		stContent.iDataLength = offer.ByteSize();
		strcpy(stContent.szContentType, "protobuf_bv1");
		stContent.iContentLength = offer.ByteSize();
		SEProto_AnswerLite(pOperation, 200, &stContent);

		if (channelIndex >= BVCU_SUBDEV_INDEXMAJOR_MIN_CHANNEL
				&& channelIndex <= BVCU_SUBDEV_INDEXMAJOR_MAX_CHANNEL) {
			SEProto_RegisterClose(pOperation->u.hDialog,
					MPUSIPFactory::OnAVDialogClose);
		} else if (channelIndex >= BVCU_SUBDEV_INDEXMAJOR_MIN_GPS
				&& channelIndex <= BVCU_SUBDEV_INDEXMAJOR_MAX_GPS) {
			SEProto_RegisterClose(pOperation->u.hDialog,
					MPUSIPFactory::OnGPSDialogClose);
		}

	}
}

void SIPModule::sip_on_recv_reinvite(SEProto_Transaction* pOperation,
		SEProto_Msg* pMsg) {
	if (pMsg->stMsgHeader.iApplierID
			== __plugin.getMPUSIPFactory()->getApplierId()) {
		LOGI("sip_on_recv_reinvite++++++++++++++++++++++++++++++");
		sdp_session offer;
		bool rc = offer.ParseFromArray(pMsg->stMsgContent.pData,
				pMsg->stMsgContent.iDataLength);

		__plugin.getMPUSIPFactory()->processInvite(&offer);

		SEProto_MsgContent stContent;
		char buf[1024] = { 0 };
		offer.SerializeToArray(buf, sizeof(buf));
		stContent.pData = buf;
		stContent.iDataLength = offer.ByteSize();
		strcpy(stContent.szContentType, "protobuf_bv1");
		stContent.iContentLength = offer.ByteSize();
		SEProto_AnswerLite(pOperation, 200, &stContent);
	}
}

void SIPModule::initialize() {
	Output("Initializing module SIP");
	Engine::install(new MessageRelay("engine.start", this, Private, 150));
	Engine::install(new MPURegisterHandler(150));
	Engine::install(new MPUBroadcastCodecHandler());
	installRelay(Halt);
	installRelay(Private + 1, "engine.stop");
	Engine::install(new MessageRelay("mpu.restart", this, Private + 2));
}

bool SIPModule::received(Message& msg, int id) {
	if (msg == "engine.start") {
		Message cmd("net.socket");
		cmd.addParam("type", "udp");
		cmd.addParam("port", String(0));
		Engine::dispatch(cmd);

		int cmdSocket = cmd.getIntValue("socketid");
		m_mpuSIPFactory->setSIPSocketId(cmdSocket);

		Message recvFrom("net.recvfrom");
		recvFrom.addParam("socketid", String(cmdSocket));
		recvFrom.userData((RefObject*) __plugin.getObject("RefObject"));
		Engine::dispatch(recvFrom);

		Message sendTo("net.send");
		sendTo.addParam("socketid", String(cmdSocket));
		Engine::dispatch(sendTo);

		RefObject *pObj = sendTo.userData();
		IOBuf* pIOBuf = (IOBuf *) pObj->getObject("IOBuf");
		m_mpuSIPFactory->setSIPIOBuf(pIOBuf);
	} else if (msg == "engine.stop") {
		m_sipThread.exit();
		LOGI("stop");
	} else if (msg == "mpu.restart") {
		LOGI("restart");
		m_mpuSIPFactory->restart();
	}
	return Module::received(msg, id);
}

MPUSIPFactory::MPUSIPFactory() {
	TransportConfig cfg;
	memset(&cfg, 0, sizeof(cfg));
	cfg.iMtu = 800;
	cfg.iFlag = SEProto_HTransport_UNRELIABLE;
	cfg.SendMsg = MPUSIPFactory::sip_send_msg_udp;
	SEProto_CreateTransport(&cfg, &m_sipTransport);
	restart();
}

void MPUSIPFactory::restart() {
	m_applierId = 0;
	m_readlyForSPSAndPPS = false;
	m_dialogStatus = 0;
	m_gpsMediaDir = m_currentMediaDir = 0;
}

MPUSIPFactory::~MPUSIPFactory() {
	SEProto_CloseObject(m_sipTransport, true);
}

void SIPModule::onRecvFrom(char* data, int size, sockaddr *src_addr,
		int addr_len, status_t status) {
	char* pBuf = NULL;
	int len = 0;
	void *token = SEProto_PrepareParse(m_mpuSIPFactory->getTransport(),
			(char**) &pBuf, &len, NULL);
	memcpy(pBuf, data, size);
	int eaten = SEProto_ParsePacket(m_mpuSIPFactory->getTransport(), token,
			size);
}

void* SIPModule::getObject(const String& name) const {
	return OnRecvFrom::getObject(name);
}

void MPUSIPFactory::setSIPSocketId(int id) {
	m_sipSocketId = id;
}

int MPUSIPFactory::getSIPSocketId() {
	return m_sipSocketId;
}

void MPUSIPFactory::setSIPIOBuf(IOBuf* pIOBuf) {
	m_sipIOBuf = pIOBuf;
}

IOBuf* MPUSIPFactory::getSIPIOBuf() {
	return m_sipIOBuf;
}

void MPUSIPFactory::Login_OnEvent(SEProto_Transaction* pOperation,
		SEProto_Result iResult) {
	MPUSIPFactory::Login_OnResponse(pOperation, iResult, NULL);
}

void MPUSIPFactory::Login_OnResponse(SEProto_Transaction* pOperation,
		SEProto_Result iResult, SEProto_Msg* pMsg) {
	if (SEProto_Result_SUCCEEDED(iResult) && pMsg != NULL) {
		int applierId = pMsg->stMsgHeader.iApplierID;
		LOGI("applierId=%d result=%d", applierId, iResult);
		__plugin.getMPUSIPFactory()->setApplierId(applierId);
		BVCU_Event_Common common;
		common.iResult = (BVCU_Result) iResult;
		__plugin.getMPUSIPFactory()->getLoginCallback()->onEvent(applierId,
				BVCU_EVENT_SESSION_OPEN, &common);
		__plugin.getMPUSIPFactory()->sendKeepalive();
	} else if (SEProto_Result_FAILED(iResult)) {
		BVCU_Event_Common common;
		common.iResult = (BVCU_Result) iResult;
		__plugin.getMPUSIPFactory()->getLoginCallback()->onEvent(0,
				BVCU_EVENT_SESSION_OPEN, &common);
	}
}

void MPUSIPFactory::OnAVDialogClose(SEProto_Transaction* pTransaction) {
	LOGI("av dialog close**********************");
	Message m("mpu.notify.invite");
	__plugin.getMPUSIPFactory()->setDialogStatus(BVCU_EVENT_DIALOG_CLOSE);
	m.addParam("status", String(BVCU_EVENT_DIALOG_CLOSE));
	m.addParam("mediadir",
			String(__plugin.getMPUSIPFactory()->getGPSMediaDir()));
	m.addParam("applierid",
			String(__plugin.getMPUSIPFactory()->getApplierId()));
	Engine::dispatch(m);
	__plugin.getMPUSIPFactory()->setCurrentMediaDir(0);
}

void MPUSIPFactory::OnGPSDialogClose(SEProto_Transaction* pTransaction) {
	LOGI("gps dialog close**********************");
	Message m("mpu.notify.invite");
	//__plugin.getMPUSIPFactory()->setDialogStatus(BVCU_EVENT_DIALOG_CLOSE);
	m.addParam("status", String(BVCU_EVENT_DIALOG_CLOSE));
	m.addParam("mediadir",
			String(__plugin.getMPUSIPFactory()->getCurrentMediaDir()));
	m.addParam("applierid",
			String(__plugin.getMPUSIPFactory()->getApplierId()));
	Engine::dispatch(m);
	__plugin.getMPUSIPFactory()->setGPSMediaDir(0);
}

void MPUSIPFactory::setDialogStatus(int status) {
	m_dialogStatus = status;
}

int MPUSIPFactory::getDialogStatus() {
	return m_dialogStatus;
}

int MPUSIPFactory::getCurrentMediaDir() {
	return m_currentMediaDir;
}

void MPUSIPFactory::setCurrentMediaDir(int d) {
	m_currentMediaDir = d;
}

int MPUSIPFactory::getGPSMediaDir() {
	return m_gpsMediaDir;
}

void MPUSIPFactory::setGPSMediaDir(int d) {
	m_gpsMediaDir = d;
}

void MPUSIPFactory::getDefaultWGS(int* longitude, int *latitude) {
	char value[64] = { 0 };
	FILE *file = fopen("/sdcard/wgs.conf", "r");
	if(file != NULL){
		fread(value, 64, 1, file);
		fclose(file);
		sscanf(value, "%d,%d", latitude, longitude);
	}else{
		*latitude = 31.205618;
		*longitude = 121.471011;
	}

	LOGI("latitude=%d longitude=%d", *latitude, *longitude);
}

char* MPUSIPFactory::getChannelName() {
	return m_channelName;
}

void MPUSIPFactory::setChannelName(char* name) {
	sprintf(m_channelName, "%s", name);
}

void MPUSIPFactory::login() {
	initRegisterInfo(&m_deviceInfo, &m_channelInfo);
	int deviceId = getDeviceId();
	char *serverAddr = getServerAddr();
	int serverPort = getServerPort();
	{
		SEProto_Msg sep_Msg;
		memset(&sep_Msg, 0, sizeof(SEProto_Msg));
		sprintf(sep_Msg.stMsgHeader.line.szTargetID, "sip:PU_%X@%s:%d",
				deviceId, serverAddr, serverPort);
		strcpy(sep_Msg.stMsgHeader.szFrom, "<sip:127.0.0.1>");
		sprintf(sep_Msg.stMsgHeader.szTo, "<sip:%s>", serverAddr);
		strcpy(sep_Msg.stMsgHeader.szContact, "sip:127.0.0.1");
		strcpy(sep_Msg.stMsgHeader.szUserAgent, "PU");
		sep_Msg.stMsgHeader.iApplierID = 0;
		sep_Msg.stMsgHeader.iSeq = 1;
		sep_Msg.stMsgHeader.iExpire = 0x7f;
		sep_Msg.stMsgHeader.iMethod = SEProto_REGISTER_Method;

		DeviceInfo device_info;
		PUChannelInfo channel_info;

		device_info.set_bsupportsms(m_deviceInfo.bSupportSMS);
		device_info.set_ialarmlinkactioncount(
				m_deviceInfo.iAlarmLinkActionCount);
		device_info.set_ialertincount(m_deviceInfo.iAlertInCount);
		device_info.set_ialertoutcount(m_deviceInfo.iAlertOutCount);
		device_info.set_iaudioincount(m_deviceInfo.iAudioInCount);
		device_info.set_iaudiooutcount(m_deviceInfo.iAudioOutCount);
		device_info.set_ichannelcount(m_deviceInfo.iChannelCount);
		device_info.set_icruisecount(m_deviceInfo.iCruiseCount);
		device_info.set_igpscount(m_deviceInfo.iGPSCount);
		for (int i = 0; i < BVCU_MAX_LANGGUAGE_COUNT; i++) {
			if (m_deviceInfo.iLanguage[i] == BVCU_LANGUAGE_INVALID)
				break;
			device_info.add_ilanguage(m_deviceInfo.iLanguage[i]);
		}
		device_info.set_ilanguageindex(m_deviceInfo.iLanguageIndex);
		device_info.set_ipresetcount(m_deviceInfo.iPresetCount);
		device_info.set_iptzcount(m_deviceInfo.iPTZCount);
		device_info.set_iputype(m_deviceInfo.iPUType);
		device_info.set_iradiocount(m_deviceInfo.iRadioCount);
		device_info.set_iserialportcount(m_deviceInfo.iSerialPortCount);
		device_info.set_istoragecount(m_deviceInfo.iStorageCount);
		device_info.set_ivideoincount(m_deviceInfo.iVideoInCount);
		device_info.set_iwificount(m_deviceInfo.iWIFICount);
		device_info.set_ilatitude(m_deviceInfo.iLatitude);
		device_info.set_ilongitude(m_deviceInfo.iLongitude);
		device_info.set_szhardwareversion(m_deviceInfo.szHardwareVersion);
		device_info.set_szid(m_deviceInfo.szID);
		device_info.set_szname(m_deviceInfo.szName);
		device_info.set_szmanufacturer(m_deviceInfo.szManufacturer);
		device_info.set_szproductname(m_deviceInfo.szProductName);
		device_info.set_szsoftwareversion(m_deviceInfo.szSoftwareVersion);

		channel_info.set_szid(m_channelInfo.szPUID);
		channel_info.set_szpuname(m_channelInfo.szPUName);
		channel_info.set_ionlinestatus(m_channelInfo.iOnlineStatus);
		channel_info.set_igpscount(m_channelInfo.iGPSCount);
		channel_info.set_ilatitude(m_channelInfo.iLatitude);
		channel_info.set_ilongitude(m_channelInfo.iLongitude);
		for (int iChannelIndex = 0; iChannelIndex < m_channelInfo.iChannelCount;
				++iChannelIndex) {
			PUOneChannelInfo *pPUOneChannel = channel_info.add_pchannel();
			pPUOneChannel->set_szname(
					m_channelInfo.pChannel[iChannelIndex].szName);
			pPUOneChannel->set_iptz(
					m_channelInfo.pChannel[iChannelIndex].iPTZIndex);
			pPUOneChannel->set_ichannelindex(
					m_channelInfo.pChannel[iChannelIndex].iChannelIndex);
			pPUOneChannel->set_imediadir(
					m_channelInfo.pChannel[iChannelIndex].iMediaDir);
		}

		char data[4096];
		int *len = (int*) data;
		device_info.SerializeToArray(data + 4, 4096);
		*len = device_info.ByteSize();
		channel_info.SerializeToArray(data + 8 + *len, 4096);
		len = (int*) (data + 4 + *len);
		*len = channel_info.ByteSize();

		SEProto_Transaction sep_Transaction;
		memset(&sep_Transaction, 0, sizeof(sep_Transaction));
		sep_Transaction.OnEvent = MPUSIPFactory::Login_OnEvent;
		sep_Transaction.cb.iFlag = SEProto_Single_Packet;
		sep_Transaction.cb.func.single.OnDataRead =
				MPUSIPFactory::Login_OnResponse;
		sep_Transaction.hTransport = m_sipTransport;
		sep_Msg.stMsgContent.pData = data;
		sep_Msg.stMsgContent.iDataLength = device_info.ByteSize()
				+ channel_info.ByteSize() + 8;
		strcpy(sep_Msg.stMsgContent.szContentType, "protobuf_bv1");
		sep_Msg.stMsgContent.iContentLength = sep_Msg.stMsgContent.iDataLength;
		if (SEProto_SendCmd(&sep_Transaction, &sep_Msg, this)
				!= SEProto_RESULT_S_OK) {
		}
	}
}

void MPUSIPFactory::setServerAddr(char* addr) {
	sprintf(m_serverAddr, "%s", addr);
}

char* MPUSIPFactory::getServerAddr() {
	return m_serverAddr;
}

int MPUSIPFactory::getServerPort() {
	return m_serverPort;
}

void MPUSIPFactory::setServerPort(int port) {
	m_serverPort = port;
}

void MPUSIPFactory::setDeviceId(int id) {
	m_deviceId = id;
}

int MPUSIPFactory::getDeviceId() {
	return m_deviceId;
}

void MPUSIPFactory::setDeviceName(char* name) {
	sprintf(m_deviceName, "%s", name);
}

char* MPUSIPFactory::getDeviceName() {
	return m_deviceName;
}

void MPUSIPFactory::setLoginCallback(OnEventCallback* cb) {
	m_loginCallback = cb;
}

OnEventCallback* MPUSIPFactory::getLoginCallback() {
	return m_loginCallback;
}

SEProto_HTransport MPUSIPFactory::getTransport() {
	return m_sipTransport;
}

void MPUSIPFactory::setApplierId(int id) {
	m_applierId = id;
}

int MPUSIPFactory::getApplierId() {
	return m_applierId;
}

void MPUSIPFactory::setSPSAndPPS(char* sps, char* pps) {
	sprintf(m_sps, "%s", sps);
	sprintf(m_pps, "%s", pps);
	m_readlyForSPSAndPPS = true;
	m_waitSPSAndPPS.signal();
}

void MPUSIPFactory::getSPSAndPPS(char* sps, char* pps) {
	while (!m_readlyForSPSAndPPS) {
		android::Mutex mutex;
		m_waitSPSAndPPS.wait(mutex);
	}
	sprintf(sps, "%s", m_sps);
	sprintf(pps, "%s", m_pps);
}

void MPUSIPFactory::sendKeepalive() {
	if (getApplierId() != 0) {
		static int seq = 2345;
		static char from[(BVCU_MAX_ID_LEN + 1) * 2];
		static char to[(BVCU_MAX_ID_LEN + 1) * 2];
		snprintf(to, sizeof(to) - 1, "sip:CMS@%s", m_serverAddr);
		char deviceId[64];
		sprintf(deviceId, "PU_%X_0_-1_-1", m_deviceId);
		snprintf(from, sizeof(from) - 1, "sip:%s@%s", deviceId, m_serverAddr);
		SEProto_Msg stParam, *pParam;
		pParam = &stParam;
		pj_bzero(pParam, sizeof(*pParam));
		pParam->stMsgHeader.iSeq = seq++;
		pParam->stMsgHeader.iExpire = 0x0f;
		pParam->stMsgHeader.iMethod = SEProto_KEEPALIVE_Method;
		strcpy(pParam->stMsgHeader.szUserAgent, "MPU");
		strcpy(pParam->stMsgHeader.szFrom, from);
		strcpy(pParam->stMsgHeader.szTo, to);
		strcpy(pParam->stMsgHeader.line.szTargetID, from);
		pParam->stMsgHeader.iApplierID = getApplierId();

		SEProto_Result result;
		SEProto_Transaction stOperation;
		pj_bzero(&stOperation, sizeof(stOperation));
		stOperation.hTransport = m_sipTransport;
		stOperation.iTimeOut = 30;

		stOperation.cb.iFlag = SEProto_Single_Packet;
		stOperation.cb.func.single.OnDataRead = NULL;
		result = SEProto_SendCmd(&stOperation, &stParam, NULL);
	}
}

int MPUSIPFactory::getMediaDir(sdp_session* ses) {
	int media_dir = 0;
	for (int i = 0; i < ses->media_size(); i++) {
		int dir = 0;
		sdp_media *media = ses->mutable_media(i);
		if (media->mutable_desc()->media() == "video") {
			dir = BVCU_MEDIADIR_VIDEOSEND;
		} else if (media->mutable_desc()->media() == "audio") {
			dir = BVCU_MEDIADIR_AUDIOSEND;
		} else if (media->mutable_desc()->media() == "data") {
			dir = BVCU_MEDIADIR_DATASEND;
		}
		for (int i = 0; i < media->attr_size(); i++) {
			sdp_attr *attr = media->mutable_attr(i);
			if (attr->name() == "sendonly") {
				dir <<= 1;
				break;
			} else if (attr->name() == "recvonly") {
				break;
			}
		}
		media_dir |= dir;
	}
	return media_dir;
}

void MPUSIPFactory::processInviteGPS(sdp_session* offer) {
	LOGI("processInviteGPS+++++++++++++++++++++++");
	int flag = getMediaDir(offer);
	setGPSMediaDir(flag);
	Message msg("mpu.notify.invite");
	msg.addParam("status", String(BVCU_EVENT_DIALOG_OPEN));
	msg.addParam("mediadir", String(flag | getCurrentMediaDir()));
	msg.addParam("applierid", String(getApplierId()));
	Engine::dispatch(msg);
	int port = 0;
	int mediacount = offer->media_size();
	for (int i = 0; i < mediacount; i++) {
		sdp_media* m = offer->mutable_media(i);
		sdp_desc* desc = m->mutable_desc();
		if (desc->media() == "data") {
			port = m->desc().port();
			for (int i = 0; i < m->attr_size(); i++) {
				sdp_attr* attr = m->mutable_attr(i);
				if (attr->name() == "recvonly") {
					attr->set_name("sendonly");
					unsigned int d1, d2, d3, d4;
					for (int j = 1; j < m->attr_size(); j++) {
						attr = m->mutable_attr(j);
						if (attr->name() == "ssrcmap") {
							Message m("mpu.get.rtp");
							m.addParam("gpssendonly", attr->value().c_str());
							Engine::dispatch(m);
							attr->set_value(m["gpssendonly"]);
						} else if (attr->name() == "rtcp") {
							//TRACE("my_OnInvite: video sendonly: rtcp\n");
						}
					}
				}
			}
		}
	}
	Message bc("mpu.broadcast.invite");
	LOGI("--------port=%d av mediaDir=%d", port, getCurrentMediaDir());
	bc.addParam("rtpport", String(port));
	bc.addParam("mediadir", String(flag | getCurrentMediaDir()));
	bc.addParam("status", String(BVCU_EVENT_DIALOG_OPEN));
	bc.addParam("applierid", String(m_applierId));
	Engine::dispatch(bc);
}

void MPUSIPFactory::processInvite(sdp_session* offer) {
	LOGI("processInvite+++++++++++++++++++++++");
	if (m_dialogStatus == 0 || m_dialogStatus == BVCU_EVENT_DIALOG_CLOSE) {
		m_dialogStatus = BVCU_EVENT_DIALOG_OPEN;
		m_readlyForSPSAndPPS = false;
	} else
		m_dialogStatus = BVCU_EVENT_DIALOG_UPDATE;
	int flag = getMediaDir(offer);

	if ((flag & getCurrentMediaDir() & BVCU_MEDIADIR_VIDEOSEND)
			== BVCU_MEDIADIR_VIDEOSEND) {
		m_readlyForSPSAndPPS = true;
	} else {
		m_readlyForSPSAndPPS = false;
	}
	setCurrentMediaDir(flag);
	Message msg("mpu.notify.invite");
	msg.addParam("status", String(m_dialogStatus));
	msg.addParam("mediadir", String(flag | getGPSMediaDir()));
	msg.addParam("applierid", String(getApplierId()));
	Engine::dispatch(msg);
	int port = 0;
	int mediacount = offer->media_size();
	for (int i = 0; i < mediacount; i++) {
		sdp_media* m = offer->mutable_media(i);
		sdp_desc* desc = m->mutable_desc();
		if (desc->media() == "video") {
			port = m->desc().port();
			sdp_attr* attr = m->mutable_attr(0);
			if (attr->name() == "recvonly") {
				attr->set_name("sendonly");
				unsigned int d1, d2, d3, d4;
				for (int j = 1; j < m->attr_size(); j++) {
					attr = m->mutable_attr(j);
					if (attr->name() == "ssrcmap") {
						Message m("mpu.get.rtp");
						m.addParam("videosendonly", attr->value().c_str());
						Engine::dispatch(m);
						attr->set_value(m["videosendonly"]);
					} else if (attr->name() == "rtcp") {
						//TRACE("my_OnInvite: video sendonly: rtcp\n");
					}
				}

				char sps[128], pps[128];
				memset(sps, 0, sizeof(sps));
				memset(pps, 0, sizeof(pps));
				getSPSAndPPS(sps, pps);
				//char* sps =
				//		"Z2QAKK2EBUViuKxUdCAqKxXFYqOhAVFYrisVHQgKisVxWKjoQFRWK4rFR0ICorFcVio6ECSFITk8nyfk/k/J8nm5s00IEkKQnJ5Pk/J/J+T5PNzZprQLBLI="; //msg["sps"];
				//char* pps = "aO48sA=="; //msg["pps"];
				LOGI("sps=%s pps=%s", sps, pps);
				attr = m->add_attr();
				attr->set_name("rtpmap");
				char rtpmap[32] = { 0 };
				sprintf(rtpmap, "%d h264/90000", 96);
				attr->set_value(rtpmap);
				attr = m->add_attr();
				attr->set_name("fmtp");
				char fmtp[512] = { 0 };
				sprintf(fmtp, "%d sprop-parameter-sets=%s,%s", 96, sps, pps);
				attr->set_value(fmtp);

				continue;
			}
		}
		if (desc->media() == "audio") {
			sdp_attr* attr = m->mutable_attr(0);
			port = m->desc().port();
			if (attr->name() == "recvonly") {
				attr->set_name("sendonly");
				unsigned int d1, d2, d3, d4;
				for (int j = 1; j < m->attr_size(); j++) {
					attr = m->mutable_attr(j);
					if (attr->name() == "ssrcmap") {
						Message m("mpu.get.rtp");
						m.addParam("audiosendonly", attr->value().c_str());
						Engine::dispatch(m);
						attr->set_value(m["audiosendonly"]);
					}
				}
				attr = m->add_attr();
				attr->set_name("rtpmap");
				char rtpmap[32] = { 0 };
				sprintf(rtpmap, "%d G726-16/8000", 97);
				attr->set_value(rtpmap);

				continue;
			}
		}
		if (desc->media() == "audio") {
			sdp_attr* attr = m->mutable_attr(0);
			port = m->desc().port();
			if (attr->name() == "sendonly") {
				attr->set_name("recvonly");
				unsigned int d1, d2, d3, d4;
				for (int j = 1; j < m->attr_size(); j++) {
					attr = m->mutable_attr(j);
					if (attr->name() == "ssrcmap") {
						Message m("mpu.get.rtp");
						m.addParam("audiorecvonly", attr->value().c_str());
						Engine::dispatch(m);
						attr->set_value(m["audiorecvonly"]);
					}
				}
				attr = m->add_attr();
				attr->set_name("rtpmap");
				char rtpmap[32] = { 0 };
				sprintf(rtpmap, "%d G726-16/8000", 98);
				attr->set_value(rtpmap);

				continue;
			}
		}
	}
	Message bc("mpu.broadcast.invite");
	bc.addParam("rtpport", String(port));
	bc.addParam("mediadir", String(flag | getGPSMediaDir()));
	bc.addParam("status", String(m_dialogStatus));
	bc.addParam("applierid", String(m_applierId));
	Engine::dispatch(bc);
}

int MPUSIPFactory::property_get(const char *key, char *value,
		const char *default_value) {
	int len;

	len = __system_property_get(key, value);
	if (len > 0) {
		return len;
	}

	if (default_value) {
		len = strlen(default_value);
		memcpy(value, default_value, len + 1);
	}
	return len;
}

void MPUSIPFactory::initRegisterInfo(BVCU_PUCFG_DeviceInfo* pDeviceInfo,
		BVCU_PUChannelInfo* pChannelInfo) {
	{
		sprintf(pDeviceInfo->szID, "PU_%X", getDeviceId());
		sprintf(pDeviceInfo->szManufacturer, "smartdu@qq.com");
		sprintf(pDeviceInfo->szProductName, "Android MPU");
		sprintf(pDeviceInfo->szSoftwareVersion, "0.0.1");
		sprintf(pDeviceInfo->szHardwareVersion, "0.0.1");
		pDeviceInfo->iPUType = 0;
		pDeviceInfo->iLanguage[0] = 1;
		pDeviceInfo->iLanguage[1] = 2;
		pDeviceInfo->iLanguage[2] = 3;
		pDeviceInfo->iLanguageIndex = 1;
		sprintf(pDeviceInfo->szName, "%s", getDeviceName());
		pDeviceInfo->iWIFICount = 0;
		pDeviceInfo->iRadioCount = 0;
		pDeviceInfo->iChannelCount = 1;
		pDeviceInfo->iVideoInCount = 1;
		pDeviceInfo->iAudioInCount = 1;
		pDeviceInfo->iAudioOutCount = 1;
		pDeviceInfo->iPTZCount = 0;
		pDeviceInfo->iSerialPortCount = 0;
		pDeviceInfo->iAlertInCount = 0;
		pDeviceInfo->iAlertOutCount = 0;
		pDeviceInfo->iStorageCount = 0;
		pDeviceInfo->iGPSCount = 1;
		pDeviceInfo->bSupportSMS = 1;
		pDeviceInfo->iPresetCount = 0;
		pDeviceInfo->iCruiseCount = 0;
		pDeviceInfo->iAlarmLinkActionCount = 0;
		getDefaultWGS(&pDeviceInfo->iLongitude, &pDeviceInfo->iLatitude);
	}
	{
		pChannelInfo->iChannelCount = 2;
		pChannelInfo->iGPSCount = 0;
		pChannelInfo->iLatitude = pDeviceInfo->iLatitude;
		pChannelInfo->iLongitude = pDeviceInfo->iLongitude;
		pChannelInfo->iOnlineStatus = 1;
		sprintf(pChannelInfo->szPUID, "PU_%X", getDeviceId());
		sprintf(pChannelInfo->szPUName, "%s", getDeviceName());
		pChannelInfo->pChannel = (BVCU_PUOneChannelInfo*) malloc(
				sizeof(BVCU_PUOneChannelInfo) * 2);
		memset(pChannelInfo->pChannel, 0, sizeof(BVCU_PUOneChannelInfo) * 2);
		{
			pChannelInfo->pChannel[0].iChannelIndex =
					BVCU_SUBDEV_INDEXMAJOR_MIN_CHANNEL;
			pChannelInfo->pChannel[0].iPTZIndex = 0;
			sprintf(pChannelInfo->pChannel[0].szName, "%s", m_channelName);
			pChannelInfo->pChannel[0].iMediaDir = BVCU_MEDIADIR_VIDEOSEND
					| BVCU_MEDIADIR_TALKONLY;
		}
		{
			pChannelInfo->pChannel[1].iChannelIndex =
					BVCU_SUBDEV_INDEXMAJOR_MIN_GPS;
			pChannelInfo->pChannel[1].iPTZIndex = 0;
			sprintf(pChannelInfo->pChannel[1].szName, "%s", "GPS");
			pChannelInfo->pChannel[1].iMediaDir = BVCU_MEDIADIR_DATASEND;
		}
	}
}

SEProto_Result MPUSIPFactory::sip_send_msg_udp(
		const SEProto_Transaction* pOperation, char* pData, int iDataLen,
		pj_sockaddr_in *rem_addr) {
	SEProto_Result result = SEProto_RESULT_S_OK;

	MPUSIPFactory *pFactory = __plugin.getMPUSIPFactory();
	pFactory->m_sendMsgMutex.lock();
	IOBuf* pList = pFactory->getSIPIOBuf();
	IOBuf::IOUnit* pUnit = pList->produceBegin(iDataLen);
	if (pUnit != NULL) {
		pUnit->data.net.buf = (char*) pUnit->pBuf;
		pUnit->data.net.len = iDataLen;
		SocketAddr lAddr(AF_INET);
		lAddr.host(pFactory->getServerAddr());
		lAddr.port(pFactory->getServerPort());
		pUnit->data.net.flags = 0;
		pUnit->data.net.addr = *(sockaddr_in*) lAddr.address();
		memcpy(pUnit->data.net.buf, pData, iDataLen);
		pList->produceEnd(pUnit);
	} else {
		result = SEProto_RESULT_E_FAILED;
	}
	pFactory->m_sendMsgMutex.unlock();
	return result;
}

SIPThread::SIPThread() {
	m_exit = 0;
}

SIPThread::~SIPThread() {

}

void SIPThread::run() {
	time_t c1 = time(NULL);
	while (!m_exit) {
		SEProto_HandleEvent(NULL);
		time_t c2 = time(NULL);
		time_t c3 = c2 - c1;
		if (c3 >= 30) {
			c1 = c2;
			__plugin.getMPUSIPFactory()->sendKeepalive();
		}
		::sleep(1);
	}
}

void SIPThread::cleanup() {

}

void SIPThread::exit() {
	m_exit = 1;
}

MPURegisterHandler::MPURegisterHandler(unsigned int prio /* = 100 */) :
		MessageHandler("mpu.register", prio, __plugin.name()) {

}

bool MPURegisterHandler::received(Message& msg) {
	MPUSIPFactory *pFactory = __plugin.getMPUSIPFactory();
	const String& serverAddr = msg["serveraddr"];
	int serverPort = msg.getIntValue("serverport");
	int deviceId = msg.getIntValue("deviceid");
	const String& deviceName = msg["devicename"];
	const String& channelName = msg["channelname"];

	pFactory->setChannelName((char*) channelName.c_str());
	pFactory->setServerAddr((char*) serverAddr.c_str());
	pFactory->setServerPort(serverPort);
	pFactory->setDeviceId(deviceId);
	pFactory->setDeviceName((char*) deviceName.c_str());
	RefObject *pObj = msg.userData();
	OnEventCallback* pOnEvent = (OnEventCallback*) pObj->getObject(
			"OnEventCallback");
	pFactory->setLoginCallback(pOnEvent);
	pFactory->login();
	return true;
}

MPUBroadcastCodecHandler::MPUBroadcastCodecHandler(
		unsigned int prio /* = 100 */) :
		MessageHandler("mpu.broadcast.codec", prio, __plugin.name()) {

}

bool MPUBroadcastCodecHandler::received(Message& msg) {
	const String& sps = msg["sps"];
	const String& pps = msg["pps"];
	__plugin.getMPUSIPFactory()->setSPSAndPPS((char*) sps.c_str(),
			(char*) pps.c_str());
	return false;
}

void __static_check() {
	pj_md5_init(0);
	pj_dns_resolver_create(0, 0, 0, 0, 0, 0);
	pj_strncpy2_escape(0, 0, 0, 0);
	pj_str_unescape(0, 0);
	pj_dns_srv_resolve(0, 0, 0, 0, 0, 0, 0, 0, 0);
	pj_array_insert(0, 0, 0, 0, 0);
}
