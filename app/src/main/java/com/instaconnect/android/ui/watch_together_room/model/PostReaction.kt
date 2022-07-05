package com.instaconnect.android.ui.watch_together_room.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PostReaction {
    @SerializedName("response")
    @Expose
    var response: Response? = null

    inner class Reaction {
        @SerializedName("likes")
        @Expose
        var likes: Int? = null

        @SerializedName("dislikes")
        @Expose
        var dislikes: Int? = null

        @SerializedName("yourReaction")
        @Expose
        var yourReaction: String? = null
    }

    inner class Response {
        @SerializedName("code")
        @Expose
        var code: String? = null

        @SerializedName("message")
        @Expose
        var message: String? = null

        @SerializedName("reaction")
        @Expose
        var reaction: Reaction? = null
    }
}