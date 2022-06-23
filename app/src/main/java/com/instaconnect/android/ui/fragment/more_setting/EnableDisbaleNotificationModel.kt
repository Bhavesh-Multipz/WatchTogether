package com.instaconnect.android.ui.fragment.more_setting

import com.google.gson.annotations.SerializedName

class EnableDisbaleNotificationModel {
    @SerializedName("response")
    var response: Response? = null

    inner class Response {
        @SerializedName("code")
        var code: String? = null

        @SerializedName("notification_status")
        var notificationStatus: String? = null

        @SerializedName("message")
        var message: String? = null
    }
}