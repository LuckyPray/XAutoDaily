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
//                    val cBaseService = load("com.tencent.mobileqq.service.MobileQQServiceBase")!!
//                    lateinit var mRealHandleReq: Method
//                    cBaseService.getMethods(false).forEach {
//                        if (it.returnType == Void.TYPE && it.isPublic) {
//                            val paramTypes = it.parameterTypes
//                            if (paramTypes.size >= 2
//                                && paramTypes.first() == ToServiceMsg::class.java
//                                && paramTypes.last() == Class::class.java) {
//                                mRealHandleReq = it
//                                LogUtil.d(it.toString())
//                            }
//                        }
//                    }
//                    val toServiceMsg: ToServiceMsg = ToServiceMsg(
//                        "mobileqq.service",
//                        "${QApplicationUtil.currentUin}", "VisitorSvc.ReqFavorite"
//                    ).apply {
//                        appSeq = 1234
//                        timeout = 10000L
//                        extraData.putLong("selfUin", QApplicationUtil.currentUin)
//                        extraData.putLong("targetUin", qq)
//                        extraData.putByteArray("vCookies", null)
//                        // 好友1，陌生人66 陌生人能突破非会员10次上限
//                        extraData.putInt("favoriteSource", 66)
//                        extraData.putInt("iCount", 1)
//                        extraData.putInt("from", 0)
//                    }
//                    kotlin.runCatching {
//
//                        appInterface.getFields(false).forEach {
//                            if (cBaseService.isAssignableFrom(it.type)) {
//                                LogUtil.d(it.name)
//                                it.isAccessible = true
//                                val mqqService = it.get(appInterface)!!
//                                if (mRealHandleReq.parameterTypes.size == 2) {
//                                    MethodHandleUtil.invokeSpecial<Unit>(mqqService, mRealHandleReq, toServiceMsg, FavoriteServlet::class.java)
//                                } else {
//                                    MethodHandleUtil.invokeSpecial<Unit>(mqqService, mRealHandleReq, toServiceMsg, null, FavoriteServlet::class.java)
//                                }
//                            }
//                        }
//                    }.onFailure { LogUtil.e(it) }
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