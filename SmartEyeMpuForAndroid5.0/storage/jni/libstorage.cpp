#include <jni.h>
#include "libstorage.h"
#include <stdio.h>
#include <android/log.h>
#undef	LOG_TAG
#define LOG_TAG "STORAGE"
//#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

StorageModule::StorageModule() :
		Module("Storage", "misc") {
	m_storageThread = NULL;
}

StorageModule::~StorageModule() {
	Output("Unload module Storage");
}

void StorageModule::initialize() {
	Output("Initializing module Storage");
	Engine::install(new StoragingHandler());

	Engine::install(new MessageRelay("engine.start", this, Private, 150));
	installRelay(Halt);
	installRelay(Private + 1, "engine.stop");
	Module::initialize();
}

bool StorageModule::received(Message& msg, int id) {
	if (msg == "engine.start") {
		Message msg0("mpu.mount.audio.raw");
		msg0.userData((RefObject*) getAudioTransmit()->getObject("RefObject"));
		Engine::dispatch(msg0);

		Message msg1("mpu.mount.video.raw");
		msg1.userData((RefObject*) getVideoTransmit()->getObject("RefObject"));
		Engine::dispatch(msg1);
	} else if (msg == "engine.stop") {
		LOGI("stop");
	}
	return Module::received(msg, id);
}

CodecInfo* StorageModule::getCodecInfo() {
	return &m_codecInfo;
}

StorageThread* StorageModule::getStorageThread() {
	return m_storageThread;
}

void StorageModule::setStorageThread(StorageThread* pThread) {
	m_storageThread = pThread;
}

void StorageModule::startupStorageThread() {
	m_storageThread->startup();
}

void StorageModule::stopStorageThread() {
	if (m_storageThread) {
		m_storageThreadMutex.lock();
		LOGI("m_storageThread=%p", m_storageThread);
		m_storageThread->exit();
		m_storageThread = NULL;
		m_storageThreadMutex.unlock();
	}
}

INIT_PLUGIN(StorageModule);

StoragingHandler::StoragingHandler(unsigned int prio) :
		MessageHandler("mpu.storage", prio, __plugin.name()) {

}

bool StoragingHandler::received(Message& msg) {
	int mediaType = msg.getIntValue("media");
	const String& status = msg["status"];
	LOGI("StoragingHandler media=%d status=%s", mediaType, status.c_str());
	RefObject *obj = msg.userData();

	int VIDEO_MASK = 1;
	int AUDIO_MASK = 2;

	if (status == "start") {
		android::Mutex *mutex = __plugin.getStorageThreadMutex();
		mutex->lock();
		do {
			StorageThread *pThread = __plugin.getStorageThread();
			if (pThread)
				break;
			pThread = new StorageThread;
			__plugin.setFileName((char*) msg["filename"].c_str());
			__plugin.setFilePath((char*) msg["filepath"].c_str());
			__plugin.setFileLenInSeconds(msg.getIntValue("seconds"));
			if ((mediaType & AUDIO_MASK) == AUDIO_MASK) {
				int bufferSize = msg.getIntValue("buffersize");
				IOBuf *audioStorageIOBuf = new IOBuf((bufferSize / 8) * 64, 32);
				IOBuf *audioEncIOBuf = new IOBuf(bufferSize * 64, 32, true);
				pThread->setAudioStorageIOBuf(audioStorageIOBuf);
				pThread->setAudioEncIOBuf(audioEncIOBuf);
			}
			if ((mediaType & VIDEO_MASK) == VIDEO_MASK) {
				int width = msg.getIntValue("width");
				int height = msg.getIntValue("height");
				IOBuf *videoStorageIOBuf = new IOBuf(512 * 1024, 32);
				IOBuf *videoEncIOBuf = new IOBuf((width * height * 3 * 128), 32,
						true);
				pThread->setVideoStorageIOBuf(videoStorageIOBuf);
				pThread->setVideoEncIOBuf(videoEncIOBuf);
			}
			__plugin.setStorageThread(pThread);
			__plugin.startupStorageThread();
		} while (0);
		mutex->unlock();
	} else if (status == "stop") {
		__plugin.stopStorageThread();
	}

	return true;
}

