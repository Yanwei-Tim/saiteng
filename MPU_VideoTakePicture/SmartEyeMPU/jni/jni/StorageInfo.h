#pragma once
#include <jni.h>
class StorageInfo {
public:
	StorageInfo(JNIEnv* env, jobject obj);
	~StorageInfo();

	char* getStatus();
	int getMediaType();
	char* getFileName();
	char* getFilePath();
	int getFileLenInSeconds();
private:
	char m_status[8];
	int m_mediaType;
	char m_fileName[256];
	char m_filePath[256];
	int m_fileLenInSeconds;
};
