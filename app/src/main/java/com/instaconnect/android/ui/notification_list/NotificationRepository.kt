package com.instaconnect.android.ui.notification_list

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi

class NotificationRepository constructor(private val api: MyApi) : BaseRepository() {

    suspend fun getNotificationList(
        user_id: String,
        page: Int,
        ) = safeApiCall {
        api.getNotificationList(user_id, page)
    }

    suspend fun clearAllNotification(
        user_id: String,
        ) = safeApiCall {
        api.clearAllNotification(user_id)
    }
}