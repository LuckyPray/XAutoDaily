package me.teble.xposed.autodaily.hook.proxy.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import me.teble.xposed.autodaily.hook.base.Global
import me.teble.xposed.autodaily.utils.LogUtil

/**
 * 所有在宿主内启动的AppCompatActivity都应该继承与此类 否则会报错
 */
//open class BaseActivity : ComponentActivity() {
open class BaseActivity : Activity(),
    OnBackPressedDispatcherOwner,
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner
{
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }


    private fun handleLifecycleEvent(event: Lifecycle.Event) =
        lifecycleRegistry.handleLifecycleEvent(event)

    private val mLoader by lazy {
        BaseActivityClassLoader(BaseActivity::class.java.classLoader!!)
    }

    override fun getClassLoader(): ClassLoader = mLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedStateRegistry.performRestore(null)
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val windowState = savedInstanceState.getBundle("android:viewHierarchyState")
        if (windowState != null) {
            windowState.classLoader = mLoader
        }
        super.onRestoreInstanceState(savedInstanceState)
    }


    override fun onDestroy() {
        super.onDestroy()
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }


    //ViewModelStore Methods
    private val store = ViewModelStore()

    override fun getViewModelStore() = store

    //SaveStateRegestry Methods

    private val savedStateRegistry = SavedStateRegistryController.create(this)

    override fun getSavedStateRegistry() = savedStateRegistry.savedStateRegistry


    private val mOnBackPressedDispatcher = OnBackPressedDispatcher {
        // Calling onBackPressed() on an Activity with its state saved can cause an
        // error on devices on API levels before 26. We catch that specific error and
        // throw all others.
        try {
            super.onBackPressed()
        } catch (e: IllegalStateException) {
            if (!TextUtils.equals(e.message,
                    "Can not perform this action after onSaveInstanceState")) {
                throw e
            }
        }
    }
    override fun onBackPressed() {
        mOnBackPressedDispatcher.onBackPressed()
    }
    override fun getOnBackPressedDispatcher() = mOnBackPressedDispatcher
}

class BaseActivityClassLoader(referencer: ClassLoader) :
    ClassLoader() {
    private val mBaseReferencer: ClassLoader = referencer
    private val mHostReferencer: ClassLoader = Global.hostClassLoader

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        LogUtil.d("loadClass -> $name")
        try {
            if (name.startsWith("androidx.compose")
                || name.startsWith("androidx.navigation")
                || name.startsWith("androidx.activity")) {
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
        }
        //with ClassNotFoundException
        return mBaseReferencer.loadClass(name)
    }
}
