#pragma once
#include <jni.h>

class LoginNotify {
public:
	LoginNotify(jobject obj);
	~LoginNotify();
	void onMPULoginMessage(int dwUserId, int dwErrorCode);
private:
	jobject m_object;
};
