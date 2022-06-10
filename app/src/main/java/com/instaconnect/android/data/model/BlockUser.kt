package com.instaconnect.android.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlockUser {
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
    }
}