StorageThread::StorageThread() {
	m_exit = false;
	m_videoStorageIOBuf = m_audioStorageIOBuf = NULL;
	m_videoEncIOBuf = m_audioEncIOBuf = NULL;
}

StorageThread::~StorageThread() {
}

char* getTime() {
	static char buf[64] = { 0 };
	time_t t = time(NULL);
	struct tm* current_time = localtime(&t);
	sprintf(buf, "%d-%02d-%02d %02d-%02d-%02d", current_time->tm_year + 1900,
			current_time->tm_mon + 1, current_time->tm_mday,
			current_time->tm_hour, current_time->tm_min, current_time->tm_sec);
	return buf;
}

void StorageThread::run() {
	m_videoEncThread = new VideoEncThread(this);
	m_audioEncThread = new AudioEncThread(this);
	m_videoEncThread->startup();
	m_audioEncThread->startup();

	IOBuf *audioStorageIOBuf = getAudioStorageIOBuf();
	IOBuf *videoStorageIOBuf = getVideoStorageIOBuf();

	Message m("mpu.dump");
	Engine::dispatch(m);

	int videoBaseTS = m.getIntValue("I_RECORD_VIDEOTS");
	int audioBaseTS = m.getIntValue("I_RECORD_AUDIOTS");
	int fileSeconds = m.getIntValue("I_RECORD_FILESECONDS");
	LOGI(
			"videoBaseTS=%d audioBaseTS=%d fileSeconds=%d", videoBaseTS, audioBaseTS, fileSeconds);
	SAV_TYPE_INT64 videoTS = videoBaseTS;
	SAV_TYPE_INT64 audioTS = audioBaseTS;

	FileParam fp;
	sprintf(fp.fileName, "%s_%s", getTime(), __plugin.getFileName());
	sprintf(fp.filePath, "%s", __plugin.getFilePath());
	fp.fileLen = __plugin.getFileLenInSeconds();
	StorageFile *sf = new StorageFile(&fp);
	bool first_run = false;
	int64_t diff = 0;
	char sps_pps[256];
	int sps_pps_size = 0;
	if (audioStorageIOBuf && videoStorageIOBuf) {
		while (!m_exit) {
			IOBuf::IOUnit *pVideoUnit = videoStorageIOBuf->consumeBegin();
			IOBuf::IOUnit *pAudioUnit = audioStorageIOBuf->consumeBegin();
			if (pVideoUnit) {
				if (!first_run) {
					first_run = true;
					sf->setExtraData((char*) pVideoUnit->data.packet.pData,
							pVideoUnit->data.packet.iDataSize);
					memcpy(sps_pps, pVideoUnit->data.packet.pData,
							pVideoUnit->data.packet.iDataSize);
					sps_pps_size = pVideoUnit->data.packet.iDataSize;
					videoStorageIOBuf->consumeEnd(pVideoUnit);
					continue;
				}
			}
			if (pVideoUnit && pAudioUnit) {
				if (pVideoUnit->data.packet.iPTS
						< pAudioUnit->data.packet.iPTS) {
					if (first_run)
						sf->open(pVideoUnit->data.packet.iPTS);
					sf->writeVideo(&pVideoUnit->data.packet);
					videoStorageIOBuf->consumeEnd(pVideoUnit);
				} else {
					if (first_run)
						sf->open(pAudioUnit->data.packet.iPTS);
					diff = sf->writeAudio(&pAudioUnit->data.packet);
					audioStorageIOBuf->consumeEnd(pAudioUnit);
				}
			} else if (pVideoUnit) {
				if (videoStorageIOBuf->getUsedPercent() > 50) {
					if (first_run)
						sf->open(pVideoUnit->data.packet.iPTS);
					sf->writeVideo(&pVideoUnit->data.packet);
					videoStorageIOBuf->consumeEnd(pVideoUnit);
				}
			} else if (pAudioUnit) {
				if (audioStorageIOBuf->getUsedPercent() > 50) {
					if (first_run)
						sf->open(pAudioUnit->data.packet.iPTS);
					diff = sf->writeAudio(&pAudioUnit->data.packet);
					audioStorageIOBuf->consumeEnd(pAudioUnit);
				}
			}
			if (diff >= (fileSeconds * 1000000)) {
				sf->close();
				delete sf;
				sf = NULL;
				diff = 0;
				{
					FileParam fp;
					sprintf(fp.fileName, "%s_%s", getTime(),
							__plugin.getFileName());
					sprintf(fp.filePath, "%s", __plugin.getFilePath());
					fp.fileLen = __plugin.getFileLenInSeconds();
					sf = new StorageFile(&fp);
					sf->setExtraData(sps_pps, sps_pps_size);
				}
			}
		}
	}
	sf->close();
	LOGI("StorageThread exit");
	m_videoEncThread->exit();
	m_audioEncThread->exit();
}

