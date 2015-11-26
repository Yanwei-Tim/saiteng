LOCAL_PATH := $(call my-dir)
LOCAL_INCLUDE := $(LOCAL_PATH)\..\..\include

include $(CLEAR_VARS)
LOCAL_MODULE := yate
LOCAL_SRC_FILES := ..\..\so\libyate.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := codec
#LOCAL_SRC_FILES := ..\..\so\libcodec.so
LOCAL_SRC_FILES := ..\..\so\libcodec_5_0.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := storage
#LOCAL_SRC_FILES := ..\..\so\libstorage.so
LOCAL_SRC_FILES := ..\..\so\libstorage_5_0.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := framework
LOCAL_SRC_FILES := ..\..\so\libframework.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := network
LOCAL_SRC_FILES := ..\..\so\libnetwork.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)  
LOCAL_MODULE := gnustl_shared      
LOCAL_SRC_FILES := ..\..\so\libgnustl_shared.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)  
LOCAL_MODULE := rtp      
LOCAL_SRC_FILES := ..\..\so\librtp.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)  
LOCAL_MODULE := sip      
LOCAL_SRC_FILES := ..\..\so\libsip.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)  
LOCAL_MODULE := render      
LOCAL_SRC_FILES := ..\..\so\librender.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)  
LOCAL_MODULE := sav      
LOCAL_SRC_FILES := ..\..\so\libsav.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)  
LOCAL_MODULE := BaiduMapSDK_v2_3_5      
LOCAL_SRC_FILES := ..\..\so\libBaiduMapSDK_v2_3_5.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)  
LOCAL_MODULE := locSDK3      
LOCAL_SRC_FILES := ..\..\so\liblocSDK3.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_C_INCLUDES += $(LOCAL_PATH)\jni
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\yate
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\aaa
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\bvcu\include
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\bvcu\inc_internal
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\libsav
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\protobuffile
LOCAL_C_INCLUDES += $(LOCAL_INCLUDE)\protobuf
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

LOCAL_CPPFLAGS += -DPJ_IS_LITTLE_ENDIAN=1 -DPJ_IS_BIG_ENDIAN=0 -DHAVE_SYS_UIO_H=1 -DHAVE_PTHREADS -fpermissive -Wwrite-strings

LOCAL_LDLIBS += -L$(LOCAL_PATH)\..\..\so -landroid_runtime -lOpenMAXAL -lutils -lstagefright -lstagefright_omx -lmedia -llog -lstagefright_foundation -lcutils	\
				-lc -lm -lpixelflinger -lgui -lsurfaceflinger -landroid -lbinder -lhardware

LOCAL_MODULE    := mpucore
LOCAL_SRC_FILES := MPUCoreSDK_jni.cpp 	\
					MPUCoreSDK.cpp		\
					PUGlobal.cpp		\
					Call.cpp			\
					libmpu.cpp			\
					jni\RegisterInfo.cpp\
					jni\StorageInfo.cpp	\
					jni\SystemInfo.cpp	\
					jni\util.cpp		\
					jni\LoginNotify.cpp	\
					jni\DialogNotify.cpp\
					DialogData.cpp		\
					jni\GPSData.cpp
					
LOCAL_SHARED_LIBRARIES  := libyate libcodec libstorage libframework libnetwork libgnustl_shared librtp libsip librender libsav

include $(BUILD_SHARED_LIBRARY)
