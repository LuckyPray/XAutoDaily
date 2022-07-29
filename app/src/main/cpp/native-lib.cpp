#include <jni.h>
#include <iostream>
#include "log.h"
#include "v2sign.cpp"
#include "DexKit.cpp"
#include <map>
#include <zlib.h>
#include <sys/mman.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <list>
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


struct MemMap {
    MemMap() = default;

    explicit MemMap(std::string file_name) {
        int fd = open(file_name.data(), O_RDONLY | O_CLOEXEC);
        if (fd > 0) {
            struct stat s{};
            fstat(fd, &s);
            auto *addr = mmap(nullptr, s.st_size, PROT_READ, MAP_PRIVATE,
                              fd, 0);
            if (addr != MAP_FAILED) {
                addr_ = static_cast<uint8_t *>(addr);
                len_ = s.st_size;
            }
        }
        close(fd);
    }

    explicit MemMap(size_t size) {
        auto *addr = mmap(nullptr, size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS,
                          -1, 0);
        if (addr != MAP_FAILED) {
            addr_ = static_cast<uint8_t *>(addr);
            len_ = size;
        }
    }

    ~MemMap() {
        if (ok()) {
            munmap(addr_, len_);
        }
    }

    [[nodiscard]] bool ok() const { return addr_ && len_; }

    [[nodiscard]] auto addr() const { return addr_; }

    [[nodiscard]] auto len() const { return len_; }

    MemMap(MemMap &&other) noexcept: addr_(other.addr_), len_(other.len_) {
        other.addr_ = nullptr;
        other.len_ = 0;
    }

    MemMap &operator=(MemMap &&other) noexcept {
        new(this) MemMap(std::move(other));
        return *this;
    }

    MemMap(const MemMap &) = delete;

    MemMap &operator=(const MemMap &) = delete;

private:
    uint8_t *addr_ = nullptr;
    size_t len_ = 0;
};

void *myalloc([[maybe_unused]] void *q, unsigned n, unsigned m) {
    return calloc(n, m);
}

void myfree([[maybe_unused]] void *q, void *p) {
    (void) q;
    free(p);
}

struct [[gnu::packed]] ZipLocalFile {
    static ZipLocalFile *from(uint8_t *begin) {
        auto *file = reinterpret_cast<ZipLocalFile *>(begin);
        if (file->signature == 0x4034b50u) {
            return file;
        } else {
            return nullptr;
        }
    }

    ZipLocalFile *next() {
        return from(reinterpret_cast<uint8_t *>(this) +
                    sizeof(ZipLocalFile) + file_name_length + extra_length + compress_size);
    }

    MemMap uncompress() {
        if (compress == 0x8) {
            MemMap out(uncompress_size);
            if (!out.ok()) {
                LOGE("failed to mmap for unocmpression");
                return {};
            }
            int err;
            z_stream d_stream; /* decompression stream */

            d_stream.zalloc = myalloc;
            d_stream.zfree = myfree;
            d_stream.opaque = nullptr;

            d_stream.next_in = data();
            d_stream.avail_in = compress_size;
            d_stream.next_out = out.addr();            /* discard the output */
            d_stream.avail_out = out.len();

            err = inflateInit2(&d_stream, -MAX_WBITS);
            if (err != Z_OK) {
                LOGE("inflateInit %d", err);
                return {};
            }

            for (int c = 0;; ++c) {
                err = inflate(&d_stream, Z_NO_FLUSH);
                if (err == Z_STREAM_END) break;
                if (err != Z_OK) {
                    LOGE("inflate %d", err);
                    return {};
                }
            }

            err = inflateEnd(&d_stream);
            if (err != Z_OK) {
                LOGE("inflateEnd %d", err);
                return {};
            }

            if (d_stream.total_out != uncompress_size) {
                LOGE("bad inflate: %ld vs %zu\n", d_stream.total_out,
                     static_cast<size_t>(uncompress_size));
                return {};
            }
            mprotect(out.addr(), out.len(), PROT_READ);
            return out;
        } else if (compress == 0 && compress_size == uncompress_size) {
            MemMap out(uncompress_size);
            memcpy(out.addr(), data(), uncompress_size);
            mprotect(out.addr(), out.len(), PROT_READ);
            return out;
        }
        LOGW("unsupported compress type");
        return {};
    }

    std::string_view file_name() {
        return {name, file_name_length};
    }

    uint8_t *data() {
        return reinterpret_cast<uint8_t *>(this) + sizeof(ZipLocalFile) + file_name_length +
               extra_length;
    }

    [[maybe_unused]] uint32_t signature;
    [[maybe_unused]] uint16_t version;
    [[maybe_unused]] uint16_t flags;
    [[maybe_unused]] uint16_t compress;
    [[maybe_unused]] uint16_t last_modify_time;
    [[maybe_unused]] uint16_t last_modify_date;
    [[maybe_unused]] uint32_t crc;
    [[maybe_unused]] uint32_t compress_size;
    [[maybe_unused]] uint32_t uncompress_size;
    [[maybe_unused]] uint16_t file_name_length;
    [[maybe_unused]] uint16_t extra_length;
    [[maybe_unused]] char name[0];
};

class ZipFile {
public:
    static std::unique_ptr<ZipFile> Open(const MemMap &map) {
        auto *local_file = ZipLocalFile::from(map.addr());
        if (!local_file) return nullptr;
        auto r = std::make_unique<ZipFile>();
        while (local_file) {
            r->entries.emplace(local_file->file_name(), local_file);
            local_file = local_file->next();
        }
        return r;
    }

    ZipLocalFile *Find(std::string_view entry_name) {
        if (auto i = entries.find(entry_name); i != entries.end()) {
            return i->second;
        }
        return nullptr;
    }

private:
    std::map<std::string_view, ZipLocalFile *> entries;
};

struct MyDexFile {
    const void *begin_{};
    size_t size_{};

    virtual ~MyDexFile() = default;
};

EXPORT void
Java_me_teble_xposed_autodaily_task_util_ConfigUtil_findDex(
        JNIEnv *env, jobject obj, jobject class_loader) {
    if (!class_loader) {
        return;
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
    env->ReleaseStringUTFChars(file, cStr);

    auto map = MemMap(hostApkPath);
    if (!map.ok()) {
        return;
    }
    auto zip_file = ZipFile::Open(map);
    vector<tuple<const void *, size_t>> dex_images;
    vector<MemMap> mmaps;
//    mmaps.emplace_back(std::move(map));
    if (zip_file) {
        for (int idx = 1;; ++idx) {
            auto entry_name = "classes" + (idx == 1 ? std::string() : std::to_string(idx)) + ".dex";
            auto entry = zip_file->Find(entry_name);
            if (!entry) {
                break;
            }
            auto dex_image = entry->uncompress();
            if (!dex_image.ok()) {
                LOGW("failed to uncompress %s", entry_name.data());
                continue;
            }
            dex_images.emplace_back(dex_image.addr(), dex_image.len());
            mmaps.emplace_back(std::move(dex_image));
        }
    }
    findDex(dex_images);
}

}