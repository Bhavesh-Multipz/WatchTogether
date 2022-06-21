package com.instaconnect.android.ui.trending_websites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TrendingWebsitesViewModelFactory(private val repository: TrendingWebsitesRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TrendingWebsitesViewModel(repository) as T
    }
}