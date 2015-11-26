#include <jni.h>
#include "libcodec.h"
#include <pjlib-util.h>
#include "BVCU.h"
#include <android/log.h>
#undef	LOG_TAG
#define LOG_TAG "CODEC"
//#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

CodecModule::CodecModule() :
		Module("Codec", "misc") {
	m_audioDecThread = NULL;
	m_audioEncThread = NULL;
	m_videoEncThread = NULL;
}

CodecModule::~CodecModule() {
	Output("Unload module Codec");
}

CodecInfo* CodecModule::getCodecInfo() {
	return &m_codecInfo;
}

CodecFactory* CodecModule::getCodecFactory() {
	return &m_codecFactory;
}

void CodecModule::startupVideoEncThread() {
	if (m_videoEncThread != NULL)
		return;
	m_videoEncThread = new VideoEncThread(&m_codecFactory);
	m_videoEncThread->startup();
}

void CodecModule::stopVideoEncThread() {
	if (m_videoEncThread) {
		m_videoEncThread->exit();
		m_videoEncThread = NULL;
	}
}

void CodecModule::startupAudioEncThread() {
	if (m_audioEncThread != NULL)
		return;
	m_audioEncThread = new AudioEncThread(&m_codecFactory);
	m_audioEncThread->startup();
}

void CodecModule::stopAudioEncThread() {
	if (m_audioEncThread) {
		m_audioEncThread->exit();
		m_audioEncThread = NULL;
	}
}

void CodecModule::startupAudioDecThread() {
	if (m_audioDecThread != NULL)
		return;
	m_audioDecThread = new AudioDecThread(&m_codecFactory);
	m_audioDecThread->startup();
}

void CodecModule::stopAudioDecThread() {
	if (m_audioDecThread) {
		m_audioDecThread->exit();
		m_audioDecThread = NULL;
	}

	getCodecFactory()->resetFrameCount();
	getCodecFactory()->resetUploadCount();
}

void CodecModule::initialize() {
	Output("Initializing module Codec");
	Engine::install(new CodecingHandler());
	Engine::install(new AudioDecMountPoint());
	Engine::install(new AudioEncMountPoint());
	Engine::install(new VideoEncMountPoint());
	Engine::install(new FrameCountMountPoint());
	Engine::install(new UploadCountMountPoint());
	Engine::install(new MessageRelay("engine.start", this, Private, 150));
	installRelay(Halt);
	installRelay(Private + 1, "engine.stop");
	Module::initialize();
}

INIT_PLUGIN(CodecModule);

bool CodecModule::received(Message& msg, int id) {
	if (msg == "engine.start") {
		Message msg0("mpu.mount.audio.raw");
		msg0.userData(
				(RefObject*) m_codecFactory.getAudioRawTransmit()->getObject(
						"RefObject"));
		Engine::dispatch(msg0);

		Message msg1("mpu.mount.video.raw");
		msg1.userData(
				(RefObject*) m_codecFactory.getVideoRawTransmit()->getObject(
						"RefObject"));
		Engine::dispatch(msg1);

		Message msg2("mpu.mount.audio.packet");
		msg2.userData(
				(RefObject*) m_codecFactory.getAudioPacketTransmit()->getObject(
						"RefObject"));
		Engine::dispatch(msg2);
	} else if (msg == "engine.stop") {
		__plugin.stopAudioDecThread();
		__plugin.stopAudioEncThread();
		__plugin.stopVideoEncThread();
		LOGI("stop");
	}
	return Module::received(msg, id);
}

CodecInfo::CodecInfo() {
}

