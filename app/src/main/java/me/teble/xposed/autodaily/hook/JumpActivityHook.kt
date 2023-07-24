package me.teble.xposed.autodaily.hook

import android.app.Activity
import android.content.Intent
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import me.teble.xposed.autodaily.activity.module.ModuleActivity
import me.teble.xposed.autodaily.config.JUMP_ACTIVITY
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.utils.LogUtil

class JumpActivityHook : BaseHook() {

    companion object {
        const val JUMP_ACTION_CMD = "xa_jump_action_cmd"
    }

    override val isCompatible: Boolean
        get() = true

    override val enabled: Boolean
        get() = true

    @MethodHook("JumpActivity hook")
    fun hookJumpActivity() {
        runCatching {
            val method = findMethod(JUMP_ACTIVITY) {
                name == "doOnCreate"
            }
            method.hookAfter {
                val activity = it.thisObject as Activity
                val intent = activity.intent
                if (intent?.hasExtra(JUMP_ACTION_CMD) == true) {
                    activity.startActivity(Intent(activity, ModuleActivity::class.java))
                }
            }.unhook()
        }.onFailure {
            LogUtil.e(it, "JumpActivity hook failed")
        }
    }
}