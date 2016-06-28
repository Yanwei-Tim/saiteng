#include "StorageInfo.h"
#include <stdio.h>
#include "util.h"

StorageInfo::StorageInfo(JNIEnv* env, jobject obj) {
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "fileName", "Ljava/lang/String;");
		jstring androidModel = (jstring) env->GetObjectField(obj, id);
		if (androidModel)
			sprintf(m_fileName, "%s", jstringTostring(env, androidModel));
	}

	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "filePath", "Ljava/lang/String;");
		jstring androidModel = (jstring) env->GetObjectField(obj, id);
		if (androidModel)
			sprintf(m_filePath, "%s", jstringTostring(env, androidModel));
	}

	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "status", "Ljava/lang/String;");
		jstring androidModel = (jstring) env->GetObjectField(obj, id);
		if (androidModel)
			sprintf(m_status, "%s", jstringTostring(env, androidModel));
	}

	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "mediaType", "I");
		m_mediaType = env->GetIntField(obj, id);
	}

	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "fileLenInSeconds", "I");
		m_fileLenInSeconds = env->GetIntField(obj, id);
	}
}

StorageInfo::~StorageInfo() {

}

char* StorageInfo::getFilePath() {
	return m_filePath;
}

char* StorageInfo::getFileName() {
	return m_fileName;
}

int StorageInfo::getFileLenInSeconds() {
	return m_fileLenInSeconds;
}

char* StorageInfo::getStatus() {
	return m_status;
}

int StorageInfo::getMediaType() {
	return m_mediaType;
}
