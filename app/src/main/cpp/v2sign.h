#include <jni.h>
#include <regex>
#include <cstring>
#include <fstream>
#include <android/log.h>
#include "md5.cpp"
#include "log.h"
#ifndef MODULE_SIGNATURE
#define MODULE_SIGNATURE FF9FF61037FF85BEDDBA5C98A3CB7600
#endif

#define __STRING(x) #x
#define STRING(x) __STRING(x)
namespace {
    const char magic[16]{
            0x32, 0x34, 0x20, 0x6b, 0x63, 0x6f, 0x6c, 0x42,
            0x20, 0x67, 0x69, 0x53, 0x20, 0x4b, 0x50, 0x41,
    };

    const uint64_t revertMagikFirst = 0x3234206b636f6c42L;
    const uint64_t revertMagikSecond = 0x20676953204b5041L;
    const uint32_t v2Id = 0x7109871a;

    std::string getModulePath(JNIEnv *env) {
        jclass cMainHook = env->FindClass("me/teble/xposed/autodaily/hook/MainHook");
        jclass cClass = env->FindClass("java/lang/Class");
        jmethodID mGetClassLoader = env->GetMethodID(cClass, "getClassLoader",
                                                     "()Ljava/lang/ClassLoader;");
        jobject classloader = env->CallObjectMethod(cMainHook, mGetClassLoader);
        jclass cClassloader = env->FindClass("java/lang/ClassLoader");
        jmethodID mGetResource = env->GetMethodID(cClassloader, "findResource",
                                                  "(Ljava/lang/String;)Ljava/net/URL;");
        jstring manifestPath = env->NewStringUTF("AndroidManifest.xml");
        jobject url = env->CallObjectMethod(classloader, mGetResource, manifestPath);
        jclass cURL = env->FindClass("java/net/URL");
        jmethodID mGetPath = env->GetMethodID(cURL, "getPath", "()Ljava/lang/String;");
        auto file = (jstring) env->CallObjectMethod(url, mGetPath);
        const char *cStr = env->GetStringUTFChars(file, nullptr);
        std::string filePathStr(cStr);
        if (filePathStr.empty()) {
            return std::string();
        }
        std::string s = filePathStr.substr(5, filePathStr.size() - 26);
        LOGD("module path -> %s", s.c_str());
        env->ReleaseStringUTFChars(file, cStr);
        return s;
    }

    std::string getSignBlock(const std::string &path) {
        std::ifstream f(path);
        std::string file((std::istreambuf_iterator<char>(f)), std::istreambuf_iterator<char>());
        uint64_t curr = 0;
        const char *base = file.c_str();
        const char *ptr = base + file.size() - 1;
        std::string signBlock;
        while (ptr >= base) {
            curr = (curr << 8) | *ptr--;
            if (curr == revertMagikFirst) {
                uint64_t tmp = 0;
                for (int i = 0; i < 8; ++i) {
                    tmp = (tmp << 8) | *(ptr - i);
                }
                if (tmp == revertMagikSecond) {
                    for (int i = 8; i < 16; ++i) {
                        tmp = (tmp << 8) | *(ptr - i);
                    }
                    // TODO 只判断魔数“APK Sig Block 42”可能存在误判
                    ptr -= 16;
                    tmp -= 24;
                    for (int i = 0; i < tmp; ++i) {
                        signBlock.push_back(*ptr--);
                    }
                    break;
                }
            }
        }
        std::reverse(signBlock.begin(), signBlock.end());
        return signBlock;
    }

    std::string getBlockMd5(const std::string &block) {
        return MD5(block).getDigest();
    }

    std::string getV2Signature(const std::string &block) {
        std::string signature;
        const char *p = block.c_str();
        const char *last = block.c_str() + block.size();
        while (p < last) {
            uint64_t blockSize = 0;
            for (int i = 0; i < 8; ++i) {
                blockSize = (blockSize >> 8) | (((uint64_t) *p++) << 56);
            }
            uint32_t id = 0;
            for (int i = 0; i < 4; ++i) {
                id = (id >> 8) | (((uint32_t) *p++) << 24);
            }
            if (id != v2Id) {
                p += blockSize - 12;
                continue;
            }
            p += 12;
            uint32_t size = 0;
            for (int i = 0; i < 4; ++i) {
                size = (size >> 8) | (((uint32_t) *p++) << 24);
            }
            p += size + 4;
            for (int i = 0; i < 4; ++i) {
                size = (size >> 8) | (((uint32_t) *p++) << 24);
            }
            for (int i = 0; i < size; ++i) {
                signature.push_back(*p++);
            }
            break;
        }
        return signature;
    }

    bool checkSignature(JNIEnv *env) {
        std::string path = getModulePath(env);
        if (path.empty()) {
            return false;
        }
        std::string block = getSignBlock(path);
        if (block.empty()) {
            return false;
        }

        std::string currSignature = getV2Signature(block);
        std::string md5 = getBlockMd5(currSignature);
        std::string str(STRING(MODULE_SIGNATURE));
        return str == md5;
    }
}