package com.instaconnect.android.ui.notification_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.model.CommonResponse
import com.instaconnect.android.network.Resource
import kotlinx.coroutines.launch

class NotificationViewModel constructor(private val repository: NotificationRepository) : BaseViewModel(repository){

    private val _getNotificationListResponse: MutableLiveData<Resource<NotificationListModelNew>> = MutableLiveData()
    val getNotificationListResponse: LiveData<Resource<NotificationListModelNew>>
        get() = _getNotificationListResponse

    private val _clearAllNotificationResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val clearAllNotificationResponse: LiveData<Resource<CommonResponse>>
        get() = _clearAllNotificationResponse

    suspend fun getNotificationList(
        user_id: String,
        page: Int,
    ) = viewModelScope.launch {
        _getNotificationListResponse.value = Resource.Loading
        _getNotificationListResponse.value = repository.getNotificationList(user_id, page)
    }

    suspend fun clearAllNotification(
        user_id: String
    ) = viewModelScope.launch {
        _clearAllNotificationResponse.value = Resource.Loading
        _clearAllNotificationResponse.value = repository.clearAllNotification(user_id)
    }
}
