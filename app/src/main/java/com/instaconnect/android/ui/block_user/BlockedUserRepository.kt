package com.instaconnect.android.ui.block_user

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import retrofit2.http.Field

class BlockedUserRepository constructor(private val api: MyApi) : BaseRepository() {

    suspend fun getBlockUserList(
        userId: String
    ) = safeApiCall {
        api.getBlockUserList(userId)
    }

    suspend fun callUnblockUser(
        blockedUserId: String?,
        status: String?,
        userId: String?,
    ) = safeApiCall {
        api.callUnblockUser(blockedUserId,status,userId)
    }
}