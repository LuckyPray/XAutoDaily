//
// Created by teble on 2022/1/4.
//

#ifndef ACDAT_AHOCORASICKDOUBLEARRAYTRIE_H
#define ACDAT_AHOCORASICKDOUBLEARRAYTRIE_H

#include <bits/stdc++.h>
#include "State.h"

template<typename V>
class Hit {
public:
    int begin;
    int end;
    V value;

    Hit(int begin, int end, V value) : begin(begin), end(end), value(value) {
    }
};

template<typename V>
class AhoCorasickDoubleArrayTrie {
public:
    std::vector<int> check;
    std::vector<int> base;
    std::vector<int> fail;
    std::vector<std::vector<int>> output;
    std::vector<V> v;
    std::vector<int> l;
    int size = 0;

    std::vector<Hit<V>> parseText(std::u16string &text);

    void parseText(std::u16string &text, std::function<void(int, int, V)> &callback);

    void parseText(std::u16string &text, std::function<bool(int, int, V)> callback);

    bool matches(std::u16string &text);

    Hit<V> findFirst(std::u16string &text);

    void save() {}

    void load() {}

    V get(std::u16string &key);

    bool set(std::u16string &key, V value);

    V get(int index) {
        return v[index];
    }

    int exactMatchSearch(std::u16string &key);

    int getSize() {
        return v.size();
    }


private:


    /**
     * transmit State, supports failure function
     *
     * @param currentState
     * @param character
     * @return
     */
    int getState(int currentState, char16_t c);

    void storeEmits(int position, int currentState, std::vector<Hit<V>> &collectedEmits);

    int exactMatchSearch(std::u16string &key, int pos, int len, int nodePos);

    int getMatched(int pos, int len, int result, std::u16string &string, int b1);

protected:
    int transition(int current, char16_t c);

    int transitionWithRoot(int nodePos, char16_t c);
};

template<typename V>
std::vector<Hit<V>> AhoCorasickDoubleArrayTrie<V>::parseText(std::u16string &text) {
    int position = 1;
    int currentState = 0;
    std::vector<Hit<V>> collectedEmits = std::vector<Hit<V>>();
    for (auto &i: text) {
        currentState = getState(currentState, i);
        storeEmits(position, currentState, collectedEmits);
        ++position;
    }

    return collectedEmits;
}

template<typename V>
void AhoCorasickDoubleArrayTrie<V>::parseText(std::u16string &text, std::function<void(int, int, V)> &callback) {
    int position = 1;
    int currentState = 0;
    for (auto &i: text) {
        currentState = getState(currentState, i);
        auto hitArray = output[currentState];
        if (!hitArray.empty()) {
            for (int &hit: hitArray) {
                callback(position - l[hit], position, v[hit]);
            }
        }
        ++position;
    }
}

template<typename V>
int AhoCorasickDoubleArrayTrie<V>::getState(int currentState, char16_t c) {
    int newCurrentState = transitionWithRoot(currentState, c);  // 先按success跳转
    while (newCurrentState == -1) // 跳转失败的话，按failure跳转
    {
        currentState = fail[currentState];
        newCurrentState = transitionWithRoot(currentState, c);
    }
    return newCurrentState;
}

template<typename V>
void AhoCorasickDoubleArrayTrie<V>::parseText(std::u16string &text, std::function<bool(int, int, V)> callback) {
    int position = 1;
    int currentState = 0;
    for (auto &c: text) {
        currentState = getState(currentState, c);
        auto hitArray = output[currentState];
        if (!hitArray.empty()) {
            for (int &hit: hitArray) {
                bool proceed = callback.hit(position - l[hit], position, v[hit]);
                if (!proceed) {
                    return;
                }
            }
        }
        ++position;
    }
}

template<typename V>
bool AhoCorasickDoubleArrayTrie<V>::matches(std::u16string &text) {
    int currentState = 0;
    for (auto &c: text) {
        currentState = getState(currentState, c);
        auto hitArray = output[currentState];
        if (!hitArray.empty()) {
            return true;
        }
    }
    return false;
}

template<typename V>
Hit<V> AhoCorasickDoubleArrayTrie<V>::findFirst(std::u16string &text) {
    int position = 1;
    int currentState = 0;
    for (auto &c: text) {
        currentState = getState(currentState, c);
        auto hitArray = output[currentState];
        if (!hitArray.empty()) {
            int hitIndex = hitArray[0];
            return Hit<V>(position - l[hitIndex], position, v[hitIndex]);
        }
        ++position;
    }
    return nullptr;
}

template<typename V>
V AhoCorasickDoubleArrayTrie<V>::get(std::u16string &key) {
    int index = exactMatchSearch(key);
    if (index >= 0) {
        return v[index];
    }
    return nullptr;
}

template<typename V>
bool AhoCorasickDoubleArrayTrie<V>::set(std::u16string &key, V value) {
    int index = exactMatchSearch(key);
    if (index >= 0) {
        v[index] = value;
        return true;
    }
    return false;
}

template<typename V>
void AhoCorasickDoubleArrayTrie<V>::storeEmits(int position, int currentState, std::vector<Hit<V>> &collectedEmits) {
    auto hitArray = output[currentState];
    if (!hitArray.empty()) {
        for (int &hit: hitArray) {
            collectedEmits.push_back(Hit<V>(position - l[hit], position, v[hit]));
        }
    }
}

template<typename V>
int AhoCorasickDoubleArrayTrie<V>::transition(int current, char16_t c) {
    int b = current;
    int p;

    p = b + c + 1;
    if (b == check[p]) {
        b = base[p];
    } else {
        return -1;
    }

    p = b;
    return p;
}

template<typename V>
int AhoCorasickDoubleArrayTrie<V>::transitionWithRoot(int nodePos, char16_t c) {
    int b = base[nodePos];
    int p;

    p = b + c + 1;
    if (b != check[p]) {
        if (nodePos == 0) {
            return 0;
        }
        return -1;
    }

    return p;
}

template<typename V>
int AhoCorasickDoubleArrayTrie<V>::exactMatchSearch(std::u16string &key) {
    return exactMatchSearch(key, 0, 0, 0);
}

template<typename V>
int AhoCorasickDoubleArrayTrie<V>::exactMatchSearch(std::u16string &key, int pos, int len, int nodePos) {
    if (len <= 0) {
        len = (int) key.size();
    }
    if (nodePos <= 0) {
        nodePos = 0;
    }

    int result = -1;

    return getMatched(pos, len, result, key, base[nodePos]);
}

template<typename V>
int AhoCorasickDoubleArrayTrie<V>::getMatched(int pos, int len, int result, std::u16string &key, int b1) {
    int b = b1;
    int p;

    for (int i = pos; i < len; i++) {
        p = b + (int) (key[i]) + 1;
        if (b == check[p]) {
            b = base[p];
        } else {
            return result;
        }
    }

    p = b; // transition through '\0' to check if it's the end of a word
    int n = base[p];
    if (b == check[p]) // yes, it is.
    {
        result = -n - 1;
    }
    return result;
}

#endif //ACDAT_AHOCORASICKDOUBLEARRAYTRIE_H
