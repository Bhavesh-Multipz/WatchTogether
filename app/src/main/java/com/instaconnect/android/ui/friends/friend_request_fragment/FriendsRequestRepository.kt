package com.instaconnect.android.ui.friends.friend_request_fragment

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import kotlin.math.ln

class FriendsRequestRepository constructor(private val api : MyApi): BaseRepository() {

    suspend fun getFriendRequestResponse(
        userId: String,
        otherUserId: String,
        responseType: String,
    ) = safeApiCall {
            api.getFriendRequestResponse(userId, otherUserId, responseType)
    }

    suspend fun getFriendRequest(
        userId: String,
        search: String,
        page: Int,
    ) = safeApiCall {
            api.getFriendRequest(userId, search, page)
    }


}