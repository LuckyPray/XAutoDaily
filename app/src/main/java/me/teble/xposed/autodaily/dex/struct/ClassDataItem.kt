package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils

class ClassDataItem {
    /**
     * uleb128 unsigned little-endian base 128
     * struct class_data_item
     * {
     * uleb128 static_fields_size;
     * uleb128 instance_fields_size;
     * uleb128 direct_methods_size;
     * uleb128 virtual_methods_size;
     * encoded_field static_fields [ static_fields_size ];
     * encoded_field instance_fields [ instance_fields_size ];
     * encoded_method direct_methods [ direct_method_size ];
     * encoded_method virtual_methods [ virtual_methods_size ];
     * }
     */
    //uleb128只用来编码32位的整型数
    var staticFieldsSize = 0
    var instanceFieldsSize = 0
    var directMethodsSize = 0
    var virtualMethodsSize = 0
    lateinit var staticFields: Array<EncodedField?>
    lateinit var instanceFields: Array<EncodedField?>
    lateinit var directMethods: Array<EncodedMethod?>
    lateinit var virtualMethods: Array<EncodedMethod?>
    override fun toString(): String {
        return """
            static_fields_size:$staticFieldsSize,instance_fields_size:$instanceFieldsSize,direct_methods_size:$directMethodsSize,virtual_methods_size:$virtualMethodsSize
            $fieldsAndMethods
            """.trimIndent()
    }

    private val fieldsAndMethods: String
        get() {
            val sb = StringBuilder()
            sb.append("static_fields:\n")
            for (static_field in staticFields) {
                sb.append(static_field).append("\n")
            }
            sb.append("instance_fields:\n")
            for (instance_field in instanceFields) {
                sb.append(instance_field).append("\n")
            }
            sb.append("direct_methods:\n")
            for (direct_method in directMethods) {
                sb.append(direct_method).append("\n")
            }
            sb.append("virtual_methods:\n")
            for (virtual_method in virtualMethods) {
                sb.append(virtual_method).append("\n")
            }
            return sb.toString()
        }

    companion object {
        fun parser(src: ByteArray?, index: Int): ClassDataItem {
            val item = ClassDataItem()
            val idx = intArrayOf(index)
            //        System.out.println(bytes2HexString(copyByte(src, index, 4)));
            item.staticFieldsSize = ByteUtils.readUleb128(src, idx)
            item.instanceFieldsSize = ByteUtils.readUleb128(src, idx)
            item.directMethodsSize = ByteUtils.readUleb128(src, idx)
            item.virtualMethodsSize = ByteUtils.readUleb128(src, idx)
            item.staticFields = arrayOfNulls(item.staticFieldsSize)
            item.instanceFields = arrayOfNulls(item.instanceFieldsSize)
            item.directMethods = arrayOfNulls(item.directMethodsSize)
            item.virtualMethods = arrayOfNulls(item.virtualMethodsSize)
            for (i in 0 until item.staticFieldsSize) {
                item.staticFields[i] = EncodedField.parser(src, idx)
            }
            for (i in 0 until item.instanceFieldsSize) {
                item.instanceFields[i] = EncodedField.parser(src, idx)
            }
            for (i in 0 until item.directMethodsSize) {
                item.directMethods[i] = EncodedMethod.parser(src, idx)
            }
            for (i in 0 until item.virtualMethodsSize) {
                item.virtualMethods[i] = EncodedMethod.parser(src, idx)
            }
            return item
        }
    }
}