SAVCodec_Context* CodecInfo::getAudioEncCtx(SAVCodec_Context* audioEncCtx) {
	memset(audioEncCtx, 0, sizeof(SAVCodec_Context));
	audioEncCtx->iSize = sizeof(SAVCodec_Context);
	audioEncCtx->eCodecID = SAVCODEC_ID_G726;
	audioEncCtx->bEncode = SAV_BOOL_TRUE;
	audioEncCtx->eMediaType = SAV_MEDIATYPE_AUDIO;
	audioEncCtx->stAudioParam.eSampleFormat = SAV_SAMPLE_FMT_S16;
	audioEncCtx->iBitRate = 16000;
	audioEncCtx->stAudioParam.iChannelCount = 1;
	audioEncCtx->stAudioParam.iFrameSize = 320;
	audioEncCtx->stAudioParam.iSampleRate = 8000;

	return audioEncCtx;
}

SAVCodec_Context* CodecInfo::getAudioDecCtx(SAVCodec_Context* audioDecCtx) {
	memset(audioDecCtx, 0, sizeof(SAVCodec_Context));
	audioDecCtx->iSize = sizeof(SAVCodec_Context);
	audioDecCtx->eCodecID = SAVCODEC_ID_G726;
	audioDecCtx->bEncode = SAV_BOOL_FALSE;
	audioDecCtx->eMediaType = SAV_MEDIATYPE_AUDIO;
	audioDecCtx->stAudioParam.eSampleFormat = SAV_SAMPLE_FMT_S16;
	audioDecCtx->stAudioParam.iChannelCount = 1;
	audioDecCtx->stAudioParam.iSampleRate = 8000;
	audioDecCtx->iBitRate = 16000;

	return audioDecCtx;
}

void CodecInfo::closeCtx(SAVCodec_Context* ctx) {
	SAVCodec_Close(ctx);
}

VideoEncThread::VideoEncThread(CodecFactory* pCodec) {
	m_codecFactory = pCodec;
}

VideoEncThread::~VideoEncThread() {

}

void VideoEncThread::run() {
	OMXClient client;
	status_t rc = client.connect();
	m_videoEncIOBuf = m_codecFactory->getVideoEncIOBuf();
	sp<MediaSource> m_videoSource = NULL;
	H264MediaSource *m_videoTrack = new H264MediaSource(
			m_codecFactory->getPreviewWidth(),
			m_codecFactory->getPreviewHeight(), m_videoEncIOBuf);

	Message m("mpu.dump");
	Engine::dispatch(m);

	int frameRate = m.getIntValue("I_CODEC_VIDEOFR");
	int bitRate = m.getIntValue("I_CODEC_VIDEOBR");
	int iFramesInterval = m.getIntValue("I_CODEC_VIDEOII");
	LOGI(
			"frameRate=%d bitRate=%d iFramesInterval=%d", frameRate, bitRate, iFramesInterval);

	m_videoTrack->setFrameRate(frameRate);
	m_videoTrack->setBitRate(bitRate);
	m_videoTrack->setIFramesInterval(iFramesInterval);

	m_videoSource = OMXCodec::Create(client.interface(),
			m_videoTrack->getFormat(), true, m_videoTrack, NULL, 0, NULL);
	rc = m_videoSource->start();
	BigData videoEnc;
	bool once = true;
	while (1 && rc == OK) {
		MediaBuffer *buffer = NULL;
		rc = m_videoSource->read(&buffer);
		if (rc == OK) {
			int length = buffer->range_length();
			int64_t stamp = 0;
			buffer->meta_data()->findInt64(kKeyTime, &stamp);
			if (length > 0) {
				if (once) {
					once = false;
					broadcastCodecInfo((char*) buffer->data(), length);
				}
				/*
				char buf[1024] = { 0 };
				char *data = (char*) buffer->data();
				int ret = 0;
				for (int i = 0; i < (length > 10 ? 10 : length); i++) {
					ret += sprintf(buf + ret, "%x ", data[i]);
				}
				LOGI("%s", buf);
				*/
				videoEnc.packet.iDataSize = length;
				videoEnc.packet.pData = (SAV_TYPE_UINT8*) buffer->data();
				videoEnc.packet.iPTS = stamp;
				m_codecFactory->videoEncTransmit(&videoEnc);
				m_codecFactory->increaseUploadCount(length);
			}
			buffer->release();
		} else {
			break;
		}
	}
	m_videoSource->stop();
	client.disconnect();
}

