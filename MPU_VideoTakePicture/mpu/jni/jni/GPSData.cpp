#include "GPSData.h"
#include <android/log.h>
#include <string.h>
#undef	LOG_TAG
#define LOG_TAG "GPSData"
#define LOGI(...) __android_log_print (ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)

GPSData::GPSData(JNIEnv* env, jobject obj) {
	memset(&m_data, 0, sizeof(m_data));
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "longitude", "I");
		m_data.iLongitude = env->GetIntField(obj, id);
	}
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "latitude", "I");
		m_data.iLatitude = env->GetIntField(obj, id);
	}
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "height", "I");
		m_data.iHeight = env->GetIntField(obj, id);
	}
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "angle", "I");
		m_data.iAngle = env->GetIntField(obj, id);
	}
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "speed", "I");
		m_data.iSpeed = env->GetIntField(obj, id);
	}
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "starCount", "I");
		m_data.iStarCount = env->GetIntField(obj, id);
	}
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "antennaState", "I");
		m_data.bAntennaState = env->GetIntField(obj, id);
	}
	{
		jclass cls = env->GetObjectClass(obj);
		jfieldID id = env->GetFieldID(cls, "orientationState", "I");
		m_data.bOrientationState = env->GetIntField(obj, id);
	}

	jclass cls = env->GetObjectClass(obj);
	jfieldID id = env->GetFieldID(cls, "time",
			"Lcom/smarteye/mpu/bean/WallTime;");
	jobject time = env->GetObjectField(obj, id);
	if (time != NULL) {
		{
			jclass cls = env->GetObjectClass(time);
			jfieldID id = env->GetFieldID(cls, "year", "S");
			m_data.stTime.iYear = env->GetShortField(time, id);
		}
		{
			jclass cls = env->GetObjectClass(time);
			jfieldID id = env->GetFieldID(cls, "month", "C");
			m_data.stTime.iMonth = env->GetCharField(time, id);
		}
		{
			jclass cls = env->GetObjectClass(time);
			jfieldID id = env->GetFieldID(cls, "day", "C");
			m_data.stTime.iDay = env->GetCharField(time, id);
		}
		{
			jclass cls = env->GetObjectClass(time);
			jfieldID id = env->GetFieldID(cls, "hour", "C");
			m_data.stTime.iHour = env->GetCharField(time, id);
		}
		{
			jclass cls = env->GetObjectClass(time);
			jfieldID id = env->GetFieldID(cls, "minute", "C");
			m_data.stTime.iMinute = env->GetCharField(time, id);
		}
		{
			jclass cls = env->GetObjectClass(time);
			jfieldID id = env->GetFieldID(cls, "second", "C");
			m_data.stTime.iSecond = env->GetCharField(time, id);
		}
	}
}

BVCU_PUCFG_GPSData* GPSData::getGPSData() {
	return &m_data;
}
