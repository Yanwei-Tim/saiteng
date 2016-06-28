LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)

LOCAL_LDLIBS += -llog

LOCAL_MODULE    := aaac
LOCAL_SRC_FILES := aaac.cpp \
					crypt.cpp \
					des.cpp \
					md5sum.cpp \
					rsa.cpp

include $(BUILD_STATIC_LIBRARY)