void VideoEncThread::cleanup() {
	LOGI("VideoEncThread::cleanup() %p", this);
	m_videoEncIOBuf->destruct();
	delete this;
}

void VideoEncThread::exit() {
	android::Mutex* mutex = __plugin.getCodecFactory()->getVideoEncMutex();
	mutex->lock();
	IOBuf::IOUnit *pUnit = m_videoEncIOBuf->produceBegin(1);
	LOGI("VideoEncThread::exit() pUnit=%p", pUnit);
	if (pUnit) {
		pUnit->data.frame.iDataSize[0] = 0;
		m_videoEncIOBuf->produceEnd(pUnit);
		LOGI("VideoEncThread::exit() %p", this);
	}
	mutex->unlock();
}

static int findhead(char* data, int len) {
	if (len < 4)
		return -1;
	for (int i = 0; i <= len - 4; i++) {
		if (data[i] == 0 && data[i + 1] == 0 && data[i + 2] == 0
				&& data[i + 3] == 1)
			return i;
	}
	return -1;
}

static int findsps(char* sps, int len, char* dst) {
	int head = findhead(sps, len);
	if (head == -1)
		return 0;
	if ((sps[head + 4] & 0xf) == 0x7) {
		int head2 = findhead(sps + 1, len - 1);
		memcpy(dst, sps + head + 4, head2 - head - 4 + 1);
		return head2 - head - 4 + 1;
	}
	return 0;
}

static int findpps(char* pps, int len, char* dst) {
	int head = findhead(pps, len);
	if (head == -1)
		return 0;
	if ((pps[head + 4] & 0xf) == 0x8) {
		memcpy(dst, pps + head + 4, len - head - 4);
		return len - head - 4;
	}
	return 0;
}

void VideoEncThread::broadcastCodecInfo(char* data, int size) {
	char m_sps[128];
	char m_pps[128];
	int tmp_len = 0;
	char tmp_buf[128];
	tmp_len = findsps(data, size, tmp_buf);
	int sps_len = 1024;
	pj_base64_encode((unsigned char*) tmp_buf, tmp_len, m_sps, &sps_len);
	m_sps[sps_len] = '\0';

	int pps_len = 1024;
	tmp_len = findpps(data + 1, size - 1, tmp_buf);
	pj_base64_encode((unsigned char*) tmp_buf, tmp_len, m_pps, &pps_len);
	m_pps[pps_len] = '\0';

	Message m("mpu.broadcast.codec");
	m.addParam("sps", m_sps);
	m.addParam("pps", m_pps);
	Engine::dispatch(m);
}

AudioEncThread::AudioEncThread(CodecFactory* pCodec) {
	__plugin.getCodecInfo()->getAudioEncCtx(&m_audioEncCtx);

	m_codecFactory = pCodec;
}

AudioEncThread::~AudioEncThread() {

}

void AudioEncThread::run() {
	m_audioEncIOBuf = m_codecFactory->getAudioEncIOBuf();
	char data[1024];
	BigData audioEnc;
	SAVCodec_Open(&m_audioEncCtx);
	while (1) {
		IOBuf::IOUnit *pUnit = m_audioEncIOBuf->consumeBegin();
		if (pUnit && pUnit->data.frame.iDataSize[0] > 0) {
			memset(&audioEnc, 0, sizeof(SAV_Packet));
			audioEnc.packet.iSize = sizeof(SAV_Packet);
			audioEnc.packet.iFlags = 0;
			audioEnc.packet.iStreamIndex = 0;
			audioEnc.packet.pData = (SAV_TYPE_UINT8*) data;
			audioEnc.packet.iPTS = 0;
			audioEnc.packet.iDataSize = sizeof(data)
					- SAV_INPUT_BUFFER_PADDING_SIZE;
			SAV_TYPE_INT64 iPTS = pUnit->data.frame.iPTS;
			pUnit->data.frame.iPTS = 0;
			int ret = SAVCodec_Process(&m_audioEncCtx, &pUnit->data.frame,
					&audioEnc.packet);
			if (ret >= 0) {
				audioEnc.packet.iDataSize = ret;
				audioEnc.packet.iPTS = iPTS * 1000;
				m_codecFactory->audioEncTransmit(&audioEnc);
			}
			m_audioEncIOBuf->consumeEnd(pUnit);
		} else
			break;
	}
	__plugin.getCodecInfo()->closeCtx(&m_audioEncCtx);
}

