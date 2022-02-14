#include <jni.h>
#include <iostream>
#include "log.h"
//
// Created by teble on 2020/2/10.
//
#define EXPORT extern "C" __attribute__((visibility("default")))
extern "C" jint MMKV_JNI_OnLoad(JavaVM *vm, void *reserved);
extern "C" jint Decrypt_JNI_OnLoad(JavaVM *vm, void *reserved);
extern "C" const char * decryptXAConf(const std::string &src);
extern "C" const char *hmacSha1Base64(const std::string &value);

EXPORT jbyteArray
Java_me_teble_xposed_autodaily_task_util_ConfigUtil_decryptXAConf(
        JNIEnv *env,
        jobject obj, jbyteArray enc_conf_bytes) {
    const char *bytes = (char *) env->GetByteArrayElements(enc_conf_bytes, nullptr);
    int len = env->GetArrayLength(enc_conf_bytes);
    std::string encConf(bytes, len);
    const char *res;
    try {
        res = decryptXAConf(encConf);
    } catch (std::exception &e) {
        LOGE("exception：" + std::string(e.what()));
        res = "";
    }
    jbyteArray ret = env->NewByteArray(strlen(res));
    env->SetByteArrayRegion(ret, 0, strlen(res), (const jbyte *) res);
    env->ReleaseByteArrayElements(enc_conf_bytes, (jbyte *) bytes, 0);
    return ret;
}

EXPORT jstring
Java_me_teble_xposed_autodaily_task_util_ConfigUtil_getTencentDigest(
        JNIEnv *env, jobject thiz,
        jstring value) {
    const char *p = env->GetStringUTFChars(value, nullptr);
    int len = env->GetStringUTFLength(value);
    std::string encValue(p, len);
    const char *ret = hmacSha1Base64(encValue);
    env->ReleaseStringUTFChars(value, p);
    return env->NewStringUTF(ret);
}

EXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) { //从JavaVM获取JNIEnv，一般使用1.4的版本
        return -1;
    }
    jint retCode = Decrypt_JNI_OnLoad(vm, reserved);
    if (retCode != JNI_VERSION_1_6) {
        return retCode;
    }
    return MMKV_JNI_OnLoad(vm, reserved);
//    return JNI_VERSION_1_4;
}