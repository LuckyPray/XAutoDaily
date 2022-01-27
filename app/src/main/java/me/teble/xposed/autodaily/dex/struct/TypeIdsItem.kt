package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils

class TypeIdsItem {
    /**
     * struct type_ids_item
     * {
     * uint descriptor_idx;
     * }
     */
    var descriptorIdx = 0
    override fun toString(): String {
        return ByteUtils.int2Hex(descriptorIdx)
    }

    companion object {
        fun parser(src: ByteArray?, index: Int): TypeIdsItem {
            val item = TypeIdsItem()
            item.descriptorIdx = ByteUtils.readInt(src, index)
            return item
        }

        fun parserAndGet(src: ByteArray?, index: Int): TypeIdsItem {
            val headerType = HeaderType.parser(src)
            val size = headerType.typeIdsSize
            if (index > size) {
                throw RuntimeException("超过可选范围")
            }
            val off = headerType.typeIdsOff
            return parser(src, off + index * 4)
        }
    }
}