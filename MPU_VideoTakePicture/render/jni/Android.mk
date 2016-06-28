LOCAL_PATH := $(call my-dir)
LOCAL_INCLUDE := $(LOCAL_PATH)\..\..\include

include $(CLEAR_VARS)
LOCAL_MODULE := yate
LOCAL_SRC_FILES := ..\..\so\libyate.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)  
LOCAL_MODULE := framework
LOCAL_SRC_FILES := ..\..\so\libframework.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\yate
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\libsav
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\bvcu\include
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\framework
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media\camera
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media\media\stagefright
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media\media\stagefright\openmax
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\hardware
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\system
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\system\cutils
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\gnu-libstdc++\4.6\include
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\gnu-libstdc++\4.6\libs\armeabi\include

LOCAL_MODULE    := render
LOCAL_SRC_FILES := librender.cpp

LOCAL_LDLIBS += -L$(LOCAL_PATH)\..\..\so -laaac -lprotobuf -lprotobuffile -gnustl_shared -lyate -lseprotocol \
				-lpjlib -lpjlib-util -lpjsip-core -lframework -lpjmedia -llog

LOCAL_CPPFLAGS += -fexceptions -fpermissive -DPJ_IS_LITTLE_ENDIAN=1 -DPJ_IS_BIG_ENDIAN=0 -DHAVE_PTHREADS

LOCAL_SHARED_LIBRARIES  := libyate libframework

include $(BUILD_SHARED_LIBRARY)
