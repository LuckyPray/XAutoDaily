package me.teble.xposed.autodaily.dexkit

import me.teble.xposed.autodaily.hook.base.hostClassLoader

class DexKitHelper(
    classLoader: ClassLoader
) {

    private var token: Long = 0

    init {
        token = initDexKit(hostClassLoader)
    }

    private external fun initDexKit(classLoader: ClassLoader): Long

    external fun release(token: Long)

    external fun batchFindClassUsedString(
        token: Long,
        map: MutableMap<String, Set<String>>,
        advancedMatch: Boolean = false,
    ): MutableMap<String, Array<String>>

    external fun batchFindMethodUsedString(
        token: Long,
        map: MutableMap<String, Set<String>>,
        advancedMatch: Boolean = false,
    ): MutableMap<String, Array<String>>

    external fun findMethodInvoked(
        token: Long,
        methodDesc: String,
        className: String,
        methodName: String,
        resultClassName: String,
        paramClassNames: Array<String>,
        dexPriority: IntArray,
        matchAnyParamIfParamsEmpty: Boolean,
    ): Array<String>

    external fun findMethodUsedString(
        token: Long,
        string: String,
        className: String,
        methodName: String,
        resultClassName: String,
        paramClassNames: Array<String>,
        dexPriority: IntArray,
        matchAnyParamIfParamsEmpty: Boolean,
        advancedMatch: Boolean = false,
    ): Array<String>

    external fun findMethod(
        token: Long,
        className: String,
        methodName: String,
        resultClassName: String,
        paramClassNames: Array<String>,
        dexPriority: IntArray,
        matchAnyParamIfParamsEmpty: Boolean,
    ): Array<String>

    external fun findSubClasses(
        token: Long,
        className: String
    ): Array<String>

    external fun findMethodOpPrefixSeq(
        token: Long,
        opPrefixSeq: IntArray,
        className: String,
        methodName: String,
        resultClassName: String,
        paramClassNames: Array<String>,
        dexPriority: IntArray,
        matchAnyParamIfParamsEmpty: Boolean,
    ): Array<String>
}