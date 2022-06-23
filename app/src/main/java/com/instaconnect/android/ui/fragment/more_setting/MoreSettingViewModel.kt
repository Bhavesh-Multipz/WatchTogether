package com.instaconnect.android.ui.fragment.more_setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.network.Resource
import kotlinx.coroutines.launch

class MoreSettingViewModel(private val repository: MoreSettingRepository) : BaseViewModel(repository) {


    private val _toggleNotificationResponse: MutableLiveData<Resource<EnableDisbaleNotificationModel>> = MutableLiveData()
    val toggleNotificationResponse: LiveData<Resource<EnableDisbaleNotificationModel>>
        get() = _toggleNotificationResponse

    suspend fun enableDisableNotification(
        user_id: String,
        noti_status : String
    ) = viewModelScope.launch {
        _toggleNotificationResponse.value = Resource.Loading
        _toggleNotificationResponse.value = repository.enableDisableNotification(user_id, noti_status)
    }



}