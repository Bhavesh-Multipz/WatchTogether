package com.instaconnect.android.ui.home

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import retrofit2.http.Field
import kotlin.math.ln

class HomeRepository constructor(private val api : MyApi): BaseRepository() {

    suspend fun loadPreference(
        version: String,
        device_type: String,
        userId: String,
    ) = safeApiCall {
        api.loadPreference(version, device_type, userId)
    }

    suspend fun updateToken(
        deviceToken: String?,
        userId: String?,
        deviceType: String?,
        voipToken: String?,
    ) = safeApiCall {
        api.updateToken(deviceToken, userId, deviceType,voipToken)
    }
}