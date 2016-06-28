#include "LoginNotify.h"
#include <stdio.h>
#include <jni.h>

extern JavaVM *g_jvm;

LoginNotify::LoginNotify(jobject obj) {
	m_object = obj;
}

LoginNotify::~LoginNotify() {

}

void LoginNotify::onMPULoginMessage(int dwUserId, int dwErrorCode) {
	JNIEnv *env = NULL;
	g_jvm->AttachCurrentThread((JNIEnv**) &env, NULL);
	jclass cls = env->GetObjectClass(m_object);
	jmethodID id = env->GetMethodID(cls, "onMPULoginMessage", "(II)V");
	env->CallVoidMethod(m_object, id, dwUserId, dwErrorCode);
	(g_jvm)->DetachCurrentThread();
}
