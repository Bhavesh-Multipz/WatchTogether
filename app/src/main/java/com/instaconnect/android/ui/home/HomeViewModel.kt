package com.instaconnect.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.data.model.BlockUser
import com.instaconnect.android.data.model.ReportPost
import com.instaconnect.android.model.LoadPreferenceResponse
import com.instaconnect.android.network.Resource
import com.instaconnect.android.utils.models.Response
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : BaseViewModel(repository) {


    private val _loadPreferenceResponse: MutableLiveData<Resource<LoadPreferenceResponse>> = MutableLiveData()
    val loadPreferenceResponse: LiveData<Resource<LoadPreferenceResponse>>
        get() = _loadPreferenceResponse

    private val _updateTokenResponse: MutableLiveData<Resource<UpdateTokenResponse>> = MutableLiveData()
    val updateTokenResponse: LiveData<Resource<UpdateTokenResponse>>
        get() = _updateTokenResponse

    suspend fun loadPreference(
        version: String,
        device_type: String,
        userId: String,
    ) = viewModelScope.launch {
        _loadPreferenceResponse.value = Resource.Loading
        _loadPreferenceResponse.value = repository.loadPreference(version, device_type,userId)
    }

    suspend fun updateToken(
        deviceToken: String?,
        userId: String?,
        deviceType: String?,
        voipToken: String?,
    ) = viewModelScope.launch {
        _updateTokenResponse.value = Resource.Loading
        _updateTokenResponse.value = repository.updateToken(deviceToken,userId,deviceType,voipToken)
    }
}