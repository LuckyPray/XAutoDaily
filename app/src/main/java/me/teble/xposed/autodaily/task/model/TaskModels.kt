package me.teble.xposed.autodaily.task.model

import androidx.compose.runtime.StableMarker
import kotlinx.serialization.Serializable
import me.teble.xposed.autodaily.task.util.EnvFormatUtil
import me.teble.xposed.autodaily.utils.LogUtil
import java.math.BigInteger
@StableMarker
@Serializable
data class TaskProperties(
    // 配置文件版本
    val version: Int,
    // 最小支持app版本
    val minAppVersion: Int,
    // 配置更新日志
    val updateLogs: List<UpdateInfo>,
    // 任务组
    val taskGroups: List<TaskGroup>,
)
@StableMarker
@Serializable
data class UpdateInfo(
    val version: Int,
    val desc: String
)
@StableMarker
@Serializable
data class TaskGroup(
    // 组名
    val id: String,
    // 类型 mini, web，如果为小程序，尾部包含小程序id，
    // 例如：mini|1108057289|超级萌宠、mini|1108789561|情侣空间
    val type: String,
    // 预处理任务列表
    val preTasks: List<Task>,
    // 任务列表
    val tasks: List<Task>,
)
@StableMarker
@Serializable
data class Task(
    // 任务名
    val id: String,
    // 任务描述
    val desc: String,
    // 如果为 null，表示执行过程中可能会触发的依赖任务
    // 如果为 "basic"，表示基础任务
    // 如果非空，则为 cron 表达式（秒级）
    val cron: String?,
    // 依赖task的id，有些任务需要前置请求获取某些参数
    val relay: String?,
    // 依赖task的id，有些任务需要后置请求
    val rear: String?,
    // 环境变量，允许自定义内容
    val envs: List<TaskEnv>?,
    // 执行条件，如果为 null，表示无条件执行
    val conditions: List<TaskCondition>?,
    // 重复请求次数，允许通过环境变量自定义
    val repeat: String,
    // 延迟（秒）
    val delay: Int,
    // 请求URL
    val reqUrl: String,
    // 请求方法
    val reqMethod: String,
    // 请求头
    val reqHeaders: Map<String, String>?,
    // 请求体
    val reqData: String?,
    // 当前任务所需要访问的域名，如果为qq域名，则获取不同域下的cookie
    val domain: String?,
    // 请求回调
    val callback: TaskCallback
) {
    constructor(id: String): this(id, "", null, null, null, null, null, "1", 0, "", "", null, null, null,
        TaskCallback(null, null, null, null, null, null))
    val isRelayTask = cron == null
    val isBasic = cron == "basic"
    val isCronTask = !isRelayTask && !isBasic
}
@StableMarker
@Serializable
data class TaskEnv(
    // 变量名
    val name: String,
    // 类型 string, list(,分割), friend(唤醒勾选好友，本质也是list), group（同理）
    val type: String,
    // 最大限制，string类型对应长度，list对应列表长度，为0表示没有限制
    val limit: Int,
    // 默认值
    val default: String,
    // 变量描述
    val desc: String,
)
@StableMarker
@Serializable
data class TaskCallback(
    // 响应的data提取正则，如果为null，则表示不需要处理
    val dataRegex: String?,
    // 字段提取器列表
    val extracts: List<MsgExtract>?,
    // 判断任务是否执行成功的断言器，如果为空则使用http code
    val assert: Assert?,
    // 签到失败提示 eval string，如果为空则使用默认成功提示：执行成功
    val sucMsg: String?,
    // 签到失败提示 eval string，如果为空则使用默认错误提示：执行失败
    val errMsg: String?,
    // 签到提示的替换规则
    val replaces: List<MsgReplace>?
)
@StableMarker
@Serializable
data class MsgExtract(
    // headers/data，提取响应中返回的响应头或者响应体
    val from: String,
    // JSONPath 或者 正则表达式，JSONPath以$开头
    val match: String,
    // 保存的eval string的key
    val key: String,
    // 保存的值 eval string
    val value: String,
)
@StableMarker
@Serializable
data class Assert(
    // eval string
    val key: String,
    // 值 eval string
    val value: String,
)
@StableMarker
@Serializable
data class TaskCondition(
    // 任务执行条件，eval string
    val var1: String,
    // 判断类型
    val operator: String,
    // 任务执行条件值，eval string
    val var2: String,
)

fun isNumber(str: String): Boolean {
    return str.matches("-?[0-9]+".toRegex())
}

fun TaskCondition.test(env: MutableMap<String, Any>): Boolean {
    var v1 = if (env.containsKey(var1)) env[var1] else EnvFormatUtil.format(var1, env)
    var v2 = if (env.containsKey(var2)) env[var2] else EnvFormatUtil.format(var2, env)
    if (v1 == null) v1 = "null"
    if (v2 == null) v2 = "null"
    LogUtil.d("test: $v1 $operator $v2")
    return when (operator) {
        "=", "==" -> v1.toString() == v2.toString()
        "!=" -> v1.toString() != v2.toString()
        ">" -> if (isNumber(v1 as String) and isNumber(v2 as String)) BigInteger(v1) > BigInteger(v2) else v1.toString() > v2.toString()
        "<" -> if (isNumber(v1 as String) and isNumber(v2 as String)) BigInteger(v1) < BigInteger(v2) else v1.toString() < v2.toString()
        ">=" -> if (isNumber(v1 as String) and isNumber(v2 as String)) BigInteger(v1) >= BigInteger(v2) else v1.toString() >= v2.toString()
        "<=" -> if (isNumber(v1 as String) and isNumber(v2 as String)) BigInteger(v1) <= BigInteger(v2) else v1.toString() <= v2.toString()
        "in" -> {
            when (v2) {
                is Iterable<*> -> {
                    v2.contains(v1)
                }
                is String -> {
                    if (v2.startsWith("[") && v2.endsWith("]")) {
                        v2.substring(1, v2.length - 1).split(",").map { it.trim() }.contains(v1.toString())
                    } else {
                        v2.contains(v1.toString())
                    }
                }
                else -> {
                    throw IllegalArgumentException("var2 must be Iterable or String")
                }
            }
        }
        else -> throw IllegalArgumentException("unknown operator: $operator")
    }
}

@Serializable
data class MsgReplace(
    // 需要替换内容对应的正则表达式
    val match: String,
    // 替换字符串
    val replacement: String,
)