void StorageThread::cleanup() {
	delete this;
}

void StorageThread::exit() {
	m_exit = true;
}

void StorageThread::setAudioStorageIOBuf(IOBuf* audioStorageIOBuf) {
	m_audioStorageIOBuf = audioStorageIOBuf;
}

void StorageThread::setVideoStorageIOBuf(IOBuf* videoStorageIOBuf) {
	m_videoStorageIOBuf = videoStorageIOBuf;
}

void StorageThread::setVideoEncIOBuf(IOBuf* videoEncIOBuf) {
	m_videoEncIOBuf = videoEncIOBuf;
}

void StorageThread::setAudioEncIOBuf(IOBuf* audioEncIOBuf) {
	m_audioEncIOBuf = audioEncIOBuf;
}

IOBuf* StorageThread::getAudioStorageIOBuf() {
	return m_audioStorageIOBuf;
}

IOBuf* StorageThread::getVideoStorageIOBuf() {
	return m_videoStorageIOBuf;
}

IOBuf* StorageThread::getVideoEncIOBuf() {
	return m_videoEncIOBuf;
}

IOBuf* StorageThread::getAudioEncIOBuf() {
	return m_audioEncIOBuf;
}

AudioTransmit* StorageModule::getAudioTransmit() {
	return &m_audioTransmit;
}

VideoTransmit* StorageModule::getVideoTransmit() {
	return &m_videoTransmit;
}

android::Mutex* StorageModule::getStorageThreadMutex() {
	return &m_storageThreadMutex;
}

char* StorageModule::getFileName() {
	return m_fileName;
}

void StorageModule::setFileName(char* f) {
	sprintf(m_fileName, "%s", f);
}

char* StorageModule::getFilePath() {
	return m_filePath;
}

void StorageModule::setFilePath(char* f) {
	sprintf(m_filePath, "%s", f);
}

int StorageModule::getFileLenInSeconds() {
	return m_fileLenInSeconds;
}

void StorageModule::setFileLenInSeconds(int s) {
	m_fileLenInSeconds = s;
}

VideoTransmit::VideoTransmit() {
}

void VideoTransmit::transmit(BigData* pData) {
	android::Mutex *mutex = __plugin.getStorageThreadMutex();
	mutex->lock();
	do {
		StorageThread *pThread = __plugin.getStorageThread();
		if (!pThread)
			break;
		IOBuf *pVideoEncIOBuf = pThread->getVideoEncIOBuf();
		if (pVideoEncIOBuf) {
			IOBuf::IOUnit *pUnit = pVideoEncIOBuf->produceBegin(
					pData->frame.iDataSize[0]);
			if (pUnit) {
				memset(&pUnit->data.frame, 0, sizeof(SAV_Frame));
				pUnit->data.frame.iSize = sizeof(SAV_Frame);
				pUnit->data.frame.iDataSize[0] = pData->frame.iDataSize[0];
				pUnit->data.frame.ppData[0] = (SAV_TYPE_UINT8*) pUnit->pBuf;
				pUnit->data.frame.iPTS = pData->frame.iPTS;

				memcpy(pUnit->data.frame.ppData[0], pData->frame.ppData[0],
						pUnit->data.frame.iDataSize[0]);
				pVideoEncIOBuf->produceEnd(pUnit);
			}
		}
	} while (0);
	mutex->unlock();
}

AudioTransmit::AudioTransmit() {
}

