package com.instaconnect.android.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.InputFilter
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.instaconnect.android.utils.CommonUtil
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicInteger

class CommonUtil private constructor() {
    private val sNextGeneratedId = AtomicInteger(1)

    /* create model class from string response
    *  @param json string response
    *  @param cls type of model class
    *  return generic class
    */
    fun <T> fromJson(json: String?, cls: Class<T>?): T? {
        val gson = Gson()
        return if (json != null && !json.isEmpty()) {
            gson.fromJson(json, cls)
        } else null
    }

    /* convert model class to string
    *  @param cls type of model class
    *  return string response
    */
    fun toJson(`object`: Any?): String {
        val gson = Gson()
        return gson.toJson(`object`)
    }

    /* create model class from string response
    *  @param json string response
    *  @param type Type Token of model class
    *  return generic class
    */
    fun <T> fromJson(json: String?, type: Type?): T? {
        val gson = Gson()
        return if (json != null && !json.isEmpty()) {
            gson.fromJson(json, type)
        } else null
    }

    /* return height and width of device*/
    fun getHeightWidth(context: Activity): IntArray {
        val displayMetrics = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return intArrayOf(height, width)
    }

    /* convert DP to PX */
    fun dipToPixels(context: Context, dipValue: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics)
    }

    /* return color using ContextCompat*/
    fun getColor(context: Context?, @ColorRes color: Int): Int {
        return ContextCompat.getColor(context!!, color)
    }

    /* create filter for max length to set on EditText of other views*/
    fun getMaxLengthFilter(length: Int): InputFilter? {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(length)
        return filterArray[0]
    }

    /* create prefix filter to set on EditText of other views. ex: 91*/
    fun getPrefixFilter(prefix: String): InputFilter {
        return InputFilter { source, start, end, dest, dstart, dend ->
            if (dstart < prefix.length) dest.subSequence(
                dstart,
                dend
            ) else null
        }
    }

    /**
     * Generate a value suitable for use in [android.view.View.setId].
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    fun generateViewId(): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            while (true) {
                val result = sNextGeneratedId.get()
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result
                }
            }
        } else {
            return View.generateViewId()
        }
    }

    companion object {
        val instance = CommonUtil()
    }
}