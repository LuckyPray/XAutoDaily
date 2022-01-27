//
// Created by teble on 2022/1/3.
//

#ifndef ACDAT_STATE_H
#define ACDAT_STATE_H

#include <bits/stdc++.h>


class State {
private:
    State *failure = nullptr;
    // 降序
    std::set<int, std::greater<int>> emits;
    std::map<char16_t, State *> success;
    int index = 0;
protected:
    const int depth;
public:
    State() : depth(0) {}

    explicit State(int depth) : depth(depth) {}

    int getDepth() const {
        return depth;
    }

    void addEmit(int keyword) {
        if (keyword == -1) return;
        this->emits.insert(keyword);
    }

    int getLargestValueId() {
        if (emits.empty()) return -1;
        return *emits.begin();
    }

    void addEmit(const std::set<int, std::greater<int>>& emitSet) {
        for (auto &i: emitSet) {
            addEmit(i);
        }
    }

    std::set<int, std::greater<int>> emit() {
        return this->emits;
    }

    bool isAcceptable() {
        return this->depth > 0 && !this->emits.empty();
    }

    State *getFailure() {
        return this->failure;
    }

    void setFailure(State *failState, std::vector<int> &fail) {
        this->failure = failState;
        fail[index] = failState->index;
    }

private:
    State *next(char16_t c, bool ignoreRootState) {
        auto it = this->success.find(c);
        State *nextState = it != this->success.end() ? (*it).second : nullptr;
        if (!ignoreRootState && nextState == nullptr && this->depth == 0) {
            nextState = this;
        }
        return nextState;
    }

public:
    State *next(char16_t c) {
        return next(c, false);
    }

    State *nextStateIgnoreRootState(char16_t c) {
        return next(c, true);
    }

    State *addState(char16_t c) {
        State *nextState = nextStateIgnoreRootState(c);
        if (nextState == nullptr) {
            nextState = new State(this->depth + 1);
            this->success[c] = nextState;
        }
        return nextState;
    }

    std::vector<State *> getStates() {
        std::vector<State*> states;
        states.reserve(this->success.size());
        for (auto &i: this->success) {
            states.push_back(i.second);
        }
        return states;
    }

    std::vector<char16_t> getTransitions() {
        std::vector<char16_t> transitions;
        transitions.reserve(this->success.size());
        for (auto &i: this->success) {
            transitions.push_back(i.first);
        }
        return transitions;
    }

    std::map<char16_t, State *> getSuccess() {
        return this->success;
    }

    int getIndex() const {
        return this->index;
    }

    void setIndex(int idx) {
        this->index = idx;
    }
};

#endif //ACDAT_STATE_H
