package com.instaconnect.android.model

import com.google.gson.annotations.SerializedName

data class CommonResponse(

	@field:SerializedName("response")
	val response: Response? = null
)

data class Response(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
