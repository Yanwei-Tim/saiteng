#include <stdio.h>
#include <string.h>
#include "PUGlobal.h"
#include <BVCU.h>
#include <android/log.h>
#undef	LOG_TAG
#define LOG_TAG "MPU"
#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
using namespace android;

const Model PUGlobal::s_androidModel[] = { { "SM-N9109W", 1 },{ "SM-N9200", 1 },{ "SM-N9008V", 1 }, { "SCH-I959", 1 },
		                                  {'GT-I9508', 1 }, { "GT-I9505", 1 }, {"GT-I9500",1},
									      {"GT-I9502" , 1}, {"GT-I9300" , 1},{"SM-G9280",1},
									      {"GT-N7108D",1 }, {"SM-N9006" , 1 }, {"SM-N9008S" , 1 },
										  };

PUGlobal::PUGlobal() :
		m_dialog(CALL_TYPE_DIALOG) {
	m_audioFetchBuffer = m_videoBuffer = m_audioBuffer = NULL;
	m_audioFetchBufferSize = m_width = m_height = 0;
	m_audioRecordBufferSize = 0;
	m_videoYUV420PBuffer = NULL;
	m_androidFlag = 0;
}

PUGlobal::~PUGlobal() {
	if (m_videoBuffer)
		free(m_videoBuffer);
	m_videoBuffer = NULL;
	if (m_audioBuffer)
		free(m_audioBuffer);
	m_audioBuffer = NULL;
}

void PUGlobal::restart() {

}

void PUGlobal::login(RegisterInfo* info) {
	Message m("mpu.register");
	m.addParam("deviceid", String(info->getDeviceId()));
	m.addParam("devicename", info->getDeviceName());
	m.addParam("serverport", String(info->getServerPort()));
	m.addParam("serveraddr", info->getServerAddr());
	char *cameraName[] = { "no camera", "black camera", "front camera" };
	m.addParam("channelname",
			cameraName[g_PUGlobal->getOptionInt(MPU_I_USERSTATE_CAMERA_INDEX)]);
	m.userData((RefObject*) m_loginCallback.getObject("RefObject"));
	Engine::dispatch(m);
}

void PUGlobal::setAudioRecordBufferSize(int size) {
	if (m_audioBuffer)
		free(m_audioBuffer);
	m_audioRecordBufferSize = size;
	m_audioBuffer = (char*) malloc(size);
}

int PUGlobal::getAudioRecordBufferSize() {
	return m_audioRecordBufferSize;
}

void PUGlobal::setPreviewSize(int width, int height) {
	m_width = width;
	m_height = height;
	if (m_videoBuffer)
		free(m_videoBuffer);
	m_videoBuffer = NULL;
	if (m_videoYUV420PBuffer)
		free(m_videoYUV420PBuffer);
	m_videoYUV420PBuffer = NULL;

	m_videoBuffer = (char*) malloc((width * height * 3) / 2);
	m_videoYUV420PBuffer = (char*) malloc((width * height * 3) / 2);
}

int PUGlobal::getPreviewWidth() {
	return m_width;
}

int PUGlobal::getPreviewHeight() {
	return m_height;
}

char* PUGlobal::getVideoBuffer() {
	return m_videoBuffer;
}

char* PUGlobal::getVideoYUV420PBuffer() {
	return m_videoYUV420PBuffer;
}

char* PUGlobal::getAudioBuffer() {
	return m_audioBuffer;
}

Call* PUGlobal::getDialog() {
	return &m_dialog;
}

void PUGlobal::setAndroidModel(char* model) {
	sprintf(m_androidModel, "%s", model);
	for (int i = 0; i < sizeof(s_androidModel) / sizeof(Model); i++) {
		if (strcasecmp(m_androidModel, s_androidModel[i].model) == 0) {
			m_androidFlag = s_androidModel[i].flags;
			break;
		}
	}
}

LoginNotify* PUGlobal::getLoginNotify() {
	return m_loginNotify;
}

void PUGlobal::setLoginNotify(LoginNotify* ln) {
	m_loginNotify = ln;
}

void PUGlobal::setDialogNotify(DialogNotify* dn) {
	m_dialogNotify = dn;
}

DialogNotify* PUGlobal::getDialogNotify() {
	return m_dialogNotify;
}

Condition* PUGlobal::getAudioFetchCond() {
	return &m_audioFetchCond;
}

char* PUGlobal::getAudioFetchBuffer() {
	return m_audioFetchBuffer;
}

int PUGlobal::getAudioFetchBufferSize() {
	return m_audioFetchBufferSize;
}

void PUGlobal::setAudioFetchBufferSize(int size) {
	if (m_audioFetchBufferSize != size) {
		if (m_audioFetchBuffer != NULL)
			free(m_audioFetchBuffer);
		m_audioFetchBuffer = (char*) malloc(size);
		m_audioFetchBufferSize = size;
	}
}

