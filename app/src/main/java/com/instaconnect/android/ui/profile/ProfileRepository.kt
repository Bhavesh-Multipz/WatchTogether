package com.instaconnect.android.ui.profile

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import okhttp3.MultipartBody

class ProfileRepository constructor(private val api : MyApi) : BaseRepository() {

    suspend fun addProfile(
        files: MultipartBody.Part,
        params: Map<String,String>
    ) = safeApiCall {
        api.userProfile(files, params)
    }

}