package com.instaconnect.android.ui.fragment.add_post

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import kotlin.math.ln

class CaptureFragmentRepository constructor(private val api : MyApi): BaseRepository() {

    suspend fun getWebLinks(
        device_type: String
    ) = safeApiCall {
            api.getWebLinks(device_type)
    }

}