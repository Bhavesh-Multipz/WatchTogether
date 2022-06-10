package com.instaconnect.android.utils.models

import com.google.gson.annotations.SerializedName
import com.instaconnect.android.utils.Model

class User : Model() {
    @SerializedName(value = "name", alternate = ["username"])
    var name = ""

    @SerializedName(value = "user_id", alternate = ["block_user_id"])
    var user_id = ""
    var phone = ""
    var chatBackground = ""

    @SerializedName(value = "avatar", alternate = ["profile_pic"])
    var avatar = ""
    var isPrivate = ""

    @SerializedName("follow")
    private val followModel: FollowModel? = null

    inner class FollowModel {
        var status = 0
    }
}