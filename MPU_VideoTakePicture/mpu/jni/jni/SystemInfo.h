#pragma once
#include <jni.h>

class SystemInfo {
public:
	SystemInfo(JNIEnv* env, jobject obj);
	~SystemInfo();

	char* getAndroidModel();
	char* getAndroidManufacture();
	char* getAndroidVersion();
	int getAndroidAPILevel();
private:
	char m_androidModel[64];
	char m_androidManufacture[64];
	char m_androidVersion[64];
	int m_androidAPILevel;
};

