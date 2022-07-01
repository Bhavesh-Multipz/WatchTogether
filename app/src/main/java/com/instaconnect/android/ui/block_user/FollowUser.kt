package com.instaconnect.android.ui.block_user

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FollowUser {
    @SerializedName("response")
    @Expose
    var response: Response? = null

    inner class Follow {
        @SerializedName("status")
        @Expose
        var status: Int? = null
    }

    inner class Response {
        @SerializedName("code")
        @Expose
        var code: String? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("follow")
        @Expose
        var follow: Follow? = null
    }
}