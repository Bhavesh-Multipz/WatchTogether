package com.instaconnect.android.ui.notification_list

import com.google.gson.annotations.SerializedName
import com.instaconnect.android.utils.Model
import com.instaconnect.android.utils.models.Response
import java.io.Serializable

class NotificationListModelNew : Model(), Serializable {
    @SerializedName("response")
    var response: Response? = null
}