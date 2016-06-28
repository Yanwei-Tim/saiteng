#include <jni.h>
#include <stdio.h>
#include "com_smarteye_mpu_MPUCoreSDK.h"
#include "com_smarteye_mpu_MPUCoreSDK_MainHandler.h"
#include "MPUCoreSDK.h"
#include "PUGlobal.h"
#include "RegisterInfo.h"
#include "SystemInfo.h"
#include "LoginNotify.h"
#include "DialogNotify.h"
#include "StorageInfo.h"
#include "jni/GPSData.h"
#include "BVCU.h"
#include <android/log.h>
#undef	LOG_TAG
#define LOG_TAG "MPU"
#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
using namespace android;

JavaVM *g_jvm;
static bool once = true;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	g_jvm = vm;
	JNIEnv *env = NULL;
	vm->GetEnv((void**) &env, JNI_VERSION_1_4);
	return JNI_VERSION_1_4;
}

jint Java_com_smarteye_mpu_MPUCoreSDK_Initialize(JNIEnv *, jclass) {
	return Initialize();
}

void JNICALL Java_com_smarteye_mpu_MPUCoreSDK_Finish(JNIEnv *, jclass) {
	Finish();
}

jint Java_com_smarteye_mpu_MPUCoreSDK_ReStart(JNIEnv *, jclass) {
	return ReStart();
}

jint Java_com_smarteye_mpu_MPUCoreSDK_Register(JNIEnv * env, jclass,
		jobject obj) {
	RegisterInfo info(env, obj);
	return Register(&info);
}

jint Java_com_smarteye_mpu_MPUCoreSDK_Storage(JNIEnv *env, jclass,
		jobject obj) {
	StorageInfo info(env, obj);
	return Storage(&info);
}

jint Java_com_smarteye_mpu_MPUCoreSDK_RegisterNotify(JNIEnv * env,
		jobject core) {
	if (once) {
		jobject globalCore = env->NewGlobalRef(core);
		LoginNotify *loginNotify = new LoginNotify(globalCore);
		g_PUGlobal->setLoginNotify(loginNotify);

		DialogNotify *dialogNotify = new DialogNotify(globalCore);
		g_PUGlobal->setDialogNotify(dialogNotify);
		once = false;
	}
	return 0;
}

jbyteArray Java_com_smarteye_mpu_MPUCoreSDK_FetchAudioPlayBuffer(JNIEnv *env,
		jclass, jint size) {
	int len = g_PUGlobal->getAudioFetchBufferSize();
	char *audioFetchBuffer = g_PUGlobal->getAudioFetchBuffer();
	Condition *audioFetchCond = g_PUGlobal->getAudioFetchCond();
	FetchAudioPlayBuffer(audioFetchBuffer, &len);
	jbyteArray result = NULL;
	if (len > 0) {
		jclass cls = env->FindClass("[B");
		result = env->NewByteArray(len);
		env->SetByteArrayRegion(result, 0, len,
				(const jbyte*) audioFetchBuffer);
	}
	return result;
}

jint Java_com_smarteye_mpu_MPUCoreSDK_InputAudioData(JNIEnv * env, jclass,
		jbyteArray data, jint size, jlong stamp) {
	char *pcm = g_PUGlobal->getAudioBuffer();
	if (!pcm)
		return 0;
	env->GetByteArrayRegion(data, 0, size, (jbyte*) pcm);
	int _size = size;
	int64_t _stamp = stamp;
	InputAudioData(pcm, _size, _stamp);
	return 0;
}


jint Java_com_smarteye_mpu_MPUCoreSDK_SetInputAudioFormat(JNIEnv *, jclass,
		jint dwChannels, jint dwSamplesPerSec, jint dwBitsPerSample,
		jint dwFlags) {
	SetInputAudioFormat(dwChannels, dwSamplesPerSec, dwBitsPerSample, dwFlags);
	g_PUGlobal->setAudioFetchBufferSize(1024);
	return 0;
}

jint Java_com_smarteye_mpu_MPUCoreSDK_InputVideoData(JNIEnv *env, jclass,
		jbyteArray data, jint size, jlong stamp) {
	char *yuv = g_PUGlobal->getVideoBuffer();
	if (!yuv)
		return 0;

	env->GetByteArrayRegion(data, 0, size, (jbyte*) yuv);
	int _size = size;
	int64_t _stamp = stamp;
	InputVideoData(yuv, _size, _stamp);
	return 0;
}

jint Java_com_smarteye_mpu_MPUCoreSDK_GetSDKOptionInt(JNIEnv *, jclass,
		jint opt) {
	return GetSDKOptionInt(opt);
}

jint Java_com_smarteye_mpu_MPUCoreSDK_SetInputVideoFormat(JNIEnv *, jclass,
		jint pixFmt, jint dwWidth, jint dwHeight, jint dwFps, jint dwFlags) {
	SetInputVideoFormat(pixFmt, dwWidth, dwHeight, dwFps, dwFlags);
	return 0;
}

jint Java_com_smarteye_mpu_MPUCoreSDK_SetSDKOptionInt(JNIEnv *, jclass,
		jint opt, jint val) {
	return SetSDKOptionInt(opt, val);
}

jstring Java_com_smarteye_mpu_MPUCoreSDK_GetSDKOptionString(JNIEnv *env,
		jobject, jint opt) {
	std::string val = GetSDKOptionString(opt);
	if (val.size() > 0) {
		return str2jstring(env, val.c_str());
	}
	return NULL;
}

jint Java_com_smarteye_mpu_MPUCoreSDK_SetSDKOptionString(JNIEnv * env, jclass,
		jint opt, jstring val) {
	return SetSDKOptionString(opt, jstring2str(env, val));
}

jint Java_com_smarteye_mpu_MPUCoreSDK_InputGPSData(JNIEnv * env, jclass,
		jobject obj) {
	::GPSData data(env, obj);
	return InputGPSData(data.getGPSData());
}

/*
 * Class:     com_smarteye_mpu_MPUCoreSDK
 * Method:    GetFrameCount
 * Signature: ()I
 */
jint JNICALL Java_com_smarteye_mpu_MPUCoreSDK_GetFrameCount(JNIEnv *jobject){
	return GetFrameCount();
}

/*
 * Class:     com_smarteye_mpu_MPUCoreSDK
 * Method:    GetUploadCount
 * Signature: ()I
 */
jint JNICALL Java_com_smarteye_mpu_MPUCoreSDK_GetUploadCount(JNIEnv *jobject){
	return GetUploadCount();
}
