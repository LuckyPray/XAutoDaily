package me.teble.xposed.autodaily.task.model

import kotlinx.serialization.Serializable

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

@Serializable
data class UpdateInfo(
    val version: Int,
    val desc: String
)

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
    // 环境变量，允许自定义内容
    val envs: List<TaskEnv>?,
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
)
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
@Serializable
data class Assert(
    // eval string
    val key: String,
    // 值 eval string
    val value: String,
)
@Serializable
data class MsgReplace(
    // 需要替换内容对应的正则表达式
    val match: String,
    // 替换字符串
    val replacement: String,
)