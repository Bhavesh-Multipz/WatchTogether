package com.instaconnect.android.ui.friends.my_friends

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

class MyFriendsViewModel(private val repository: MyFriendsRepository) : BaseViewModel(repository) {

    private val _myFriendsResponse: MutableLiveData<Resource<FriendListModel>> = MutableLiveData()
    val myFriendsResponse: LiveData<Resource<FriendListModel>>
        get() = _myFriendsResponse

    private val _makeUnfriendResponse: MutableLiveData<Resource<UnfriendResponse>> = MutableLiveData()
    val makeUnfriendResponse: LiveData<Resource<UnfriendResponse>>
        get() = _makeUnfriendResponse

    suspend fun getMyFriendList(
        userId: String,
        search: String,
        page: Int,
    ) = viewModelScope.launch {
        _myFriendsResponse.value = Resource.Loading
        _myFriendsResponse.value = repository.getMyFriendList(userId, search, page)
    }

    suspend fun makeUnfriendUser(
        userId: String,
        otherUserId: String
    ) = viewModelScope.launch {
        _makeUnfriendResponse.value = Resource.Loading
        _makeUnfriendResponse.value = repository.makeUnfriendUser(userId, otherUserId)
    }





}