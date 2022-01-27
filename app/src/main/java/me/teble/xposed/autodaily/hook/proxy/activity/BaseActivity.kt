package me.teble.xposed.autodaily.hook.proxy.activity

import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * 所有在宿主内启动的AppCompatActivity都应该继承与此类 否则会报错
 */
open class BaseActivity : ComponentActivity() {
    private val mLoader by lazy { BaseActivity::class.java.classLoader }

    override fun getClassLoader(): ClassLoader = mLoader

    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val windowState = savedInstanceState.getBundle("android:viewHierarchyState")
        if (windowState != null) {
            windowState.classLoader = mLoader
        }
        super.onRestoreInstanceState(savedInstanceState)
    }
}