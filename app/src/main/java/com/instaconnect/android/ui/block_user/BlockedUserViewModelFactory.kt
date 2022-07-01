package com.instaconnect.android.ui.block_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BlockedUserViewModelFactory(private val repository: BlockedUserRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BlockedUserViewModel(repository) as T
    }
}