#include "util.h"
#include <stdio.h>
#include <string.h>

char* jstringTostring(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("utf-8");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);
	return rtn;
}

jstring str2jstring(JNIEnv* env, const char* pStr) {
	int strLen = strlen(pStr);
	jclass jstrObj = env->FindClass("java/lang/String");
	jmethodID methodId = env->GetMethodID(jstrObj, "<init>",
			"([BLjava/lang/String;)V");
	jbyteArray byteArray = env->NewByteArray(strLen);
	jstring encode = env->NewStringUTF("utf-8");

	env->SetByteArrayRegion(byteArray, 0, strLen, (jbyte*) pStr);

	return (jstring) env->NewObject(jstrObj, methodId, byteArray, encode);
}

std::string jstring2str(JNIEnv* env, jstring jstr) {
	char* rtn = NULL;
	jclass clsstring = env->FindClass("java/lang/String");
	jstring strencode = env->NewStringUTF("GB2312");
	jmethodID mid = env->GetMethodID(clsstring, "getBytes",
			"(Ljava/lang/String;)[B");
	jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
	jsize alen = env->GetArrayLength(barr);
	jbyte* ba = env->GetByteArrayElements(barr, JNI_FALSE);
	if (alen > 0) {
		rtn = (char*) malloc(alen + 1);
		memcpy(rtn, ba, alen);
		rtn[alen] = 0;
	}
	env->ReleaseByteArrayElements(barr, ba, 0);
	std::string stemp(rtn);
	free(rtn);
	return stemp;
}
