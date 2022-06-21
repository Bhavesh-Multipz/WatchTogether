package com.instaconnect.android.ui.friends.friend_request_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.network.Resource
import kotlinx.coroutines.launch

class FriendsRequestViewModel(private val repository: FriendsRequestRepository) : BaseViewModel(repository) {

    private val _getFriendListResponse: MutableLiveData<Resource<FriendListModel>> = MutableLiveData()
    val getFriendListResponse: LiveData<Resource<FriendListModel>>
        get() = _getFriendListResponse

    private val _getFriendRequestResponse: MutableLiveData<Resource<FriendListModel>> = MutableLiveData()
    val getFriendRequestResponse: LiveData<Resource<FriendListModel>>
        get() = _getFriendRequestResponse

    suspend fun getFriendRequestResponse(
        userId: String,
        otherUserId: String,
        responseType: String
    ) = viewModelScope.launch {
        _getFriendRequestResponse.value = Resource.Loading
        _getFriendRequestResponse.value = repository.getFriendRequestResponse(userId, otherUserId, responseType)
    }

    suspend fun getFriendRequest(
        userId: String,
        search: String,
        page: Int
    ) = viewModelScope.launch {
        _getFriendListResponse.value = Resource.Loading
        _getFriendListResponse.value = repository.getFriendRequest(userId, search, page)
    }

}