package com.instaconnect.android.ui.friends.add_friend_fragment

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import kotlin.math.ln

class AddFriendRepository constructor(private val api : MyApi): BaseRepository() {

    suspend fun getAddFriendList(
        user_id: String,
        search: String,
        page: Int
    ) = safeApiCall {
        api.getAddFriendList(user_id, search, page)
    }

    suspend fun sendFriendRequest(
        user_id: String,
        otherUserId: String
    ) = safeApiCall {
        api.sendFriendRequest(user_id, otherUserId)
    }


}