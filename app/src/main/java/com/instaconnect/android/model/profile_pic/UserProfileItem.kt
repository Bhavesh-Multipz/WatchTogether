package com.instaconnect.android.model.profile_pic

import com.google.gson.annotations.SerializedName

class UserProfileItem {
    @SerializedName("user_id")
    var userId: String? = null

    @SerializedName("profile_pic")
    var profilePic: String? = null
}