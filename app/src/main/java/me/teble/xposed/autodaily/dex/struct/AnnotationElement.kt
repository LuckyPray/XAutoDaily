package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils

class AnnotationElement {
    var nameIds = 0
    lateinit var encodeValue: EncodeValue

    companion object {
        @JvmStatic
        fun parser(src: ByteArray?, index: IntArray): AnnotationElement {
            val annotationElement = AnnotationElement()
            annotationElement.nameIds = ByteUtils.readUleb128(src, index)
            annotationElement.encodeValue = EncodeValue.parser(src, index)
            return annotationElement
        }
    }
}