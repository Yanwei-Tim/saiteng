#pragma once
#include <jni.h>
extern "C" {
#include <SAVCodec.h>
#include <SAVContainer.h>
}
;
#include "PUConfig.h"

class GPSData {
public:
	GPSData(JNIEnv* env, jobject obj);
	BVCU_PUCFG_GPSData* getGPSData();
private:
	BVCU_PUCFG_GPSData m_data;
};
