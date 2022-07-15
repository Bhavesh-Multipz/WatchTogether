package com.instaconnect.android.ui.watch_together_room

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import okhttp3.MultipartBody

class WatchTogetherVideoRepository constructor(private val api : MyApi) : BaseRepository(){

    suspend fun getFriendListForWatchTogetherRoom(
        user_id: String,
        search: String,
        postId: String,
        page: Int
    ) = safeApiCall {
        api.getFriendListForWatchTogetherRoom(user_id, search,postId, page)
    }

    suspend fun invitePeopleToWatchVideo(
        user_id: String,
        other_user_id: String,
        post_id: String
    ) = safeApiCall {
        api.invitePeopleToWatchVideo(user_id, other_user_id, post_id)
    }

    suspend fun addPostReaction(
        post_id: String,
        reaction: String,
        userId: String
    ) = safeApiCall {
        api.addPostReaction(post_id, reaction, userId)
    }

    suspend fun userRating(
        userId: String,
        appVersion: String,
        isRated: String,
    ) = safeApiCall {
        api.userRating(userId,appVersion,isRated)
    }

    suspend fun deletePost(
        postId: String
    ) = safeApiCall {
        api.deletePost(postId)
    }
}