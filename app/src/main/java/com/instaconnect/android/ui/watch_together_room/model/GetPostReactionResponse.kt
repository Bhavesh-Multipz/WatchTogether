package com.instaconnect.android.ui.watch_together_room

import com.google.gson.annotations.SerializedName

data class GetPostReactionResponse(

	@field:SerializedName("post_id")
	val postId: String? = null,

	@field:SerializedName("success")
	val success: Int? = null,

	@field:SerializedName("messageData")
	val messageData: MessageData? = null
)

data class MessageData(

	@field:SerializedName("dislikes")
	val dislikes: Int? = null,

	@field:SerializedName("yourReaction")
	val yourReaction: String? = null,

	@field:SerializedName("likes")
	val likes: Int? = null
)
