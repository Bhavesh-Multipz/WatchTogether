package com.instaconnect.android.ui.block_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.login.SendOtp
import kotlinx.coroutines.launch

class BlockedUserViewModel constructor(private val repository: BlockedUserRepository) : BaseViewModel(repository){

    private val _blockedUserListResponse: MutableLiveData<Resource<BlockUserResponse>> = MutableLiveData()
    val blockedUserListResponse: LiveData<Resource<BlockUserResponse>>
        get() = _blockedUserListResponse

    private val _unblockUserResponse: MutableLiveData<Resource<FollowUser>> = MutableLiveData()
    val unblockUserResponse: LiveData<Resource<FollowUser>>
        get() = _unblockUserResponse

    suspend fun getBlockUserList(
        userId: String,
    ) = viewModelScope.launch {
        _blockedUserListResponse.value = Resource.Loading
        _blockedUserListResponse.value = repository.getBlockUserList(userId)
    }

    suspend fun callUnblockUser(
        blockedUserId: String?,
        status: String?,
        userId: String?,
    ) = viewModelScope.launch {
        _unblockUserResponse.value = Resource.Loading
        _unblockUserResponse.value = repository.callUnblockUser(blockedUserId,status,userId)
    }
}
