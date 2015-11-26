#ifndef __MPU_CORE_SDK_H__
#define __MPU_CORE_SDK_H__
#include "SystemInfo.h"
#include "RegisterInfo.h"
#include "StorageInfo.h"
extern "C" {
#include <SAVCodec.h>
#include <SAVContainer.h>
}
;
#include "PUConfig.h"
#include <string>

using namespace std;

int Initialize();

void Finish();

int ReStart();

int Register(RegisterInfo*);

int Storage(StorageInfo*);

void FetchAudioPlayBuffer(char*, int*);

void InputAudioData(char*, int, int64_t);

void SetInputAudioFormat(int dwChannels, int dwSamplesPerSec,
		int dwBitsPerSample, int dwFlags);

void InputVideoData(char*, int, int64_t);

int GetSDKOptionInt(int opt);

void SetInputVideoFormat(int pixFmt, int dwWidth, int dwHeight, int dwFps,
		int dwFlags);

int SetSDKOptionInt(int opt, int val);

int SetSDKOptionString(int optname, std::string optvalue);

std::string GetSDKOptionString(int optname);

int InputGPSData(BVCU_PUCFG_GPSData*);
#endif
