package com.instaconnect.android.utils

import android.Manifest

/**
 * Created by Bhavesh on 20-04-2022.
 */
object Constants {

    const val IS_RATED: String = "is_rated"
    const val PORT = "5005"
    const val FOR_CHAT_HOST_NAME = "http://15.222.88.69/"
    const val APP_NAME = "Watch Together"
    const val LOGIN_STATUS = "login_status"

    const val TEXT_TYPE = "text"
    const val CONTACT_TYPE = "contact"
    const val LOCATION_TYPE = "location"
    const val IMAGE_TYPE = "image"
    const val AUDIO_TYPE = "audio"
    const val VIDEO_TYPE = "video"
    const val EMOJI_TYPE = "emoji"
    const val YOUTUBE_VIDEO = "youtube"
    const val STICKER_TYPE = "sticker"
    const val CALL_MESSAGE_TYPE = "call_message"
    const val GROUP_MEMBER = "groupmember"
    const val GROUP_JOIN = "group_join"
    const val RECEIPT_DEL_TYPE = "deliverreceipt"
    const val RECEIPT_REC_TYPE = "receipt"
    const val WATCH_TOGETHER_CHAT = "watchTogetherChat"
    const val CHAT_MESSAGE_MODEL: String = "chat_message_model"

    const val visiblePercent = 80f

    // Preference
    const val PREF_DEVICE_TOKEN = "pref_device_token"
    const val PREF_USER_ID = "pref_user_id"
    const val PREF_USER_NAME = "pref_user_name"
    const val PREF_USER_PROFILE_PIC = "pref_user_profile_pic"
    const val PREF_USER_PHONE_NUMBER = "pref_user_phone_number"
    const val PREF_HAS_COUNTRY = "pref_has_country"
    const val PREF_HAS_DISTANCE = "pref_has_distance"
    const val PREF_SEARCH_BY_DISTANCE = "pref_search_by_distance"

    const val PREF_NOTIFICATION_STATUS = "pref_notification_status"

    var appPermissionsForHomeScreen = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.READ_CONTACTS
    )

    var appPermissionsForContacts = arrayOf(
        Manifest.permission.READ_CONTACTS
    )

    const val TYPING_STATUS_TRUE = "composing"
    const val TYPING_STATUS_FALSE = "paused"

    const val CHAT_TYPE_PRIVATE = "private"

    const val CHAT_MODE_ONE_TO_ONE = "single"
    const val CHAT_MODE_CONFERENCE = "conference"

    const val NOTIFICATION_TYPE_SEND_REQUEST = "send_request"
    const val NOTIFICATION_TYPE_SEND_INVITATION = "send_invitation"
    const val NOTIFICATION_TYPE_POST_LIKED = "post_liked"
}