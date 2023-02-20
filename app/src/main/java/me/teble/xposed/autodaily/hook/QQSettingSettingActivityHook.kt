package me.teble.xposed.autodaily.hook

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
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
import me.teble.xposed.autodaily.utils.dp2px
import me.teble.xposed.autodaily.utils.invoke
import me.teble.xposed.autodaily.utils.invokeAs
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
                val entityRoot = LinearLayout(activity)
                val entityRootLayoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 1).apply {
                    topMargin = 20.dp2px(activity)
                }
                entityRoot.layoutParams = entityRootLayoutParams
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
                    obj.invokeAs<View>("findViewById", id)!!.parent as ViewGroup
                } else {
                    activity.findViewById<View>(id).parent as ViewGroup
                }
                entityRoot.addView(entity, entityRootLayoutParams)
                try {
                    (viewGroup.parent as ViewGroup).addView(
                        entityRoot, 0, ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                    )
                } catch (e: Throwable) {
                    viewGroup.addView(
                        entityRoot, 0, ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
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