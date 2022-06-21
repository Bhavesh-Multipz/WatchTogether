package com.instaconnect.android.ui.friends.add_friend_fragment

import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.data.model.db.Contacts
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.fragment.worldwide.Post
import com.instaconnect.android.utils.RegexUtil
import com.instaconnect.android.utils.helper_classes.ContactUtil
import gun0912.tedimagepicker.util.ToastUtil.context
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class AddFriendFragmentViewModel(private val repository: AddFriendRepository) : BaseViewModel(repository) {

    private val _friendListResponse: MutableLiveData<Resource<FriendListModel>> = MutableLiveData()
    val friendListResponse: LiveData<Resource<FriendListModel>>
        get() = _friendListResponse

    private val _sendFriendRequestResponse: MutableLiveData<Resource<FriendListModel>> = MutableLiveData()
    val sendFriendRequestResponse: LiveData<Resource<FriendListModel>>
        get() = _sendFriendRequestResponse

    suspend fun getAddFriendList(
        user_id: String,
        search: String,
        page: Int
    ) = viewModelScope.launch {
        _friendListResponse.value = Resource.Loading
        _friendListResponse.value = repository.getAddFriendList(user_id, search, page)
    }

    suspend fun sendFriendRequest(
        user_id: String,
        otherUserId: String
    ) = viewModelScope.launch {
        _sendFriendRequestResponse.value = Resource.Loading
        _sendFriendRequestResponse.value = repository.sendFriendRequest(user_id, otherUserId)
    }

}