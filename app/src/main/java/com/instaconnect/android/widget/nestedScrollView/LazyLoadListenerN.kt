package com.instaconnect.android.widget.nestedScrollView

/**
 * Created by jatin on 6/8/2017.
 */
interface LazyLoadListenerN {
    fun onScrollNext(page: Int, totalItemsCount: Int): Boolean
    fun onScrollPrev(page: Int, totalItemsCount: Int): Boolean
}