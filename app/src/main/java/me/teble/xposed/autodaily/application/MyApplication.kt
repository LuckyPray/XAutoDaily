package me.teble.xposed.autodaily.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import me.teble.xposed.autodaily.hook.shizuku.ShizukuApi
import rikka.shizuku.Shizuku

@SuppressLint("StaticFieldLeak")
lateinit var xaApp: MyApplication

@SuppressLint("StaticFieldLeak")
lateinit var context: Context

class MyApplication : Application() {

    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        ShizukuApi.init()
        Shizuku.pingBinder()
        context = applicationContext
        xaApp = this
        prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
}