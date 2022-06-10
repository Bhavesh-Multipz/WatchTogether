package com.instaconnect.android.utils

import com.instaconnect.android.utils.FragmentUtil
import com.instaconnect.android.R
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.io.Serializable
import java.lang.Exception

class FragmentUtil(private val fragmentManager: FragmentManager?) {
    private val TAG = FragmentUtil::class.java.name
    private var fragment: Fragment? = null
    private var container = 0
    private var replace = false
    private var addToStack = true
    private var animExit = R.anim.fragment_out
    private var animEnter = R.anim.fragment_in
    private var extras: Bundle? = null
    private var serializable: Serializable? = null
    private var model: Model? = null

    /**
     * Same as [.fragment] with replace = true
     *
     * @return self
     */
    fun replace(fragment: Fragment?, @IdRes container: Int): FragmentUtil {
        fragment(fragment, container, true)
        return this
    }

    /**
     * Same as [.fragment] with replace = false
     *
     * @return self
     */
    fun add(fragment: Fragment?, @IdRes container: Int): FragmentUtil {
        fragment(fragment, container, false)
        return this
    }

    /**
     * Set fragment to add in transaction
     *
     * @param fragment  fragment to add
     * @param container setExtrasId of view where to add fragment
     * @param replace   true to replace fragment, false to add fragment and hide current
     * @return self
     */
    fun fragment(fragment: Fragment?, @IdRes container: Int, replace: Boolean): FragmentUtil {
        this.fragment = fragment
        this.container = container
        this.replace = replace
        return this
    }

    fun showDialogFragment(fragment: DialogFragment): FragmentUtil {
        fragment.show(fragmentManager!!, null)
        return this
    }

    /**
     * Set flag to not add this transaction to back stack
     * @param skipStack true to not add to back stack
     * @return self
     */
    fun skipStack(skipStack: Boolean): FragmentUtil {
        addToStack = !skipStack
        return this
    }

    /**
     * Same as [.skipStack] with true parameter
     * @return self
     */
    fun skipStack(): FragmentUtil {
        skipStack(true)
        return this
    }

    /**
     * @param model class which implements serializable passed as arguments to fragment
     * @return self
     */
    fun setModel(model: Serializable?): FragmentUtil {
        serializable = model
        return this
    }

    /**
     * @param model class which extends [Model]passed as arguments to fragment
     * @return self
     */
    fun setModel(model: Model?): FragmentUtil {
        this.model = model
        return this
    }

    /**
     * Additional extras to be put into fragment's arguments
     * @param extras extras to add
     * @return self
     */
    fun extras(extras: Bundle?): FragmentUtil {
        this.extras = extras
        return this
    }

    /**
     * set custom animations for fragment transactions
     * @param enter animation resource id
     * @param exit animation resource id
     * @return self
     */
    fun setCustomAnimations(enter: Int, exit: Int): FragmentUtil {
        animEnter = enter
        animExit = exit
        return this
    }

    /**
     * Compare two fragment to avoid adding the same fragment again
     * @param left
     * @param right
     * @return true if fragment are equal
     */
    private fun equal(left: Fragment?, right: Fragment?): Boolean {
        return left != null && right != null && left.javaClass.name.equals(
            right.javaClass.name,
            ignoreCase = true
        )
    }

    /*
     * Create and commit new transaction executing all collected options.
     * @return true if fragment was added, else false
     */
    fun commit(): Boolean {
        val backStateName = fragment!!.javaClass.name
        val current = fragmentManager!!.findFragmentById(container)
        if (equal(fragment, current)) return false
        var args = fragment!!.arguments
        if (args == null) {
            args = Bundle()
        }
        if (serializable != null) args.putSerializable(Extras.MODEL.name, serializable)
        if (model != null) args.putSerializable(Extras.MODEL.name, model)
        if (extras != null) args.putAll(extras)
        fragment!!.arguments = args
        val transaction = fragmentManager.beginTransaction()
        if (addToStack) transaction.setCustomAnimations(animEnter, animExit, animEnter, animExit)
        if (replace) {
            transaction.replace(container, fragment!!)
        } else {
            if (current != null) transaction.hide(current)
            transaction.add(container, fragment!!, container.toString())
        }
        if (addToStack) transaction.addToBackStack(backStateName)
        transaction.commitAllowingStateLoss()
        return true
    }

    /*
    * remove fragment
    * @param fragment to be removed
    * return self
    */
    fun removeFragment(fragment: Fragment?): FragmentUtil {
        if (fragment == null) return this
        try {
            val ft = fragmentManager!!.beginTransaction()
            ft.remove(fragment)
            ft.commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        } catch (e: Exception) {
        }
        return this
    }

    /*
    * remove fragment from container
    * @param containerId from fragment to be removed
    * return self
    */
    fun removeFragFromContainer(containerId: Int): FragmentUtil {
        try {
            val fragment = fragmentManager!!.findFragmentById(container)
            removeFragment(fragment)
        } catch (e: Exception) {
        }
        return this
    }

    /*
    * reload fragment
    * @param fragment to reload
    * return self
    */
    fun reLoadFragment(fragment: Fragment): FragmentUtil {
        // Reload current fragment
        var frg: Fragment? = null
        frg = fragmentManager!!.findFragmentByTag(fragment.javaClass.name)
        frg!!.onDetach()
        fragmentManager.beginTransaction()
            .detach(frg)
            .commitNowAllowingStateLoss()
        fragmentManager.beginTransaction()
            .attach(frg)
            .commitAllowingStateLoss()
        return this
    }

    /*
    * remove all the fragments from back stack
    * return self
    */
    fun clearBackStack(): FragmentUtil {
        if (fragmentManager!!.backStackEntryCount > 0) {
            val first = fragmentManager.getBackStackEntryAt(0)
            fragmentManager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        return this
    }

    companion object {
        /**
         * Creating new [FragmentUtil] for fragmentManager
         * Usage: FragmentUtil.with(this).replace(fragment, R.setExtrasId.container).skipStack().commit();
         *
         * @return new object of [FragmentUtil]
         */
        private fun with(fragmentManager: FragmentManager?): FragmentUtil {
            return FragmentUtil(fragmentManager)
        }

        /**
         * See [.with]
         *
         * @return new object of [FragmentUtil]
         */
        fun with(context: AppCompatActivity): FragmentUtil {
            return with(context.supportFragmentManager)
        }

        /**
         * See [.with]
         *
         * @return new object of [FragmentUtil]
         */
        fun with(fragment: Fragment): FragmentUtil {
            return with(fragment.fragmentManager)
        }

        /**
         * See [.with]
         *
         * @return new object of [FragmentUtil]
         */
        fun with(fragment: android.app.Fragment): FragmentUtil {
            val appCompatActivity = fragment.activity as AppCompatActivity
            return with(appCompatActivity)
        }
    }
}