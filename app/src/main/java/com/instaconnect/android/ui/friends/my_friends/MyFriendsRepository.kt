package com.instaconnect.android.ui.friends.my_friends

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import kotlin.math.ln

class MyFriendsRepository constructor(private val api : MyApi): BaseRepository() {

    suspend fun getMyFriendList(
        userId: String,
        search: String,
        page: Int,
    ) = safeApiCall {
            api.getMyFriendList(userId, search, page)
    }

    suspend fun makeUnfriendUser(
        userId: String,
        otherUserId: String
    ) = safeApiCall {
            api.makeUnfriendUser(userId, otherUserId)
    }


}