#pragma once

#include <unistd.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <dirent.h>
#include <string>
#include "linux_syscall_support.h"
#include "v2sign_v2.h"
#include "md5.h"

#define __STRING(x) #x
#define STRING(x) __STRING(x)

#ifndef MODULE_SIGNATURE
#define MODULE_SIGNATURE FF9FF61037FF85BEDDBA5C98A3CB7600
#endif

bool string2int(const std::string &str, int &value) {
    char *endptr = nullptr;
    value = static_cast<int>(strtol(str.c_str(), &endptr, 10));
    return endptr != str.c_str() && *endptr == '\0';
}

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
        return {};
    }
    std::string s = filePathStr.substr(5, filePathStr.size() - 26);
    LOGD("module path -> %s", s.c_str());
    env->ReleaseStringUTFChars(file, cStr);
    return s;
}

int getModulePathFd(JNIEnv *env) {
    auto apkPath = getModulePath(env);
    return sys_openat(AT_FDCWD, apkPath.c_str(), O_RDONLY, 0);
}

int getSelfApkFd() {
    // walk through /proc/pid/fd to find the apk path
    std::string selfFdDir = "/proc/" + std::to_string(getpid()) + "/fd";
    DIR *dir = opendir(selfFdDir.c_str());
    if (dir == nullptr) {
        return -1;
    }
    struct dirent *entry;
    while ((entry = readdir(dir)) != nullptr) {
        if (entry->d_type != DT_LNK) {
            continue;
        }
        std::string linkPath = selfFdDir + "/" + entry->d_name;
        char buf[PATH_MAX] = {};
        ssize_t len = sys_readlinkat(AT_FDCWD, linkPath.c_str(), buf, sizeof(buf));
        if (len < 0) {
            continue;
        }
        buf[len] = '\0';
        std::string path(buf);
        if (path.starts_with("/data/app/") && path.find(STRING(PKG_NAME)) != std::string::npos && path.ends_with(".apk")) {
            closedir(dir);
            int resultFd = -1;
            if (string2int(entry->d_name, resultFd)) {
                return resultFd;
            }
            return -1;
        }
    }
    closedir(dir);
    return -1;
}

std::string getBlockMd5(const std::string &block) {
    return MD5(block).getDigest();
}

bool checkSignature(JNIEnv *env, bool isInHostAsModule) {
    int fd = -1;
    if (isInHostAsModule) {
        fd = getModulePathFd(env);
    } else {
        fd = getSelfApkFd();
    }
    // fd is stolen from system, so we don't need to close it
    if (fd < 0) {
        LOGE("getSelfApkFd failed");
        return false;
    }
    int ret;
    size_t fileSize = 0;
#if defined(__LP64__)
    kernel_stat stat = {};
    ret = sys_fstat(fd, &stat);
    fileSize = stat.st_size;
#else
    kernel_stat64 stat = {};
            ret = sys_fstat64(fd, &stat);
            fileSize = stat.st_size;
#endif
    if (ret < 0) {
        sys_close(fd);
        LOGE("fstat failed");
        return false;
    }
    auto signBlock = readSignBlock(fd);
    auto cert = getV2SignCert(signBlock);
    std::string cert_str(cert.begin(), cert.end());
    std::string md5 = getBlockMd5(cert_str);
    std::string str(STRING(MODULE_SIGNATURE));
    LOGD("cal md5: %s", md5.c_str());
    LOGD("rel md5: %s", str.c_str());
    return md5 == str;
}
