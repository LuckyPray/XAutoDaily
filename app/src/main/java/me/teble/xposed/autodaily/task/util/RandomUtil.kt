package me.teble.xposed.autodaily.task.util

import java.util.*

object RandomUtil {

    fun randHex(length: Int): String {
        val sb = StringBuilder()
        val random = Random()
        for (i in 0 until length) {
            val rd = random.nextInt(16)
            if (rd < 10) {
                sb.append((48 + rd).toChar())
            } else {
                sb.append((55 + rd).toChar())
            }
        }
        return sb.toString()
    }

    fun randLowerHex(length: Int): String {
        val sb = StringBuilder()
        val random = Random()
        for (i in 0 until length) {
            val rd = random.nextInt(16)
            if (rd < 10) {
                sb.append((48 + rd).toChar())
            } else {
                sb.append((87 + rd).toChar())
            }
        }
        return sb.toString()
    }
}