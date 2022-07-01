package com.instaconnect.android.network

import com.instaconnect.android.BuildConfig
import com.instaconnect.android.network.ApiEndPoint

object ApiEndPoint {
    //public static final String SERVER_BASE_URL = "http://3.98.51.200/webapp/dev/api/";  // LOKESH WORK
    //  public static final String SERVER_BASE_URL = "http://99.79.19.208/dev/api/";
    // public static final String UPLOADS_BASE_URL = "http://99.79.19.208/dev/uploads/";

    const val IPADDRESS = "15.222.88.69"
    const val SERVER_BASE_URL = "http://$IPADDRESS/api/"
    const val UPLOADS_BASE_URL = "http://$IPADDRESS/webapp/dev/uploads/"

    const val ENDPOINT_SEND_OTP = (SERVER_BASE_URL
            + "/send_otp")
    const val ENDPOINT_SEND_SOCIAL_ID = ("$SERVER_BASE_URL/send_socialid")
    const val ENDPOINT_RESEND_OTP = (SERVER_BASE_URL
            + "/resend_otp")
    const val ENDPOINT_UPLOADS = (SERVER_BASE_URL
            + "uploads")
    const val ENDPOINT_UPLOADS_PUBLIC_POST = (SERVER_BASE_URL
            + "post")
    const val ENDPOINT_GET_MEDIA = (SERVER_BASE_URL
            + "get_media")
    const val CHANGE_NOTIFICATION_STATUS = (SERVER_BASE_URL
            + "change_notification_status")
    const val ENDPOINT_GET_PUBLIC_PROFILE = (SERVER_BASE_URL
            + "get_user_profile")
    const val ENDPOINT_DEL_POST = (SERVER_BASE_URL
            + "del_post")
    const val ENDPOINT_GET_CHAT_BG = (SERVER_BASE_URL
            + "get_backgrounds")
    const val ENDPOINT_GET_TRENDING_WEBSITES = (SERVER_BASE_URL
            + "get_streamingwebsites?device_type=Android")
    const val ENDPOINT_GET_YOUTUBE_VIDEO_DETAILS =
        "https://noembed.com/embed?url=http://www.youtube.com/watch?v="
    const val ENDPOINT_GET_POSTS = (SERVER_BASE_URL
            + "get_posts")
    const val ENDPOINT_WATCH_LIST = (SERVER_BASE_URL
            + "get_watch_list")
    const val ENDPOINT_USER_PROFILE = (SERVER_BASE_URL
            + "user_profile")
    const val ENDPOINT_FOLLOW_FOLLOWING = (SERVER_BASE_URL
            + "/followersFollowings")
    const val ENDPOINT_FOLLOW_USER = (SERVER_BASE_URL
            + "/followUser")
    const val ENDPOINT_ADD_REMOVE_FAVOURITE = (SERVER_BASE_URL
            + "/addRemoveFavourite")
    const val ENDPOINT_ADD_POST_REACTION = (SERVER_BASE_URL
            + "/addPostReaction")
    const val ENDPOINT_BLOCK_USER = (SERVER_BASE_URL
            + "/block_user")
    const val ENDPOINT_FOLLOW_REQUEST = (SERVER_BASE_URL
            + "/getFollowRequest")
    const val ENDPOINT_BLOCK_USER_LIST = (SERVER_BASE_URL
            + "/block_list")
    const val ENDPOINT_UNBLOCK_USER = (SERVER_BASE_URL
            + "/block_user")
    const val ENDPOINT_REPORT_POST = (SERVER_BASE_URL
            + "report_post")
    const val ENDPOINT_INCREASE_POST_VIEW = (SERVER_BASE_URL
            + "increasePostView")
    const val ENDPOINT_GET_POST_COMMENTS = (SERVER_BASE_URL
            + "get_postComments")
    const val ENDPOINT_ADD_POST_COMMENTS = (SERVER_BASE_URL
            + "addPostComment")
    const val ENDPOINT_DELETE_POST_COMMENTS = (SERVER_BASE_URL
            + "deletePostComment")
    const val ENDPOINT_EDIT_POST_COMMENTS = (SERVER_BASE_URL
            + "editPostComment")
    const val ENDPOINT_GET_USERS_PROFILES = (SERVER_BASE_URL
            + "get_profile_pics")
    const val ENDPOINT_GET_WEB_LINKS = (SERVER_BASE_URL
            + "get_web_links")
    const val ENDPOINT_ADD_FRIEND_LIST = (SERVER_BASE_URL
            + "add_friend_list")
    const val ENDPOINT_GET_MY_FRIEND = (SERVER_BASE_URL
            + "get_my_friends")
    const val ENDPOINT_FRIEND_REQUEST = (SERVER_BASE_URL
            + "get_friend_request")
    const val ENDPOINT_SEND_FRIEND_REQUEST = (SERVER_BASE_URL
            + "send_friend_request")
    const val ENDPOINT_FRIEND_REQUEST_RESPONSE = (SERVER_BASE_URL
            + "friend_request_response")
    const val ENDPOINT_WRITE_REVIEW = (SERVER_BASE_URL
            + "write_review")
    const val ENDPOINT_NOTIFICATION_LIST = (SERVER_BASE_URL
            + "get_all_notifications")
    const val ENDPOINT_INVITE_PEOPLE_TO_WATCH_VIDEO = (SERVER_BASE_URL
            + "send_invitation_watch_togather")
    const val ENDPOINT_UNFRIEND_USER = (SERVER_BASE_URL
            + "unfriend_user")

    const val DELETE_USER_ACCOUNT = (SERVER_BASE_URL
            + "delete_user")

    const val LOAD_PREFERENCE = (SERVER_BASE_URL
            + "loadprefreance")
}