package com.instaconnect.android.ui.home

import com.google.gson.annotations.SerializedName

data class UpdateTokenResponse(

	@field:SerializedName("response")
	val response: Response? = null
)

data class Response(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("notification_status")
	val notificationStatus: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)
