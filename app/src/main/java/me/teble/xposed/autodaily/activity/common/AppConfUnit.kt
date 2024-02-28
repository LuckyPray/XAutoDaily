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

    var theme: XAutodailyTheme.Theme
        get() = xaConfig.getInt(Const.THEME, 0).toTheme()
        set(value) = xaConfig.putInt(Const.THEME, value.toCode())


    var blackTheme: Boolean
        get() = xaConfig.getBoolean(Const.BLACK_THEME, false)
        set(value) = xaConfig.putBoolean(Const.BLACK_THEME, value)
}