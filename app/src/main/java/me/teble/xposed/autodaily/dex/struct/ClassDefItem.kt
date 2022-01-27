package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils
import java.util.*

class ClassDefItem {
    /**
     * struct class_def_item
     * {
     * uint class_idx;
     * uint access_flags;
     * uint superclass_idx;
     * uint interfaces_off;
     * uint source_file_idx;
     * uint annotations_off;
     * uint class_data_off;
     * uint static_value_off;
     * }
     */
    var classIdx = 0
    var accessFlags = 0
    var superclassIdx = 0
    var interfacesOff = 0
    var sourceFileIdx = 0
    var annotationsOff = 0
    var classDataOff = 0
    var staticValueOff = 0

    companion object {
        // class, field, method, ic
        const val ACC_PUBLIC = 0x00000001

        // field, method, ic
        const val ACC_PRIVATE = 0x00000002

        // field, method, ic
        const val ACC_PROTECTED = 0x00000004

        // field, method, ic
        const val ACC_STATIC = 0x00000008

        // class, field, method, ic
        const val ACC_FINAL = 0x00000010

        // method (only allowed on natives)
        const val ACC_SYNCHRONIZED = 0x00000020

        // class (not used in Dalvik)
        const val ACC_SUPER = 0x00000020

        // field
        const val ACC_VOLATILE = 0x00000040

        // method (1.5)
        const val ACC_BRIDGE = 0x00000040

        // field
        const val ACC_TRANSIENT = 0x00000080

        // method (1.5)
        const val ACC_VARARGS = 0x00000080

        // method
        const val ACC_NATIVE = 0x00000100

        // class, ic
        const val ACC_INTERFACE = 0x00000200

        // class, method, ic
        const val ACC_ABSTRACT = 0x00000400

        // method
        const val ACC_STRICT = 0x00000800

        // field, method, ic
        const val ACC_SYNTHETIC = 0x00001000

        // class, ic (1.5)
        const val ACC_ANNOTATION = 0x00002000

        // class, field, ic (1.5)
        const val ACC_ENUM = 0x00004000

        // method (Dalvik only)
        const val ACC_CONSTRUCTOR = 0x00010000

        // method (Dalvik only)
        const val ACC_DECLARED_SYNCHRONIZED = 0x00020000

        const val ACC_CLASS_MASK = (ACC_PUBLIC or ACC_FINAL or ACC_INTERFACE or ACC_ABSTRACT
            or ACC_SYNTHETIC or ACC_ANNOTATION or ACC_ENUM)
        const val ACC_INNER_CLASS_MASK =
            ACC_CLASS_MASK or ACC_PRIVATE or ACC_PROTECTED or ACC_STATIC
        const val ACC_FIELD_MASK =
            (ACC_PUBLIC or ACC_PRIVATE or ACC_PROTECTED or ACC_STATIC or ACC_FINAL
                or ACC_VOLATILE or ACC_TRANSIENT or ACC_SYNTHETIC or ACC_ENUM)
        const val ACC_METHOD_MASK =
            (ACC_PUBLIC or ACC_PRIVATE or ACC_PROTECTED or ACC_STATIC or ACC_FINAL
                or ACC_SYNCHRONIZED or ACC_BRIDGE or ACC_VARARGS or ACC_NATIVE
                or ACC_ABSTRACT or ACC_STRICT or ACC_SYNTHETIC or ACC_CONSTRUCTOR
                or ACC_DECLARED_SYNCHRONIZED)

        fun parserClassDefItems(src: ByteArray?): List<ClassDefItem> {
            val headerType = HeaderType.parser(src)
            val classDefsSize = headerType.classDefsSize
            val classDefsOff = headerType.classDefsOff
            val items: MutableList<ClassDefItem> = ArrayList()
            val idx = intArrayOf(classDefsOff)
            for (i in 0 until classDefsSize) {
                val classDefItem = ClassDefItem()
                classDefItem.classIdx = ByteUtils.readInt(src, idx)
                classDefItem.accessFlags = ByteUtils.readInt(src, idx)
                classDefItem.superclassIdx = ByteUtils.readInt(src, idx)
                classDefItem.interfacesOff = ByteUtils.readInt(src, idx)
                classDefItem.sourceFileIdx = ByteUtils.readInt(src, idx)
                classDefItem.annotationsOff = ByteUtils.readInt(src, idx)
                classDefItem.classDataOff = ByteUtils.readInt(src, idx)
                classDefItem.staticValueOff = ByteUtils.readInt(src, idx)
                items.add(classDefItem)
            }
            return items
        }
    }
}