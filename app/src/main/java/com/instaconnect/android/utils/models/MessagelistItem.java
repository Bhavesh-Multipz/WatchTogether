package com.instaconnect.android.utils.models;

import com.google.gson.annotations.SerializedName;

public class MessagelistItem{

	@SerializedName("post_detail_arr")
	private PostDetailArr postDetailArr;

	@SerializedName("notify_type")
	private String notifyType;

	@SerializedName("message")
	private String message;

	public void setPostDetailArr(PostDetailArr postDetailArr){
		this.postDetailArr = postDetailArr;
	}

	public PostDetailArr getPostDetailArr(){
		return postDetailArr;
	}

	public void setNotifyType(String notifyType){
		this.notifyType = notifyType;
	}

	public String getNotifyType(){
		return notifyType;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}