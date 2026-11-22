package me.teble.xposed.autodaily.shizuku

import android.os.Build
import android.os.Bundle
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.config.PACKAGE_NAME_QQ
import java.time.LocalDateTime

/**
 * 用于截图和复制反馈的唤醒检查结果，不包含账号、任务内容和登录凭据。
 */
data class WakeTestResult(
    val packageName: String,
    val running: Boolean,
    val summary: String,
    val steps: List<Step>,
    val details: String,
) {
    val hostName: String get() = if (packageName == PACKAGE_NAME_QQ) "QQ" else "TIM"

    /**
     * 单项检查状态；尚未执行的步骤不显示为成功。
     */
    data class Step(val title: String, val result: String, val success: Boolean? = null)

    companion object {
        /**
         * 将守护返回的状态转换为用户可读结果，并保留完整诊断上下文。
         */
        fun from(
            packageName: String,
            state: Bundle? = null,
            running: Boolean = true,
            daemonConnected: Boolean = false,
            message: String? = null,
            error: String = "",
            runId: String? = null,
        ): WakeTestResult {
            val hostName = if (packageName == PACKAGE_NAME_QQ) "QQ" else "TIM"
            val ownResult = runId == null || state?.getString("runId") == runId
            val diagnostic = state?.getBundle("wakeDiagnostic")
                ?.takeIf { ownResult && it.getString("runId") == state.getString("runId") }
            val connected = state?.getBoolean("connected") == true
            val ready = state?.getBoolean("hostReady") == true
            val handshake = state?.getBundle("handshake")
            val summary = message ?: when {
                running && !daemonConnected -> "正在连接任务守护"
                running -> "正在检查 $hostName 唤醒能力"
                !ownResult -> "$hostName 正在唤醒或执行任务，请稍后再试"
                state?.getString("state") == "ready" -> "$hostName 唤醒检查通过"
                state?.getString("state") == "partial" -> if (diagnostic?.getString("protection") == "unsupported"
                    || state.getBoolean("unfreezeUnsupported")) "$hostName 已唤醒，当前系统不支持任务防冻结"
                    else "$hostName 已唤醒，防冻结未能开启，签到仍可正常执行"
                diagnostic?.getString("provider") == "failed" -> "$hostName 唤醒请求未完成"
                !connected && diagnostic?.getString("provider") == "passed" ->
                    "$hostName 进程已找到，守护会话未建立"
                connected && !ready -> "$hostName 已连接，账号与模块就绪尚未确认"
                state?.getString("message")?.contains("超时") == true ->
                    "未等到 $hostName 就绪，请打开 $hostName 后重试"
                diagnostic?.getString("protection") == "unsupported" ->
                    "${if (connected) "$hostName 已连接，" else ""}当前系统不支持任务防冻结"
                diagnostic?.getString("protection") == "failed" ->
                    "${if (connected) "$hostName 已连接，" else ""}任务防冻结未通过"
                diagnostic?.getString("restore") == "failed" -> "防冻结恢复尚未确认"
                else -> "$hostName 唤醒检查未通过"
            }
            val steps = listOf(
                Step("任务守护", if (daemonConnected) "已连接" else if (running) "连接中" else "未连接",
                    if (daemonConnected) true else if (running) null else false),
                Step("$hostName 连接", if (state == null) "尚未检查" else if (connected) "已连接" else "未连接",
                    state?.let { connected }),
                Step("账号与模块", if (!connected || state?.containsKey("hostReady") != true) "尚未确认"
                    else if (ready) "已就绪" else "未就绪", if (connected && state?.containsKey("hostReady") == true) ready else null),
                step("唤醒请求", diagnostic?.getString("provider"), "已完成", "未完成"),
                step("任务防冻结", diagnostic?.getString("protection"), "命令已执行", "未通过"),
                step("恢复冻结策略", diagnostic?.getString("restore"), "已恢复", "未确认"),
            )
            val details = buildString {
                appendLine("XAutoDaily 唤醒检查")
                appendLine("时间：${LocalDateTime.now()}")
                appendLine("模块：${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                appendLine("系统：Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                appendLine("设备：${Build.MANUFACTURER} ${Build.MODEL}")
                appendLine("宿主：$packageName")
                state?.let {
                    val uid = it.getInt("daemonUid", -1)
                    appendLine("守护身份：${when (uid) { 0 -> "root"; 2000 -> "ADB"; else -> "未知" }} (uid=$uid)")
                    appendLine("守护 PID：${it.getInt("daemonPid", -1)}")
                    if (it.containsKey("daemonInstance")) {
                        appendLine("守护实际版本：${it.getString("daemonVersionName")} (${it.getInt("daemonVersionCode")})")
                        appendLine("守护协议：${it.getInt("daemonProtocol")}")
                        appendLine("守护实例：${it.getString("daemonInstance")}")
                        appendLine("守护启动时间戳：${it.getLong("daemonStartedAt")}")
                        appendLine("守护安装更新时间戳：${it.getLong("daemonUpdatedAt")}")
                        appendLine("守护加载路径：${it.getString("daemonApkPath")}")
                        appendLine("当前会话标识：${it.getString("sessionId") ?: "无"}")
                        appendLine("当前会话宿主 PID：${it.getInt("hostPid", -1)}")
                    }
                    appendLine("检查标识：${it.getString("runId").orEmpty()}")
                    appendLine("原始状态：${it.getString("state").orEmpty()}")
                    appendLine("原始原因：${it.getString("message").orEmpty()}")
                    appendLine("仍有恢复待确认：${it.getBoolean("pendingRestore")}")
                    if (!it.containsKey("wakeDiagnostic")) appendLine("当前守护未提供分步诊断，请停止守护后重新运行。")
                }
                appendLine("结果：$summary")
                steps.forEach { appendLine("${it.title}：${it.result}") }
                diagnostic?.getString("failure")?.let { appendLine("最初失败原因：$it") }
                diagnostic?.getString("capability")?.let { appendLine("防冻结能力：$it") }
                handshake?.let {
                    val stage = when (it.getString("stage")) {
                        "offered" -> "已尝试发送，尚未收到宿主回连"
                        "received" -> "宿主已回连，正在验证会话"
                        "connected" -> "会话已建立，等待就绪确认"
                        "ready" -> "宿主已确认就绪"
                        "closed" -> "会话已关闭"
                        "failed" -> "交接失败"
                        else -> "未检查"
                    }
                    appendLine("最近发现的宿主 PID：${it.getInt("hostPid", -1)}")
                    appendLine("连接交接：$stage")
                    if (it.containsKey("hostVersionCode")) {
                        appendLine("宿主回报的模块版本：${it.getString("hostVersionName")} (${it.getInt("hostVersionCode")})")
                    }
                    if (it.containsKey("broadcastResult")) appendLine("交接广播返回码：${it.getInt("broadcastResult")}（0 仅表示系统接受）")
                    it.getString("error")?.let { reason -> appendLine("最近交接原因：$reason") }
                    it.getString("events")?.let { events -> appendLine("\n连接交接记录：\n$events") }
                }
                if (error.isNotBlank()) appendLine("异常：$error")
                diagnostic?.getString("commands")?.let { appendLine("\n系统命令记录：\n$it") }
            }
            return WakeTestResult(packageName, running, summary, steps, details)
        }

        /**
         * 统一转换单项状态，未知状态保留为未检查。
         */
        private fun step(title: String, state: String?, passed: String, failed: String): Step = when (state) {
            "passed" -> Step(title, passed, true)
            "failed" -> Step(title, failed, false)
            "unsupported" -> Step(title, "系统不支持", false)
            "running" -> Step(title, "检查中")
            "not_needed" -> Step(title, "无需恢复")
            else -> Step(title, "未检查")
        }
    }
}
