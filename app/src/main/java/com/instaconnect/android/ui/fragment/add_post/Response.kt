package com.instaconnect.android.ui.fragment.add_post

import com.google.gson.annotations.SerializedName
import com.instaconnect.android.ui.fragment.add_post.WebLinksItem

class Response {
    @SerializedName("code")
    var code: String? = null

    @SerializedName("web_links")
    var webLinks: ArrayList<WebLinksItem>? = null

    @SerializedName("message")
    var message: String? = null
}