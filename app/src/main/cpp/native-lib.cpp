#include <jni.h>
#include "log.h"
#include <string_view>
#include <vector>
#include <map>
#include <sstream>
#include "v2sign.h"
#include "CaesarCipher.h"
#include "base64.h"
#include "rsa.h"
#include "aes.h"

#define JNI_ExceptionCheckAndClear(env) \
    if (env->ExceptionCheck()) { \
        env->ExceptionDescribe(); \
        env->ExceptionClear(); \
        return {}; \
    }

namespace {

#define EXPORT extern "C" __attribute__((visibility("default")))
extern "C" jint MMKV_JNI_OnLoad(JavaVM *vm, void *reserved);

const char *encRsaPublicKey =
        "GMJrGK0JYImJIMp3ZEOFKEWKK4JAKZYFaEXFhEYNiQdP15L4zogm4u\n"
        "ERI4oTNdFOzts1LaU2i3EwbKMbxGYlNuarl9nKNqbVeA9h8f+BJSjO\n"
        "kDrEIEX8Y/OjU6kuHixx6gEmR3wyoVEQX83IlUXeKNLMKlc1o88jY3\n"
        "3GnojHG6oq7bnBwFMjQCvODaggH1ZiArlh1T+tuBobux9u0cMZKEKF";

EXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
#if defined(NDEBUG) || defined(TEST_SIGNATURE)
    // 模块签名不正确拒绝加载 jni
    if (checkSignature(env) != JNI_TRUE) {
        return -2;
    }
    LOGI("signature pass");
#endif
    auto ret = MMKV_JNI_OnLoad(vm, reserved);
    if (ret != JNI_VERSION_1_6) {
        return -3;
    }
    LOGI("MMKV_JNI_OnLoad success");
    return JNI_VERSION_1_6;
}

void log_to_java(JNIEnv *env, const std::string& content) {
    jclass LogUtil = env->FindClass("me/teble/xposed/autodaily/utils/LogUtil");
    jmethodID LogUtil_i = env->GetStaticMethodID(LogUtil, "i", "(Ljava/lang/String;)V");
    env->CallStaticVoidMethod(LogUtil, LogUtil_i, env->NewStringUTF(content.c_str()));
}

std::string
decryptByPublicKey(JNIEnv *env, const std::string &encode_key, const std::string &pub_key) {
    jclass CX509EncodedKeySpec = env->FindClass("java/security/spec/X509EncodedKeySpec");
    jmethodID CX509EncodedKeySpec_init = env->GetMethodID(CX509EncodedKeySpec, "<init>", "([B)V");
    jbyteArray jpub_key = env->NewByteArray((int) pub_key.size());
    env->SetByteArrayRegion(jpub_key, 0, (int) pub_key.size(), (jbyte *) pub_key.data());
    jobject jX509EncodedKeySpec = env->NewObject(CX509EncodedKeySpec, CX509EncodedKeySpec_init,
                                                 jpub_key);
    env->DeleteLocalRef(jpub_key);
    jclass CKeyFactory = env->FindClass("java/security/KeyFactory");
    jmethodID CKeyFactory_getInstance = env->GetStaticMethodID(CKeyFactory, "getInstance",
                                                               "(Ljava/lang/String;)Ljava/security/KeyFactory;");
    jstring jRSA = env->NewStringUTF("RSA");
    jobject jKeyFactory = env->CallStaticObjectMethod(CKeyFactory, CKeyFactory_getInstance, jRSA);
    env->DeleteLocalRef(jRSA);
    jmethodID CKeyFactory_generatePublic = env->GetMethodID(CKeyFactory, "generatePublic",
                                                            "(Ljava/security/spec/KeySpec;)Ljava/security/PublicKey;");
    jobject jPublicKey = env->CallObjectMethod(jKeyFactory, CKeyFactory_generatePublic,
                                               jX509EncodedKeySpec);
    JNI_ExceptionCheckAndClear(env)
    env->DeleteLocalRef(jX509EncodedKeySpec);
    env->DeleteLocalRef(jKeyFactory);
    jclass CCipher = env->FindClass("javax/crypto/Cipher");
    jmethodID CCipher_getInstance = env->GetStaticMethodID(CCipher, "getInstance",
                                                           "(Ljava/lang/String;)Ljavax/crypto/Cipher;");
    jstring jRSA_ECB_PKCS1Padding = env->NewStringUTF("RSA/ECB/PKCS1Padding");
    jobject jCipher = env->CallStaticObjectMethod(CCipher, CCipher_getInstance,
                                                  jRSA_ECB_PKCS1Padding);
    JNI_ExceptionCheckAndClear(env)
    env->DeleteLocalRef(jRSA_ECB_PKCS1Padding);
    jmethodID CCipher_init = env->GetMethodID(CCipher, "init", "(ILjava/security/Key;)V");
    env->CallVoidMethod(jCipher, CCipher_init, 2, jPublicKey);
    JNI_ExceptionCheckAndClear(env)
    env->DeleteLocalRef(jPublicKey);
    jmethodID CCipher_doFinal = env->GetMethodID(CCipher, "doFinal", "([B)[B");
    jbyteArray jencode_key = env->NewByteArray((int) encode_key.size());
    env->SetByteArrayRegion(jencode_key, 0, (int) encode_key.size(), (jbyte *) encode_key.data());
    auto jdecode_key = (jbyteArray) env->CallObjectMethod(jCipher, CCipher_doFinal, jencode_key);
    JNI_ExceptionCheckAndClear(env)
    env->DeleteLocalRef(jencode_key);
    env->DeleteLocalRef(jCipher);
    jsize len = env->GetArrayLength(jdecode_key);
    std::string decode_key;
    decode_key.resize(len);
    env->GetByteArrayRegion(jdecode_key, 0, len, (jbyte *) decode_key.data());
    env->DeleteLocalRef(jdecode_key);
    return decode_key;
}

