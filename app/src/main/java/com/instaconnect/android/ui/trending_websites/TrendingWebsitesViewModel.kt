package com.instaconnect.android.ui.trending_websites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.trending_websites.models.TrendingWebsiteResponseModel
import kotlinx.coroutines.launch

class TrendingWebsitesViewModel constructor(private val repository: TrendingWebsitesRepository) : BaseViewModel(repository){

    private val _trendingWebsitesResponse: MutableLiveData<Resource<TrendingWebsiteResponseModel>> = MutableLiveData()
    val trendingWebsitesResponse: LiveData<Resource<TrendingWebsiteResponseModel>>
        get() = _trendingWebsitesResponse

    suspend fun getTrendingWebsites(

    ) = viewModelScope.launch {
        _trendingWebsitesResponse.value = Resource.Loading
        _trendingWebsitesResponse.value = repository.getTrendingWebsites()
    }
}
