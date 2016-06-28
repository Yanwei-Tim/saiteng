#pragma once
#include <jni.h>
#include <string>

char* jstringTostring(JNIEnv* env, jstring jstr);
jstring str2jstring(JNIEnv* env, const char* pat);
std::string jstring2str(JNIEnv* env, jstring jstr);
