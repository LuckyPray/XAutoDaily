package me.teble.xposed.autodaily.hook.utils

import java.io.*

object WupUtil {

    fun encode(arg4: ByteArray): ByteArray? {
        var v0: ByteArray? = null
        val v2 = ByteArrayOutputStream(arg4.size + 4)
        val v3 = DataOutputStream(v2)
        try {
            v3.writeInt(arg4.size + 4)
            v3.write(arg4)
            v0 = v2.toByteArray()
        } catch (ignored: Exception) {
        } finally {
            try {
                v2.close()
                v3.close()
            } catch (ignored: IOException) {
            }
        }
        return v0
    }

    fun decode(encodeByte: ByteArray?): ByteArray? {
        var v0: ByteArray? = null
        try {
            DataInputStream(ByteArrayInputStream(encodeByte)).use { v3 ->
                v0 = ByteArray(v3.readInt() - 4)
                v3.read(v0)
            }
        } catch (ignored: Exception) {
        }
        return v0
    }
}