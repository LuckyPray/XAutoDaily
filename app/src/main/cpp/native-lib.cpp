#include <jni.h>
#include "log.h"
#include <string_view>
#include <vector>
#include <map>
#include <sstream>
#include "v2sign.h"
#include <dex_kit.h>

namespace {
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

}