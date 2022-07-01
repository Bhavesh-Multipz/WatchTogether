package com.instaconnect.android.ui.fragment.more_setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.model.CommonResponse
import com.instaconnect.android.network.Resource
import kotlinx.coroutines.launch

class MoreSettingViewModel(private val repository: MoreSettingRepository) : BaseViewModel(repository) {


    private val _toggleNotificationResponse: MutableLiveData<Resource<EnableDisbaleNotificationModel>> = MutableLiveData()
    val toggleNotificationResponse: LiveData<Resource<EnableDisbaleNotificationModel>>
        get() = _toggleNotificationResponse

    private val _deleteAccountResponse: MutableLiveData<Resource<CommonResponse>> = MutableLiveData()
    val deleteAccountResponse: LiveData<Resource<CommonResponse>>
        get() = _deleteAccountResponse

    suspend fun enableDisableNotification(
        user_id: String,
        noti_status : String
    ) = viewModelScope.launch {
        _toggleNotificationResponse.value = Resource.Loading
        _toggleNotificationResponse.value = repository.enableDisableNotification(user_id, noti_status)
    }

    suspend fun deleteUserAccount(
        user_id: String
    ) = viewModelScope.launch {
        _deleteAccountResponse.value = Resource.Loading
        _deleteAccountResponse.value = repository.deleteUserAccount(user_id)
    }



}