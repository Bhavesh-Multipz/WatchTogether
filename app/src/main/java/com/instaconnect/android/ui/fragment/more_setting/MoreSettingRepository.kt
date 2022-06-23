package com.instaconnect.android.ui.fragment.more_setting

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi

class MoreSettingRepository constructor(private val api : MyApi): BaseRepository() {

    suspend fun enableDisableNotification(
        userId: String,
        noti_status : String
    ) = safeApiCall {
            api.enableDisableNotification(userId,noti_status)
    }

}