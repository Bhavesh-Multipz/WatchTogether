package com.instaconnect.android.model

import com.google.gson.annotations.SerializedName

data class LoadPreferenceResponse(

	@field:SerializedName("response")
	val response: Response1? = null
)

data class Response1(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("alert_arr")
	val alertArr: AlertArr? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class AlertArr(

	@field:SerializedName("button_2")
	val button2: String? = null,

	@field:SerializedName("button_1")
	val button1: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("button_1_link")
	val button1Link: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