void AudioTransmit::transmit(BigData *pData) {
	android::Mutex *mutex = __plugin.getStorageThreadMutex();
	mutex->lock();
	do {
		StorageThread *pThread = __plugin.getStorageThread();
		if (!pThread)
			break;
		IOBuf *pAudioEncIOBuf = pThread->getAudioEncIOBuf();
		if (pAudioEncIOBuf) {
			IOBuf::IOUnit *pUnit = pAudioEncIOBuf->produceBegin(
					pData->frame.iDataSize[0]);
			if (pUnit) {
				memset(&pUnit->data.frame, 0, sizeof(SAV_Frame));
				pUnit->data.frame.iSize = sizeof(SAV_Frame);
				pUnit->data.frame.iDataSize[0] = pData->frame.iDataSize[0];
				pUnit->data.frame.ppData[0] = (SAV_TYPE_UINT8*) pUnit->pBuf;
				pUnit->data.frame.iPTS = pData->frame.iPTS;

				memcpy(pUnit->data.frame.ppData[0], pData->frame.ppData[0],
						pUnit->data.frame.iDataSize[0]);
				pAudioEncIOBuf->produceEnd(pUnit);
			}
		}
	} while (0);
	mutex->unlock();
}

StorageFile::StorageFile(FileParam* fp) {
	m_entry.pKey = m_entry_key;
	strlcpy(m_entry.pKey, "title", 12);
	m_entry.pValue = m_entry_value;
	strlcpy(m_entry.pValue, "BesoVideo", 24);

	memset(&m_context, 0, sizeof(m_context));
	m_context.iSize = sizeof(m_context);
	m_context.bMux = SAV_BOOL_TRUE;
	m_context.sFileName = m_fileName;
	SAVDict_Set(&m_context.pMetaData, &m_entry, SAVDICT_FLAG_MATCH_CASE);
	sprintf(m_fileName, "%s%s", fp->filePath, fp->fileName);
	LOGI("fileName=%s", m_fileName);
	m_open = false;
	{
		memset(&m_ctxVideoDec, 0, sizeof(SAVCodec_Context));
		m_ctxVideoDec.iSize = sizeof(SAVCodec_Context);
		m_ctxVideoDec.eCodecID = SAVCODEC_ID_H264;
		m_ctxVideoDec.bEncode = SAV_BOOL_FALSE;
		m_ctxVideoDec.eMediaType = SAV_MEDIATYPE_VIDEO;
		m_ctxVideoDec.TimeBase.num = 1;
		m_ctxVideoDec.TimeBase.den = 25;
		Message m("mpu.dump");
		Engine::dispatch(m);
		m_ctxVideoDec.stVideoParam.iWidth = m.getIntValue("I_RECORD_VIDEOW");
		m_ctxVideoDec.stVideoParam.iHeight = m.getIntValue("I_RECORD_VIDEOH");
		m_ctxVideoDec.pExtraData = NULL;
		m_ctxVideoDec.iExtraDataSize = 0;
	}

	{
		memset(&m_ctxAudioDec, 0, sizeof(SAVCodec_Context));
		m_ctxAudioDec.iSize = sizeof(SAVCodec_Context);
		m_ctxAudioDec.eCodecID = SAVCODEC_ID_G726;
		m_ctxAudioDec.bEncode = SAV_BOOL_FALSE;
		m_ctxAudioDec.eMediaType = SAV_MEDIATYPE_AUDIO;
		m_ctxAudioDec.stAudioParam.eSampleFormat = SAV_SAMPLE_FMT_S16;
		m_ctxAudioDec.stAudioParam.iChannelCount = 1;
		m_ctxAudioDec.stAudioParam.iSampleRate = 8000;
		m_ctxAudioDec.iBitRate = 16000;
	}

	m_context.iStreamCount = 2;
	m_context.ppStreams = m_pStreams;
	m_context.ppStreams[0] = &m_ctxVideoDec;
	m_context.ppStreams[1] = &m_ctxAudioDec;
	m_context.iCreationTime = 0;
}

void StorageFile::open(int64_t ts) {
	if (!m_open) {
		m_context.iCreationTime = ts;
		m_createTime = ts;
		SAV_Result result = SAVContainer_Open(&m_context);
		LOGI("SAVContainer_Open result=%d", result);
		m_open = true;
	}
}