void AudioEncThread::cleanup() {
	LOGI("AudioEncThread::cleanup() %p", this);
	m_audioEncIOBuf->destruct();
	delete this;
}

void AudioEncThread::exit() {
	android::Mutex* mutex = __plugin.getCodecFactory()->getAudioEncMutex();
	mutex->lock();
	IOBuf::IOUnit *pUnit = m_audioEncIOBuf->produceBegin(1);
	LOGI("AudioEncThread::exit() pUnit=%p", pUnit);
	if (pUnit) {
		pUnit->data.frame.iDataSize[0] = 0;
		m_audioEncIOBuf->produceEnd(pUnit);
		LOGI("AudioEncThread::exit() %p", this);
	}
	mutex->unlock();
}

AudioDecThread::AudioDecThread(CodecFactory* pCodec) {
	__plugin.getCodecInfo()->getAudioDecCtx(&m_audioDecCtx);

	m_codecFactory = pCodec;
}

AudioDecThread::~AudioDecThread() {

}

void AudioDecThread::run() {
	SAVCodec_Open(&m_audioDecCtx);
	CodecFactory *codecFactory = __plugin.getCodecFactory();
	m_audioDecIOBuf = codecFactory->getAudioDecIOBuf();
	char data[1024];
	BigData audioDec;
	while (1) {
		IOBuf::IOUnit *pUnit = m_audioDecIOBuf->consumeBegin();
		if (pUnit && pUnit->data.packet.iDataSize > 0) {
			audioDec.frame.iSize = sizeof(SAV_Frame);
			audioDec.frame.iDataSize[0] = sizeof(data);
			audioDec.frame.ppData[0] = (SAV_TYPE_UINT8*) data;

			pUnit->data.packet.iSize = sizeof(SAV_Packet);
			pUnit->data.packet.iFlags = SAV_PKT_FLAG_KEY;
			pUnit->data.packet.iPTS = 2264;
			int ret = SAVCodec_Process(&m_audioDecCtx, &audioDec.frame,
					&pUnit->data.packet);
			if (ret >= 0) {
				m_codecFactory->audioDecTransmit(&audioDec);
			}
			m_audioDecIOBuf->consumeEnd(pUnit);
		} else
			break;
	}
	__plugin.getCodecInfo()->closeCtx(&m_audioDecCtx);
}

void AudioDecThread::cleanup() {
	LOGI("AudioDecThread::cleanup() %p", this);
	m_audioDecIOBuf->destruct();
	delete this;
}

void AudioDecThread::exit() {
	android::Mutex* mutex = __plugin.getCodecFactory()->getAudioDecMutex();
	mutex->lock();
	IOBuf::IOUnit *pUnit = m_audioDecIOBuf->produceBegin(1);
	LOGI("AudioDecThread::exit() pUnit=%p", pUnit);
	if (pUnit) {
		pUnit->data.packet.iDataSize = 0;
		m_audioDecIOBuf->produceEnd(pUnit);
		LOGI("AudioDecThread::exit() %p", this);
	}
	mutex->unlock();
}

CodecFactory::CodecFactory() {
	m_audioDecIOBuf = m_videoEncIOBuf = m_audioEncIOBuf = NULL;
	m_currentMediaDir = 0;
	m_width = m_height = 0;
}

CodecFactory::~CodecFactory() {
	if (m_audioEncIOBuf) {
		m_audioEncIOBuf = NULL;
	}
	if (m_videoEncIOBuf) {
		m_videoEncIOBuf = NULL;
	}
	if (m_audioDecIOBuf) {
		m_audioDecIOBuf = NULL;
	}
}

