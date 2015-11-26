#include "RegisterInfo.h"
#include <stdio.h>

RegisterInfo::RegisterInfo(JNIEnv* env, jobject obj) {
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "serverAddr", "Ljava/lang/String;");
		jstring androidModel = (jstring) env->GetObjectField(obj, id);
		sprintf(m_serverAddr, "%s", jstringTostring(env, androidModel));
	}

	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "serverPort", "I");
		m_serverPort = env->GetIntField(obj, id);
	}

	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "deviceId", "I");
		m_deviceId = env->GetIntField(obj, id);
	}

	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "deviceName", "Ljava/lang/String;");
		jstring androidModel = (jstring) env->GetObjectField(obj, id);
		sprintf(m_deviceName, "%s", jstringTostring(env, androidModel));
	}
}

RegisterInfo::~RegisterInfo() {

}

char* RegisterInfo::getServerAddr() {
	return m_serverAddr;
}

int RegisterInfo::getServerPort() {
	return m_serverPort;
}

int RegisterInfo::getDeviceId() {
	return m_deviceId;
}

char* RegisterInfo::getDeviceName() {
	return m_deviceName;
}
