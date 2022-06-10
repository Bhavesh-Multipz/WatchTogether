package com.instaconnect.android.ui.login

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class SendOtp {
    @SerializedName("response")
    @Expose
    var response: Response? = null

    inner class Response {
        @SerializedName("code")
        @Expose
        var code: String? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("user_profile_url")
        @Expose
        var userProfileUrl: String? = null
    }
}