void CodecFactory::setAudioEncIOBuf(IOBuf *audioEncIOBuf) {
	m_audioEncMutex.lock();
	m_audioEncIOBuf = audioEncIOBuf;
	m_audioEncMutex.unlock();
}

void CodecFactory::setVideoEncIOBuf(IOBuf *videoEncIOBuf) {
	m_videoEncMutex.lock();
	m_videoEncIOBuf = videoEncIOBuf;
	m_videoEncMutex.unlock();
}

void CodecFactory::setAudioDecIOBuf(IOBuf* audioDecIOBuf) {
	m_audioDecMutex.lock();
	m_audioDecIOBuf = audioDecIOBuf;
	m_audioDecMutex.unlock();
}

IOBuf* CodecFactory::getAudioEncIOBuf() {
	return m_audioEncIOBuf;
}

IOBuf* CodecFactory::getVideoEncIOBuf() {
	return m_videoEncIOBuf;
}

IOBuf* CodecFactory::getAudioDecIOBuf() {
	return m_audioDecIOBuf;
}

void CodecFactory::setPreviewSize(int w, int h) {
	m_width = w;
	m_height = h;
}

int CodecFactory::getPreviewWidth() {
	return m_width;
}

int CodecFactory::getPreviewHeight() {
	return m_height;
}

int CodecFactory::getFrameCount() {
	return m_totalframecount;
}

int CodecFactory::getUploadCount() {
	return m_totaluploadcount;
}

void CodecFactory::increaseFrameCount(int iCount) {
	m_totalframecount += iCount;
}

void CodecFactory::increaseUploadCount(int iCount) {
	m_totaluploadcount += iCount;
}

void CodecFactory::resetFrameCount() {
	m_totalframecount = 0;
}

void CodecFactory::resetUploadCount() {
	m_totaluploadcount = 0;
}

void CodecFactory::videoEncTransmit(BigData *pFrame) {
	for (int i = 0; i < m_videoEncTransmit.size(); i++){
		m_videoEncTransmit[i]->transmit(pFrame);
	}
	increaseFrameCount(1);
}

void CodecFactory::audioEncTransmit(BigData *pFrame) {
	for (int i = 0; i < m_audioEncTransmit.size(); i++)
		m_audioEncTransmit[i]->transmit(pFrame);
}

void CodecFactory::audioDecTransmit(BigData *pFrame) {
	for (int i = 0; i < m_audioDecTransmit.size(); i++)
		m_audioDecTransmit[i]->transmit(pFrame);
}

void CodecFactory::addVideoEncTransmit(Transmit* fo) {
	m_videoEncTransmit.push_back(fo);
}

void CodecFactory::addAudioEncTransmit(Transmit* fo) {
	m_audioEncTransmit.push_back(fo);
}

void CodecFactory::addAudioDecTransmit(Transmit* fo) {
	m_audioDecTransmit.push_back(fo);
}

Transmit* CodecFactory::getAudioRawTransmit() {
	return &m_audioRawTransmit;
}

Transmit* CodecFactory::getVideoRawTransmit() {
	return &m_videoRawTransmit;
}

Transmit* CodecFactory::getAudioPacketTransmit() {
	return &m_audioPacketTransmit;
}

void CodecFactory::setCurrentMediaDir(int dir) {
	m_currentMediaDir = dir;
}

int CodecFactory::getCurrentMediaDir() {
	return m_currentMediaDir;
}

android::Mutex* CodecFactory::getAudioEncMutex() {
	return &m_audioEncMutex;
}

android::Mutex* CodecFactory::getVideoEncMutex() {
	return &m_videoEncMutex;
}

android::Mutex* CodecFactory::getAudioDecMutex() {
	return &m_audioDecMutex;
}

CodecingHandler::CodecingHandler(unsigned int prio) :
		MessageHandler("mpu.codec", prio, __plugin.name()) {

}

