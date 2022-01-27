package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils

class CodeItem {
    /**
     * struct code_item
     * {
     * ushort registers_size;
     * ushort ins_size;
     * ushort outs_size;
     * ushort tries_size;
     * uint debug_info_off;
     * uint insns_size;
     * ushort insns [ insns_size ];
     * ushort paddding; // optional
     * try_item tries [ tyies_size ]; // optional
     * encoded_catch_handler_list handlers; // optional
     * }
     */
    var registersSize: Short = 0
    var insSize: Short = 0
    var outsSize: Short = 0
    var triesSize: Short = 0
    var debugInfoOff = 0
    var insnsSize = 0
    lateinit var insns: ByteArray
    override fun toString(): String {
        return """
            regsize:$registersSize,ins_size:$insSize,outs_size:$outsSize,tries_size:$triesSize,debug_info_off:$debugInfoOff,insns_size:$insnsSize
            insns:${ByteUtils.bytes2HexString(insns)}
            """.trimIndent()
    }

    companion object {
        fun parser(src: ByteArray?, index: Int): CodeItem {
            val codeItem = CodeItem()
            if (index == 0) {
                codeItem.insns = ByteArray(0)
                return codeItem
            }
            val idx = intArrayOf(index)
            codeItem.registersSize = ByteUtils.readShort(src, idx)
            codeItem.insSize = ByteUtils.readShort(src, idx)
            codeItem.outsSize = ByteUtils.readShort(src, idx)
            codeItem.triesSize = ByteUtils.readShort(src, idx)
            codeItem.debugInfoOff = ByteUtils.readInt(src, idx)
            codeItem.insnsSize = ByteUtils.readInt(src, idx)
            codeItem.insns = ByteUtils.copyByte(src, idx[0], codeItem.insnsSize * 2)
            return codeItem
        }
    }
}