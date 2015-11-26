#pragma once
#include <jni.h>
#include "util.h"

class RegisterInfo {
public:
	RegisterInfo(JNIEnv* env, jobject obj);
	~RegisterInfo();

	char* getServerAddr();
	int getServerPort();
	int getDeviceId();
	char* getDeviceName();
private:
	char m_serverAddr[64];
	int m_serverPort;
	int m_deviceId;
	char m_deviceName[64];
};