bool CodecingHandler::received(Message& msg) {
	int mediaDir = msg.getIntValue("mediadir");
	LOGI("mediaDir=%d", mediaDir);
	CodecFactory *codecFactory = __plugin.getCodecFactory();
	RefObject *obj = msg.userData();

	if ((mediaDir & BVCU_MEDIADIR_AUDIOSEND) == BVCU_MEDIADIR_AUDIOSEND) {
		if ((codecFactory->getCurrentMediaDir() & BVCU_MEDIADIR_AUDIOSEND)
				!= BVCU_MEDIADIR_AUDIOSEND) {
			LOGI("audio send---------------");
			int bufferSize = msg.getIntValue("buffersize");
			IOBuf *audioEncIOBuf = new IOBuf(bufferSize * 64, 32, true);
			codecFactory->setAudioEncIOBuf(audioEncIOBuf);

			__plugin.startupAudioEncThread();
		}
	} else {
		codecFactory->setAudioEncIOBuf(NULL);
		__plugin.stopAudioEncThread();
	}

	if ((mediaDir & BVCU_MEDIADIR_AUDIORECV) == BVCU_MEDIADIR_AUDIORECV) {
		if ((codecFactory->getCurrentMediaDir() & BVCU_MEDIADIR_AUDIORECV)
				!= BVCU_MEDIADIR_AUDIORECV) {
			//int packetSize = msg.getIntValue("packetsize");
			int packetSize = 90;
			IOBuf *audioDecIOBuf = new IOBuf(packetSize * 64, 32, true);
			codecFactory->setAudioDecIOBuf(audioDecIOBuf);

			__plugin.startupAudioDecThread();
		}
	} else {
		codecFactory->setAudioDecIOBuf(NULL);
		__plugin.stopAudioDecThread();
	}
	if ((mediaDir & BVCU_MEDIADIR_VIDEOSEND) == BVCU_MEDIADIR_VIDEOSEND) {
		if ((codecFactory->getCurrentMediaDir() & BVCU_MEDIADIR_VIDEOSEND)
				!= BVCU_MEDIADIR_VIDEOSEND) {
			LOGI("video send---------------");
			int width = msg.getIntValue("width");
			int height = msg.getIntValue("height");
			codecFactory->setPreviewSize(width, height);
			IOBuf *videoEncIOBuf = new IOBuf(width * height * 3 * 128, 32,
					true);
			codecFactory->setVideoEncIOBuf(videoEncIOBuf);

			__plugin.startupVideoEncThread();
		}
	} else {
		codecFactory->setVideoEncIOBuf(NULL);
		__plugin.stopVideoEncThread();
	}
	if (mediaDir == 0) {
		LOGI("close dialog");
	}
	codecFactory->setCurrentMediaDir(mediaDir);
	return false;
}

H264MediaSource::H264MediaSource(int width, int height, IOBuf* videoEncIOBuf) {
	mGroup = new MediaBufferGroup;
	m_mediaBuffer = new MediaBuffer((width * height * 3) / 2);
	mGroup->add_buffer(m_mediaBuffer);
	meta = new MetaData;
	m_width = width;
	m_height = height;
	m_videoEncIOBuf = videoEncIOBuf;

	Message m("mpu.dump");
	Engine::dispatch(m);
	m_keyTime = m.getIntValue("I_CODEC_VIDEOKT");
	LOGI("keyTime=%d", m_keyTime);
}

H264MediaSource::~H264MediaSource() {
	delete mGroup;
}

status_t H264MediaSource::start(MetaData *params) {
	return OK;
}

status_t H264MediaSource::stop() {
	return OK;
}

