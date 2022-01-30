package me.teble.xposed.autodaily.hook

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import me.teble.xposed.autodaily.BuildConfig
import me.teble.xposed.autodaily.R
import me.teble.xposed.autodaily.activity.module.ModuleActivity
import me.teble.xposed.autodaily.config.QQClasses.Companion.FormCommonSingleLineItem
import me.teble.xposed.autodaily.config.QQClasses.Companion.FormSimpleItem
import me.teble.xposed.autodaily.config.QQClasses.Companion.QQSettingSettingActivity
import me.teble.xposed.autodaily.hook.annotation.MethodHook
import me.teble.xposed.autodaily.hook.base.BaseHook
import me.teble.xposed.autodaily.hook.base.Global.hostContext
import me.teble.xposed.autodaily.hook.base.Global.hostProcessName
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.*

/**
 * @author teble
 * @date 2021/6/5 0:47
 * @description
 */
class QQSettingSettingActivityHook : BaseHook() {

    override val isCompatible: Boolean
        get() = hostProcessName == ""

    override val enabled: Boolean
        get() = true

    @ExperimentalFoundationApi
    @MethodHook("创建模块设置入口")
    private fun addModuleEntrance() {
        findMethod(QQSettingSettingActivity) { name == "doOnCreate" }.hookAfter(52) { param ->
            val clazz: Class<*> = load(FormSimpleItem) ?: load(FormCommonSingleLineItem)!!
            val item: View = param.thisObject.fieldValueAs(clazz)!!
            val context = param.thisObject as Activity
            val entity = clazz.new(context) as View
            entity.invoke("setLeftText", hostContext.getString(R.string.app_name))
            entity.invoke("setRightText", BuildConfig.VERSION_NAME)
            (item.parent as LinearLayout).apply {
                addView(entity, 0)
            }
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
        }
    }
}