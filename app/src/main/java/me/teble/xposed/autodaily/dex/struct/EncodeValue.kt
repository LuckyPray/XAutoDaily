package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils
import java.util.*


class EncodeValue {
    lateinit var valueType: VALUE
    var valueArg = 0
    lateinit var values: ByteArray
    var value: Number? = null
    var vBoolean: Boolean? = null
    var encodeArray: EncodeArray? = null
    var encodeAnnotation: EncodeAnnotation? = null
    fun findStrIdx(strIdx: Int): Boolean {
        if (valueType == VALUE.VALUE_STRING) {
            return value == strIdx
        } else if (valueType == VALUE.VALUE_ARRAY) {
            return encodeArray!!.findStrIdx(strIdx)
        } else if (valueType == VALUE.VALUE_ANNOTATION) {
            return encodeAnnotation!!.findStrIdx(strIdx)
        }
        return false
    }

    val strIdSet: Set<Int>
        get() {
            val set: MutableSet<Int> = HashSet()
            when (valueType) {
                VALUE.VALUE_STRING -> {
                    set.add((value as Long).toInt())
                }
                VALUE.VALUE_ARRAY -> {
                    set.addAll(encodeArray!!.strIdSet)
                }
                VALUE.VALUE_ANNOTATION -> {
                    set.addAll(encodeAnnotation!!.strIdSet)
                }
                else -> {}
            }
            return set
        }

    companion object {
        fun parser(src: ByteArray?, index: IntArray): EncodeValue {
            val encodeValue = EncodeValue()
            val b: Int = (ByteUtils.readByte(src, index).toInt() and 0xff)
            encodeValue.valueType = VALUE.valueOf(b and 0x1f)
            encodeValue.valueArg = b shr 5
            // 0x1d后的类型不存在value
            if (encodeValue.valueType.type <= 0x1d) {
                encodeValue.values = ByteUtils.copyByte(src, index[0], encodeValue.valueArg + 1)
                index[0] += encodeValue.valueArg + 1
                // TODO 浮点数等类型暂未解析
                encodeValue.value = ByteUtils.bytes2num(encodeValue.values)
            } else if (encodeValue.valueType == VALUE.VALUE_ARRAY) {
                encodeValue.encodeArray = EncodeArray.parser(src, index)
            } else if (encodeValue.valueType == VALUE.VALUE_ANNOTATION) {
                encodeValue.encodeAnnotation = EncodeAnnotation.parser(src, index)
            } else if (encodeValue.valueType == VALUE.VALUE_NULL) {
                encodeValue.value = null
            } else {
                encodeValue.vBoolean = encodeValue.valueArg == 1
            }
            return encodeValue
        }
    }
}