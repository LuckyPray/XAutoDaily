package me.teble.xposed.autodaily.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.view.View
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner

@SuppressLint("ResourceType")
class CustomDialog(context: Context) : Dialog(context, 5),
    OnBackPressedDispatcherOwner,
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {
    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    override fun onStart() {
        super.onStart()
        savedStateRegistry.performRestore(null)
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onStop() {
        super.onStop()
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        ViewTreeLifecycleOwner.set(view, this)
        ViewTreeViewModelStoreOwner.set(view, this)
        ViewTreeSavedStateRegistryOwner.set(view, this)
    }

    private fun handleLifecycleEvent(event: Lifecycle.Event) =
        lifecycleRegistry.handleLifecycleEvent(event)

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