package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils

class EncodedMethod {
    /**
     * struct encoded_method
     * {
     * uleb128 method_idx_diff;
     * uleb128 access_flags;
     * uleb128 code_off;
     * }
     */
    var methodIdxDiff = 0
    var accessFlags = 0
    var codeOff = 0
    override fun toString(): String {
        return "method_idx_diff:" + ByteUtils.int2Hex(methodIdxDiff) +
            ",access_flags:" + ByteUtils.int2Hex(accessFlags) +
            ",code_off:" + ByteUtils.int2Hex(codeOff)
    }

    companion object {
        @JvmStatic
        fun parser(src: ByteArray?, index: IntArray?): EncodedMethod {
            val encodedMethod = EncodedMethod()
            encodedMethod.methodIdxDiff = ByteUtils.readUleb128(src, index)
            encodedMethod.accessFlags = ByteUtils.readUleb128(src, index)
            encodedMethod.codeOff = ByteUtils.readUleb128(src, index)
            return encodedMethod
        }
    }
}