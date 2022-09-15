package me.teble.xposed.autodaily.dexkit

class DexKitHelper(
    classLoader: ClassLoader
) {

    /**
     * 使用完成后切记记得调用 [release]，否则内存不会释放
     */
    private var token: Long = 0

    init {
        token = initDexKit(classLoader)
    }

    fun batchFindClassUsedString(
        map: Map<String, Set<String>>,
        advancedMatch: Boolean = true,
    ): Map<String, Array<String>> {
        return batchFindClassUsedString(token, map, advancedMatch)
    }

    fun batchFindMethodUsedString(
        map: Map<String, Set<String>>,
        advancedMatch: Boolean = true,
    ): Map<String, Array<String>> {
        return batchFindMethodUsedString(token, map, advancedMatch)
    }

    fun findMethodInvoked(
        methodDesc: String,
        className: String,
        methodName: String,
        resultClassName: String,
        paramClassNames: Array<String>,
        dexPriority: IntArray,
        matchAnyParamIfParamsEmpty: Boolean,
    ): Array<String> {
        return findMethodInvoked(
            token,
            methodDesc,
            className,
            methodName,
            resultClassName,
            paramClassNames,
            dexPriority,
            matchAnyParamIfParamsEmpty
        )
    }

    fun findMethodUsedString(
        string: String,
        className: String,
        methodName: String,
        resultClassName: String,
        paramClassNames: Array<String>,
        dexPriority: IntArray,
        matchAnyParamIfParamsEmpty: Boolean,
        advancedMatch: Boolean = false,
    ) : Array<String> {
        return findMethodUsedString(
            token,
            string,
            className,
            methodName,
            resultClassName,
            paramClassNames,
            dexPriority,
            matchAnyParamIfParamsEmpty,
            advancedMatch
        )
    }

    fun findMethod(
        className: String,
        methodName: String,
        resultClassName: String,
        paramClassNames: Array<String>,
        dexPriority: IntArray,
        matchAnyParamIfParamsEmpty: Boolean,
    ): Array<String> {
        return findMethod(
            token,
            className,
            methodName,
            resultClassName,
            paramClassNames,
            dexPriority,
            matchAnyParamIfParamsEmpty
        )
    }

    fun findSubClasses(
        className: String
    ): Array<String> {
        return findSubClasses(
            token,
            className
        )
    }

    fun findMethodOpPrefixSeq(
        opPrefixSeq: IntArray,
        className: String,
        methodName: String,
        resultClassName: String,
        paramClassNames: Array<String>,
        dexPriority: IntArray,
        matchAnyParamIfParamsEmpty: Boolean,
    ): Array<String> {
        return findMethodOpPrefixSeq(
            token,
            opPrefixSeq,
            className,
            methodName,
            resultClassName,
            paramClassNames,
            dexPriority,
            matchAnyParamIfParamsEmpty
        )
    }

    fun release() {
        release(token)
    }

    private external fun initDexKit(classLoader: ClassLoader): Long

    private external fun release(token: Long)

    private external fun batchFindClassUsedString(
        token: Long,
        map: Map<String, Set<String>>,
        advancedMatch: Boolean = false,
    ): Map<String, Array<String>>

    private external fun batchFindMethodUsedString(
        token: Long,
        map: Map<String, Set<String>>,
        advancedMatch: Boolean = false,
    ): Map<String, Array<String>>

    private external fun findMethodInvoked(
        token: Long,
        methodDesc: String,
        className: String,
        methodName: String,
        resultClassName: String,
        paramClassNames: Array<String>,
        dexPriority: IntArray,
        matchAnyParamIfParamsEmpty: Boolean,
    ): Array<String>

    private external fun findMethodUsedString(
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

    private external fun findMethod(
        token: Long,
        className: String,
        methodName: String,
        resultClassName: String,
        paramClassNames: Array<String>,
        dexPriority: IntArray,
        matchAnyParamIfParamsEmpty: Boolean,
    ): Array<String>

    private external fun findSubClasses(
        token: Long,
        className: String
    ): Array<String>

    private external fun findMethodOpPrefixSeq(
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