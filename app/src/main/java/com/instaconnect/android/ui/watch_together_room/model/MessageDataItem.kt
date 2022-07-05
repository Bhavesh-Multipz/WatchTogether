package com.instaconnect.android.ui.watch_together_room.model

import com.google.gson.annotations.SerializedName

class MessageDataItem {

    @SerializedName("is_read")
    val isRead = 0

    @SerializedName("filename")
    val filename: String? = null

    @SerializedName("updated_at")
    val updatedAt: String? = null

    @SerializedName("chat_time")
    val chatTime: String? = null

    @SerializedName("user_id")
    val userId = 0

    @SerializedName("group_id")
    val groupId = 0

    @SerializedName("created_at")
    val createdAt: String? = null

    @SerializedName("id")
    val id = 0

    @SerializedName("message")
    val message: String = ""

    @SerializedName("totalpage")
    var totalPage: String? = ""

    @SerializedName("status")
    var status: String? = ""

    @SerializedName("type")
    val type = 0

}