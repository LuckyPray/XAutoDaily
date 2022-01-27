#include <jni.h>
#include <iostream>
#include "CaesarCipher.h"
//
// Created by teble on 2020/2/10.
//


extern "C" JNIEXPORT
jstring JNICALL Java_me_teble_xposed_autodaily_task_util_ConfigUtil_getPublicKey(
        JNIEnv *env,
        jobject obj) {
    std::string enc_key = "GMJrGK0JYImJIMp3ZEOFKEWKK4JAKZYFaEXFhEYNiQdP15L4zogm4uERI4oTNdFOzts1LaU2i3EwbKMbxGYlNuarl9nKNqbVeA9h8f+BJSjOkDrEIEX8Y/OjU6kuHixx6gEmR3wyoVEQX83IlUXeKNLMKlc1o88jY33GnojHG6oq7bnBwFMjQCvODaggH1ZiArlh1T+tuBobux9u0cMZKEKF";
    return env->NewStringUTF(CaesarCipher::decrypt(enc_key).c_str());
}