//
// Created by teble on 2022/2/11.
//

#include "openssl/rsa.h"
#include "openssl/pem.h"

#define PUBLIC_KEY_PEM   1
#define PRIVATE_KEY_PEM  0

static RSA *readRSAKey(const std::string &key, int pem_type) {
    BIO *pBioSt = BIO_new_mem_buf(key.c_str(), -1);
    RSA *rsa;
    if (pem_type == PUBLIC_KEY_PEM) {
        rsa = PEM_read_bio_RSA_PUBKEY(pBioSt, nullptr, nullptr, nullptr);
    } else {
        rsa = PEM_read_bio_RSAPrivateKey(pBioSt, nullptr, nullptr, nullptr);
    }
    BIO_free_all(pBioSt);
    return rsa;
}

static std::string encodeByRSAPubKey(const std::string &src, const std::string &publicKey) {
    RSA *rsa = readRSAKey(publicKey, PUBLIC_KEY_PEM);
    char *encrypt = (char *) malloc(RSA_size(rsa));
    int encrypt_length = RSA_public_encrypt((int) src.size(), (unsigned char *) src.c_str(),
                                            (unsigned char *) encrypt, rsa, RSA_PKCS1_PADDING);
    if (encrypt_length == -1) {
        return {};
    }
    std::string res(encrypt, encrypt_length);
    free(encrypt);
    return res;
}

static std::string decodeByRSAPubKey(const std::string &src, const std::string &publicKey) {
    RSA *rsa = readRSAKey(publicKey, PUBLIC_KEY_PEM);
    char *decrypt = (char *) malloc(RSA_size(rsa));
    int decrypt_length = RSA_public_decrypt((int) src.size(), (unsigned char *) src.c_str(),
                                            (unsigned char *) decrypt, rsa, RSA_PKCS1_PADDING);
    if (decrypt_length == -1) {
        return {};
    }
    std::string res(decrypt, decrypt_length);
    free(decrypt);
    return res;
}

static std::string encodeByRSAPriKey(const std::string &src, const std::string &privateKey) {
    RSA *rsa = readRSAKey(privateKey, PRIVATE_KEY_PEM);
    char *encrypt = (char *) malloc(RSA_size(rsa));
    int encrypt_length = RSA_private_encrypt((int) src.size(), (unsigned char *) src.c_str(),
                                             (unsigned char *) encrypt, rsa, RSA_PKCS1_PADDING);
    if (encrypt_length == -1) {
        return {};
    }
    std::string res(encrypt, encrypt_length);
    free(encrypt);
    return res;
}

static std::string decodeByRSAPriKey(const std::string &src, const std::string &privateKey) {
    RSA *rsa = readRSAKey(privateKey, PRIVATE_KEY_PEM);
    char *decypt = (char *) malloc(RSA_size(rsa));
    int decrypt_length = RSA_private_decrypt((int) src.size(), (unsigned char *) src.c_str(),
                                             (unsigned char *) decypt, rsa, RSA_PKCS1_PADDING);
    if (decrypt_length == -1) {
        return {};
    }
    std::string res(decypt, decrypt_length);
    free(decypt);
    return res;
}