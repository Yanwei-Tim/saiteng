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
LOCAL_MODULE := sav      
LOCAL_SRC_FILES := ..\..\so\libsav.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := gnustl_shared
LOCAL_SRC_FILES := ..\..\so\libgnustl_shared.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_LDLIBS += -L$(LOCAL_PATH)\..\..\so -lsav

MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\yate
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\bvcu\include
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\libsav
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\framework
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\jnibvcuadapter
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\system
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\pjlib
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\pjlib-util
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\pjmedia
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media\media\stagefright
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\media\media\stagefright\openmax
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\hardware
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\system\cutils
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\gnu-libstdc++\4.6\include
MY_LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\gnu-libstdc++\4.6\libs\armeabi\include

LOCAL_C_INCLUDES += $(MY_LOCAL_C_INCLUDES)

MY_LOCAL_CPPFLAGS += -DPJ_IS_LITTLE_ENDIAN=1 -DPJ_IS_BIG_ENDIAN=0 -DHAVE_SYS_UIO_H=1 -DHAVE_PTHREADS -fpermissive
LOCAL_CPPFLAGS += $(MY_LOCAL_CPPFLAGS)

LOCAL_MODULE    := codec
MY_LOCAL_SRC_FILES := libcodec.cpp
LOCAL_SRC_FILES := $(MY_LOCAL_SRC_FILES)

MY_LOCAL_LDLIBS += -L$(LOCAL_PATH)\..\..\so -landroid_runtime -lOpenMAXAL -lutils -lstagefright -lstagefright_omx -lmedia -llog -lstagefright_foundation -lcutils -lsav -lpjlib-util
LOCAL_LDLIBS += $(MY_LOCAL_LDLIBS)
LOCAL_LDLIBS += -L$(LOCAL_PATH)\..\..\so\4.0

MY_LOCAL_SHARED_LIBRARIES  := libyate libframework libgnustl_shared libsav

LOCAL_SHARED_LIBRARIES := $(MY_LOCAL_SHARED_LIBRARIES)

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_CPPFLAGS += $(MY_LOCAL_CPPFLAGS)
LOCAL_CPPFLAGS += -D__ANDROID_5_0__

LOCAL_C_INCLUDES += $(MY_LOCAL_C_INCLUDES)

LOCAL_LDLIBS += $(MY_LOCAL_LDLIBS)
LOCAL_LDLIBS += -L$(LOCAL_PATH)\..\..\so\5.0

LOCAL_MODULE   := codec_5_0
LOCAL_SRC_FILES := $(MY_LOCAL_SRC_FILES)

LOCAL_SHARED_LIBRARIES := $(MY_LOCAL_SHARED_LIBRARIES)

include $(BUILD_SHARED_LIBRARY)
