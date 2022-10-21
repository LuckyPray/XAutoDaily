package me.teble.xposed.autodaily.hook

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.compose.foundation.ExperimentalFoundationApi
import com.github.kyuubiran.ezxhelper.utils.Hooker
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.activity.module.ModuleActivity
import me.teble.xposed.autodaily.config.FormCommonSingleLineItem
import me.teble.xposed.autodaily.config.FormSimpleItem
import me.teble.xposed.autodaily.config.QQSettingSettingActivity
import me.teble.xposed.autodaily.config.QQSettingSettingFragment
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.ProcUtil
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.proxy.activity.injectRes
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.fieldValue
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
        val hooker: Hooker = { param ->
            try {
                val clazz: Class<*> = load(FormSimpleItem) ?: load(FormCommonSingleLineItem)!!
                val obj = param.thisObject
                var isFragment = false
                val activity = if (obj is Activity) {
                    obj
                } else {
                    isFragment = true
                    obj.invoke("getActivity") as Activity
                }
                val entity = clazz.new(activity) as View
                injectRes(activity.resources)
                entity.invoke("setLeftText", activity.getString(R.string.app_name))
                entity.invoke("setRightText", BuildConfig.VERSION_NAME)
                entity.setOnClickListener {
                    try {
                        val intent = Intent(activity, ModuleActivity::class.java)
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        LogUtil.e(e)
                    }
                }
                entity.setOnLongClickListener {
                    true
                }
                val id: Int = activity.resources
                    .getIdentifier("account_switch", "id", activity.packageName)
                val viewGroup: ViewGroup = if (isFragment) {
                    (obj.fieldValue(clazz) as View).parent as ViewGroup
                } else {
                    activity.findViewById<View>(id).parent as ViewGroup
                }
                try {
                    (viewGroup.parent as ViewGroup).addView(
                        entity, 0, ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    )
                } catch (e: Throwable) {
                    viewGroup.addView(
                        entity, 0, ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    )
                }
            } catch (e: Resources.NotFoundException) {
                ToastUtil.send("是否更新模块后未重启QQ?")
            } catch (e: Throwable) {
                LogUtil.e(e)
                ToastUtil.send("创建入口失败")
            }
        }
        findMethod(QQSettingSettingActivity) { name == "doOnCreate" }.hookAfter(52, hooker)
        findMethod(QQSettingSettingFragment) { name == "doOnCreateView" }.hookAfter(52, hooker)
    }
}