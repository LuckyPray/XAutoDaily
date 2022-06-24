package me.teble.xposed.autodaily.dex.struct

enum class VALUE(
    var type: Int
) {
    VALUE_BYTE(0x00),
    VALUE_SHORT(0x02),
    VALUE_CHAR(0x03),
    VALUE_INT(0x04),
    VALUE_LONG(0x06),
    VALUE_FLOAT(0x10),
    VALUE_DOUBLE(0x11),
    VALUE_METHOD_TYPE(0x15),
    VALUE_METHOD_HANDLE(0x16),
    VALUE_STRING(0x17),
    VALUE_TYPE(0x18),
    VALUE_FIELD(0x19),
    VALUE_METHOD(0x1a),
    VALUE_ENUM(0x1b),
    VALUE_ARRAY(0x1c),
    VALUE_ANNOTATION(0x1d),
    VALUE_NULL(0x1e),
    VALUE_BOOLEAN(0x1f);

    companion object {
        @JvmStatic
        fun valueOf(type: Int): VALUE {
            for (value in values()) {
                if (type == value.type) {
                    return value
                }
            }
            throw RuntimeException("")
        }
    }
}