int64_t StorageFile::write(SAV_Packet * packet) {
	if (m_open) {
		packet->iPTS -= m_context.iCreationTime;
		SAV_Result result = SAVContainer_Process(&m_context, packet);
		return packet->iPTS;
	}
	return 0;
}

int64_t StorageFile::writeVideo(SAV_Packet *packet) {
	packet->iSize = sizeof(SAV_Packet);
	packet->iStreamIndex = 0;
	packet->iFlags = 0;
	char *FuHeader = (char*) packet->pData;
	//LOGI("%d %2X %2x %2x %2x %2x %2x", packet->iDataSize, FuHeader[0],FuHeader[1],FuHeader[2],FuHeader[3],FuHeader[4], FuHeader[5]);
	int type = (FuHeader[4] & 0x1f);
	if (type == 5 || type == 7 || type == 8)
		packet->iFlags = SAV_PKT_FLAG_KEY;
	//LOGI("write video------ pts=%lld", packet->iPTS);
	return write(packet);
}

int64_t StorageFile::writeAudio(SAV_Packet *packet) {
	packet->iStreamIndex = 1;
	packet->iSize = sizeof(SAV_Packet);
	packet->iFlags = SAV_PKT_FLAG_KEY;
	//LOGI("write audio------ pts=%lld", packet->iPTS);
	return write(packet);
}

void StorageFile::close() {
	SAV_Result result = SAVContainer_Close(&m_context);
	LOGI("SAVContainer_Close result=%d", result);
	m_open = false;
}

void StorageFile::setExtraData(char* data, int size) {
	static char extra[128];
	memcpy(extra, data, size);
	m_ctxVideoDec.pExtraData = (SAV_TYPE_UINT8*) extra;
	m_ctxVideoDec.iExtraDataSize = size;
}

VideoEncThread::VideoEncThread(StorageThread* pThread) {
	m_storageThread = pThread;
}

VideoEncThread::~VideoEncThread() {

}

void VideoEncThread::run() {
	OMXClient client;
	status_t rc = client.connect();

	Message m("mpu.dump");
	Engine::dispatch(m);
	int width = m.getIntValue("I_RECORD_VIDEOW");
	int height = m.getIntValue("I_RECORD_VIDEOH");
	LOGI("width=%d height=%d", width, height);

	IOBuf *videoEncIOBuf = m_storageThread->getVideoEncIOBuf();

	sp<MediaSource> m_videoSource = NULL;
	H264MediaSource *m_videoTrack = new H264MediaSource(width, height,
			videoEncIOBuf);

	int frameRate = m.getIntValue("I_RECORD_VIDEOFR");
	int bitRate = m.getIntValue("I_RECORD_VIDEOBR");
	int iFramesInterval = m.getIntValue("I_RECORD_VIDEOII");
	LOGI(
			"frameRate=%d bitRate=%d iFramesInterval=%d", frameRate, bitRate, iFramesInterval);
	m_videoTrack->setFrameRate(frameRate);
	m_videoTrack->setBitRate(bitRate);
	m_videoTrack->setIFramesInterval(iFramesInterval);

	m_videoSource = OMXCodec::Create(client.interface(),
			m_videoTrack->getFormat(), true, m_videoTrack, NULL, 0, NULL);
	rc = m_videoSource->start();

	IOBuf *pVideoStorageIOBuf = m_storageThread->getVideoStorageIOBuf();
	while (rc == OK) {
		MediaBuffer *buffer = NULL;
		rc = m_videoSource->read(&buffer);
		if (rc == OK) {
			int length = buffer->range_length();
			int64_t keyTime = 0;
			buffer->meta_data()->findInt64(kKeyTime, &keyTime);
			if (length > 0) {
				IOBuf::IOUnit *pUnit = pVideoStorageIOBuf->produceBegin(length);
				if (pUnit) {
					memset(&pUnit->data.packet, 0, sizeof(SAV_Packet));
					pUnit->data.packet.iPTS = keyTime;
					pUnit->data.packet.iDataSize = length;
					pUnit->data.packet.pData = (SAV_TYPE_UINT8*) pUnit->pBuf;
					memcpy(pUnit->data.packet.pData, buffer->data(), length);
					pVideoStorageIOBuf->produceEnd(pUnit);
				}
			}
			buffer->release();
		}
	}
	m_videoSource->stop();
	client.disconnect();
	videoEncIOBuf->destruct();
	pVideoStorageIOBuf->destruct();
}

