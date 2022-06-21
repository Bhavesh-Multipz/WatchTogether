package com.instaconnect.android.ui.trending_websites.models

import com.google.gson.annotations.SerializedName
import com.instaconnect.android.ui.trending_websites.models.Streamingwebsites
import com.instaconnect.android.ui.trending_websites.models.WebsitesItem

class Response {
    @SerializedName("code")
    var code: String? = null

    @SerializedName("streamingwebsites")
    var streamingwebsites: Streamingwebsites? = null

    @SerializedName("message")
    var message: String? = null
}