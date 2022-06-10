package com.instaconnect.android.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.network.Resource
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel constructor(private val repository: ProfileRepository) : BaseViewModel(repository){

    private val _profileResponse: MutableLiveData<Resource<ProfileResponse>> = MutableLiveData()
    val profileResponse: LiveData<Resource<ProfileResponse>>
        get() = _profileResponse

    suspend fun addProfile(
        files: MultipartBody.Part,
        params: Map<String,String>
    ) = viewModelScope.launch {
        _profileResponse.value = Resource.Loading
        _profileResponse.value = repository.addProfile(files, params)
    }

}