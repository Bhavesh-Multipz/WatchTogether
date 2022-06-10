package com.instaconnect.android.ui.fragment.worldwide

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.data.model.BlockUser
import com.instaconnect.android.data.model.ReportPost
import com.instaconnect.android.network.Resource
import kotlinx.coroutines.launch

class WorldWideViewModel(private val repository: WorldWideRepository) : BaseViewModel(repository) {

    private val _getWatchlistResponse: MutableLiveData<Resource<Post>> = MutableLiveData()
    val getWatchlistResponse: LiveData<Resource<Post>>
        get() = _getWatchlistResponse

    private val _reportPostResponse: MutableLiveData<Resource<ReportPost>> = MutableLiveData()
    val reportPostResponse: LiveData<Resource<ReportPost>>
        get() = _reportPostResponse

    private val _blockUserResponse: MutableLiveData<Resource<BlockUser>> = MutableLiveData()
    val blockUserResponse: LiveData<Resource<BlockUser>>
        get() = _blockUserResponse

    suspend fun getWatchList(
        userId: String,
        category: String,
        page: String,
        country: String,
        radius: String,
        lat: String,
        lng: String
    ) = viewModelScope.launch {
        _getWatchlistResponse.value = Resource.Loading
        _getWatchlistResponse.value = repository.getWatchList(userId, category, page, country,radius,lat, lng)
    }

    suspend fun reportPost(
        postId: String,
        reason: String,
        userId: String,
    ) = viewModelScope.launch {
        _reportPostResponse.value = Resource.Loading
        _reportPostResponse.value = repository.reportPost(postId, reason, userId)
    }

    suspend fun blocUser(
        blocUserId: String,
        status: String,
        userId: String
    ) = viewModelScope.launch {
        _blockUserResponse.value = Resource.Loading
        _blockUserResponse.value = repository.blocUser(blocUserId, status, userId)
    }

}