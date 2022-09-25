package me.teble.xposed.autodaily.dexkit

import java.io.Closeable

class DexKitHelper(
    classLoader: ClassLoader
) : Closeable {
    companion object {
        const val FLAG_GETTING = 1
        const val FLAG_SETTING = 2
        const val FLAG_USING = FLAG_GETTING or FLAG_SETTING
    }

    /**
     * 使用完成后切记记得调用 [release]，否则内存不会释放
     */
    private var token: Long = 0

    val isValid: Boolean
        get() = token != 0L

    init {
        token = initDexKit(classLoader)
    }

    @Synchronized
    fun release() {
        release(token)
        token = 0
    }

    override fun close() {
        if (isValid) {
            release()
        }
    }

    protected fun finalize() {
        close()
    }

    fun batchFindClassesUsedStrings(
        map: Map<String, Set<String>>,
        advancedMatch: Boolean = true,
        dexPriority: IntArray? = intArrayOf(),
    ): Map<String, Array<String>> {
        return batchFindClassesUsedStrings(token, map, advancedMatch, dexPriority)
    }

    fun batchFindMethodsUsedStrings(
        map: Map<String, Set<String>>,
        advancedMatch: Boolean = true,
        dexPriority: IntArray? = intArrayOf(),
    ): Map<String, Array<String>> {
        return batchFindMethodsUsedStrings(token, map, advancedMatch, dexPriority)
    }

    fun findMethodBeInvoked(
        methodDescriptor: String,
        methodDeclareClass: String,
        methodName: String,
        methodReturnType: String,
        methodParamTypes: Array<String>? = null,
        callerMethodDeclareClass: String,
        callerMethodName: String,
        callerMethodReturnType: String,
        callerMethodParamTypes: Array<String>? = null,
        dexPriority: IntArray? = intArrayOf(),
    ): Array<String> {
        return findMethodBeInvoked(
            token,
            methodDescriptor,
            methodDeclareClass,
            methodName,
            methodReturnType,
            methodParamTypes,
            callerMethodDeclareClass,
            callerMethodName,
            callerMethodReturnType,
            callerMethodParamTypes,
            dexPriority
        )
    }

    fun findMethodInvoking(
        methodDescriptor: String,
        methodDeclareClass: String,
        methodName: String,
        methodReturnType: String,
        methodParamTypes: Array<String>? = null,
        beCalledMethodDeclareClass: String,
        beCalledMethodName: String,
        beCalledMethodReturnType: String,
        beCalledMethodParamTypes: Array<String>? = null,
        dexPriority: IntArray? = intArrayOf(),
    ): Map<String, Array<String>> {
        return findMethodInvoking(
            token,
            methodDescriptor,
            methodDeclareClass,
            methodName,
            methodReturnType,
            methodParamTypes,
            beCalledMethodDeclareClass,
            beCalledMethodName,
            beCalledMethodReturnType,
            beCalledMethodParamTypes,
            dexPriority
        )
    }

    fun findMethodUsedField(
        fieldDescriptor: String,
        fieldDeclareClass: String,
        fieldName: String,
        fieldType: String,
        usedFlags: Int,
        callerMethodDeclareClass: String,
        callerMethodName: String,
        callerMethodReturnType: String,
        callerMethodParamTypes: Array<String>? = null,
        dexPriority: IntArray? = intArrayOf(),
    ): Map<String, Array<String>> {
        return findMethodUsedField(
            token,
            fieldDescriptor,
            fieldDeclareClass,
            fieldName,
            fieldType,
            usedFlags,
            callerMethodDeclareClass,
            callerMethodName,
            callerMethodReturnType,
            callerMethodParamTypes,
            dexPriority
        )
    }

    fun findMethodUsedString(
        usedString: String,
        advancedMatch: Boolean = true,
        methodDeclareClass: String,
        methodName: String,
        methodReturnType: String,
        methodParamTypes: Array<String>? = null,
        dexPriority: IntArray? = intArrayOf(),
    ): Array<String> {
        return findMethodUsedString(
            token,
            usedString,
            advancedMatch,
            methodDeclareClass,
            methodName,
            methodReturnType,
            methodParamTypes,
            dexPriority
        )
    }

    fun findMethod(
        methodDeclareClass: String,
        methodName: String,
        methodReturnType: String,
        methodParamTypes: Array<String>? = null,
        dexPriority: IntArray? = intArrayOf(),
    ): Array<String> {
        return findMethod(
            token,
            methodDeclareClass,
            methodName,
            methodReturnType,
            methodParamTypes,
            dexPriority
        )
    }

    fun findSubClasses(
        parentClass: String,
        dexPriority: IntArray? = intArrayOf(),
    ): Array<String> {
        return findSubClasses(token, parentClass, dexPriority)
    }

    fun findMethodOpPrefixSeq(
        opPrefixSeq: IntArray,
        methodDeclareClass: String,
        methodName: String,
        methodReturnType: String,
        methodParamTypes: Array<String>? = null,
        dexPriority: IntArray? = intArrayOf(),
    ): Array<String> {
        return findMethodOpPrefixSeq(
            token,
            opPrefixSeq,
            methodDeclareClass,
            methodName,
            methodReturnType,
            methodParamTypes,
            dexPriority
        )
    }

    private external fun initDexKit(classLoader: ClassLoader): Long

    private external fun release(token: Long)

    private external fun batchFindClassesUsedStrings(
        token: Long,
        map: Map<String, Set<String>>,
        advancedMatch: Boolean,
        dexPriority: IntArray?,
    ): Map<String, Array<String>>

    private external fun batchFindMethodsUsedStrings(
        token: Long,
        map: Map<String, Set<String>>,
        advancedMatch: Boolean,
        dexPriority: IntArray?,
    ): Map<String, Array<String>>

    private external fun findMethodBeInvoked(
        token: Long,
        methodDescriptor: String,
        methodDeclareClass: String,
        methodName: String,
        methodReturnType: String,
        methodParamTypes: Array<String>?,
        callerMethodDeclareClass: String,
        callerMethodName: String,
        callerMethodReturnType: String,
        callerMethodParamTypes: Array<String>?,
        dexPriority: IntArray?,
    ): Array<String>

    private external fun findMethodInvoking(
        token: Long,
        methodDescriptor: String,
        methodDeclareClass: String,
        methodName: String,
        methodReturnType: String,
        methodParamTypes: Array<String>?,
        beCalledMethodDeclareClass: String,
        beCalledMethodName: String,
        beCalledMethodReturnType: String,
        beCalledMethodParamTypes: Array<String>?,
        dexPriority: IntArray?,
    ): Map<String, Array<String>>

    private external fun findMethodUsedField(
        token: Long,
        fieldDescriptor: String,
        fieldDeclareClass: String,
        fieldName: String,
        fieldType: String,
        usedFlags: Int,
        callerMethodDeclareClass: String,
        callerMethodName: String,
        callerMethodReturnType: String,
        callerMethodParamTypes: Array<String>?,
        dexPriority: IntArray?,
    ): Map<String, Array<String>>

    private external fun findMethodUsedString(
        token: Long,
        usedString: String,
        advancedMatch: Boolean,
        methodDeclareClass: String,
        methodName: String,
        methodReturnType: String,
        methodParamTypes: Array<String>?,
        dexPriority: IntArray?,
    ): Array<String>

    private external fun findMethod(
        token: Long,
        methodDeclareClass: String,
        methodName: String,
        methodReturnType: String,
        methodParamTypes: Array<String>?,
        dexPriority: IntArray?,
    ): Array<String>

    private external fun findSubClasses(
        token: Long,
        parentClass: String,
        dexPriority: IntArray?,
    ): Array<String>

    private external fun findMethodOpPrefixSeq(
        token: Long,
        opPrefixSeq: IntArray,
        methodDeclareClass: String,
        methodName: String,
        methodReturnType: String,
        methodParamTypes: Array<String>?,
        dexPriority: IntArray?,
    ): Array<String>
}