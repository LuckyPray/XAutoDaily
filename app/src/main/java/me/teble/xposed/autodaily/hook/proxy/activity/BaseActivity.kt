package me.teble.xposed.autodaily.hook.proxy.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import me.teble.xposed.autodaily.hook.base.hostClassLoader
import me.teble.xposed.autodaily.utils.LogUtil

/**
 * 所有在宿主内启动的AppCompatActivity都应该继承与此类 否则会报错
 */
//open class BaseActivity : ComponentActivity() {
open class BaseActivity : ComponentActivity() {

    private val mLoader by lazy {
        BaseActivityClassLoader(BaseActivity::class.java.classLoader!!)
    }

    override fun getClassLoader(): ClassLoader = mLoader

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val windowState = savedInstanceState.getBundle("android:viewHierarchyState")
        if (windowState != null) {
            windowState.classLoader = mLoader
        }
        super.onRestoreInstanceState(savedInstanceState)
    }
}

class BaseActivityClassLoader(referencer: ClassLoader) :
    ClassLoader() {
    private val mBaseReferencer: ClassLoader = referencer
    private val mHostReferencer: ClassLoader = hostClassLoader

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        LogUtil.d("loadClass -> $name")
        try {
            if (name.startsWith("androidx.compose")
                || name.startsWith("androidx.navigation")
                || name.startsWith("androidx.activity")
            ) {
                return mBaseReferencer.loadClass(name)
            }
            return Context::class.java.classLoader!!.loadClass(name)
        } catch (ignored: ClassNotFoundException) {
        }
        try {
            //start: overloaded
            if (name == "androidx.lifecycle.LifecycleOwner"
                || name == "androidx.lifecycle.ViewModelStoreOwner"
                || name == "androidx.savedstate.SavedStateRegistryOwner"
            ) {
                return mHostReferencer.loadClass(name)
            }
        } catch (ignored: ClassNotFoundException) {
            LogUtil.e(ignored, "loadClass -> $name")
        }
        //with ClassNotFoundException

        return mBaseReferencer.loadClass(name)
    }

}
