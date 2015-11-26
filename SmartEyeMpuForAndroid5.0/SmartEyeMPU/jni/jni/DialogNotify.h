#pragma once
#include <jni.h>

class DialogNotify {
public:
	DialogNotify(jobject obj);
	~DialogNotify();
	void onMPUDialogMessage(int userId, int status, int mediaDir);
private:
	jobject m_object;
};
