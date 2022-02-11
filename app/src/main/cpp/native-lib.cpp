#include <jni.h>
#include <iostream>
#include "log.h"
//
// Created by teble on 2020/2/10.
//
#define EXPORT extern "C" __attribute__((visibility("default")))
extern "C" jint MMKV_JNI_OnLoad(JavaVM *vm, void *reserved);
extern "C" jint Decrypt_JNI_OnLoad(JavaVM *vm, void *reserved);
extern "C" const char * decryptXAConf(std::string &src);

EXPORT jstring
Java_me_teble_xposed_autodaily_task_util_ConfigUtil_decryptXAConf(
        JNIEnv *env,
        jobject obj, jstring enc_conf) {
    const char *p = env->GetStringUTFChars(enc_conf, nullptr);
    std::string encConf(p);
    env->ReleaseStringUTFChars(enc_conf, p);
    const char *res;
    try {
        res = decryptXAConf(encConf);
    } catch (...) {
        res = "";
    }
    return env->NewStringUTF(res);
}

EXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv* env = nullptr;
    if(vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) { //从JavaVM获取JNIEnv，一般使用1.4的版本
        return -1;
    }
    jint retCode = Decrypt_JNI_OnLoad(vm, reserved);
    if (retCode != JNI_VERSION_1_6) {
        return retCode;
    }
    return MMKV_JNI_OnLoad(vm, reserved);
//    return JNI_VERSION_1_4;
}