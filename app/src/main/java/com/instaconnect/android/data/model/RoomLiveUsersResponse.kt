package com.instaconnect.android.data.model

import com.google.gson.annotations.SerializedName

data class RoomLiveUsersResponse(

	@field:SerializedName("post_id")
	val postId: String? = null,

	@field:SerializedName("liveusers")
	val liveUsers: ArrayList<LiveUsersItem> = ArrayList()
)

data class LiveUsersItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("post_id")
	val postId: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("cid")
	val cid: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
