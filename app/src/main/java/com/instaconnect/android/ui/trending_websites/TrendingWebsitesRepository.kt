package com.instaconnect.android.ui.trending_websites

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi

class TrendingWebsitesRepository constructor(private val api: MyApi) : BaseRepository() {

    suspend fun getTrendingWebsites(
    ) = safeApiCall {
        api.getTrendingWebsites()
    }
}