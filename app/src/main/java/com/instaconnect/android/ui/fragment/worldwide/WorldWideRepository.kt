package com.instaconnect.android.ui.fragment.worldwide

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import kotlin.math.ln

class WorldWideRepository constructor(private val api : MyApi): BaseRepository() {

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

    suspend fun reportPost(
        postId: String,
        reason: String,
        userId: String
    ) = safeApiCall {
        api.reportPost(postId, reason, userId)
    }

    suspend fun blocUser(
        blocUserId: String,
        status: String,
        userId: String
    ) = safeApiCall {
        api.blockUser(blocUserId, status, userId)
    }
}