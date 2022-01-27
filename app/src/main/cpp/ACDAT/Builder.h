//
// Created by teble on 2022/1/6.
//

#ifndef ACDAT_BUILDER_H
#define ACDAT_BUILDER_H

#include "AhoCorasickDoubleArrayTrie.h"

template<typename V>
class Builder {
private:
    State *rootState = new State();
    std::vector<bool> used;
    int allocSize = 0;
    int progress = 0;
    int nextCheckPos = 0;
    int keySize = 0;

    AhoCorasickDoubleArrayTrie<V> *mACTrie = nullptr;

    static int fetch(State *parent, std::vector<std::pair<int, State *>> &siblings) {
        if (parent->isAcceptable()) {
            auto *fakeNode = new State(-(parent->getDepth() + 1));
            fakeNode->addEmit(parent->getLargestValueId());
            siblings.emplace_back(0, fakeNode);
        }
        for (auto &item: parent->getSuccess()) {
            siblings.emplace_back(item.first + 1, item.second);
        }
        return (int) siblings.size();
    }

    void addKeyword(const std::u16string &keyword, int index) {
        State *currentState = this->rootState;
        for (auto &ch: keyword) {
            currentState = currentState->addState(ch);
        }
        currentState->addEmit(index);
        mACTrie->l[index] = keyword.length();
    }

    void addAllKeyword(const std::vector<std::u16string> &keywords) {
        int i = 0;
        for (const std::u16string &keyword: keywords) {
            addKeyword(keyword, i++);
        }
    }

    void constructFailureStates() {
        mACTrie->fail.resize(mACTrie->size + 1);
        mACTrie->output.resize(mACTrie->size + 1);
        std::queue<State *> queue;

        // 第一步，将深度为1的节点的failure设为根节点
        for (State *depthOneState: this->rootState->getStates()) {
            depthOneState->setFailure(this->rootState, mACTrie->fail);
            queue.push(depthOneState);
            constructOutput(depthOneState);
        }

        // 第二步，为深度 > 1 的节点建立failure表，这是一个bfs
        while (!queue.empty()) {
            State *currentState = queue.front();
            queue.pop();

            for (char16_t &c: currentState->getTransitions()) {
                State *targetState = currentState->next(c);
                queue.push(targetState);

                State *traceFailureState = currentState->getFailure();
                while (traceFailureState->next(c) == nullptr) {
                    traceFailureState = traceFailureState->getFailure();
                }
                State *newFailureState = traceFailureState->next(c);
                targetState->setFailure(newFailureState, mACTrie->fail);
                targetState->addEmit(newFailureState->emit());
                constructOutput(targetState);
            }
        }
    }

    void constructOutput(State *targetState) {
        std::set<int, std::greater<int>> emit = targetState->emit();
        if (emit.empty()) {
            return;
        }
        std::vector<int> vec;
        vec.reserve(emit.size());
        for (auto &item: emit) {
            vec.push_back(item);
        }
        mACTrie->output[targetState->getIndex()] = vec;
    }

    void buildDoubleArrayTrie(int siz) {
        this->progress = 0;
        this->keySize = siz;
        resize(65535 * 32);

        mACTrie->base[0] = 1;
        nextCheckPos = 0;

        State *rootNode = this->rootState;

        std::vector<std::pair<int, State *>> siblings;
        fetch(rootNode, siblings);
        if (!siblings.empty()) {
            insert(siblings);
        }
    }

    int resize(int newSize) {
        mACTrie->base.resize(newSize);
        mACTrie->check.resize(newSize);
        used.resize(newSize);

        return allocSize = newSize;
    }

    void insert(std::vector<std::pair<int, State *>> &firstSiblings) {
        std::queue<std::pair<int, std::vector<std::pair<int, State *>>>> siblingQueue;
        siblingQueue.push(std::pair<int, std::vector<std::pair<int, State *>>>(-1, firstSiblings));
        while (!siblingQueue.empty()) {
            insert(siblingQueue);
        }
    }

    void insert(std::queue<std::pair<int, std::vector<std::pair<int, State *>>>> &siblingQueue) {
        std::pair<int, std::vector<std::pair<int, State *>>> tCurrent = siblingQueue.front();
        siblingQueue.pop();
        std::vector<std::pair<int, State *>> siblings = tCurrent.second;

        int begin = 0;
        int pos = std::max(siblings[0].first + 1, nextCheckPos) - 1;
        int nonzero_num = 0;
        int first = 0;

        if (allocSize <= pos) {
            resize(pos + 1);
        }

        outer:
        while (true) {
            pos++;

            if (allocSize <= pos) {
                resize(pos + 1);
            }

            if (mACTrie->check[pos] != 0) {
                nonzero_num++;
                continue;
            } else if (first == 0) {
                nextCheckPos = pos;
                first = 1;
            }

            // 当前位置离第一个兄弟节点的距离
            begin = pos - siblings[0].first;
            if (allocSize <= (begin + siblings[siblings.size() - 1].first)) {
                // progress can be zero // 防止progress产生除零错误
                double ll = std::max(1.05, 1.5 * keySize / (progress + 1));
                resize((int) (allocSize * ll));
            }

            if (used[begin]) {
                continue;
            }

            for (int i = 1; i < siblings.size(); ++i) {
                if (mACTrie->check[begin + siblings[i].first] != 0) {
                    goto outer;
                }
            }

            break;
        }

        // -- Simple heuristics --
        // if the percentage of non-empty contents in check between the
        // index
        // 'next_check_pos' and 'check' is greater than some constant value
        // (e.g. 0.9),
        // new 'next_check_pos' index is written by 'check'.
        if (1.0 * nonzero_num / (pos - nextCheckPos + 1) >= 0.95) {
            nextCheckPos = pos;
        }
        used[begin] = true;

        mACTrie->size = std::max(mACTrie->size, begin + siblings[siblings.size() - 1].first + 1);

        for (auto &sibling: siblings) {
            mACTrie->check[begin + sibling.first] = begin;
        }

        for (auto &sibling: siblings) {
            std::vector<std::pair<int, State *>> newSiblings;
            newSiblings.reserve(rootState->getSuccess().size());
            if (fetch(sibling.second, newSiblings) == 0) {
                mACTrie->base[begin + sibling.first] = (-sibling.second->getLargestValueId() - 1);
                progress++;
            } else {
                auto pair = std::pair<int, std::vector<std::pair<int, State *>>>(begin + sibling.first, newSiblings);
                siblingQueue.push(pair);
            }
            sibling.second->setIndex(begin + sibling.first);
        }

        // Insert siblings
        int parentBaseIndex = tCurrent.first;
        if (parentBaseIndex != -1) {
            mACTrie->base[parentBaseIndex] = begin;
        }
    }

    /**
     * free the unnecessary memory
     */
    void losWeight() {
        mACTrie->base.resize(mACTrie->size + 65535);
        mACTrie->check.resize(mACTrie->size + 65535);
    }

public:
    void build(std::map<std::u16string, V> &map, AhoCorasickDoubleArrayTrie<V> *acdat) {
        this->mACTrie = acdat;
        std::vector<std::u16string> keys;
        keys.reserve(map.size());
        for (auto &i: map) {
            mACTrie->v.push_back(i.second);
            keys.push_back(i.first);
        }
        mACTrie->l.resize(map.size());
        addAllKeyword(keys);
        buildDoubleArrayTrie(keys.size());
        constructFailureStates();
        rootState = nullptr;
        losWeight();
    }
};

#endif //ACDAT_BUILDER_H
