package com.instaconnect.android.base

import android.app.Activity
import android.content.ContentResolver.wrap
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.instaconnect.android.base.BaseActivity
import com.instaconnect.android.data.model.Event
import com.instaconnect.android.etc.ContextWrapper
import com.instaconnect.android.rxBus.RxBus
import com.instaconnect.android.utils.Extras
import com.instaconnect.android.utils.FragmentUtil
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import io.reactivex.functions.Consumer
import java.util.*
import javax.inject.Inject

/*
*  BaseActivity is a super-powered {@link android.support.v7.app.AppCompatActivity AppCompatActivity}
*  to be used with {@link rana.jatin.core.base.BaseIntent}
*/
abstract class BaseActivity<T : ViewDataBinding?, V : BaseViewModelExplorii<*>?> :
    AppCompatActivity()/*, HasSupportFragmentInjector*/ {
    private val TAG = BaseActivity::class.java.name
    private var mViewDataBinding: T? = null
    private var mViewModel: V? = null

    @Inject
    var fragmentInjector: DispatchingAndroidInjector<Fragment>? = null

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        window.statusBarColor = Color.TRANSPARENT
        performDataBinding()
        mViewModel!!.onViewCreated()
        //        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
//            @Override
//            public boolean verify(String hostname, SSLSession arg1) {
//                if (hostname.equalsIgnoreCase("http://99.79.19.208/webapp/terms_and_conditions.html")||
//                        hostname.equalsIgnoreCase("http://99.79.19.208/webapp/dev/api/") ||
//                        hostname.equalsIgnoreCase("http://99.79.19.208/webapp/dev/uploads/")) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
        //Subscribe for RxBus REFRESH event.
        //see {@link rana.jatin.core.model.Event#REFRESH}
        RxBus.instance!!.subscribe(
            Event.REFRESH.name, this,
            String::class.java, refreshConsumer
        )
    }

    private fun performDataBinding() {
        mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        mViewModel = if (mViewModel == null) getViewModel() else mViewModel
        mViewDataBinding!!.setVariable(getBindingVariable(), mViewModel)
        mViewDataBinding!!.executePendingBindings()
    }

    fun performDependencyInjection() {
        AndroidInjection.inject(this)
    }

    fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentInjector
    }

    /**
     * @return layout resource id
     */
    @LayoutRes
    abstract fun getLayoutId(): Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract fun getViewModel(): V

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract fun getBindingVariable(): Int
    fun getViewDataBinding(): T? {
        return mViewDataBinding
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(Extras.CLEAR_STACK.name, false)) { //TODO fix animation
            supportFragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }
        onNewIntentInternal(intent)
    }

    private fun onNewIntentInternal(intent: Intent) {
        onIntent(intent, null)
        if (intent.data != null) onDataIntent(intent)
        if (intent.hasExtra(Extras.FRAGMENT.name)) onFragmentIntent(intent)
    }

    /**
     * Called for any new incoming intent
     *
     * @param intent
     * @param savedInstanceState
     */
    fun onIntent(intent: Intent?, savedInstanceState: Bundle?) {}

    /**
     * Called when new intent contains data
     *
     * @param intent
     */
    fun onDataIntent(intent: Intent?) {}

    /*
    * return Extras passed to the activity using
    * {@link BaseIntent#setModel(Serializable object) setModel}
    * and {@link BaseIntent#setModel(Model object) setModel} methods.
    */
    fun <T> getModel(): T? {
        val args = intent.extras ?: return null
        return args.getSerializable(Extras.MODEL.name) as T?
    }

    /**
     * Called when new intent contains fragment instructions. See [BaseIntent]
     *
     * @param intent
     */
    fun onFragmentIntent(intent: Intent) {
        var needRefresh = intent.getBooleanExtra(Extras.REFRESH.name, false)
        val fragmentType =
            intent.getSerializableExtra(Extras.FRAGMENT.name) as Class<out Fragment>?
        val fragment = instantiate(fragmentType) ?: return
        val container = intent.getIntExtra(Extras.CONTAINER.name, 0)
        fragment.arguments = intent.extras //copy extras to fragment
        fragment.setTargetFragment(
            getCurrentFragment(container),
            0
        ) //set current fragment as target
        if (fragment is DialogFragment && fragment.showsDialog) {
            fragment.show(supportFragmentManager, null)
        } else {
            val skipStack = intent.getBooleanExtra(Extras.SKIP_STACK.name, false)
            val replace = intent.getBooleanExtra(Extras.REPLACE.name, false)
            val added: Boolean =
                FragmentUtil.with(this).fragment(fragment, container, replace).skipStack(skipStack)
                    .commit()
            needRefresh = !added
        }
        if (needRefresh) RxBus.instance!!.publish(Event.REFRESH.toString(), fragment.javaClass.name) //refresh
    }

    private fun instantiate(cls: Class<out Fragment>?): Fragment? {
        if (cls == null) return null
        try {
            return cls.newInstance()
        } catch (ignore: Exception) {
        }
        return null
    }

    private fun getCurrentFragment(container: Int): Fragment? {
        return supportFragmentManager.findFragmentById(container)
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyHelper()
        mViewModel!!.onDestroyView()
        RxBus.instance!!.unregister(this)
    }

    private fun destroyHelper() {}
    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun attachBaseContext(newBase: Context?) {
        val context: Context = ContextWrapper.wrap(newBase!!, Locale(Locale.ENGLISH.language))
        super.attachBaseContext(context)
    }

    /*
    * Invoke abstract method {@link onBackPress(int) onBackPress}
    */
    override fun onBackPressed() {
        val i = supportFragmentManager.backStackEntryCount
        var onBackPress = true
        onBackPress = onBackPress(i)
        val fragments = supportFragmentManager.fragments
        if (fragments != null && !fragments.isEmpty()) {
            for (fragment in fragments) {
                if (fragment is BaseFragmentExplorii<*, *> && fragment.isVisible()) onBackPress =
                    fragment.onBackPress(i)
            }
        }
        // if false super.onBackPressed() will not be called because sometimes we need to perform some action on back press
        if (!onBackPress) return
        super.onBackPressed()
    }

    /*
    * invoke method {@link android.support.v4.app.Fragment#onActivityResult(int ,int ,Intent) onActivityResult} of visible fragments.
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            if (fragment != null && fragment.isVisible) fragment.onActivityResult(
                requestCode,
                resultCode,
                data
            )
        }
    }

    /*
   * invoke method {@link android.support.v4.app.Fragment#onRequestPermissionsResult(int ,String[] ,int[]) onRequestPermissionsResult} of visible fragments.
   */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (fragment in supportFragmentManager.fragments) {
            if (fragment != null && fragment.isVisible) fragment.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        }
    }

    // return true if you want to call super.onBackPressed()
    // if false super.onBackPressed() will not be called because sometimes we need to perform some action on back press
    abstract fun onBackPress(fragmentCount: Int): Boolean
    private val refreshConsumer: Consumer<String> =
        Consumer { classToRefresh -> onRefresh(classToRefresh) }

    // @param classToRefresh will be #getClass().getName()
    abstract fun onRefresh(classToRefresh: String?)

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val win = activity.window
            val winParams = win.attributes
            if (on) {
                winParams.flags = winParams.flags or bits
            } else {
                winParams.flags = winParams.flags and bits.inv()
            }
            win.attributes = winParams
        }
    }
}