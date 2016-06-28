LOCAL_PATH := $(call my-dir)
LOCAL_INCLUDE := $(LOCAL_PATH)\..\..\include

include $(CLEAR_VARS)
LOCAL_MODULE := protobuf
LOCAL_SRC_FILES := ..\..\so\libprotobuf.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := protobuffile
LOCAL_SRC_FILES := ..\..\so\libprotobuffile.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := aaac
LOCAL_SRC_FILES := ..\..\so\libaaac.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := pjlib-util
LOCAL_SRC_FILES := ..\..\so\libpjlib-util.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)  
LOCAL_MODULE := gnustl_shared      
LOCAL_SRC_FILES := ..\..\so\libgnustl_shared.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\yate
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\framework
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\aaa
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\bvcu\include
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\bvcu\inc_internal
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\libsav
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\protobuffile
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\pjmedia
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\pjlib
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\gnu-libstdc++\4.6\include
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\gnu-libstdc++\4.6\libs\armeabi\include
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\protobuf
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\seprotocol
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\pjsip
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\pjlib-util
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media\camera
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media\media\stagefright
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media\media\stagefright\openmax
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\hardware
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\system
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\system\cutils

LOCAL_CPPFLAGS += -fexceptions -fpermissive -DPJ_IS_LITTLE_ENDIAN=1 -DPJ_IS_BIG_ENDIAN=0 -DHAVE_PTHREADS

LOCAL_LDLIBS += -L$(LOCAL_PATH)\..\..\so -laaac -lprotobuf -lprotobuffile -gnustl_shared -lyate -lseprotocol \
				-lpjlib -lpjlib-util -lpjsip-core -lframework -lpjmedia -llog

LOCAL_MODULE    := sip
LOCAL_SRC_FILES := libsip.cpp

LOCAL_STATIC_LIBRARIES := libprotobuf libprotobuffile libaaac libpjlib-util

LOCAL_SHARED_LIBRARIES := gnustl_shared framework yate

include $(BUILD_SHARED_LIBRARY)
