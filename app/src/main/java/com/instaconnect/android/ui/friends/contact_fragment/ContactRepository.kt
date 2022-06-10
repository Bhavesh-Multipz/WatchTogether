package com.instaconnect.android.ui.friends.contact_fragment

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import kotlin.math.ln

class ContactRepository constructor(private val api : MyApi): BaseRepository() {

    suspend fun getWatchList(
        userId: String,
        category: String,
        page: String,
        country: String,
        radius: String,
        lat: String,
        lng: String
    ) = safeApiCall {
            api.getWatchList(userId, category, page, country,radius,lat, lng)
    }


}