void VideoEncThread::cleanup() {
	LOGI("VideoEncThread exit!");
	delete this;
}

void VideoEncThread::exit() {
	android::Mutex *mutex = __plugin.getStorageThreadMutex();
	mutex->lock();
	IOBuf *videoEncIOBuf = m_storageThread->getVideoEncIOBuf();
	if (videoEncIOBuf) {
		IOBuf::IOUnit *pUnit = videoEncIOBuf->produceBegin(1);
		if (pUnit) {
			pUnit->data.frame.iDataSize[0] = 0;
			videoEncIOBuf->produceEnd(pUnit);
		}
	}
	mutex->unlock();
}

AudioEncThread::AudioEncThread(StorageThread* pThread) {
	__plugin.getCodecInfo()->getAudioEncCtx(&m_audioEncCtx);

	SAVCodec_Open(&m_audioEncCtx);
	m_storageThread = pThread;
}

AudioEncThread::~AudioEncThread() {
	__plugin.getCodecInfo()->closeCtx(&m_audioEncCtx);
}

void AudioEncThread::run() {
	IOBuf *audioEncIOBuf = m_storageThread->getAudioEncIOBuf();
	IOBuf *audioStorageIOBuf = m_storageThread->getAudioStorageIOBuf();
	LOGI("AudioEncThread::run");
	while (1) {
		IOBuf::IOUnit *pUnit = audioEncIOBuf->consumeBegin();
		IOBuf::IOUnit *pDstUnit = audioStorageIOBuf->produceBegin(120);
		if (pUnit && pUnit->data.frame.iDataSize[0] <= 0) {
			break;
		}
		if (pUnit && pDstUnit) {
			memset(&pDstUnit->data.packet, 0, sizeof(SAV_Packet));
			pDstUnit->data.packet.iSize = sizeof(SAV_Packet);
			pDstUnit->data.packet.iFlags = 0;
			pDstUnit->data.packet.iStreamIndex = 0;
			pDstUnit->data.packet.pData = (SAV_TYPE_UINT8*) pDstUnit->pBuf;
			pDstUnit->data.packet.iPTS = 0;
			pDstUnit->data.packet.iDataSize = 120
					- SAV_INPUT_BUFFER_PADDING_SIZE;
			SAV_TYPE_INT64 iPTS = pUnit->data.frame.iPTS;
			int ret = SAVCodec_Process(&m_audioEncCtx, &pUnit->data.frame,
					&pDstUnit->data.packet);
			if (ret >= 0) {
				pDstUnit->data.packet.iPTS = iPTS * 1000;
				pDstUnit->data.packet.iDataSize = ret;
				audioStorageIOBuf->produceEnd(pDstUnit);
			}
			audioEncIOBuf->consumeEnd(pUnit);
		}
	}
	audioEncIOBuf->destruct();
	audioStorageIOBuf->destruct();
}

void AudioEncThread::cleanup() {
	LOGI("AudioEncThread exit!");
	delete this;
}

void AudioEncThread::exit() {
	android::Mutex *mutex = __plugin.getStorageThreadMutex();
	mutex->lock();
	IOBuf *audioEncIOBuf = m_storageThread->getAudioEncIOBuf();
	if (audioEncIOBuf) {
		IOBuf::IOUnit *pUnit = audioEncIOBuf->produceBegin(1);
		if (pUnit) {
			pUnit->data.frame.iDataSize[0] = 0;
			audioEncIOBuf->produceEnd(pUnit);
		}
	}
	mutex->unlock();
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
	m_keyTime = m.getIntValue("I_RECORD_VIDEOKT");
	LOGI("keyTime=%d", m_keyTime);
	pts = m_keyTime;
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
		pts += m_keyTime;
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
