package me.teble.xposed.autodaily.shizuku

import android.os.Bundle

/**
 * 保存一次手动唤醒检查的分步结果，随状态接口返回给模块应用。
 *
 * @param runId 本次检查标识
 */
internal class WakeDiagnostics(val runId: String) {
    var active = true
    private val values = Bundle()
    private var protectionChanged = false

    /**
     * 更新检查步骤，不将尚未执行的步骤视为成功。
     */
    fun step(name: String, result: String) {
        if (!active) return
        values.putString(name, result)
    }

    /**
     * 展示已确认的命令能力限制，同时保留缓存依据，不伪造新的命令执行记录。
     */
    fun unavailable(output: String) {
        if (!active) return
        values.putString("protection", "unsupported")
        values.putString("capability", "系统已明确返回命令缺失，防冻结已跳过：\n${output.take(2_048)}")
    }

    /**
     * 保留防冻结首次失败的原因，继续唤醒宿主时也不能将该步骤展示为成功。
     */
    fun protectionFailure(message: String) {
        if (!active) return
        if (values.getString("protection") != "unsupported") values.putString("protection", "failed")
        if (!values.containsKey("failure")) values.putString("failure", message)
    }

    /**
     * 记录实际命令的退出状态和输出，保留首次失败原因供用户复制反馈。
     */
    fun command(process: HostProcess, sticky: Boolean, result: AmUnfreezeController.Result) {
        if (!active) return
        val step = if (sticky) "protection" else "restore"
        if (sticky && result.success) protectionChanged = true
        if (!result.success) {
            val unsupported = Regex("(?im)(unknown command.*unfreeze|(?:unknown|unrecognized|invalid) option.*--sticky)")
                .containsMatchIn(result.output)
            values.putString(step, if (unsupported) "unsupported" else "failed")
        }
        val entry = buildString {
            appendLine("am unfreeze ${if (sticky) "--sticky " else ""}${process.pid} (${process.name})")
            appendLine("exitCode=${result.exitCode}, timedOut=${result.timedOut}, commandSucceeded=${result.success}")
            if (result.error.isNotBlank()) appendLine(result.error)
            appendLine(result.output.take(2_048).ifBlank { "（无输出）" })
        }
        values.putString("commands", (values.getString("commands").orEmpty() + entry).take(12_288))
    }

    /**
     * 完成检查并保存原始原因，恢复失败不会覆盖前面步骤的错误。
     */
    fun finish(message: String, success: Boolean, restored: Boolean) {
        if (!success && !values.containsKey("failure")) values.putString("failure", message)
        for (step in listOf("provider", "protection")) {
            if (values.getString(step) == "running") values.putString(step, "failed")
        }
        values.putString("restore", when {
            !restored -> "failed"
            values.getString("protection") == null -> "not_needed"
            values.getString("protection") == "unsupported" && !protectionChanged -> "not_needed"
            else -> "passed"
        })
        active = false
    }

    /**
     * 返回独立快照，防止后续恢复重试改变已经展示的检查结果。
     */
    fun snapshot(): Bundle = Bundle(values).apply { putString("runId", runId) }
}
