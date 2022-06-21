package com.instaconnect.android.ui.fragment.add_post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.network.Resource
import kotlinx.coroutines.launch

class CaptureFragmentViewModel(private val repository: CaptureFragmentRepository) : BaseViewModel(repository) {

    private var postType: String? = null
    private var captureType: String? = null

    private val _getWebLinksResponse: MutableLiveData<Resource<WebLinkResponse>> = MutableLiveData()
    val getWebLinksResponse: LiveData<Resource<WebLinkResponse>>
        get() = _getWebLinksResponse

    suspend fun getWebLinks(
        device_type: String
    ) = viewModelScope.launch {
        _getWebLinksResponse.value = Resource.Loading
        _getWebLinksResponse.value = repository.getWebLinks(device_type)
    }


    fun setPostType(postType: String) {
        this.postType = postType
    }

    fun setCaptureType(captureType: String) {
        this.captureType = captureType
    }
}