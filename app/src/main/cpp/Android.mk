LOCAL_PATH := $(call my-dir)
OPENCV_ANDROID_SDK := /home/ndtho8205/Downloads/OpenCV-android-sdk
EIGEN3_DIR := /home/ndtho8205/Downloads/eigen-eigen-5a0156e40feb

include $(CLEAR_VARS)

OPENCV_INSTALL_MODULES := on
include $(OPENCV_ANDROID_SDK)/sdk/native/jni/OpenCV.mk

LOCAL_MODULE := face-lib
LOCAL_SRC_FILES += $(LOCAL_PATH)/face-lib.cpp $(LOCAL_PATH)/FaceRecognitionLib/Facebase.cpp
LOCAL_SRC_FILES += $(LOCAL_PATH)/FaceRecognitionLib/Eigenfaces.cpp $(LOCAL_PATH)/FaceRecognitionLib/Fisherfaces.cpp
LOCAL_SRC_FILES += $(LOCAL_PATH)/FaceRecognitionLib/PCA.cpp $(LOCAL_PATH)/FaceRecognitionLib/LDA.cpp
LOCAL_C_INCLUDES += $(EIGEN3_DIR) $(LOCAL_PATH)/FaceRecognitionLib/RedSVD/include
LOCAL_LDLIBS += -llog -ldl
LOCAL_CPPFLAGS += -std=gnu++11 -frtti -fexceptions

include $(BUILD_SHARED_LIBRARY)
