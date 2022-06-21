package com.instaconnect.android.ui.youtube_webview

import com.google.gson.annotations.SerializedName

class YoutubeVideoDetails {
    @SerializedName("author_name")
    var authorName: String? = null

    @SerializedName("provider_url")
    var providerUrl: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("thumbnail_url")
    var thumbnailUrl: String? = null

    @SerializedName("version")
    var version: String? = null

    @SerializedName("url")
    var url: String? = null

    @SerializedName("thumbnail_height")
    var thumbnailHeight = 0

    @SerializedName("author_url")
    var authorUrl: String? = null

    @SerializedName("width")
    var width = 0

    @SerializedName("thumbnail_width")
    var thumbnailWidth = 0

    @SerializedName("html")
    var html: String? = null

    @SerializedName("provider_name")
    var providerName: String? = null

    @SerializedName("height")
    var height = 0
}