std::string decryptXAConf(JNIEnv *env, const std::string &src) {
    LOGD("src size -> %lu", src.size());
    std::string encPublicKey(encRsaPublicKey);
    std::string pubKey;
    pubKey.append("-----BEGIN PUBLIC KEY-----\n");
    pubKey.append(CaesarCipher::decrypt(encPublicKey));
    pubKey.append("\n-----END PUBLIC KEY-----");
    LOGD("pubKey -> \n%s", encPublicKey.c_str());
    std::string encAesKey = src.substr(0, 171) + "=";
    LOGD("bas64 aes key -> \n%s", encAesKey.c_str());
    std::string encConf = src.substr(171);
    LOGD("encode conf size -> %lu", encConf.size());
    std::string aesKey = decodeByRSAPubKey(base64_decode(encAesKey), pubKey);
    if (aesKey.empty()) {
        LOGE("decode aes key failed");
        return "";
    }
    LOGD("aesKey size -> %lu", aesKey.size());
    std::string res = decryptByAES(base64_decode(encConf), aesKey);
    LOGD("conf size -> %lu", res.size());
    return res;
}

const char *md5Hex(const std::string &value) {
    static const char *ret;
    ret = getBlockMd5(value).c_str();
    return ret;
}

EXPORT jbyteArray
Java_me_teble_xposed_autodaily_task_util_ConfigUtil_decryptXAConf(
        JNIEnv *env,
        jobject obj, jbyteArray enc_conf_bytes) {
    const char *bytes = (char *) env->GetByteArrayElements(enc_conf_bytes, nullptr);
    int len = env->GetArrayLength(enc_conf_bytes);
    std::string encConf(bytes, len);
    std::string res = decryptXAConf(env, encConf);
    jbyteArray ret = env->NewByteArray((int) res.size());
    env->SetByteArrayRegion(ret, 0, (int) res.size(), (const jbyte *) res.c_str());
    env->ReleaseByteArrayElements(enc_conf_bytes, (jbyte *) bytes, 0);
    return ret;
}

EXPORT jstring
Java_me_teble_xposed_autodaily_task_util_ConfigUtil_getMd5Hex(
        JNIEnv *env, jobject thiz, jstring value) {
    const char *p = env->GetStringUTFChars(value, nullptr);
    int len = env->GetStringUTFLength(value);
    std::string encValue(p, len);
    const char *ret = md5Hex(encValue);
    env->ReleaseStringUTFChars(value, p);
    return env->NewStringUTF(ret);
}

}