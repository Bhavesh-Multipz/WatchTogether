package com.instaconnect.android.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.network.Resource
import kotlinx.coroutines.launch

class LoginViewModel constructor(private val repository: LoginRepository) : BaseViewModel(repository){

    private val _sendSocialIdResponse: MutableLiveData<Resource<SendOtp>> = MutableLiveData()
    val sendSocialIdResponse: LiveData<Resource<SendOtp>>
        get() = _sendSocialIdResponse

    suspend fun sendSocialId(
        phone: String,
        code: String,
        device_token: String,
        device_type: String,
        userName: String,
        userProfileUrl: String
    ) = viewModelScope.launch {
        _sendSocialIdResponse.value = Resource.Loading
        _sendSocialIdResponse.value = repository.sendSocialId(phone, code, device_token, device_type,userName,userProfileUrl)
    }
}
