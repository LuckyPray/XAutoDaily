package me.teble.xposed.autodaily.activity.common

import com.tencent.mmkv.MMKV
import me.teble.xposed.autodaily.hook.config.ConfProxy
import me.teble.xposed.autodaily.task.util.Const
import me.teble.xposed.autodaily.ui.theme.XAutodailyTheme
import me.teble.xposed.autodaily.utils.toCode
import me.teble.xposed.autodaily.utils.toTheme

object AppConfUnit {
    private val xaConfig by lazy {
        ConfProxy(MMKV.mmkvWithID("XAConfig", MMKV.MULTI_PROCESS_MODE))
    }

    var keepAlive: Boolean
        get() = xaConfig.getBoolean(Const.KEEP_ALIVE, false)
        set(value) = xaConfig.putBoolean(Const.KEEP_ALIVE, value)
    var qKeepAlive: Boolean
        get() = xaConfig.getBoolean(Const.Q_KEEP_ALIVE, false)
        set(value) = xaConfig.putBoolean(Const.Q_KEEP_ALIVE, value)
    var timKeepAlive: Boolean
        get() = xaConfig.getBoolean(Const.TIM_KEEP_ALIVE, false)
        set(value) = xaConfig.putBoolean(Const.TIM_KEEP_ALIVE, value)
    var untrustedTouchEvents: Boolean
        get() = xaConfig.getBoolean(Const.UNTRUSTED_TOUCH_EVENTS, false)
        set(value) = xaConfig.putBoolean(Const.UNTRUSTED_TOUCH_EVENTS, value)
    var hiddenAppIcon: Boolean
        get() = xaConfig.getBoolean(Const.HIDDEN_APP_ICON, false)
        set(value) = xaConfig.putBoolean(Const.HIDDEN_APP_ICON, value)


    var theme: XAutodailyTheme.Theme
        get() = xaConfig.getInt(Const.THEME, 0).toTheme()
        set(value) = xaConfig.putInt(Const.THEME, value.toCode())


    var blackTheme: Boolean
        get() = xaConfig.getBoolean(Const.BLACK_THEME, false)
        set(value) = xaConfig.putBoolean(Const.BLACK_THEME, value)
}