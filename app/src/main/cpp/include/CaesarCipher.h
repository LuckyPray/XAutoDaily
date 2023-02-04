//
// Created by teble on 2022/1/19.
//

#ifndef XAUTODAILY_CAESARCIPHER_H
#define XAUTODAILY_CAESARCIPHER_H

#include <iostream>

namespace {
    class CaesarCipher {
    private:
        static const char *UC_ENCRYPT_CHARS;
        static const char *LC_ENCRYPT_CHARS;

        static const char *UC_DECRYPT_CHARS;
        static const char *LC_DECRYPT_CHARS;
    public:
        static std::string encrypt(std::string &s);

        static std::string decrypt(std::string &s);
    };

    const char *CaesarCipher::UC_ENCRYPT_CHARS = new char[]{
            'K', 'F', 'Y', 'Z', 'O', 'T', 'J', 'Q', 'M', 'D',
            'X', 'S', 'G', 'A', 'P', 'C', 'E', 'H', 'I',
            'V', 'W', 'B', 'U', 'N', 'R', 'L'
    };
    const char *CaesarCipher::LC_ENCRYPT_CHARS = new char[]{
            't', 'p', 'z', 'l', 'o', 'r', 'h', 'b', 'a', 'f',
            'i', 'q', 'k', 'u', 'y', 's', 'm', 'v', 'e',
            'g', 'x', 'd', 'c', 'j', 'n', 'w'
    };

    const char *CaesarCipher::UC_DECRYPT_CHARS = new char[]{
            'N', 'V', 'P', 'J', 'Q', 'B', 'M', 'R', 'S', 'G',
            'A', 'Z', 'I', 'X', 'E', 'O', 'H', 'Y', 'L',
            'F', 'W', 'T', 'U', 'K', 'C', 'D'
    };
    const char *CaesarCipher::LC_DECRYPT_CHARS = new char[]{
            'i', 'h', 'w', 'v', 's', 'j', 't', 'g', 'k', 'x',
            'm', 'd', 'q', 'y', 'e', 'b', 'l', 'f', 'p',
            'a', 'n', 'r', 'z', 'u', 'o', 'c'
    };

    std::string CaesarCipher::encrypt(std::string &s) {
        std::string res;
        for (char &i : s) {
            if (std::isupper(i)) {
                res.push_back(UC_ENCRYPT_CHARS[i - 'A']);
            } else if (std::islower(i)) {
                res.push_back(LC_ENCRYPT_CHARS[i - 'a']);
            } else {
                res.push_back(i);
            }
        }
        return res;
    }

    std::string CaesarCipher::decrypt(std::string &s) {
        std::string res;
        for (char &i : s) {
            if (std::isupper(i)) {
                res.push_back(UC_DECRYPT_CHARS[i - 'A']);
            } else if (std::islower(i)) {
                res.push_back(LC_DECRYPT_CHARS[i - 'a']);
            } else {
                res.push_back(i);
            }
        }
        return res;
    }
}

#endif //XAUTODAILY_CAESARCIPHER_H