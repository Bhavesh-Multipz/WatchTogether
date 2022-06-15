package com.instaconnect.android.data.model

import com.google.gson.annotations.SerializedName

data class ChatDataNewResponse(

	@field:SerializedName("post_id")
	val postId: String? = null,

	@field:SerializedName("success")
	val success: Int? = null,

	@field:SerializedName("messageData")
	val messageData: ArrayList<MessageDataItem>? = null,

	@field:SerializedName("result")
	val result: MessageDataItem? = null

)

data class MessageDataItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("user_profile")
	val userProfile: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("send_by")
	val sendBy: String? = null,

	@field:SerializedName("video_id")
	val videoId: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

)
