//
// Created by tingting on 2017/12/15.
//
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include "com_haiersmart_smartsale_JniPir.h"
#include "android/log.h"
static const char *TAG="PIR";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)

enum  PIRCOMMAND {
    CMD_PROBE_GPIO,
    CMD_GET_GPIO,
    CMD_RELEASE_GPIO,
    CMD_SET_GPIO,
    CMD_MAX
};
struct UserData{
    int gpio;
    int state;
} x;


JNIEXPORT jint JNICALL Java_com_haiersmart_smartsale_JniPir_add(JNIEnv *env, jobject thiz, jint gpio, jint state) {
    jint ret=-1;
    int fd=0;
    struct UserData userdata;
    memset(&userdata,0x00, sizeof(struct UserData));
    //strlcpy(userdata.name, "gpio",10);
    userdata.gpio=gpio;
    userdata.state=0;

    ret = ioctl(fd, CMD_PROBE_GPIO, &userdata);
    return ret;
}

//static jint openGpioDev(JNIEnv *env, jobject thiz)
JNIEXPORT jint JNICALL Java_com_haiersmart_smartsale_JniPir_openGpioDev(JNIEnv *env, jobject thiz){

    jint ret=0;
    int fd=0;
    fd = open("/sys/devices/leds", O_RDWR);
    LOGI("fd value = %d ", fd);
    if (fd < 0) {
        LOGE("open pir device failed!");
        ret=-1;
    }
    return ret;
}

//static jint closeGpioDev(JNIEnv *env, jobject thiz)
JNIEXPORT jint JNICALL Java_com_haiersmart_smartsale_JniPir_closeGpioDev(JNIEnv *env, jobject thiz){

    jint ret=0;
    int fd=0;
    ret = close(fd);
    if (fd < 0) {
        ret=-1;
    }
    return ret;
}


//static jint getGpio(JNIEnv *env, jobject thiz,jint num)
JNIEXPORT jint JNICALL Java_com_haiersmart_smartsale_JniPir_getGpio(JNIEnv *env, jobject thiz,jint num){

    jint ret=-1;
    int fd=0;
    struct UserData userdata;
    memset(&userdata,0x00, sizeof(struct UserData));
    //strlcpy(userdata.name, "gpio",10);
    userdata.gpio=num;
    userdata.state=0;

    ret = ioctl(fd, CMD_GET_GPIO, &userdata);
    return ret;
}

//static jint releaseGpio(JNIEnv *env, jobject thiz,jint num)
JNIEXPORT jint JNICALL Java_com_haiersmart_smartsale_JniPir_releaseGpio(JNIEnv *env, jobject thiz, jint num){

    int fd=0;
    jint ret=-1;
    struct UserData userdata;
    memset(&userdata,0x00, sizeof(struct UserData));
    userdata.gpio=num;
    userdata.state=0;
    ret = ioctl(fd, CMD_RELEASE_GPIO, &userdata);
    return ret;
}

//static jint setGpioState(JNIEnv *env, jobject thiz,jint num,jint state)
 JNIEXPORT jint JNICALL Java_com_haiersmart_smartsale_JniPir_setGpioState(JNIEnv *env, jobject thiz, jint num, jint state){
    int fd=0;
    jint err=-1;
    struct UserData userdata;
    memset(&userdata,0x00, sizeof(struct UserData));
    userdata.gpio=num;
    userdata.state=state;

    err = ioctl(fd, CMD_SET_GPIO, &userdata);
    if(err<0){
        err=-1;
    }
    return err;
}
