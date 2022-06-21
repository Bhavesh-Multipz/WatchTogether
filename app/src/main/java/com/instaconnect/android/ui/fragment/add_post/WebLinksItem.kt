package com.instaconnect.android.ui.fragment.add_post

import com.google.gson.annotations.SerializedName

class WebLinksItem {
    @SerializedName("image")
    var image = ""

    @SerializedName("dateupdated")
    var dateupdated = ""

    @SerializedName("name")
    var name = ""

    @SerializedName("link")
    var link = ""

    @SerializedName("device_type")
    var deviceType: String? = null

    @SerializedName("id")
    var id = ""

    @SerializedName("datecreated")
    var datecreated = ""

    @SerializedName("status")
    var status = ""
}