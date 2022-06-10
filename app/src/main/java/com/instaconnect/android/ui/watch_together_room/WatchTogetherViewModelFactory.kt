package com.instaconnect.android.ui.watch_together_room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WatchTogetherViewModelFactory(private val repository: WatchTogetherVideoRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WatchTogetherVideoViewModel(repository) as T
    }
}