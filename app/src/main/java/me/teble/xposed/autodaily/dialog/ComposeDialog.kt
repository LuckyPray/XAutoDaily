package me.teble.xposed.autodaily.dialog

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import me.teble.xposed.autodaily.R

class ComposeDialog(activity: Activity) : AlertDialog(activity, R.style.AlertDialogStyle),
    ViewModelStoreOwner,
    SavedStateRegistryOwner, LifecycleOwner {

    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle = LifecycleRegistry(this)
    override val viewModelStore: ViewModelStore = ViewModelStore()

    //SaveStateRegestry Methods

    private val mSavedStateRegistryController: SavedStateRegistryController =
        SavedStateRegistryController.create(this)

    override val savedStateRegistry: SavedStateRegistry =
        mSavedStateRegistryController.savedStateRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.decorView?.setViewTreeLifecycleOwner(this)
        window?.decorView?.setViewTreeViewModelStoreOwner(this)
        window?.decorView?.setViewTreeSavedStateRegistryOwner(this)
    }


    override fun onStart() {
        super.onStart()
        mSavedStateRegistryController.performRestore(null)
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onStop() {
        super.onStop()
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }


    private fun handleLifecycleEvent(event: Lifecycle.Event) =
        lifecycleRegistry.handleLifecycleEvent(event)


//    fun setContent(content: @Composable () -> Unit) {
//        val composeView = ComposeView(context).apply {
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//        }
//        composeView.setViewCompositionStrategy(
//            ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this)
//        )
//
//        this.setView(composeView)
//        composeView.setContent(content)
//    }

//    override fun show() {
//        val lp = WindowManager.LayoutParams().apply {
//            copyFrom(window!!.attributes)
//            width = WindowManager.LayoutParams.MATCH_PARENT
//            height = WindowManager.LayoutParams.MATCH_PARENT
//        }
//        super.show()
//        window!!.setAttributes(lp)
//    }

}