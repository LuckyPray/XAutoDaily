package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils

class StringIdsItem {
    /**
     * struct string_ids_item
     * {
     * uint string_data_off;
     * }
     */
    var stringDataOff = 0
    override fun toString(): String {
        return "" + stringDataOff
        //        return bytesToHexString(int2Byte(stringDataOff));
    }

    companion object {
        fun parserStringIdsItems(src: ByteArray?): List<StringIdsItem> {
            val headerType = HeaderType.parser(src)
            val idsSize = headerType.stringIdsSize
            val idsOff = headerType.stringIdsOff
            val items: MutableList<StringIdsItem> = ArrayList()
            for (i in 0 until idsSize) {
                val stringIdsItem = StringIdsItem()
                stringIdsItem.stringDataOff =
                    ByteUtils.byte2int(ByteUtils.copyByte(src, idsOff + i * 4, 4))
                items.add(stringIdsItem)
            }
            return items
        }
    }
}