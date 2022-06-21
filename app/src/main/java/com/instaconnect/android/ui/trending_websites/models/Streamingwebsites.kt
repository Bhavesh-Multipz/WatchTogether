package com.instaconnect.android.ui.trending_websites.models

import com.google.gson.annotations.SerializedName

class Streamingwebsites {
    @SerializedName("Total")
    var total = 0

    @SerializedName("websites")
    var websites: ArrayList<WebsitesItem>? = null
}