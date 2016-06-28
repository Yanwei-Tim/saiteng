LOCAL_PATH := $(call my-dir)
LOCAL_INCLUDE := $(LOCAL_PATH)\..\..\include

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\ffmpeg
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\libsav
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

LOCAL_CPPFLAGS += -D__STDC_CONSTANT_MACROS -fpermissive

LOCAL_LDLIBS += -L$(LOCAL_PATH)\..\..\so -landroid_runtime -lOpenMAXAL -lutils -lstagefright -lstagefright_omx -lmedia -llog -lstagefright_foundation -lcutils	\
				-lc -lm -lpixelflinger -lgui -lsurfaceflinger -landroid -lbinder -lpjlib-util -lavformat  -lavdevice -lavcodec  -lavutil -lswscale -lm -lz

LOCAL_MODULE    := sav
LOCAL_SRC_FILES := SAVCodec.c	\
					SAVUtil.c	\
					SAVContainer.c

include $(BUILD_SHARED_LIBRARY)
