#pragma once
#include <fcntl.h>
#include <unistd.h>
#include <cstdio>
#include <csignal>
#include <cstring>
#include <vector>

#include "log.h"

#define forceinline __inline__ __attribute__((always_inline))

const uint32_t EOCD_MAGIC = 0x06054b50;
const uint32_t CD_MAGIC = 0x2014b50;
const uint8_t APK_SIGNING_BLOCK_MAGIC[16]{
        0x41, 0x50, 0x4b, 0x20, 0x53, 0x69, 0x67, 0x20,
        0x42, 0x6c, 0x6f, 0x63, 0x6b, 0x20, 0x34, 0x32
};
const uint32_t SIGNATURE_SCHEME_V2_MAGIC = 0x7109871a;

std::vector<uint8_t> readSignBlock(int fd) {
    auto seek_and_read = [](int fd, int64_t offset, void *buffer, size_t size) -> bool {
        if (lseek(fd, offset, SEEK_CUR) == -1) {
            LOGE("Failed to seek file");
            return false;
        }
        if (read(fd, buffer, size) != (ssize_t)size) {
            LOGE("Failed to read file");
            return false;
        }
        return true;
    };

    off_t file_size = lseek(fd, 0, SEEK_END);
    if (file_size <= 0) {
        LOGE("Invalid file size");
        return {};
    }

    uint32_t magic = 0;

    for (uint16_t i = 0; i <= 0xffff; ++i) {
        uint16_t comment_size = 0;
        if (!seek_and_read(fd, -(i + 2), &comment_size, sizeof(comment_size))) {
            return {};
        }

        if (comment_size == i) {
            if (!seek_and_read(fd, -22, &magic, sizeof(magic))) {
                return {};
            }
            if (magic == EOCD_MAGIC) {
                LOGD("EndOfCentralDirectory off: 0x%x", (uint32_t)(file_size - i - 22));
                break;
            }
        }

        if (i == 0xffff) {
            return {};
        }
    }

    uint32_t central_directory_off = 0;
    if (!seek_and_read(fd, 12, &central_directory_off, sizeof(central_directory_off))) {
        return {};
    }

    LOGD("CentralDirectory off: 0x%x", central_directory_off);

    if (lseek(fd, central_directory_off, SEEK_SET) == -1) {
        LOGE("Failed to seek to CentralDirectory");
        return {};
    }
    if (read(fd, &magic, sizeof(magic)) != sizeof(magic) || magic != CD_MAGIC) {
        LOGE("Invalid CentralDirectory magic");
        return {};
    }

    uint64_t signing_block_size = 0;
    if (!seek_and_read(fd, -28, &signing_block_size, sizeof(signing_block_size))) {
        return {};
    }

    LOGD("signing_block_size: %lu", signing_block_size);

    uint8_t magic_buf[16] = {0};
    if (!seek_and_read(fd, 0, magic_buf, sizeof(magic_buf))) {
        return {};
    }
    if (std::memcmp(magic_buf, APK_SIGNING_BLOCK_MAGIC, sizeof(magic_buf)) != 0) {
        LOGE("Invalid signing block magic");
        return {};
    }

    std::vector<uint8_t> sign_block(signing_block_size);
    if (!seek_and_read(fd, (int64_t) -signing_block_size, sign_block.data(), signing_block_size)) {
        return {};
    }

    return sign_block;
}

std::vector<uint8_t> getV2SignCert(std::vector<uint8_t> &block) {
    std::vector<uint8_t> certificate;
    auto *p = block.data();
    auto *end_p = block.data() + block.size();
    while (p < end_p) {
        uint64_t blockSize = 0;
        for (int i = 0; i < 8; ++i) {
            blockSize = (blockSize >> 8) | (((uint64_t) *p++) << (8 * 7));
        }
        uint32_t id = 0;
        for (int i = 0; i < 4; ++i) {
            id = (id >> 8) | (((uint32_t) *p++) << (8 * 3));
        }
        if (id != SIGNATURE_SCHEME_V2_MAGIC) {
            p += blockSize - 12;
            continue;
        }
        p += 12;
        uint32_t size = 0;
        for (int i = 0; i < 4; ++i) {
            size = (size >> 8) | (((uint32_t) *p++) << (8 * 3));
        }
        p += size + 4;
        for (int i = 0; i < 4; ++i) {
            size = (size >> 8) | (((uint32_t) *p++) << (8 * 3));
        }
        for (int i = 0; i < size; ++i) {
            certificate.push_back(*p++);
        }
        break;
    }

    return certificate;
}