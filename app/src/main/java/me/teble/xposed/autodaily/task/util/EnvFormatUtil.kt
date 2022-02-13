package me.teble.xposed.autodaily.task.util

import cn.hutool.core.codec.Base64
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.ReUtil
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool.miniLoginManager
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool.miniProfileManager
import me.teble.xposed.autodaily.hook.function.proxy.FunctionPool.ticketManager
import me.teble.xposed.autodaily.hook.utils.QApplicationUtil.currentUin
import me.teble.xposed.autodaily.hook.utils.VersionUtil
import me.teble.xposed.autodaily.task.model.MiniProfile
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.toJsonString
import java.util.*
import java.util.regex.Pattern
import java.util.regex.Pattern.DOTALL

object EnvFormatUtil {

    private const val TAG = "EnvFormatUtil"

    private val ARG_REG = Pattern.compile("(\\\$\\{.*?\\}|\\\$[a-zA-Z0-9\\_]+)", DOTALL)
    private val FUN_REG = Pattern.compile("\\(.*\\)")

    fun format(evalStr: String, env: MutableMap<String, Any>): String {
        return format(evalStr, null, env)
    }

    fun format(evalStr: String, qDomain: String?, env: MutableMap<String, Any>): String {
        return formatList(evalStr, qDomain, env)[0]
    }

    fun formatList(evalStr: String, env: MutableMap<String, Any>): List<String> {
        return formatList(evalStr, null, env)
    }

    fun formatList(evalStr: String, qDomain: String?, env: MutableMap<String, Any>): List<String> {
        val values = mutableListOf<Any>()
        val args = ReUtil.findAllGroup1(ARG_REG, evalStr).apply {
            LogUtil.d(TAG, "regex find result -> ${this.toJsonString()}")
            forEachIndexed { index, s ->
                val start = if (s.startsWith("\${")) 2 else 1
                val end = if (s.endsWith("}")) s.length - 1 else s.length
                val name = s.substring(start, end)
                LogUtil.d(TAG, "name -> $name")
                this[index] = name
                values.add(getFormatArgValue(name, qDomain, env))
            }
        }
        LogUtil.d(TAG, "evalString -> $evalStr")
        LogUtil.d(TAG, "formatArgs -> ${args.toJsonString()}")
        LogUtil.d(TAG, "formatValues -> ${values.toJsonString()}")
        val formatStr = ReUtil.replaceAll(evalStr, ARG_REG, "%s")
        val resList: List<String>
        if (args.isEmpty()) {
            resList = listOf(evalStr)
        } else {
            var firstListSize = -1
            for (value in values) {
                if (value is List<*>) {
                    if (firstListSize == -1) firstListSize = value.size
                    else if (firstListSize != value.size) {
                        throw RuntimeException("参数列表长度不一致")
                    }
                }
            }
            if (firstListSize == -1) {
                resList = listOf(String.format(formatStr, *values.toTypedArray()))
            } else {
                resList = mutableListOf<String>().apply {
                    for (i in 0 until firstListSize) {
                        val lis = mutableListOf<String>()
                        for (value in values) {
                            if (value is List<*>) {
                                lis.add(value[i] as String)
                            } else {
                                lis.add(value as String)
                            }
                        }
                        add(String.format(formatStr, *lis.toTypedArray()))
                    }
                }
            }
        }
        LogUtil.d(TAG, "format -> $resList")
        return resList
    }

    private fun getFormatArgFunction(
        argFunc: String,
        qDomain: String?,
        env: MutableMap<String, Any>
    ): String {
        when {
            argFunc.startsWith("randInt(") -> {
                val tmpStr = argFunc.substring(8, argFunc.length - 1)
                val strings = tmpStr.split(",\\s*").toTypedArray()
                return when (strings.size) {
                    1 -> {
                        val integer = strings[0].toInt()
                        (Random().nextInt(integer) + 1).toString()
                    }
                    2 -> {
                        val start = strings[0].toInt()
                        val end = strings[1].toInt()
                        (Random().nextInt(end - start + 1) + start).toString()
                    }
                    else -> {
                        throw RuntimeException("表达式错误: $argFunc")
                    }
                }
            }
            argFunc.startsWith("randHex(") -> {
                val tmpStr = argFunc.substring(8, argFunc.length - 1)
                return RandomUtil.randHex(tmpStr.toInt())
            }
            argFunc.startsWith("randLowerHex(") -> {
                val tmpStr = argFunc.substring(13, argFunc.length - 1)
                return RandomUtil.randLowerHex(tmpStr.toInt())
            }
            argFunc.startsWith("encBase64(") -> {
                val tmpStr = argFunc.substring(10, argFunc.length - 1)
                val str = format(tmpStr, qDomain, env)
                return Base64.encode(str)
            }
            argFunc.startsWith("decBase64(") -> {
                val tmpStr = argFunc.substring(10, argFunc.length - 1)
                val str = format("\$$tmpStr", qDomain, env)
                return Base64.decodeStr(str)
            }
            else -> throw RuntimeException("没有找到对应的函数: $argFunc")
        }
    }

    private fun getFormatArgValue(
        argField: String,
        qDomain: String?,
        env: MutableMap<String, Any>
    ): Any {
        if (ReUtil.contains(FUN_REG, argField)) {
            return getFormatArgFunction(argField, qDomain, env)
        }
        val res = when (argField) {
            "week_day_index" -> buildString { append((DateUtil.dayOfWeek(Date()) + 5) % 7) }
            "week_day" -> buildString { append((DateUtil.dayOfWeek(Date()) + 5) % 7 + 1) }
            "random" -> CalculationUtil.getRandom().toString()
            "microsecond" -> CalculationUtil.getMicrosecondTime().toString()
            "second" -> CalculationUtil.getSecondTime().toString()
            "uin" -> currentUin.toString()
            "qua" -> VersionUtil.qua
            "csrf_token" -> CalculationUtil.getCSRFToken(ticketManager.getSkey() ?: "")
            "bkn" -> {
                val skey = env["skey"] ?: let {
                    env["skey"] = ticketManager.getSkey()
                        ?: throw RuntimeException("获取skey失败")
                    env["skey"]
                }
                CalculationUtil.getBkn(skey as String).toString()
            }
            "ps_tk" -> {
                val pskey = env["pskey"] ?: let {
                    env["pskey"] = ticketManager.getPskey(qDomain ?: "")
                        ?: throw RuntimeException("获取pskey失败")
                    env["pskey"]
                }
                CalculationUtil.getPsToken(pskey as String).toString()
            }
            "client_key" -> ticketManager.getStweb() ?: ""
            "mini_nick" -> {
                val miniProfile = (env["mini_profile"] ?: let {
                    val profile = miniProfileManager.syncGetProfile(env["mini_app_id"] as String)
                        ?: throw RuntimeException("获取mini_profile失败")
                    env["mini_profile"] = profile
                    profile
                }) as MiniProfile
                miniProfile.nick
            }
            "mini_avatar" -> {
                val miniProfile = (env["mini_profile"] ?: let {
                    val profile = miniProfileManager.syncGetProfile(env["mini_app_id"] as String)
                        ?: throw RuntimeException("获取mini_profile失败")
                    env["mini_profile"] = profile
                    profile
                }) as MiniProfile
                miniProfile.avatar
            }
            "mini_login_code" -> miniLoginManager.syncGetLoginCode(env["mini_app_id"] as String)
                ?: throw RuntimeException("获取mini_login_code失败")
            else -> env[argField] ?: throw RuntimeException("不存在的变量 $argField")
        }
        return res
    }
}