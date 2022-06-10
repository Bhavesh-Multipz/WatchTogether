package com.instaconnect.android.ui.fragment.worldwide

import android.graphics.Bitmap
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.instaconnect.android.ui.fragment.worldwide.Post.Reaction
import com.instaconnect.android.ui.fragment.worldwide.Post.Follow
import com.instaconnect.android.ui.fragment.worldwide.Post.Favourite
import com.instaconnect.android.ui.fragment.worldwide.Post.UserProfile
import com.instaconnect.android.ui.fragment.worldwide.Post.PostsList
import com.instaconnect.android.ui.fragment.worldwide.Post.FollowersFollowings
import com.instaconnect.android.utils.Model
import java.io.Serializable

class Post : Model(), Serializable {
    var response: Response? = null

    inner class PostsList : Model(), Serializable {
        var blurBitmap: Bitmap? = null

        @SerializedName("rejected_post")
        @Expose
        var rejectedPost: Boolean? = null

        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("image")
        @Expose
        var image: String? = null

        @SerializedName("thumbnail")
        @Expose
        var thumbnail: String? = null

        @SerializedName("category")
        @Expose
        var category: String? = null

        @SerializedName("caption")
        @Expose
        var caption: String? = null

        @SerializedName("date")
        @Expose
        var date: String? = null

        @SerializedName("Country")
        @Expose
        var country: String? = null

        @SerializedName("media")
        @Expose
        var media: String? = null

        @SerializedName("media_ratio")
        @Expose
        var mediaRatio: String? = null

        @SerializedName("group_name")
        @Expose
        var groupName: String? = null

        @SerializedName("group_password")
        @Expose
        var groupPassword: String? = null

        @SerializedName("is_video_link")
        @Expose
        var isVideoLink: String? = null

        @SerializedName("mediaType")
        @Expose
        var mediaType: String? = null

        @SerializedName("uniqueCode")
        @Expose
        var uniqueCode: String? = null

        @SerializedName("video")
        @Expose
        var video: String? = null

        @SerializedName("gif")
        @Expose
        var gif: String? = null

        @SerializedName("hyperlink")
        @Expose
        var hyperlink: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("isPrivate")
        @Expose
        var isPrivate: String? = null

        @SerializedName("userimage")
        @Expose
        var userimage: String? = null

        @SerializedName("user_id")
        @Expose
        var userId: String? = null

        @SerializedName("total_views")
        @Expose
        var totalViews: String? = null

        @SerializedName("reaction")
        @Expose
        var reaction: Reaction? = null

        @SerializedName("report_count")
        @Expose
        var reportCount: String? = null

        @SerializedName("commentTotal")
        @Expose
        var commentTotal: Int? = null

        @SerializedName("comments")
        @Expose
        var comments: List<Any>? = null

        @SerializedName("youTubeVideoId")
        @Expose
        var youTubeVideoId: String? = null

        @SerializedName("follow")
        @Expose
        var follow: Follow? = null

        @SerializedName("favourite")
        @Expose
        var favourite: Favourite? = null
        var commentVisible = false
    }

    inner class Response : Serializable {
        @SerializedName("user_profile")
        @Expose
        var userProfile: UserProfile? = null

        @SerializedName("posts_list")
        @Expose
        var postsList: List<PostsList>? = null

        @SerializedName("code")
        @Expose
        var code: String? = null

        @SerializedName("message")
        @Expose
        var message: String? = null
    }

    inner class Reaction : Serializable {
        @SerializedName("likes")
        @Expose
        var likes: Int? = null

        @SerializedName("dislikes")
        @Expose
        var dislikes: Int? = null

        @SerializedName("yourReaction")
        @Expose
        var yourReaction: String? = null
    }

    inner class UserProfile : Serializable {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("user_id")
        @Expose
        var userId: String? = null

        @SerializedName("username")
        @Expose
        var username: String? = null

        @SerializedName("user_active_status")
        @Expose
        var userActiveStatus: String? = null

        @SerializedName("version_code")
        @Expose
        var versionCode: String? = null

        @SerializedName("profile_pic")
        @Expose
        var profilePic: String? = null

        @SerializedName("bio")
        @Expose
        var bio: String? = null

        @SerializedName("website")
        @Expose
        var website: String? = null

        @SerializedName("posts")
        @Expose
        var posts: Int? = null

        @SerializedName("followersFollowings")
        @Expose
        var followersFollowings: FollowersFollowings? = null

        @SerializedName("isPrivate")
        @Expose
        var isPrivate: String? = null

        @SerializedName("my_favorite_post")
        @Expose
        var myFavouritePost: String? = null
        var isFollow = false
        var isMyPost = false
        var isUserProfileData = false
    }

    inner class Favourite : Serializable {
        @SerializedName("status")
        @Expose
        var status: Int? = null
    }

    inner class Follow : Serializable {
        @SerializedName("status")
        @Expose
        var status: Int? = null
    }

    inner class FollowersFollowings : Serializable {
        @SerializedName("followers")
        @Expose
        var followers: Int? = null

        @SerializedName("followings")
        @Expose
        var followings: Int? = null

        @SerializedName("requests")
        @Expose
        var requests: Int? = null
    }
}