package com.instaconnect.android.ui.watch_together_room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.fragment.worldwide.InvitePeopleForWatchTogether
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class WatchTogetherVideoViewModel constructor(private val repository : WatchTogetherVideoRepository) : BaseViewModel(repository) {

    private val _friendListResponse: MutableLiveData<Resource<FriendListModel>> = MutableLiveData()
    val friendListResponse: LiveData<Resource<FriendListModel>>
        get() = _friendListResponse

    private val _invitePeopleResponse: MutableLiveData<Resource<InvitePeopleForWatchTogether>> = MutableLiveData()
    val invitePeopleResponse: LiveData<Resource<InvitePeopleForWatchTogether>>
        get() = _invitePeopleResponse

    suspend fun getAddFriendList(
        user_id: String,
        search: String,
        page: Int
    ) = viewModelScope.launch {
        _friendListResponse.value = Resource.Loading
        _friendListResponse.value = repository.getAddFriendList(user_id, search, page)
    }

    suspend fun invitePeopleToWatchVideo(
        user_id: String,
        other_user_id: String,
        post_id: String
    ) = viewModelScope.launch {
        _invitePeopleResponse.value = Resource.Loading
        _invitePeopleResponse.value = repository.invitePeopleToWatchVideo(user_id, other_user_id, post_id)
    }
}