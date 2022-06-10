package com.instaconnect.android.widget.nestedScrollView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.core.widget.NestedScrollView

/*
*  BasicNestedScrollView is a super-powered {@link android.support.v4.widget.NestedScrollView}
*/
class BasicNestedScrollView : NestedScrollView {
    private val TAG = BasicNestedScrollView::class.java.name
    var prevPage = 1
    var nextPage = 1
    var isPrevScrollEnabled = false
        private set
    var isNextScrollEnabled = false
        private set
    var isLoading = false

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
    }

    fun setLazyLoadListener(lazyLoadListener: LazyLoadListenerN) {
        this.setOnScrollChangeListener(object : OnScrollChangeListener {
            override fun onScrollChange(
                v: NestedScrollView,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (scrollY > oldScrollY) {
                    Log.i(TAG, "Scroll DOWN")
                }
                if (scrollY < oldScrollY) {
                    Log.i(TAG, "Scroll UP")
                }
                if (scrollY == 0 && !isLoading && isPrevScrollEnabled) {
                    Log.i(TAG, "TOP SCROLL")
                    isLoading = true
                    prevPage++
                    isLoading = lazyLoadListener.onScrollPrev(prevPage, scrollY)
                }
                if (!isLoading && isNextScrollEnabled && scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                    isLoading = true
                    nextPage++
                    isLoading = lazyLoadListener.onScrollNext(nextPage, scrollX)
                    Log.i(TAG, "BOTTOM SCROLL")
                }
            }
        })
    }

    fun reset() {
        nextPage = 1
        prevPage = 1
        isLoading = false
        isPrevScrollEnabled = true
        isNextScrollEnabled = true
    }

    fun setPrevScroll(scrollingEnabled: Boolean) {
        isPrevScrollEnabled = scrollingEnabled
    }

    fun setNextScroll(scrollingEnabled: Boolean) {
        isNextScrollEnabled = scrollingEnabled
    }
}