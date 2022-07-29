//
// Created by teble on 2022/7/28.
//
#include <iostream>
#include <fstream>
#include <chrono>
#include <stack>
#include <map>
#include <thread>
#include <unistd.h>
#include "slicer/reader.h"
#include "slicer/dex_bytecode.h"
#include "acdat/Builder.h"
#include "DexHelper.h"
#include "log.h"

namespace {

#pragma clang diagnostic push
#pragma ide diagnostic ignored "LocalValueEscapesScope"
using namespace dex;
using namespace std;

void locateClassInDex(dex::Reader &reader,
                      AhoCorasickDoubleArrayTrie<string> &acdat,
                      map<string, vector<string>> &resourceMap,
                      map<string, vector<string>> &resultMap) {
    std::map<int, string> strIndexMap;
    int strSize = (int) reader.StringIds().size();
    for (int index = 0; index < strSize; ++index) {
        const char *str = reader.GetStringMUTF8(index);
        std::function<void(int, int, string)> callback = [&strIndexMap, index](int begin, int end,
                                                                               const string &value) -> void {
            strIndexMap[index] = value;
//            cout << "str: " << value << ", index: " << index << "\n";
        };
        acdat.parseText(str, callback);
    }
    if (strIndexMap.empty()) {
        LOGI("该dex不存在特征串");
//            cout << "该dex不存在特征串" << "\n";
        return;
    }
    bool confuseFlag = false;
    int classDefSize = (int) reader.ClassDefs().size();
    for (int idx = 0; idx < classDefSize; ++idx) {
        reader.CreateClassIr(idx);
        auto ir = reader.GetIr();
        // class_def_item -> static_values
        if (ir->classes[idx]->static_init != nullptr) {
            queue<ir::EncodedValue *> encodeValueQueue;
            for (auto value: ir->classes[idx]->static_init->values) {
                encodeValueQueue.push(value);
            }
            while (!encodeValueQueue.empty()) {
                auto value = encodeValueQueue.front();
                encodeValueQueue.pop();
                switch (value->type) {
                    case dex::kEncodedString: {
                        int index = (int) value->u.string_value->orig_index;
//                            cout << "DBG: strIdx: " << index << ", str: " << value->u.string_value->c_str() << "\n";
                        if (strIndexMap.find(index) != strIndexMap.end()) {
                            LOGI("class: %s 在static_fields区域包含字符串: %s",
                                 ir->classes[idx]->type->descriptor->c_str(),
                                 strIndexMap[index].c_str());
//                                cout << "class: " << ir->classes[idx]->type->descriptor->c_str()
//                                     << " 在static_fields区域包含字符串: " << strIndexMap[index] << "\n";
//                            return;
                        }
                        break;
                    }
                    case dex::kEncodedArray: {
                        for (auto v: value->u.array_value->values) {
                            encodeValueQueue.push(v);
                        }
                        break;
                    }
                    case dex::kEncodedAnnotation: {
                        for (auto v: value->u.annotation_value->elements) {
                            encodeValueQueue.push(v->value);
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        // class_def_item -> class_data_item -> direct_methods
        if (!ir->classes[idx]->direct_methods.empty()) {
            for (auto method: ir->classes[idx]->direct_methods) {
                if (method->code == nullptr) {
                    continue;
                }
                auto insns = method->code->instructions;
                auto p = insns.begin();
                while (p < insns.end()) {
                    auto instruction = dex::DecodeInstruction(p);
                    auto pp = p;
                    auto width = GetWidthFromBytecode(pp++);
                    auto end = p + width;
//                    cout << instruction.opcode << " (" << width << ")";
//                    while (pp < end) {
//                        cout << " " << std::setw(4) << std::setfill('0') << std::hex << *pp++;
//                    }
//                    cout << ";" << "\n" << std::dec;
                    switch (instruction.opcode) {
                        case 0x1a: {
                            auto index = p[1];
//                            cout << "DBG: strIdx: " << index << ", str: " << reader.GetStringMUTF8(index) << "\n";
                            if (strIndexMap.find(index) != strIndexMap.end()) {
                                LOGI("%s->%s 包含字符串: %s",
                                     ir->classes[idx]->type->descriptor->c_str(),
                                     method->decl->prototype->Signature().c_str(),
                                     strIndexMap[index].c_str());
//                                    cout << ir->classes[idx]->type->descriptor->c_str()
//                                         << "->" << method->decl->prototype->Signature()
//                                         << " 包含字符串: " << strIndexMap[index] << "\n";
//                                return;
                            }
                            break;
                        }
                        case 0x1b: {
                            auto index = p[1] | (p[2] << 16);
//                            cout << "DBG: strIdx: " << index << ", str: " << reader.GetStringMUTF8(index) << "\n";
                            if (strIndexMap.find(index) != strIndexMap.end()) {
                                LOGI("%s->%s 包含字符串: %s",
                                     ir->classes[idx]->type->descriptor->c_str(),
                                     method->decl->prototype->Signature().c_str(),
                                     strIndexMap[index].c_str());
//                                    cout << ir->classes[idx]->type->descriptor->c_str()
//                                         << "->" << method->decl->prototype->Signature()
//                                         << " 包含字符串: " << strIndexMap[index] << "\n";
//                                return;
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    p += width;
                }

//                cout << "method: " << method->decl->name->c_str() << "\n";
//                for (auto item : insns) {
//                    cout << " " << std::setw(4) << std::setfill('0') << std::hex << item;
//                }
//                cout << "\n" << std::dec;
            }
        }
        // class_def_item -> class_data_item -> virtual_methods
        if (!ir->classes[idx]->virtual_methods.empty()) {
            for (auto method: ir->classes[idx]->virtual_methods) {
                if (method->code == nullptr) {
                    continue;
                }
                auto insns = method->code->instructions;
                auto p = insns.begin();
                while (p < insns.end()) {
                    auto instruction = dex::DecodeInstruction(p);
                    auto pp = p;
                    auto width = GetWidthFromBytecode(pp++);
                    auto end = p + width;
//                    cout << instruction.opcode << " (" << width << ")";
//                    while (pp < end) {
//                        cout << " " << std::setw(4) << std::setfill('0') << std::hex << *pp++;
//                    }
//                    cout << ";" << "\n" << std::dec;
                    switch (instruction.opcode) {
                        case 0x1a: {
                            auto index = p[1];
//                            cout << "DBG: strIdx: " << index << ", str: " << reader.GetStringMUTF8(index) << "\n";
                            if (strIndexMap.find(index) != strIndexMap.end()) {
                                LOGI("%s->%s 包含字符串: %s",
                                     ir->classes[idx]->type->descriptor->c_str(),
                                     method->decl->prototype->Signature().c_str(),
                                     strIndexMap[index].c_str());
//                                    cout << ir->classes[idx]->type->descriptor->c_str()
//                                         << "->" << method->decl->prototype->Signature()
//                                         << " 包含字符串: " << strIndexMap[index] << "\n";
//                                return;
                            }
                            break;
                        }
                        case 0x1b: {
                            auto index = p[1] | (p[2] << 16);
//                            cout << "DBG: strIdx: " << index << ", str: " << reader.GetStringMUTF8(index) << "\n";
                            if (strIndexMap.find(index) != strIndexMap.end()) {
                                LOGI("%s->%s 包含字符串: %s",
                                     ir->classes[idx]->type->descriptor->c_str(),
                                     method->decl->prototype->Signature().c_str(),
                                     strIndexMap[index].c_str());
//                                    cout << ir->classes[idx]->type->descriptor->c_str()
//                                         << "->" << method->decl->prototype->Signature()
//                                         << " 包含字符串: " << strIndexMap[index] << "\n";
//                                return;
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    p += width;
                }

//                cout << "method: " << method->decl->name->c_str() << "\n";
//                for (auto item : insns) {
//                    cout << " " << std::setw(4) << std::setfill('0') << std::hex << item;
//                }
//                cout << "\n" << std::dec;
            }
        }
        // 释放空间
        delete ir->classes[idx].release(); // NOLINT(bugprone-unused-return-value)
    }
}

void fastLocate(vector<tuple<const void *, size_t>> dex_images,
                int index,
                AhoCorasickDoubleArrayTrie<string> &acdat,
                map<string, vector<string>> &resourceMap,
                map<string, vector<string>> &resultMap) {
    auto &[p, size] = dex_images[index];
    dex::Reader reader((dex::u1 *) p, size);
    LOGI("开始解析 dex 文件: classes%d.dex", index);
//    int classDefSize = (int) reader.ClassDefs().size();
//    for (int i = 0; i < classDefSize; ++i) {
//        reader.CreateClassIr(i);
//        delete reader.GetIr()->classes[i].release(); // NOLINT(bugprone-unused-return-value)
//    }
    locateClassInDex(reader, acdat, resourceMap, resultMap);
}

void findDex(vector<tuple<const void *, size_t>> dex_images) {
    map<string, vector<string>> obfuscate = {
            {"Lcom/tencent/mobileqq/activity/ChatActivityFacade;",               {"reSendEmo"}},
            {"Lcooperation/qzone/PlatformInfor;",                                {"52b7f2", "qimei"}},
            {"Lcom/tencent/mobileqq/troop/clockin/handler/TroopClockInHandler;", {"TroopClockInHandler"}},
            {"test",                                                             {"mark_uin_upload"}},
    };

    auto now = std::chrono::system_clock::now();
    auto now_ms = std::chrono::duration_cast<std::chrono::milliseconds>(now.time_since_epoch());

    auto acdat = AhoCorasickDoubleArrayTrie<string>();
    map<string, string> buildMap;
    map<string, vector<string>> result;
    for (auto &[name, vec]: obfuscate) {
        for (auto &str: vec) {
            buildMap[str] = str;
        }
    }
    Builder<string>().build(buildMap, &acdat);
    vector<thread> threads;
    for (int index = 0; index < dex_images.size(); ++index) {
//        fastLocate(dex_images, index, acdat, obfuscate, result);
        threads.emplace_back(std::move(
                thread(fastLocate, std::ref(dex_images), index, std::ref(acdat),
                       std::ref(obfuscate), std::ref(result))));
    }
    for (auto &th: threads) {
        th.join();
    }
    auto now1 = std::chrono::system_clock::now();
    auto now_ms1 = std::chrono::duration_cast<std::chrono::milliseconds>(now1.time_since_epoch());
    LOGI("used time: %lld ms", now_ms1.count() - now_ms.count());
//    cout << "used time: " << now_ms1.count() - now_ms.count() << "\n";
//    while (true) sleep(1);
    return;
//    #include <filesystem>
//    for (const auto & file : std::filesystem::directory_iterator("../dex/qq-8.9.2")) {
//        cout << file.path() << "\n";
//        ifstream in(file.path());
//        std::vector<uint8_t> buf{std::istreambuf_iterator<char>(in), std::istreambuf_iterator<char>()};
//        cout << "dex size: " << buf.size() << "\n";
//        dex::Reader reader(buf.data(), buf.size());
//
//        locateClassInDex(reader, acdat, obfuscate, result);
//    }
}

#pragma clang diagnostic pop
}
