package me.teble.xposed.autodaily.activity.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.contextaware.ContextAware
import androidx.activity.contextaware.ContextAwareHelper
import androidx.activity.contextaware.OnContextAvailableListener
import androidx.activity.result.*
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import java.util.concurrent.atomic.AtomicInteger

//open class XaActivity : Activity(),
//    OnBackPressedDispatcherOwner,
//    LifecycleOwner,
//    ViewModelStoreOwner,
//    SavedStateRegistryOwner,
//    ActivityResultRegistryOwner,
//    ActivityResultCaller,
//    ContextAware {
//
//    companion object {
//        private const val ACTIVITY_RESULT_TAG = "android:support:activity-result"
//    }
//
//    private val mContextAwareHelper = ContextAwareHelper()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mSavedStateRegistryController.performRestore(savedInstanceState)
//        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
//
//        savedStateRegistry.registerSavedStateProvider(
//            ACTIVITY_RESULT_TAG
//        ) {
//            val outState = Bundle()
//            mActivityResultRegistry.onSaveInstanceState(outState)
//            outState
//        }
//        addOnContextAvailableListener {
//            val instanceState = savedStateRegistry
//                .consumeRestoredStateForKey(ACTIVITY_RESULT_TAG)
//            if (instanceState != null) {
//                mActivityResultRegistry.onRestoreInstanceState(instanceState)
//            }
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        (lifecycle as? LifecycleRegistry)?.currentState = Lifecycle.State.CREATED
//        super.onSaveInstanceState(outState)
//        mSavedStateRegistryController.performSave(outState)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        handleLifecycleEvent(Lifecycle.Event.ON_START)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        handleLifecycleEvent(Lifecycle.Event.ON_STOP)
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//    }
//
//    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
//
//    override fun getLifecycle(): Lifecycle {
//        return mLifecycleRegistry
//    }
//
//    private fun handleLifecycleEvent(event: Lifecycle.Event) =
//        mLifecycleRegistry.handleLifecycleEvent(event)
//
//    //ViewModelStore Methods
//    private val store = ViewModelStore()
//
//    override fun getViewModelStore() = store
//
//    //SaveStateRegestry Methods
//
//    private val mSavedStateRegistryController: SavedStateRegistryController =
//        SavedStateRegistryController.create(this)
//
//    override val savedStateRegistry: SavedStateRegistry =
//        mSavedStateRegistryController.savedStateRegistry
//
//    private val mOnBackPressedDispatcher = OnBackPressedDispatcher {
//        // Calling onBackPressed() on an Activity with its state saved can cause an
//        // error on devices on API levels before 26. We catch that specific error and
//        // throw all others.
//        try {
//            super.onBackPressed()
//        } catch (e: IllegalStateException) {
//            if (!TextUtils.equals(
//                    e.message,
//                    "Can not perform this action after onSaveInstanceState"
//                )
//            ) {
//                throw e
//            }
//        }
//    }
//
//    override fun onBackPressed() {
//        mOnBackPressedDispatcher.onBackPressed()
//    }
//
//    override fun getOnBackPressedDispatcher() = mOnBackPressedDispatcher
//
//    private val mActivityResultRegistry = object : ActivityResultRegistry() {
//        override fun <I, O> onLaunch(
//            requestCode: Int,
//            contract: ActivityResultContract<I, O>,
//            input: I,
//            options: ActivityOptionsCompat?
//        ) {
//            val activity: Activity = this@XaActivity
//
//            // Immediate result path
//            val synchronousResult: ActivityResultContract.SynchronousResult<O>? =
//                contract.getSynchronousResult(activity, input)
//            if (synchronousResult != null) {
//                Handler(Looper.getMainLooper()).post {
//                    dispatchResult(
//                        requestCode,
//                        synchronousResult.value
//                    )
//                }
//                return
//            }
//
//            // Start activity path
//            val intent = contract.createIntent(activity, input)
//            var optionsBundle: Bundle? = null
//            // If there are any extras, we should defensively set the classLoader
//            if (intent.extras != null && intent.extras!!.classLoader == null) {
//                intent.setExtrasClassLoader(activity.classLoader)
//            }
//            if (intent.hasExtra(ActivityResultContracts.StartActivityForResult.EXTRA_ACTIVITY_OPTIONS_BUNDLE)) {
//                optionsBundle =
//                    intent.getBundleExtra(ActivityResultContracts.StartActivityForResult.EXTRA_ACTIVITY_OPTIONS_BUNDLE)
//                intent.removeExtra(ActivityResultContracts.StartActivityForResult.EXTRA_ACTIVITY_OPTIONS_BUNDLE)
//            } else if (options != null) {
//                optionsBundle = options.toBundle()
//            }
//            if (ActivityResultContracts.RequestMultiplePermissions.ACTION_REQUEST_PERMISSIONS == intent.action) {
//
//                // requestPermissions path
//                var permissions =
//                    intent.getStringArrayExtra(ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSIONS)
//                if (permissions == null) {
//                    permissions = arrayOfNulls(0)
//                }
//                ActivityCompat.requestPermissions(activity, permissions, requestCode)
//            } else if (ActivityResultContracts.StartIntentSenderForResult.ACTION_INTENT_SENDER_REQUEST == intent.action) {
//                val request: IntentSenderRequest =
//                    intent.getParcelableExtra(ActivityResultContracts.StartIntentSenderForResult.EXTRA_INTENT_SENDER_REQUEST)!!
//                try {
//                    // startIntentSenderForResult path
//                    ActivityCompat.startIntentSenderForResult(
//                        activity, request.intentSender,
//                        requestCode, request.fillInIntent, request.flagsMask,
//                        request.flagsValues, 0, optionsBundle
//                    )
//                } catch (e: IntentSender.SendIntentException) {
//                    Handler(Looper.getMainLooper()).post {
//                        dispatchResult(
//                            requestCode, RESULT_CANCELED,
//                            Intent()
//                                .setAction(ActivityResultContracts.StartIntentSenderForResult.ACTION_INTENT_SENDER_REQUEST)
//                                .putExtra(
//                                    ActivityResultContracts.StartIntentSenderForResult.EXTRA_SEND_INTENT_EXCEPTION,
//                                    e
//                                )
//                        )
//                    }
//                }
//            } else {
//                // startActivityForResult path
//                ActivityCompat.startActivityForResult(activity, intent, requestCode, optionsBundle)
//            }
//        }
//    }
//
//    override fun getActivityResultRegistry() = mActivityResultRegistry
//
//    private val mNextLocalRequestCode = AtomicInteger()
//
//    override fun <I : Any?, O : Any?> registerForActivityResult(
//        contract: ActivityResultContract<I, O>,
//        registry: ActivityResultRegistry,
//        callback: ActivityResultCallback<O>
//    ): ActivityResultLauncher<I> {
//        return registry.register(
//            "activity_rq#${mNextLocalRequestCode.getAndIncrement()}",
//            this, contract, callback
//        )
//    }
//
//    override fun <I : Any?, O : Any?> registerForActivityResult(
//        contract: ActivityResultContract<I, O>,
//        callback: ActivityResultCallback<O>
//    ): ActivityResultLauncher<I> {
//        return registerForActivityResult(contract, mActivityResultRegistry, callback)
//    }
//
//
//    override fun peekAvailableContext(): Context? {
//        return mContextAwareHelper.peekAvailableContext()
//    }
//
//    override fun addOnContextAvailableListener(
//        listener: OnContextAvailableListener
//    ) {
//        mContextAwareHelper.addOnContextAvailableListener(listener)
//    }
//
//    override fun removeOnContextAvailableListener(
//        listener: OnContextAvailableListener
//    ) {
//        mContextAwareHelper.removeOnContextAvailableListener(listener)
//    }
//
//    @CallSuper
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (!mActivityResultRegistry.dispatchResult(requestCode, resultCode, data)) {
//            super.onActivityResult(requestCode, resultCode, data)
//        }
//    }
//
//    @CallSuper
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>,
//        grantResults: IntArray
//    ) {
//        if (!mActivityResultRegistry.dispatchResult(
//                requestCode, RESULT_OK, Intent()
//                    .putExtra(ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSIONS, permissions)
//                    .putExtra(
//                        ActivityResultContracts.RequestMultiplePermissions.EXTRA_PERMISSION_GRANT_RESULTS,
//                        grantResults
//                    )
//            )
//        ) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//    }
//}