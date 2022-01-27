package me.teble.xposed.autodaily.task.request.enum

enum class ReqType {
    HTTP,
    XA_FUNC
    ;

    companion object {
        fun getType(type: String): ReqType {
            return when(type.lowercase()) {
                "web", "mini" -> HTTP
                "func" -> XA_FUNC
                else -> throw RuntimeException("未知的type: $type")
            }
        }
    }
}