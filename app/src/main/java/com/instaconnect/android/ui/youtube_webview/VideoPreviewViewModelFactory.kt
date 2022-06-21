package com.instaconnect.android.ui.youtube_webview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VideoPreviewViewModelFactory(private val repository: VideoPreviewRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VideoPreviewViewModel(repository) as T
    }
}