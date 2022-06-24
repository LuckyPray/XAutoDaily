package me.teble.xposed.autodaily.hook

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.compose.foundation.ExperimentalFoundationApi
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.activity.module.ModuleActivity
import me.teble.xposed.autodaily.config.FormCommonSingleLineItem
import me.teble.xposed.autodaily.config.FormSimpleItem
import me.teble.xposed.autodaily.config.QQSettingSettingActivity
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.proxy.activity.injectRes
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.invoke
import me.teble.xposed.autodaily.utils.new

/**
 * @author teble
 * @date 2021/6/5 0:47
 * @description
 */
class QQSettingSettingActivityHook : BaseHook() {

    override val isCompatible: Boolean
        get() = ProcUtil.isMain

    override val enabled: Boolean
        get() = true

    @ExperimentalFoundationApi
    @MethodHook("创建模块设置入口")
    private fun addModuleEntrance() {
        findMethod(QQSettingSettingActivity) { name == "doOnCreate" }.hookAfter(52) { param ->
            try {
                val clazz: Class<*> = load(FormSimpleItem) ?: load(FormCommonSingleLineItem)!!
                val context = param.thisObject as Activity
                val entity = clazz.new(context) as View
                injectRes(context.resources)
                entity.invoke("setLeftText", context.getString(R.string.app_name))
                entity.invoke("setRightText", BuildConfig.VERSION_NAME)
                entity.setOnClickListener {
                    try {
                        val intent = Intent(context, ModuleActivity::class.java)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        LogUtil.e(e)
                    }
                }
                entity.setOnLongClickListener {
                    true
                }
                val id: Int = context.resources
                    .getIdentifier("account_switch", "id", context.packageName)
                val viewGroup: ViewGroup = context.findViewById<View>(id).parent as ViewGroup
                try {
                    (viewGroup.parent as ViewGroup).addView(
                        entity, 0, ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    )
                } catch (e: Throwable) {
                    viewGroup.addView(
                        entity, 0, ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    )
                }
            } catch (e: Throwable) {
                LogUtil.e(e)
                ToastUtil.send("创建入口失败")
            }
        }
    }
}