sp<MetaData> H264MediaSource::getFormat() {
	LOGI(
			"%d %d %d %d %d", m_width, m_height, m_frameRate, m_bitRate, m_IFramesInterval);
	;
	meta->setCString(kKeyMIMEType, "video/avc");
	meta->setInt32(kKeyWidth, m_width);
	meta->setInt32(kKeyHeight, m_height);
	meta->setInt32(kKeyFrameRate, m_frameRate);
	meta->setInt32(kKeyBitRate, m_bitRate);
	meta->setInt32(kKeyColorFormat, OMX_COLOR_FormatYUV420SemiPlanar);
	meta->setInt32(kKeyIFramesInterval, m_IFramesInterval);
	meta->setInt32(kKeyStride, m_width);
	meta->setInt32(kKeySliceHeight, m_height);
	meta->setInt32(kKeyMaxInputSize, (m_width * m_height * 3) / 2);
//meta->setInt32(kKeyVideoProfile, OMX_VIDEO_AVCProfileMain);
//meta->setInt32(kKeyVideoLevel, OMX_VIDEO_AVCLevel1);
	return meta;
}

status_t H264MediaSource::read(MediaBuffer **out, const ReadOptions *options) {
	MediaBuffer *buffer;
	ssize_t e = mGroup->acquire_buffer(&buffer);
	if (e != OK)
		return e;

	IOBuf::IOUnit *pUnit = m_videoEncIOBuf->consumeBegin();
	if (pUnit && pUnit->data.frame.iDataSize[0] > 0) {
		memcpy(buffer->data(), pUnit->data.frame.ppData[0],
				pUnit->data.frame.iDataSize[0]);
		buffer->set_range(0, pUnit->data.frame.iDataSize[0]);
		buffer->meta_data()->clear();

		buffer->meta_data()->setInt64(kKeyTime, pUnit->data.frame.iPTS * 1000);
		//buffer->meta_data()->setInt64(kKeyTime, pUnit->data.frame.iPTS);
		//static int64_t keytime = m_keyTime;
		//buffer->meta_data()->setInt64(kKeyTime, keytime);
		//keytime += m_keyTime;
		*out = buffer;
		m_videoEncIOBuf->consumeEnd(pUnit);
		return OK;
	}
	buffer->release();
	return UNKNOWN_ERROR;
}

void H264MediaSource::setFrameRate(int fr) {
	m_frameRate = fr;
}

void H264MediaSource::setBitRate(int br) {
	m_bitRate = br;
}

void H264MediaSource::setIFramesInterval(int ifi) {
	m_IFramesInterval = ifi;
}

VideoEncMountPoint::VideoEncMountPoint(unsigned int prio) :
		MessageHandler("mpu.mount.video.enc", prio, __plugin.name()) {

}

bool VideoEncMountPoint::received(Message& msg) {
	RefObject *obj = msg.userData();
	Transmit *fo = (Transmit*) obj->getObject("Transmit");
	__plugin.getCodecFactory()->addVideoEncTransmit(fo);
	return true;
}

AudioEncMountPoint::AudioEncMountPoint(unsigned int prio) :
		MessageHandler("mpu.mount.audio.enc", prio, __plugin.name()) {

}

bool AudioEncMountPoint::received(Message& msg) {
	RefObject *obj = msg.userData();
	Transmit *fo = (Transmit*) obj->getObject("Transmit");
	__plugin.getCodecFactory()->addAudioEncTransmit(fo);
	return true;
}

AudioDecMountPoint::AudioDecMountPoint(unsigned int prio) :
		MessageHandler("mpu.mount.audio.dec", prio, __plugin.name()) {

}

bool AudioDecMountPoint::received(Message& msg) {
	RefObject *obj = msg.userData();
	Transmit *fo = (Transmit*) obj->getObject("Transmit");
	__plugin.getCodecFactory()->addAudioDecTransmit(fo);
	return true;
}

FrameCountMountPoint::FrameCountMountPoint(unsigned int prio) :
	MessageHandler("mpu.mount.video.frame.count", prio, __plugin.name()) {
}

bool FrameCountMountPoint::received(Message& msg) {
	LOGI("FrameCountMountPoint %d",__plugin.getCodecFactory()->getFrameCount());
	msg.addParam("framecount", String(__plugin.getCodecFactory()->getFrameCount()));
	return true;
}

