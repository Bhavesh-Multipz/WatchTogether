package com.instaconnect.android.utils.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Response{

	@SerializedName("code")
	private String code;

	@SerializedName("messagelist")
	private List<MessagelistItem> messagelist;

	@SerializedName("is_last_page")
	private Integer isLastPage;

	@SerializedName("message")
	private String message;

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return code;
	}

	public void setMessagelist(List<MessagelistItem> messagelist){
		this.messagelist = messagelist;
	}

	public List<MessagelistItem> getMessagelist(){
		return messagelist;
	}

	public void setIsLastPage(Integer isLastPage){
		this.isLastPage = isLastPage;
	}

	public Integer getIsLastPage(){
		return isLastPage;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}