#include <jni.h>
#include <iostream>
#include "CaesarCipher.h"
//
// Created by teble on 2020/2/10.
//
#define EXPORT extern "C" __attribute__((visibility("default")))
extern "C" jint MMKV_JNI_OnLoad(JavaVM *vm, void *reserved);

EXPORT jstring
Java_me_teble_xposed_autodaily_task_util_ConfigUtil_getPublicKey(
        JNIEnv *env,
        jobject obj) {
    std::string enc_key = "GMJrGK0JYImJIMp3ZEOFKEWKK4JAKZYFaEXFhEYNiQdP15L4zogm4uERI4oTNdFOzts1LaU2i3EwbKMbxGYlNuarl9nKNqbVeA9h8f+BJSjOkDrEIEX8Y/OjU6kuHixx6gEmR3wyoVEQX83IlUXeKNLMKlc1o88jY33GnojHG6oq7bnBwFMjQCvODaggH1ZiArlh1T+tuBobux9u0cMZKEKF";
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