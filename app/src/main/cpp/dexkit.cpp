#include <dex_kit.h>
#include <DexKitHelper.h>

extern "C"
JNIEXPORT jlong JNICALL
Java_me_teble_xposed_autodaily_dexkit_DexKitHelper_initDexKit(JNIEnv *env, jobject thiz,
                                                              jobject class_loader) {
    if (!class_loader) {
        return 0;
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
    std::string hostApkPath = filePathStr.substr(5, filePathStr.size() - 26);
    return (jlong) new dexkit::DexKit(hostApkPath);
}

extern "C"
JNIEXPORT void JNICALL
Java_me_teble_xposed_autodaily_dexkit_DexKitHelper_release(JNIEnv *env, jobject thiz, jlong token) {
    ReleaseDexKitInstance(env, token);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_me_teble_xposed_autodaily_dexkit_DexKitHelper_batchFindClassUsedString(JNIEnv *env,
                                                                            jobject thiz,
                                                                            jlong token,
                                                                            jobject map,
                                                                            jboolean advanced_match) {
    return LocationClasses(env, token, map, advanced_match);
}

extern "C"
JNIEXPORT jobject JNICALL
Java_me_teble_xposed_autodaily_dexkit_DexKitHelper_batchFindMethodUsedString(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jlong token,
                                                                             jobject map,
                                                                             jboolean advanced_match) {
    return LocationMethods(env, token, map, advanced_match);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_me_teble_xposed_autodaily_dexkit_DexKitHelper_findMethodInvoked(JNIEnv *env, jobject thiz,
                                                                     jlong token,
                                                                     jstring method_desc,
                                                                     jstring class_name,
                                                                     jstring method_name,
                                                                     jstring result_class_name,
                                                                     jobjectArray param_class_names,
                                                                     jintArray dex_priority,
                                                                     jboolean match_any_param_if_params_empty) {
    return FindMethodInvoked(env, token, method_desc, class_name, method_name, result_class_name,
                             param_class_names, dex_priority, match_any_param_if_params_empty);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_me_teble_xposed_autodaily_dexkit_DexKitHelper_findMethodUsedString(JNIEnv *env, jobject thiz,
                                                                        jlong token, jstring string,
                                                                        jstring class_name,
                                                                        jstring method_name,
                                                                        jstring result_class_name,
                                                                        jobjectArray param_class_names,
                                                                        jintArray dex_priority,
                                                                        jboolean match_any_param_if_params_empty,
                                                                        jboolean advanced_match) {
    return FindMethodUsedString(env, token, string, class_name, method_name, result_class_name,
                                param_class_names, dex_priority, match_any_param_if_params_empty,
                                advanced_match);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_me_teble_xposed_autodaily_dexkit_DexKitHelper_findMethod(JNIEnv *env, jobject thiz,
                                                              jlong token, jstring class_name,
                                                              jstring method_name,
                                                              jstring result_class_name,
                                                              jobjectArray param_class_names,
                                                              jintArray dex_priority,
                                                              jboolean match_any_param_if_params_empty) {
    return FindMethod(env, token, class_name, method_name, result_class_name, param_class_names,
                      dex_priority, match_any_param_if_params_empty);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_me_teble_xposed_autodaily_dexkit_DexKitHelper_findSubClasses(JNIEnv *env, jobject thiz,
                                                                  jlong token, jstring class_name) {
    return FindSubClasses(env, token, class_name);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_me_teble_xposed_autodaily_dexkit_DexKitHelper_findMethodOpPrefixSeq(JNIEnv *env, jobject thiz,
                                                                         jlong token,
                                                                         jintArray op_prefix_seq,
                                                                         jstring class_name,
                                                                         jstring method_name,
                                                                         jstring result_class_name,
                                                                         jobjectArray param_class_names,
                                                                         jintArray dex_priority,
                                                                         jboolean match_any_param_if_params_empty) {
    return FindMethodOpPrefixSeq(env, token, op_prefix_seq, class_name, method_name,
                                 result_class_name, param_class_names, dex_priority,
                                 match_any_param_if_params_empty);
}