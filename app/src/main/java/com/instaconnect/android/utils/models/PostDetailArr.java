package com.instaconnect.android.utils.models;

import com.google.gson.annotations.SerializedName;

public class PostDetailArr{

	@SerializedName("date")
	private String date;

	@SerializedName("country")
	private String country;

	@SerializedName("report_count")
	private String reportCount;

	@SerializedName("is_video_link")
	private String isVideoLink;

	@SerializedName("gif")
	private String gif;

	@SerializedName("caption")
	private String caption;

	@SerializedName("total_views")
	private String totalViews;

	@SerializedName("media")
	private String media;

	@SerializedName("video")
	private String video;

	@SerializedName("media_ratio")
	private String mediaRatio;

	@SerializedName("uniqueCode")
	private String uniqueCode;

	@SerializedName("updated_at")
	private Object updatedAt;

	@SerializedName("id")
	private String id;

	@SerializedName("youTubeVideoId")
	private String youTubeVideoId;

	@SerializedName("lat")
	private String lat;

	@SerializedName("likes")
	private Integer likes;

	@SerializedName("image")
	private String image;

	@SerializedName("hyperlink")
	private String hyperlink;

	@SerializedName("thumbnail")
	private String thumbnail;

	@SerializedName("lng")
	private String lng;

	@SerializedName("group_name")
	private String groupName;

	@SerializedName("mediaType")
	private String mediaType;

	@SerializedName("group_password")
	private String groupPassword;

	@SerializedName("is_delete")
	private String isDelete;

	@SerializedName("user_id")
	private String userId;

	@SerializedName("userimage")
	private String userimage;

	@SerializedName("category")
	private String category;

	@SerializedName("username")
	private String username;

	@SerializedName("yourReaction")
	private String yourReaction;

	public void setDate(String date){
		this.date = date;
	}

	public String getDate(){
		return date;
	}

	public void setYourReaction(String yourReaction){
		this.yourReaction = yourReaction;
	}

	public String getYourReaction(){
		return yourReaction;
	}

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setReportCount(String reportCount){
		this.reportCount = reportCount;
	}

	public String getReportCount(){
		return reportCount;
	}

	public void setIsVideoLink(String isVideoLink){
		this.isVideoLink = isVideoLink;
	}

	public String getIsVideoLink(){
		return isVideoLink;
	}

	public void setGif(String gif){
		this.gif = gif;
	}

	public String getGif(){
		return gif;
	}

	public void setCaption(String caption){
		this.caption = caption;
	}

	public String getCaption(){
		return caption;
	}

	public void setTotalViews(String totalViews){
		this.totalViews = totalViews;
	}

	public String getTotalViews(){
		return totalViews;
	}

	public void setMedia(String media){
		this.media = media;
	}

	public String getMedia(){
		return media;
	}

	public void setVideo(String video){
		this.video = video;
	}

	public String getVideo(){
		return video;
	}

	public void setMediaRatio(String mediaRatio){
		this.mediaRatio = mediaRatio;
	}

	public String getMediaRatio(){
		return mediaRatio;
	}

	public void setUniqueCode(String uniqueCode){
		this.uniqueCode = uniqueCode;
	}

	public String getUniqueCode(){
		return uniqueCode;
	}

	public void setUpdatedAt(Object updatedAt){
		this.updatedAt = updatedAt;
	}

	public Object getUpdatedAt(){
		return updatedAt;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setYouTubeVideoId(String youTubeVideoId){
		this.youTubeVideoId = youTubeVideoId;
	}

	public String getYouTubeVideoId(){
		return youTubeVideoId;
	}

	public void setLat(String lat){
		this.lat = lat;
	}

	public String getLat(){
		return lat;
	}

	public void setLikes(Integer likes){
		this.likes = likes;
	}

	public Integer getLikes(){
		return likes;
	}

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setHyperlink(String hyperlink){
		this.hyperlink = hyperlink;
	}

	public String getHyperlink(){
		return hyperlink;
	}

	public void setThumbnail(String thumbnail){
		this.thumbnail = thumbnail;
	}

	public String getThumbnail(){
		return thumbnail;
	}

	public void setLng(String lng){
		this.lng = lng;
	}

	public String getLng(){
		return lng;
	}

	public void setGroupName(String groupName){
		this.groupName = groupName;
	}

	public String getGroupName(){
		return groupName;
	}

	public void setMediaType(String mediaType){
		this.mediaType = mediaType;
	}

	public String getMediaType(){
		return mediaType;
	}

	public void setGroupPassword(String groupPassword){
		this.groupPassword = groupPassword;
	}

	public String getGroupPassword(){
		return groupPassword;
	}

	public void setIsDelete(String isDelete){
		this.isDelete = isDelete;
	}

	public String getIsDelete(){
		return isDelete;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return userId;
	}

	public void setUserimage(String userimage){
		this.userimage = userimage;
	}

	public String getUserimage(){
		return userimage;
	}

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setUsername(String username){
		this.username = username;
	}

	public String getUsername(){
		return username;
	}
}