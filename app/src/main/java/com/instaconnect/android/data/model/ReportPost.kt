package com.instaconnect.android.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ReportPost {
    @SerializedName("response")
    @Expose
    var response: Response? = null

    inner class Response {
        @SerializedName("report_count")
        @Expose
        var reportCount: Int? = null

        @SerializedName("code")
        @Expose
        var code: String? = null

        @SerializedName("message")
        @Expose
        var message: String? = null
    }
}