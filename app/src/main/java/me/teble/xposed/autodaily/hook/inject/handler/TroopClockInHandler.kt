package me.teble.xposed.autodaily.hook.inject.handler

import com.tencent.mobileqq.app.BusinessHandler
import com.tencent.qphone.base.remote.FromServiceMsg
import com.tencent.qphone.base.remote.ToServiceMsg

class TroopClockInHandler: BusinessHandler() {
    override fun getCommandList(): MutableSet<Any?> {
        TODO("Not yet implemented")
    }

    override fun getPushCommandList(): MutableSet<Any?> {
        TODO("Not yet implemented")
    }

    override fun getPushPBCommandList(): MutableSet<Any?> {
        TODO("Not yet implemented")
    }

    override fun onReceive(arg1: ToServiceMsg?, arg2: FromServiceMsg?, arg3: Any?) {
        TODO("Not yet implemented")
    }
}