package me.teble.xposed.autodaily.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.teble.xposed.autodaily.hook.utils.ClipboardUtil
import me.teble.xposed.autodaily.shizuku.WakeTestResult
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme.colors

/**
 * 展示可截图的唤醒检查结果，技术明细仅在展开或复制反馈时提供。
 */
@Composable
fun WakeDiagnosticDialog(result: WakeTestResult, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var expanded by remember(result.packageName) { mutableStateOf(false) }
    var copied by remember(result) { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${result.hostName} 唤醒检查") },
        text = {
            Column(
                modifier = Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(result.summary, color = colors.colorText, fontWeight = FontWeight.Medium)
                if (result.running) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = colors.themeColor)
                }
                result.steps.forEach { step ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(step.title, modifier = Modifier.weight(1f), fontSize = 14.sp)
                        Text(
                            step.result,
                            color = when (step.success) {
                                true -> colors.colorSuccess
                                false -> Color(0xFFE79338)
                                null -> colors.colorTextSecondary
                            },
                            fontSize = 14.sp
                        )
                    }
                }
                Text("只检查唤醒能力，不执行签到。可截图或复制结果反馈。", fontSize = 12.sp)
                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "收起诊断详情" else "查看诊断详情", color = colors.themeColor)
                }
                if (expanded) {
                    SelectionContainer {
                        Text(result.details, modifier = Modifier.padding(bottom = 8.dp), fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !result.running,
                onClick = {
                    ClipboardUtil.copy(context, result.details, "XAutoDaily 唤醒检查")
                    copied = true
                }
            ) {
                Text(if (copied) "已复制" else "复制诊断信息", color = colors.themeColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(if (result.running) "收起" else "关闭", color = colors.themeColor) }
        },
        containerColor = colors.colorBgDialog,
        titleContentColor = colors.colorText,
        textContentColor = colors.colorTextSecondary
    )
}
