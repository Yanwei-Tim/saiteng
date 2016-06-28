#include "SystemInfo.h"
#include "util.h"
#include <stdio.h>

SystemInfo::SystemInfo(JNIEnv* env, jobject obj) {
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "androidModel",
				"Ljava/lang/String;");
		jstring androidModel = (jstring) env->GetObjectField(obj, id);
		sprintf(m_androidModel, "%s", jstringTostring(env, androidModel));
	}

	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "androidManufacture",
				"Ljava/lang/String;");
		jstring androidModel = (jstring) env->GetObjectField(obj, id);
		sprintf(m_androidManufacture, "%s", jstringTostring(env, androidModel));
	}

	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "androidVersion",
				"Ljava/lang/String;");
		jstring androidModel = (jstring) env->GetObjectField(obj, id);
		sprintf(m_androidVersion, "%s", jstringTostring(env, androidModel));
	}

	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "androidAPILevel", "I");
		m_androidAPILevel = env->GetIntField(obj, id);
	}
}

SystemInfo::~SystemInfo() {

}

char* SystemInfo::getAndroidModel() {
	return m_androidModel;
}

char* SystemInfo::getAndroidManufacture() {
	return m_androidManufacture;
}

char* SystemInfo::getAndroidVersion() {
	return m_androidVersion;
}

int SystemInfo::getAndroidAPILevel() {
	return m_androidAPILevel;
}
