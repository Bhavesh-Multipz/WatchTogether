package com.instaconnect.android.ui.login

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi

class LoginRepository constructor(private val api: MyApi) : BaseRepository() {

    suspend fun sendSocialId(
        phone: String,
        code: String,
        device_token: String,
        device_type: String,
        userName: String,
        userProfileUrl: String,
    ) = safeApiCall {
        api.sendSocialId(phone, code, device_token, device_type, userName, userProfileUrl
        )
    }
}