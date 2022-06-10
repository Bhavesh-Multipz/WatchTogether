package com.instaconnect.android.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.instaconnect.android.base.BaseIntent
import com.instaconnect.android.utils.Extras
import com.instaconnect.android.utils.Model
import java.io.Serializable

class BaseIntent : Intent {
    /**
     * Same as [.BaseIntent] class will be taken from activity. Used to pass parameters to self.
     *
     * @param activity context
     */
    constructor(activity: Activity) : super(activity, activity.javaClass) {}
    constructor(activity: Activity, cls: Class<*>?, finish: Boolean) : super(activity, cls) {
        if (finish) {
            Handler(Looper.getMainLooper()).postDelayed({ activity.finish() }, 2000)
        }
    }

    constructor(context: Context?, cls: Class<*>?) : super(context, cls) {}

    /**
     * @param model class which implements [Serializable] passed as extras to activity
     * @return self
     */
    fun setModel(model: Serializable?): BaseIntent {
        putExtra(Extras.MODEL.name, model)
        return this
    }

    /**
     * @param model class which extends [Model]passed as extras to activity
     * @return self
     */
    fun setModel(model: Model?): BaseIntent {
        putExtra(Extras.MODEL.name, model)
        return this
    }

    /**
     * @param fragment class of fragment that will be pushed
     * @param container id in which fragment to be pushed
     * @return self
     */
    fun fragment(fragment: Class<out Fragment?>?, container: Int): BaseIntent {
        putExtra(Extras.CONTAINER.name, container)
        putExtra(Extras.FRAGMENT.name, fragment)
        return this
    }

    /**
     * @param fragment  class of fragment that will be pushed
     * @param model     setModel object to be passed to fragment
     * @param container id in which fragment to be pushed
     * @return self
     */
    fun fragment(fragment: Class<out Fragment?>?, model: Model?, container: Int): BaseIntent {
        fragment(fragment, container)
        setModel(model)
        return this
    }

    /**
     * @param fragment  class of fragment that will be pushed
     * @param model     class which implements serializable, setModel object to be passed to fragment
     * @param container id in which fragment to be pushed
     * @return self
     */
    fun fragment(
        fragment: Class<out Fragment?>?,
        model: Serializable?,
        container: Int
    ): BaseIntent {
        fragment(fragment, container)
        setModel(model)
        return this
    }

    /**
     * If set will replace fragment using [android.app.FragmentTransaction] instead of adding fragment on top
     * @return self
     */
    fun replace(): BaseIntent {
        putExtra(Extras.REPLACE.name, true)
        return this
    }

    /**
     * If set fragment will not be added to back stack
     * @return self
     */
    fun skipStack(): BaseIntent {
        putExtra(Extras.SKIP_STACK.name, true)
        return this
    }

    /**
     * Combination of [.replace] and [.skipStack] to use fragment as root of hierarchy.
     * @return self
     */
    fun asRoot(): BaseIntent {
        replace()
        skipStack()
        return this
    }

    /**
     * If set fragment will be refreshed if already in back stack
     * @return self
     */
    fun refresh(): BaseIntent {
        putExtra(Extras.REFRESH.name, true)
        return this
    }

    /**
     * If set fragment's back stack will be cleared before adding new fragment
     * @return self
     */
    fun clearStack(): BaseIntent {
        putExtra(Extras.CLEAR_STACK.name, true)
        return this
    }
}