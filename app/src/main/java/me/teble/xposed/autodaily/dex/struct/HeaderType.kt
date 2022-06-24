package me.teble.xposed.autodaily.dex.struct

import me.teble.xposed.autodaily.dex.utils.ByteUtils


class HeaderType {
    /**
     * struct header_item
     * {
     * ubyte[8] magic;
     * unit checksum;
     * ubyte[20] siganature;
     * uint file_size;
     * uint header_size;
     * unit endian_tag;
     * uint link_size;
     * uint link_off;
     * uint map_off;
     * uint string_ids_size;
     * uint string_ids_off;
     * uint type_ids_size;
     * uint type_ids_off;
     * uint proto_ids_size;
     * uint proto_ids_off;
     * uint method_ids_size;
     * uint method_ids_off;
     * uint class_defs_size;
     * uint class_defs_off;
     * uint data_size;
     * uint data_off;
     * }
     */
    /**
     * 文件标识符
     */
    var magicMask = ByteArray(4)

    /**
     * 文件版本
     */
    var magicVersion = ByteArray(4)

    /**
     * 校验码
     */
    var checksum = 0

    /**
     * SHA-1签名
     */
    var siganature = ByteArray(20)

    /**
     * 文件大小
     */
    var fileSize = 0

    /**
     * 文件头大小
     */
    var headerSize = 0

    /**
     * 字节序交换标志
     */
    var endianTag = 0

    /**
     * 链接段大小
     */
    var linkSize = 0

    /**
     * 链接段偏移位置
     */
    var linkOff = 0

    /**
     * map数据偏移位置
     */
    var mapOff = 0

    /**
     * 字符串数量
     */
    var stringIdsSize = 0

    /**
     * 字符串偏移位置
     */
    var stringIdsOff = 0

    /**
     * 类数量
     */
    var typeIdsSize = 0

    /**
     * 类偏移位置
     */
    var typeIdsOff = 0

    /**
     * 方法原型数量
     */
    var protoIdsSize = 0

    /**
     * 方法原型偏移位置
     */
    var protoIdsOff = 0

    /**
     * 字段数量
     */
    var fieldIdsSize = 0

    /**
     * 字段偏移位置
     */
    var fieldIdsOff = 0

    /**
     * 方法数量
     */
    var methodIdsSize = 0

    /**
     * 方法偏移位置
     */
    var methodIdsOff = 0

    /**
     * 类定义数量
     */
    var classDefsSize = 0

    /**
     * 类定义偏移位置
     */
    var classDefsOff = 0

    /**
     * 数据段大小
     */
    var dataSize = 0

    /**
     * 数据段偏移位置
     */
    var dataOff = 0
    override fun toString(): String {
        return """
            文件标识符：${String(magicMask)}
            文件版本：${String(magicVersion)}
            校验码：$checksum (${ByteUtils.bytes2HexString(ByteUtils.int2Byte(checksum))})
            SHA-1签名: ${ByteUtils.bytes2HexString(siganature)}
            文件大小：$fileSize byte
            文件头大小：$headerSize byte
            字节序交换标志：$fileSize
            链接段大小：$linkSize byte
            链接段偏移位置：${ByteUtils.int2Hex(linkOff)}
            map数据偏移位置：${ByteUtils.int2Hex(mapOff)}
            字符串数量：$stringIdsSize
            字符串偏移位置：${ByteUtils.int2Hex(stringIdsOff)}
            类数量：$typeIdsSize(${ByteUtils.int2Hex(typeIdsSize)})
            类偏移位置：${ByteUtils.int2Hex(typeIdsOff)}
            方法原型数量：$protoIdsSize(${ByteUtils.int2Hex(protoIdsSize)})
            方法原型偏移位置：${ByteUtils.int2Hex(protoIdsOff)}
            字段数量：$fieldIdsSize(${ByteUtils.int2Hex(fieldIdsSize)})
            字段偏移位置：${ByteUtils.int2Hex(fieldIdsOff)}
            方法数量：$methodIdsSize(${ByteUtils.int2Hex(methodIdsSize)})
            方法偏移位置：${ByteUtils.int2Hex(methodIdsOff)}
            类定义数量：$classDefsSize(${ByteUtils.int2Hex(classDefsSize)})
            类定义偏移位置：${ByteUtils.int2Hex(classDefsOff)}
            数据段大小：$dataSize(${ByteUtils.int2Hex(dataSize)})
            数据段偏移位置: ${ByteUtils.int2Hex(dataOff)}
            """.trimIndent()
    }

    companion object {
        @JvmStatic
        fun parser(src: ByteArray?): HeaderType {
            val headerType = HeaderType()
            headerType.magicMask = ByteUtils.copyByte(src, 0, 4)
            headerType.magicVersion = ByteUtils.copyByte(src, 4, 4)
            headerType.checksum = ByteUtils.readInt(src, 8)
            headerType.siganature = ByteUtils.copyByte(src, 12, 20)
            headerType.fileSize = ByteUtils.readInt(src, 32)
            headerType.headerSize = ByteUtils.readInt(src, 36)
            headerType.endianTag = ByteUtils.readInt(src, 40)
            headerType.linkSize = ByteUtils.readInt(src, 44)
            headerType.linkOff = ByteUtils.readInt(src, 48)
            headerType.mapOff = ByteUtils.readInt(src, 52)
            headerType.stringIdsSize = ByteUtils.readInt(src, 56)
            headerType.stringIdsOff = ByteUtils.readInt(src, 60)
            headerType.typeIdsSize = ByteUtils.readInt(src, 64)
            headerType.typeIdsOff = ByteUtils.readInt(src, 68)
            headerType.protoIdsSize = ByteUtils.readInt(src, 72)
            headerType.protoIdsOff = ByteUtils.readInt(src, 76)
            headerType.fieldIdsSize = ByteUtils.readInt(src, 80)
            headerType.fieldIdsOff = ByteUtils.readInt(src, 84)
            headerType.methodIdsSize = ByteUtils.readInt(src, 88)
            headerType.methodIdsOff = ByteUtils.readInt(src, 92)
            headerType.classDefsSize = ByteUtils.readInt(src, 96)
            headerType.classDefsOff = ByteUtils.readInt(src, 100)
            headerType.dataSize = ByteUtils.readInt(src, 104)
            headerType.dataOff = ByteUtils.readInt(src, 108)
            return headerType
        }
    }
}
