package com.instaconnect.android.ui.trending_websites.models

import com.google.gson.annotations.SerializedName

class WebsitesItem {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("link")
    var link: String? = null
}