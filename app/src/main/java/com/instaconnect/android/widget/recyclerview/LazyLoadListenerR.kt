package com.instaconnect.android.widget.recyclerview

/**
 * Created by bhavesh on 19/04/2022.
 */
interface LazyLoadListenerR {
    fun onScrollNext(page: Int, totalItemsCount: Int): Boolean
    fun onScrollPrev(page: Int, totalItemsCount: Int): Boolean
}