package com.instaconnect.android.ui.watch_together_room

import com.google.gson.annotations.SerializedName

data class RatingResponse(

	@field:SerializedName("response")
	val response: Response? = null
)

data class Response(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("is_rated")
	val isRated: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
