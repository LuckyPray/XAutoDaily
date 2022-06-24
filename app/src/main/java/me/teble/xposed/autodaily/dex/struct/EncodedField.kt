package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils

class EncodedField {
    /**
     * struct encoded_field
     * {
     * uleb128 filed_idx_diff; // index into filed_ids for ID of this filed
     * uleb128 access_flags; // access flags like public, static etc.
     * }
     */
    var filedIdxDiff = 0
    var accessFlags = 0
    override fun toString(): String {
        return "field_idx_diff:" + ByteUtils.int2Hex(filedIdxDiff) +
            ",access_flags:" + ByteUtils.int2Hex(accessFlags)
    }

    companion object {
        @JvmStatic
        fun parser(src: ByteArray?, index: IntArray?): EncodedField {
            val encodedField = EncodedField()
            encodedField.filedIdxDiff = ByteUtils.readUleb128(src, index)
            encodedField.accessFlags = ByteUtils.readUleb128(src, index)
            return encodedField
        }
    }
}