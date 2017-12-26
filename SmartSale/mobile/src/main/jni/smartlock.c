//
// Created by Gui Yan on 2017/12/22.
//

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <jni.h>
#include <assert.h>
#include "android/log.h"

#define JNIREG_CLASS "com/haiersmart/smartsale/Smartlock" //指定要注册的类

static const char *TAG="smartlock";
#define LOGI(TAG, fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(TAG, fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(TAG, fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

JNIEXPORT jobject JNICALL native_open(JNIEnv *env, jobject thiz, jstring path, jint flags)
{
    int fd;
    jobject mFileDescriptor;
    jboolean iscopy;
    const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);

    fd = open(path_utf, flags);
    (*env)->ReleaseStringUTFChars(env, path, path_utf);
    if (fd == -1) {
        /* Throw an exception */
        LOGE(TAG, "Cannot open port");
        /* TODO: throw an exception */
        return NULL;
    }

    /* Create a corresponding file descriptor */
    jclass cFileDescriptor = (*env)->FindClass(env, "java/io/FileDescriptor");  //get class
    jmethodID iFileDescriptor = (*env)->GetMethodID(env, cFileDescriptor, "<init>", "()V"); //construct
    jfieldID descriptorID = (*env)->GetFieldID(env, cFileDescriptor, "descriptor", "I");
    mFileDescriptor = (*env)->NewObject(env, cFileDescriptor, iFileDescriptor);
    (*env)->SetIntField(env, mFileDescriptor, descriptorID, (jint)fd);  //set fd to obj

    return mFileDescriptor;
}

JNIEXPORT void JNICALL native_close(JNIEnv *env, jobject thiz)
{
    jclass SerialPortClass = (*env)->GetObjectClass(env, thiz);
    jclass FileDescriptorClass = (*env)->FindClass(env, "java/io/FileDescriptor");

    jfieldID mFdID = (*env)->GetFieldID(env, SerialPortClass, "mFd", "Ljava/io/FileDescriptor;");
    jfieldID descriptorID = (*env)->GetFieldID(env, FileDescriptorClass, "descriptor", "I");

    jobject mFd = (*env)->GetObjectField(env, thiz, mFdID);
    jint descriptor = (*env)->GetIntField(env, mFd, descriptorID);

    close(descriptor);
}

/**
* Table of methods associated with a single class.
*/
static JNINativeMethod gMethods[] = {
    { "nativeOpen",  "(Ljava/lang/String;I)Ljava/lang/Object;", (void*)native_open },
    { "nativeClose", "()V", (void*)native_close }
};

/*
* Register several native methods for one class.
*/
static int registerNativeMethods(JNIEnv* env, const char* className, JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

/*
* Register native methods for all classes we know about.
*/
static int registerNatives(JNIEnv* env)
{
    if (!registerNativeMethods(env, JNIREG_CLASS, gMethods, sizeof(gMethods) / sizeof(gMethods[0])))
        return JNI_FALSE;

    return JNI_TRUE;
}

/*
* Set some test stuff up.
*
* Returns the JNI version on success, -1 on failure.
*/
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;

    if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);

    if (!registerNatives(env)) {
        return -1;
    }
    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

    return result;
}