void PUGlobal::setOptionInt(int opt, int val) {
	m_optionIntMap[opt] = val;
}

int PUGlobal::getOptionInt(int opt) {
	return m_optionIntMap[opt];
}

void PUGlobal::setOptionString(int opt, std::string val) {
	m_optionStrMap[opt] = val;
}

std::string PUGlobal::getOptionString(int opt) {
	return m_optionStrMap[opt];
}

void PUGlobal::yuv420sp_to_yuv420p(unsigned char* yuv420sp,
		unsigned char* yuv420p, int width, int height) {
	if (yuv420sp == NULL || yuv420p == NULL)
		return;
	int framesize = width * height;
	int i = 0, j = 0;
	//copy y
	for (i = 0; i < framesize; i++) {
		*(yuv420p + i) = *(yuv420sp + i);
	}
	i = 0;
	//copy u
	for (j = 0; j < framesize / 2; j += 2) {
		*(yuv420p + (j + 1 + framesize)) = *(yuv420sp + (j + framesize));
		//*(yuv420p + (i + framesize + framesize/4)) = *(yuv420sp + (j + framesize));
		i++;
	}
	i = 0;
	//copy v
	for (j = 1; j < framesize / 2; j += 2) {
		*(yuv420p + (j - 1 + framesize)) = *(yuv420sp + (j + framesize));
		//*(yuv420p + (i + framesize)) = *(yuv420sp + (j + framesize));
		i++;
	}
}

void* PUGlobal::ConvertYUV420SP_TO_YUV420P(unsigned char* yuv420sp,
		unsigned char* yuv420p, int width, int height, int size) {
	if (m_androidFlag) {
		yuv420sp_to_yuv420p(yuv420sp, yuv420p, width, height);
		return yuv420p;
	} else {
		//memcpy(yuv420p, yuv420sp, size);
		return yuv420sp;
	}
}

void PUGlobal::inputAudioData(char* data, int size, int64_t stamp) {
	BigData bigData;
	memset(&bigData.frame, 0, sizeof(bigData.frame));
	bigData.frame.iPTS = stamp;
	bigData.frame.iDataSize[0] = size;
	bigData.frame.ppData[0] = (SAV_TYPE_UINT8*) data;
	for (int i = 0; i < m_audioRawTransmit.size(); i++) {
		Transmit* t = m_audioRawTransmit[i];
		t->transmit(&bigData);
	}
}

void PUGlobal::inputVideoData(char* data, int size, int64_t stamp) {
	BigData bigData;
	memset(&bigData.frame, 0, sizeof(bigData.frame));
	bigData.frame.iPTS = stamp;
	bigData.frame.iDataSize[0] = size;
	bigData.frame.ppData[0] =
			(SAV_TYPE_UINT8*) g_PUGlobal->ConvertYUV420SP_TO_YUV420P(
					(unsigned char*) data,
					(unsigned char*) g_PUGlobal->getVideoYUV420PBuffer(),
					g_PUGlobal->getPreviewWidth(),
					g_PUGlobal->getPreviewHeight(), size);
	if (bigData.frame.ppData[0]) {
		for (int i = 0; i < m_videoRawTransmit.size(); i++) {
			Transmit* t = m_videoRawTransmit[i];
			t->transmit(&bigData);
		}
	} else {
		LOGI("video raw NULL");
	}
}

void PUGlobal::inputGPSData(BVCU_PUCFG_GPSData* gps) {
	FILE *file = fopen("/sdcard/wgs.conf", "w");
	fprintf(file, "%d,%d", gps->iLatitude, gps->iLongitude);
	fclose(file);
	BigData bigData;
	memset(&bigData.frame, 0, sizeof(bigData.frame));
	bigData.frame.iDataSize[0] = sizeof(BVCU_PUCFG_GPSData);
	bigData.frame.ppData[0] = (SAV_TYPE_UINT8*) gps;
	for (int i = 0; i < m_gpsRawTransmit.size(); i++) {
		Transmit* t = m_gpsRawTransmit[i];
		t->transmit(&bigData);
	}
}

void PUGlobal::addAudioRawTransmit(Transmit* t) {
	m_audioRawTransmit.push_back(t);
}

void PUGlobal::addVideoRawTransmit(Transmit* t) {
	m_videoRawTransmit.push_back(t);
}

void PUGlobal::addGPSRawTransmit(Transmit* t) {
	m_gpsRawTransmit.push_back(t);
}

LoginCallback::LoginCallback() {

}

void LoginCallback::onEvent(int id, int iEventCode, void* pParam) {
	BVCU_Event_Common *pCommon = (BVCU_Event_Common*) pParam;
	g_PUGlobal->getLoginNotify()->onMPULoginMessage(id, pCommon->iResult);

}

void* LoginCallback::getParam() {
	return NULL;
}

PUGlobal* g_PUGlobal = new PUGlobal();
