package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils

class StringDataItem {
    /**
     * struct string_data_item
     * {
     * uleb128 utf16_size;
     * ubyte data;
     * }
     */
    /**
     * 上述描述里提到了 LEB128 （ little endian base 128 ) 格式 ，是基于 1 个 Byte 的一种不定长度的
     * 编码方式 。若第一个 Byte 的最高位为 1 ，则表示还需要下一个 Byte 来描述 ，直至最后一个 Byte 的最高
     * 位为 0 。每个 Byte 的其余 Bit 用来表示数据
     */
    var size = 0
    lateinit var dataBytes: ByteArray
    lateinit var data: String
    override fun toString(): String {
        return "size=$size, data=$data"
    }

    companion object {
        @JvmStatic
        fun parser(src: ByteArray?, index: Int): StringDataItem {
            val item = StringDataItem()
            val idx = intArrayOf(index)
            item.size = ByteUtils.readUleb128(src, idx)
            item.dataBytes = ByteUtils.readByteString(src, idx)
            item.data = String(item.dataBytes)
            return item
        }
    }
}