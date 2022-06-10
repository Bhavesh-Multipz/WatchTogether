package com.instaconnect.android.utils

import android.app.Activity
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.EditText
import com.instaconnect.android.utils.ViewUtil
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import kotlin.jvm.Synchronized
import android.widget.TextView
import com.instaconnect.android.R
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.lang.Exception

/**
 * Contains set of utils for working with activity or fragment interfaces, popups, toasts, snack bar etc
 */
class ViewUtil {
    private var activity: Activity
    private var fragment: Fragment? = null
    /* return snack bar*/  var snackbar: Snackbar? = null
        private set

    constructor(activity: Activity) {
        this.activity = activity
        setSoftInputListener()
    }

    constructor(activity: Activity, fragment: Fragment?) {
        this.activity = activity
        this.fragment = fragment
        setSoftInputListener()
    }

    /* open android app settings */
    private fun openAppSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivity(intent)
    }

    /* show soft keyboard */
    fun showSoftInput(editText: EditText) {
        val mHandler = Handler()
        mHandler.post {
            val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInputFromWindow(
                editText.applicationWindowToken,
                InputMethodManager.SHOW_FORCED,
                0
            )
            editText.requestFocus()
        }
    }

    /* hide soft keyboard*/
    fun toggleSoftInput() {
        if (!isSoftInputOpen) return
        val mHandler = Handler()
        mHandler.post {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            isSoftInputOpen = false
        }
    }

    // This utility method is used with fragment
    // view = getView().getRootView();
    // view = fragment.getView();
    fun hideKeyboardFrom(context: Context, view: View?) {
        var view = view
        if (view == null) view = View(activity)
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
        isSoftInputOpen = false
    }

    // This utility method ONLY works when called from an Activity
    fun hideKeyboard() {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
        isSoftInputOpen = false
    }

    fun setSoftInputListener() {
        val activityRootView = activity.window.decorView.findViewById<View>(android.R.id.content)
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            activityRootView.getWindowVisibleDisplayFrame(r)
            val screenHeight = activityRootView.rootView.height

            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                isSoftInputOpen = true
            } else {
                isSoftInputOpen = false
                val handler = Handler()
                handler.postDelayed({
                }, 100)
            }
        }
    }

    /* show a toast message
    *  @param message to show
    */
    fun toast(message: String?) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    /* show a toast message
    *  @param message string resource id
    */
    fun toast(@StringRes message: Int) {
        Toast.makeText(activity, activity.getString(message), Toast.LENGTH_LONG).show()
    }

    /* fire sharing intent
    * @param sharebody share subject
    * @param shareSub  share extra text
    */

    /*
    * show snack bar
    * @param message to show
    */
    @Synchronized
    fun showSnack(message: String?) {
        activity.runOnUiThread(Runnable {
            try {
                snackbar = Snackbar
                    .make(activity.findViewById(snackViewId), message!!, Snackbar.LENGTH_LONG)
                val snackbarView = snackbar!!.view
                val textView =
                    snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
                textView.maxLines = 5
                setSnackBackground(snackBackground)
                setSnackActionTextColor(snackActionColor)
                setSnackTextColor(snackTxtColor)
                snackbar!!.show()
            } catch (e: Exception) {
                toast(message)
            }
        })
    }

    /*
    * show snack bar
    * @param string resource id of message to show
    */
    @Synchronized
    fun showSnack(@StringRes message: Int) {
        activity.runOnUiThread {
            try {
                snackbar = Snackbar
                    .make(
                        activity.findViewById(snackViewId),
                        activity.getString(message),
                        Snackbar.LENGTH_LONG
                    )
                val snackbarView = snackbar!!.view
                val textView =
                    snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
                textView.maxLines = 5
                setSnackBackground(snackBackground)
                setSnackActionTextColor(snackActionColor)
                setSnackTextColor(snackTxtColor)
                snackbar!!.show()
            } catch (e: Exception) {
                toast(activity.getString(message))
            }
        }
    }

    /*
    * show permission snack bar with action button to open app settings
    */

    fun showPermissionSnack() {
        activity.runOnUiThread {
            try {
                showSnack(
                    activity.getString(R.string.message_permissions_reject),
                    activity.getString(R.string.settings)
                ) { openAppSettings() }
            } catch (e: Exception) {
                toast(R.string.message_permissions_reject)
            }
        }
    }

    /*
    * show snack bar
    * @param message to show
    * @param actionText action button text
    * @param onClickListener click listener for action button click
    */
    @Synchronized
    fun showSnack(message: String?, actionText: String?, onClickListener: View.OnClickListener?) {
        activity.runOnUiThread {
            try {
                snackbar = Snackbar
                    .make(activity.findViewById(snackViewId), message!!, Snackbar.LENGTH_LONG)
                    .setAction(actionText, onClickListener)
                val snackbarView = snackbar!!.view
                val textView =
                    snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
                textView.maxLines = 5
                setSnackBackground(snackBackground)
                setSnackActionTextColor(snackActionColor)
                setSnackTextColor(snackTxtColor)
                snackbar!!.show()
            } catch (e: Exception) {
                toast(message)
            }
        }
    }

    /*
   * show snack bar with custom duration
   * @param message to show
   * @param actionText action button text
   * @param onClickListener click listener for action button click
   */
    @Synchronized
    fun showSnackWithCustomTiming(
        message: String?,
        actionText: String?,
        duration: Int,
        onClickListener: View.OnClickListener?
    ) {
        activity.runOnUiThread {
            try {
                snackbar = Snackbar
                    .make(activity.findViewById(snackViewId), message!!, Snackbar.LENGTH_INDEFINITE)
                    .setAction(actionText, onClickListener)
                val snackbarView = snackbar!!.view
                val textView =
                    snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
                textView.maxLines = 5
                setSnackBackground(snackBackground)
                setSnackActionTextColor(snackActionColor)
                setSnackTextColor(snackTxtColor)
                snackbar!!.show()
                val handler = Handler()
                handler.postDelayed({ snackbar!!.dismiss() }, duration.toLong())
            } catch (e: Exception) {
                toast(message)
            }
        }
    }

    /*
   * show snack bar until dismissed by user
   * @param message to show
   * @param actionText action button text
   * @param onClickListener click listener for action button click
   */
    @Synchronized
    fun showSnackIndefinite(
        message: String?,
        actionText: String?,
        onClickListener: View.OnClickListener?
    ) {
        activity.runOnUiThread {
            try {
                snackbar = Snackbar
                    .make(activity.findViewById(snackViewId), message!!, Snackbar.LENGTH_INDEFINITE)
                    .setAction(actionText, onClickListener)
                val snackbarView = snackbar!!.view
                val textView =
                    snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
                textView.maxLines = 5
                setSnackBackground(snackBackground)
                setSnackActionTextColor(snackActionColor)
                setSnackTextColor(snackTxtColor)
                snackbar!!.show()
            } catch (e: Exception) {
                toast(message)
            }
        }
    }

    /*
   * show snack bar until dismissed by user
   * @param message to show
   */
    @Synchronized
    fun showSnackIndefinite(message: String?) {
        activity.runOnUiThread {
            try {
                snackbar = Snackbar
                    .make(activity.findViewById(snackViewId), message!!, Snackbar.LENGTH_INDEFINITE)
                val snackbarView = snackbar!!.view
                val textView =
                    snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
                textView.maxLines = 5
                setSnackBackground(snackBackground)
                setSnackActionTextColor(snackActionColor)
                setSnackTextColor(snackTxtColor)
                snackbar!!.show()
            } catch (e: Exception) {
                toast(message)
            }
        }
    }

    /*
   * set snack bar view id
   * @param id of view on which to make snack bar ( in most cases coordinator layout )
   */
    fun makeSnackbar(id: Int): ViewUtil {
        snackViewId = id
        return this
    }

    /*
   * set snack bar text view color
   * @param textColor color resource id
   * return self
   */
    fun setSnackTextColor(textColor: Int): ViewUtil {
        snackTxtColor = textColor
        if (snackbar != null) {
            val snackbarView = snackbar!!.view
            val textView =
                snackbarView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
            textView.setTextColor(ContextCompat.getColor(activity, textColor))
        }
        return this
    }

    /*
   * set snack bar action text color
   * @param textColor color resource id
   * return self
   */
    fun setSnackActionTextColor(textColor: Int): ViewUtil {
        snackActionColor = textColor
        if (snackbar != null) snackbar!!.setActionTextColor(
            ContextCompat.getColor(
                activity,
                textColor
            )
        )
        return this
    }

    /*
   * set snack bar background color
   * @param background color resource id
   * return self
   */
    fun setSnackBackground(background: Int): ViewUtil {
        snackBackground = background
        if (snackbar != null) snackbar!!.view.setBackgroundColor(
            ContextCompat.getColor(
                activity,
                background
            )
        )
        return this
    }

    companion object {
        private var snackViewId = 0
        private var snackTxtColor = R.color.black
        private var snackBackground = R.color.white
        private var snackActionColor = R.color.red

        /*
    * return true if soft keyboard open else false if soft keyboard closed
    */  var isSoftInputOpen = false
            private set


        @Synchronized
        fun showShareSheet(shareSub: String?, shareBody: String?, context: Context) {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            context.startActivity(Intent.createChooser(sharingIntent, "Share using"))
        }

        fun isActivityDead(activity: Activity?): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                // Do something for lollipop and above versions
                activity == null || activity.isFinishing || activity.isDestroyed
            } else {
                // do something for phones running an SDK before lollipop
                activity == null || activity.isFinishing
            }
        }

        fun isFragmentDead(fragment: Fragment?): Boolean {
            if (fragment == null) return true
            val activity: Activity? = fragment.activity
            return activity == null || !fragment.isAdded || fragment.isRemoving || fragment.isDetached
        }
    }
}