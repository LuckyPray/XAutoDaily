#include <jni.h>
#include <iostream>
#include "log.h"
#include "v2sign.cpp"
//
// Created by teble on 2020/2/10.
//
#define EXPORT extern "C" __attribute__((visibility("default")))
extern "C" jint MMKV_JNI_OnLoad(JavaVM *vm, void *reserved);

EXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    // 模块签名不正确拒绝加载 jni
    if (checkSignature(env) != JNI_TRUE) {
        return -2;
    }
    return MMKV_JNI_OnLoad(vm, reserved);
}