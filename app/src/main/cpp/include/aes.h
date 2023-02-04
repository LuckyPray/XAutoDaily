//
// Created by teble on 2022/2/11.
//
//#include <iostream>
#include "openssl/aes.h"

#define AES_KEY_LENGTH 16  // aes-128

std::string String2Hex(const unsigned char *src, size_t len) {
    std::string dest;
    for (size_t i = 0; i < len; ++i) {
        char tmp[3] = {0};
        snprintf(tmp, 3, "%02X", src[i]);
        dest += tmp;
    }
    return dest;
}

std::string Hex2String(const std::string &hex) {
    std::string dest;
    if (hex.length() % 2 != 0) {
        return dest;
    }
    int len = (int) hex.length();
    std::string newString;
    for (int i = 0; i < len; i += 2) {
        std::string byte = hex.substr(i, 2);
        char chr = (char) (int) strtol(byte.c_str(), nullptr, 16);
        newString.push_back(chr);
    }
    return newString;
}

std::string PKCS5Padding(const std::string &in) {
    std::string out = in;
    int pad_len = AES_BLOCK_SIZE - out.length() % AES_BLOCK_SIZE;
    for (int i = 0; i < pad_len; ++i) {
        out.push_back((char) pad_len);
    }
    return out;
}

std::string PKCS5UnPadding(const std::string &in) {
    std::string out = in;
    if (in.length() % AES_BLOCK_SIZE != 0 && in.length() >= AES_BLOCK_SIZE) {
        return "";
    }
    int pad_len = ((int) in[in.length() - 1]);
    try {
        out.erase(in.length() - pad_len);
    }
    catch (...) {
        return "";
    }
    return out;
}

std::string encryptByAES(const std::string &in, const std::string &key) {
    std::string out;
    if (key.length() != AES_KEY_LENGTH) {
        return out;
    }
    std::string src = PKCS5Padding(in);
    AES_KEY aes_key;
    AES_set_encrypt_key((unsigned char *) key.c_str(), AES_KEY_LENGTH * 8, &aes_key);
    auto *p = (unsigned char *) src.c_str();
    for (int i = 0; i < src.length() / AES_BLOCK_SIZE; ++i) {
        unsigned char dec[AES_BLOCK_SIZE] = {'\0'};
        AES_ecb_encrypt(p + i * AES_BLOCK_SIZE, dec, &aes_key, AES_ENCRYPT);
        for (auto ch : dec) {
            out.push_back((char) ch);
        }
    }
    return out;
}

std::string decryptByAES(const std::string &in, const std::string &key) {
    std::string out;
    if (key.length() != AES_KEY_LENGTH || in.length() % AES_BLOCK_SIZE != 0) {
        return out;
    }
    AES_KEY aes_key;
    AES_set_decrypt_key((unsigned char *) key.c_str(), AES_KEY_LENGTH * 8, &aes_key);
    auto *p = (unsigned char *) in.c_str();
    for (int i = 0; i < in.length() / AES_BLOCK_SIZE; ++i) {
        unsigned char src[AES_BLOCK_SIZE + 1] = {0};
        AES_ecb_encrypt(p + i * AES_BLOCK_SIZE, src, &aes_key, AES_DECRYPT);
        out.append((char *) src);
    }
    return PKCS5UnPadding(out);
}