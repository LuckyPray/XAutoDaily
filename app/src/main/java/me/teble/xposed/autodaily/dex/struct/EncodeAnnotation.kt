package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils

class EncodeAnnotation {
    var typeIds = 0
    var size = 0
    lateinit var annotationElements: Array<AnnotationElement>
    fun findStrIdx(strIdx: Int): Boolean {
        for (element in annotationElements) {
            if (element.encodeValue.findStrIdx(strIdx)) {
                return true
            }
        }
        return false
    }

    val strIdSet: Set<Int>
        get() {
            val set: MutableSet<Int> = HashSet()
            for (element in annotationElements) {
                set.addAll(element.encodeValue.strIdSet)
            }
            return set
        }

    companion object {
        fun parser(src: ByteArray?, index: IntArray): EncodeAnnotation {
            val encodeAnnotation = EncodeAnnotation()
            encodeAnnotation.typeIds = ByteUtils.readUleb128(src, index)
            encodeAnnotation.size = ByteUtils.readUleb128(src, index)
            for (i in 0 until encodeAnnotation.size) {
                encodeAnnotation.annotationElements[i] = AnnotationElement.parser(src, index)
            }
            return encodeAnnotation
        }
    }
}
