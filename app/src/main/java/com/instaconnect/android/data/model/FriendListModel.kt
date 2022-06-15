package com.instaconnect.android.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.instaconnect.android.utils.Model;

import java.io.Serializable;
import java.util.List;

public class FriendListModel extends Model implements Serializable {

        @SerializedName("response")
        @Expose
        private Response response;

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }


    public class Response implements Serializable{

        @SerializedName("is_last_page")
        @Expose
        private Integer isLastPage;
        @SerializedName("code")
        @Expose
        private String code;
        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("userlist")
        @Expose
        private List<User> userlist = null;

        public Integer getIsLastPage() {
            return isLastPage;
        }

        public void setIsLastPage(Integer isLastPage) {
            this.isLastPage = isLastPage;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<User> getUserlist() {
            return userlist;
        }

        public void setUserlist(List<User> userlist) {
            this.userlist = userlist;
        }

    }


    public class User implements  Serializable{

        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("profile_pic")
        @Expose
        private String profilePic;
        @SerializedName("profile_url")
        @Expose
        private String profileUrl;
        @SerializedName("is_online")
        @Expose
        private String isOnline;
        @SerializedName("datecreated")
        @Expose
        private String datecreated;
        @SerializedName("is_friended")
        @Expose
        private int is_friended;

        public String getDatecreated() {
            return datecreated;
        }

        public void setDatecreated(String datecreated) {
            this.datecreated = datecreated;
        }

        public int getIs_friended() {
            return is_friended;
        }

        public void setIs_friended(int is_friended) {
            this.is_friended = is_friended;
        }

        public String getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(String isOnline) {
            this.isOnline = isOnline;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getProfileUrl() {
            return profileUrl;
        }

        public void setProfileUrl(String profileUrl) {
            this.profileUrl = profileUrl;
        }

    }
}