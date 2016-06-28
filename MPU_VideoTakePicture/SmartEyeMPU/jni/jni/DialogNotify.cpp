#include "DialogNotify.h"
#include <stdio.h>
#include <jni.h>
extern JavaVM *g_jvm;
DialogNotify::DialogNotify(jobject obj) {
	m_object = obj;
}

DialogNotify::~DialogNotify() {

}

void DialogNotify::onMPUDialogMessage(int userId, int status, int mediaDir) {
	JNIEnv *env = NULL;
	g_jvm->AttachCurrentThread((JNIEnv**) &env, NULL);
	jclass cls = env->GetObjectClass(m_object);
	jmethodID id = env->GetMethodID(cls, "onMPUDialogMessage", "(III)V");
	env->CallVoidMethod(m_object, id, userId, status, mediaDir);
	(g_jvm)->DetachCurrentThread();
}