UploadCountMountPoint::UploadCountMountPoint(unsigned int prio) :
	MessageHandler("mpu.mount.video.upload.count", prio, __plugin.name()) {
}

bool UploadCountMountPoint::received(Message& msg) {
	LOGI("UploadCountMountPoint %d",__plugin.getCodecFactory()->getUploadCount());

	msg.addParam("uploadcount", String(__plugin.getCodecFactory()->getUploadCount()));
	return true;
}

VideoTransmit::VideoTransmit() {
}

void VideoTransmit::transmit(BigData* pData) {
	android::Mutex* mutex = __plugin.getCodecFactory()->getVideoEncMutex();
	mutex->lock();
	IOBuf* videoEncIOBuf = __plugin.getCodecFactory()->getVideoEncIOBuf();
	if (videoEncIOBuf) {
		IOBuf::IOUnit *pUnit = videoEncIOBuf->produceBegin(
				pData->frame.iDataSize[0]);
		if (pUnit) {
			memset(&pUnit->data.frame, 0, sizeof(SAV_Frame));
			pUnit->data.frame.iDataSize[0] = pData->frame.iDataSize[0];
			pUnit->data.frame.iSize = sizeof(SAV_Frame);
			pUnit->data.frame.iPTS = pData->frame.iPTS;
			pUnit->data.frame.ppData[0] = (SAV_TYPE_UINT8*) pUnit->pBuf;
			memcpy(pUnit->data.frame.ppData[0], pData->frame.ppData[0],
					pUnit->data.frame.iDataSize[0]);
			videoEncIOBuf->produceEnd(pUnit);
		}
	}
	mutex->unlock();
}

AudioTransmit::AudioTransmit() {
}

void AudioTransmit::transmit(BigData* pData) {
	android::Mutex *mutex = __plugin.getCodecFactory()->getAudioEncMutex();
	mutex->lock();
	IOBuf* audioEncIOBuf = __plugin.getCodecFactory()->getAudioEncIOBuf();
	if (audioEncIOBuf) {
		IOBuf::IOUnit *pUnit = audioEncIOBuf->produceBegin(
				pData->frame.iDataSize[0]);
		if (pUnit) {
			memset(&pUnit->data.frame, 0, sizeof(SAV_Frame));
			pUnit->data.frame.iDataSize[0] = pData->frame.iDataSize[0];
			pUnit->data.frame.iSize = sizeof(SAV_Frame);
			pUnit->data.frame.iPTS = pData->frame.iPTS;
			pUnit->data.frame.ppData[0] = (SAV_TYPE_UINT8*) pUnit->pBuf;
			memcpy(pUnit->data.frame.ppData[0], pData->frame.ppData[0],
					pUnit->data.frame.iDataSize[0]);
			audioEncIOBuf->produceEnd(pUnit);
		}
	}
	mutex->unlock();
}

AudioPacketTransmit::AudioPacketTransmit() {
}

void AudioPacketTransmit::transmit(BigData* pData) {
	android::Mutex *mutex = __plugin.getCodecFactory()->getAudioDecMutex();
	mutex->lock();
	IOBuf* audioDecIOBuf = __plugin.getCodecFactory()->getAudioDecIOBuf();
	if (audioDecIOBuf) {
		IOBuf::IOUnit *pUnit = audioDecIOBuf->produceBegin(
				pData->packet.iDataSize);
		if (pUnit) {
			memset(&pUnit->data.packet, 0, sizeof(SAV_Packet));
			pUnit->data.packet.iDataSize = pData->packet.iDataSize;
			pUnit->data.packet.iSize = sizeof(SAV_Packet);
			pUnit->data.packet.iPTS = pData->packet.iPTS;
			pUnit->data.packet.pData = (SAV_TYPE_UINT8*) pUnit->pBuf;
			memcpy(pUnit->data.packet.pData, pData->packet.pData,
					pUnit->data.packet.iDataSize);
			audioDecIOBuf->produceEnd(pUnit);
		}
	}
	mutex->unlock();
}
