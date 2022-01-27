package me.teble.xposed.autodaily.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
    }
}