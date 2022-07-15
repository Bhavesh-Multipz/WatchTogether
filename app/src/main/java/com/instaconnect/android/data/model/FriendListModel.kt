package com.instaconnect.android.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.instaconnect.android.utils.Model
import java.io.Serializable
import java.util.ArrayList

class FriendListModel : Model(), Serializable {
    @SerializedName("response")
    @Expose
    var response: Response? = null

    inner class Response : Serializable {
        @SerializedName("is_last_page")
        @Expose
        var isLastPage: Int? = null

        @SerializedName("code")
        @Expose
        var code: String? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("userlist")
        @Expose
        var userlist: ArrayList<User>? = null
    }

    inner class User : Serializable {
        @SerializedName("user_id")
        @Expose
        var userId: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("profile_pic")
        @Expose
        var profilePic: String? = null

        @SerializedName("profile_url")
        @Expose
        var profileUrl: String? = null

        @SerializedName("is_online")
        @Expose
        var isOnline: String? = null

        @SerializedName("datecreated")
        @Expose
        var datecreated: String? = null

        @SerializedName("is_friended")
        @Expose
        var is_friended = 0

        @SerializedName("is_invited")
        @Expose
        var is_invited = 0
    }
}