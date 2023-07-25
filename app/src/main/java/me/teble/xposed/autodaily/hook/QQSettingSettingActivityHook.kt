package me.teble.xposed.autodaily.hook

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import me.teble.xposed.autodaily.hook.base.hostClassLoader
import me.teble.xposed.autodaily.hook.base.hostPackageName
import me.teble.xposed.autodaily.hook.base.load
import me.teble.xposed.autodaily.hook.proxy.activity.injectRes
import me.teble.xposed.autodaily.hook.utils.ToastUtil
import me.teble.xposed.autodaily.utils.LogUtil
import me.teble.xposed.autodaily.utils.dp2px
import me.teble.xposed.autodaily.utils.fieldValue
import me.teble.xposed.autodaily.utils.getMethods
import me.teble.xposed.autodaily.utils.invoke
import me.teble.xposed.autodaily.utils.invokeAs
import me.teble.xposed.autodaily.utils.new
import java.lang.reflect.Proxy
import kotlin.jvm.internal.DefaultConstructorMarker

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

    @SuppressLint("DiscouragedApi")
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
        runCatching {
            findMethod(QQSettingSettingActivity) { name == "doOnCreate" }.hookAfter(52, hooker)
            findMethod(QQSettingSettingFragment) { name == "doOnCreateView" }.hookAfter(52, hooker)
        }
    }

    @SuppressLint("DiscouragedApi")
    @MethodHook("创建模块设置入口 for 8.9.70+")
    private fun addModuleEntranceForMainSettingFragment() {
        // 8.9.70+
        val cMainSettingFragment = load("com.tencent.mobileqq.setting.main.MainSettingFragment")
        cMainSettingFragment ?: return
        try {
            val cMainSettingConfigProvider =
                load("com.tencent.mobileqq.setting.main.MainSettingConfigProvider")!!
            val methods = cMainSettingConfigProvider.getMethods(false)
            val returnTypes = methods.map { it.returnType }.distinct()
            if (returnTypes.size != 2) {
                throw IllegalStateException("returnTypes.size != 2")
            }
            val cSimpleItemProcessor = returnTypes.find { it.classes != List::class.java }!!
            val buildMethod = methods.findMethod { returnType != cSimpleItemProcessor }
            LogUtil.d("${cSimpleItemProcessor}, ${cSimpleItemProcessor.getMethods(false).toList()}")
            val listeners = cSimpleItemProcessor.getMethods(false)
                .filter {
                    runCatching {
                        LogUtil.d("${it} \n${it.returnType == Void.TYPE && it.parameterTypes.size == 1}\n${it.parameterTypes[0]}, ${kotlin.jvm.functions.Function0::class.java}")
                    }
                    it.returnType == Void.TYPE && it.parameterTypes.size == 1
                        && it.parameterTypes[0].name == kotlin.jvm.functions.Function0::class.java.name
                }
                .sortedBy { it.name }
            LogUtil.d("listeners: $listeners")
            if (listeners.size != 2) {
                throw IllegalStateException("listeners.size != 2, count: ${listeners.size}")
            }
            val mSimpleItemProcessorOnClickListener = listeners.first()
            buildMethod.hookAfter {
                val context = it.args.first() as Context
                val result = it.result as List<*>
                val resId: Int = context.resources.getIdentifier("qui_tuning", "drawable", hostPackageName)
                val item = cSimpleItemProcessor.new(
                    context,
                    R.id.setting2Activity_settingEntryItem,
                    "XAutoDaily",
                    resId
                )
                val function0 = mSimpleItemProcessorOnClickListener.parameterTypes.first()
                val unit = hostClassLoader.loadClass(Unit::class.java.name).fieldValue("INSTANCE")!!
                val proxy = Proxy.newProxyInstance(hostClassLoader, arrayOf(function0)) { _, method, args ->
                    if (method.name == "invoke") {
                        try {
                            val intent = Intent(context, ModuleActivity::class.java)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            LogUtil.e(e)
                        }
                    }
                    unit
                }
                mSimpleItemProcessorOnClickListener.invoke(item, proxy)
                val groupClass = result.first()!!::class.java
                LogUtil.d("groupClass: $groupClass")
                val constructor = groupClass.getConstructor(java.util.List::class.java, CharSequence::class.java, CharSequence::class.java,
                    Int::class.javaPrimitiveType, hostClassLoader.loadClass(DefaultConstructorMarker::class.java.name))
                val group = constructor.newInstance(listOf(item), null, null, 6, null)
                result.invoke("add", 0, group)
            }
        } catch (e: Throwable) {
            LogUtil.e(e)
            ToastUtil.send("新版设置创建入口失败")
        }
    }
}