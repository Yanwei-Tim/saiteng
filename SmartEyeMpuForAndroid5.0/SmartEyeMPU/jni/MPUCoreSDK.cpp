#include "MPUCoreSDK.h"
#include "PUGlobal.h"
#include "framework.h"
#include <yatengine.h>
#include <yatephone.h>
#include <android/log.h>
#include <pthread.h>
#include <utils/threads.h>
using namespace android;

#undef	LOG_TAG
#define LOG_TAG "MPU"
#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

void androidOutFunc(const char* buf, int len) {
	LOGI("%s", buf);
}

void* startYATE(void*) {
	int argc = 1;
	const char* argv[] = { "./MPU" };
	TelEngine::Debugger::setOutput(androidOutFunc);
	TelEngine::Engine::main(argc, (const char**) argv, NULL);
	LOGI("stop YATE");
	return NULL;
}

int Initialize() {
	LOGI(
			"-------------- %s", g_PUGlobal->getOptionString(MPU_S_SYSTEM_MODEL).c_str());
	g_PUGlobal->setAndroidModel(
			(char*) (g_PUGlobal->getOptionString(MPU_S_SYSTEM_MODEL).c_str()));
	pthread_t pt;
	return pthread_create(&pt, NULL, startYATE, NULL);
}

void Finish() {
	TelEngine::Engine::halt(0xdead);
	return;
	if (g_PUGlobal)
		delete g_PUGlobal;
	g_PUGlobal = NULL;
}

int ReStart() {
	TelEngine::Engine::restart(0xea, true);
	return STATUS_SUCCESS;
}

int Register(RegisterInfo* info) {
	LOGI("-------------- %d", info->getDeviceId());
	LOGI("-------------- %s", info->getDeviceName());
	LOGI("-------------- %d", info->getServerPort());
	LOGI("-------------- %s", info->getServerAddr());

	g_PUGlobal->login(info);

	Message s("mpu.dump");
	Engine::dispatch(s);
	String dd = s.encode("mpu.dump");
	LOGI("%s", dd.c_str());
	return STATUS_SUCCESS;
}

int Storage(StorageInfo* info) {
	Message msg("mpu.storage");
	msg.addParam("status", info->getStatus());
	if (msg["status"] == "start") {
		msg.addParam("media", String(info->getMediaType()));
		msg.addParam("filename", info->getFileName());
		msg.addParam("filepath", info->getFilePath());
		msg.addParam("seconds", String(info->getFileLenInSeconds()));

		msg.addParam("width",
				String(g_PUGlobal->getOptionInt(MPU_I_RECORD_VIDEOW)));
		msg.addParam("height",
				String(g_PUGlobal->getOptionInt(MPU_I_RECORD_VIDEOH)));
		msg.addParam("buffersize", String(640));
	}
	Engine::dispatch(msg);

	return STATUS_SUCCESS;
}

void FetchAudioPlayBuffer(char* data, int* len) {
	Call *pDialog = g_PUGlobal->getDialog();
	Condition *audioFetchCond = g_PUGlobal->getAudioFetchCond();
	DialogData* pDialogData = pDialog->getDialogData();
	IOBuf *audioRender = pDialogData->getIOBuf(DIALOGDATA_LIST_AUDIORENDER);
	if (audioRender != NULL) {
		IOBuf::IOUnit *unit = audioRender->consumeBegin();
		if (unit != NULL && unit->data.frame.iDataSize[0] > 0) {
			memcpy(data, unit->data.frame.ppData[0],
					unit->data.frame.iDataSize[0]);
			*len = unit->data.frame.iDataSize[0];
			audioRender->consumeEnd(unit);
		} else {
			*len = 0;
		}
	} else {
		*len = 0;
	}
}

void InputAudioData(char* data, int size, int64_t stamp) {
	g_PUGlobal->inputAudioData(data, size, stamp);
}

void SetInputAudioFormat(int dwChannels, int dwSamplesPerSec,
		int dwBitsPerSample, int dwFlags) {
	LOGI(
			"dwChannels=%d dwSamplesPerSec=%d dwBitsPerSample=%d dwFlags=%d", dwChannels, dwSamplesPerSec, dwBitsPerSample, dwFlags);
	g_PUGlobal->setAudioRecordBufferSize(640);
}

void InputVideoData(char* data, int size, int64_t stamp) {
	g_PUGlobal->inputVideoData(data, size, stamp);
}

int GetSDKOptionInt(int opt) {
	return g_PUGlobal->getOptionInt(opt);
}

void SetInputVideoFormat(int pixFmt, int dwWidth, int dwHeight, int dwFps,
		int dwFlags) {
	LOGI(
			"pixFmt=%d dwWidth=%d dwHeight=%d dwFps=%d dwFlags=%d", pixFmt, dwWidth, dwHeight, dwFps, dwFlags);
	g_PUGlobal->setPreviewSize(dwWidth, dwHeight);
}

int SetSDKOptionInt(int opt, int val) {
	g_PUGlobal->setOptionInt(opt, val);
	return opt;
}

int SetSDKOptionString(int optname, std::string optvalue) {
	g_PUGlobal->setOptionString(optname, optvalue);
	return optname;
}

std::string GetSDKOptionString(int optname) {
	return g_PUGlobal->getOptionString(optname);
}

int InputGPSData(BVCU_PUCFG_GPSData* gps) {
	g_PUGlobal->inputGPSData(gps);
	return 0;
}

int GetFrameCount(){
	int iFrameCount = 0;

	LOGI("GetFrameCount Entry");

	Message msg("mpu.mount.video.frame.count");
	Engine::dispatch(msg);
	iFrameCount = msg.getIntValue("framecount", 0);

	return iFrameCount;
}

int GetUploadCount(){
	int iUploadCount = 0;

	LOGI("GetUploadCount Entry");

	Message msg("mpu.mount.video.upload.count");
	Engine::dispatch(msg);

	iUploadCount = msg.getIntValue("uploadcount", 0);

	return iUploadCount;
}
