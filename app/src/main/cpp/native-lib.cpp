#include <jni.h>
#include <iostream>
#include "CaesarCipher.h"
//
// Created by teble on 2020/2/10.
//
#define EXPORT extern "C" __attribute__((visibility("default")))
extern "C" jint MMKV_JNI_OnLoad(JavaVM *vm, void *reserved);

EXPORT jstring
Java_me_teble_xposed_autodaily_task_util_ConfigUtil_getPrivateKey(
        JNIEnv *env,
        jobject obj) {
    // TODO 单独提取成一个私有库
    std::string enc_key = "GMJVKhOKGFGJFnmJIG49KhOJYYmJIG49KcOQFQiclcMFKEEhnUGyste9cWk/pFjBmwgtksApg4RC8P7dGAxnWTscXd6hYhRMXyLMwf0ZKEobHKAYKKHnS7vObAjs1sLVWK0Ls0tloZ7shbVH4svByB66bF1++6BzvQdiqrJO/aUOSmLpBaMbKXwJoEvHvdWdm7wSysz4";
    return env->NewStringUTF(CaesarCipher::decrypt(enc_key).c_str());
}

EXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv* env = nullptr;
    if(vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) { //从JavaVM获取JNIEnv，一般使用1.4的版本
        return -1;
    }
    jint retCode = MMKV_JNI_OnLoad(vm, reserved);
    return retCode;
//    return JNI_VERSION_1_4;
}