#include <jni.h>
#include <map>
#include <sstream>
#include "log.h"
#include "v2sign.h"
#include "dex_kit.h"
//
// Created by teble on 2020/2/10.
//
namespace {
#define EXPORT extern "C" __attribute__((visibility("default")))
extern "C" jint MMKV_JNI_OnLoad(JavaVM *vm, void *reserved);

std::string hostApkPath;

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

vector<string> split(const string &s, char delim) {
    vector<string> result;
    std::stringstream ss(s);
    string item;

    while (getline(ss, item, delim)) {
        result.push_back(item);
    }

    return result;
}

EXPORT jstring
Java_me_teble_xposed_autodaily_task_util_ConfigUtil_findDex(
        JNIEnv *env, jobject obj, jobject class_loader, jstring input_str) {
    if (!class_loader) {
        return env->NewStringUTF("");
    }
    jclass cClassloader = env->FindClass("java/lang/ClassLoader");
    jmethodID mGetResource = env->GetMethodID(cClassloader, "findResource",
                                              "(Ljava/lang/String;)Ljava/net/URL;");
    jstring manifestPath = env->NewStringUTF("AndroidManifest.xml");
    jobject url = env->CallObjectMethod(class_loader, mGetResource, manifestPath);
    jclass cURL = env->FindClass("java/net/URL");
    jmethodID mGetPath = env->GetMethodID(cURL, "getPath", "()Ljava/lang/String;");
    auto file = (jstring) env->CallObjectMethod(url, mGetPath);
    const char *cStr = env->GetStringUTFChars(file, nullptr);
    std::string filePathStr(cStr);
    hostApkPath = filePathStr.substr(5, filePathStr.size() - 26);
    LOGD("host apk path -> %s", hostApkPath.c_str());
    const char *obfuscate_str = env->GetStringUTFChars(input_str, nullptr);
    std::string inputStr(obfuscate_str);

//    map<string_view, set<string_view>> obfuscate = {
//            {"Lcom/tencent/mobileqq/activity/ChatActivityFacade;",               {"^reSendEmo"}},
//            {"Lcooperation/qzone/PlatformInfor;",                                {"^52b7f2$", "qimei"}},
//            {"Lcom/tencent/mobileqq/troop/clockin/handler/TroopClockInHandler;", {"^TroopClockInHandler$"}},
//    };

    map<string, set<string>> obfuscate;

    for (auto &vec: split(inputStr, '\n')) {
        if (vec.empty()) continue;
        auto lines = split(vec, '\t');
        set<string> strSet;
        for (int i = 1; i < lines.size(); ++i) {
            strSet.emplace(lines[i]);
        }
        obfuscate.emplace(lines[0], strSet);
    }

    dexkit::DexKit dexKit(hostApkPath);

    auto now = std::chrono::system_clock::now();
    auto now_ms = std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch());

    std::string result;
    auto res = dexKit.LocationClasses(obfuscate, true);
    for (auto &[key, value]: res) {
        result += key;
        result += "\t";
        for (int i = 0; i < value.size(); ++i) {
            if (i > 0) {
                result += "\t";
            }
            result += value[i];
        }
        result += "\n";
    }

    auto now1 = std::chrono::system_clock::now();
    auto now_ms1 = std::chrono::duration_cast<std::chrono::milliseconds>(now1.time_since_epoch());
    LOGI("used time: %lld ms", now_ms1.count() - now_ms.count());

    env->ReleaseStringUTFChars(file, cStr);
    env->ReleaseStringUTFChars(input_str, obfuscate_str);
    return env->NewStringUTF(result.c_str());
}

}