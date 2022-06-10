package com.instaconnect.android.widget.viewPager

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager
import android.view.MotionEvent
import androidx.core.view.MotionEventCompat

class BasicViewPager : ViewPager {
    var isSwipeEnabled = true
    private var mStartDragX = 0f
    private var mOnSwipeListener: OnSwipeListener? = null

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    fun setOnSwipeOutListener(listener: OnSwipeListener?) {
        mOnSwipeListener = listener
    }

    private fun onSwipeFirst() {
        if (mOnSwipeListener != null) {
            mOnSwipeListener!!.onSwipeFirst()
        }
    }

    private fun onSwipeLast() {
        if (mOnSwipeListener != null) {
            mOnSwipeListener!!.onSwipeLast()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isSwipeEnabled) return false
        when (ev.action and MotionEventCompat.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> mStartDragX = ev.x
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!isSwipeEnabled) return false
        if (currentItem == 0 || currentItem == adapter!!.count - 1) {
            val action = ev.action
            val x = ev.x
            when (action and MotionEventCompat.ACTION_MASK) {
                MotionEvent.ACTION_MOVE -> {}
                MotionEvent.ACTION_UP -> {
                    if (currentItem == 0 && x > mStartDragX) {
                        onSwipeFirst()
                    }
                    if (currentItem == adapter!!.count - 1 && x < mStartDragX) {
                        onSwipeLast()
                    }
                }
            }
        } else {
            mStartDragX = 0f
        }
        return super.onTouchEvent(ev)
    }

    interface OnSwipeListener {
        fun onSwipeFirst()
        fun onSwipeLast()
    }
}