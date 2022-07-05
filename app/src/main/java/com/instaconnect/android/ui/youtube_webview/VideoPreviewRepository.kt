package com.instaconnect.android.ui.youtube_webview

import com.instaconnect.android.base.BaseRepository
import com.instaconnect.android.network.MyApi
import okhttp3.MultipartBody
import okhttp3.RequestBody

class VideoPreviewRepository constructor(private val api: MyApi) : BaseRepository() {

    suspend fun getYoutubeVideoDetails(
        url: String,
    ) = safeApiCall {
        api.getYoutubeVideoDetails(url)
    }

    suspend fun publicPostYoutube(
        user_id: RequestBody?,
        media: RequestBody?,
        category: RequestBody?,
        lat: RequestBody?,
        lng: RequestBody?,
        caption: RequestBody?,
        country: RequestBody?,
        datatype: RequestBody?,
        hyperlink: RequestBody?,
        mediaType: RequestBody?,
        mediaRatio: RequestBody?,
        youTubeVideoId: RequestBody?,
        file: MultipartBody.Part?,
    ) = safeApiCall {
        api.publicPostYoutube(
            user_id,
            media,
            category,
            lat,
            lng,
            caption,
            country,
            datatype,
            hyperlink,
            mediaType,
            mediaRatio,
            youTubeVideoId,
            file)
    }

    suspend fun publicPostWatchTogether(
        user_id: RequestBody?,
        media: RequestBody?,
        category: RequestBody?,
        lat: RequestBody?,
        lng: RequestBody?,
        caption: RequestBody?,
        country: RequestBody?,
        datatype: RequestBody?,
        hyperlink: RequestBody?,
        mediaType: RequestBody?,
        mediaRatio: RequestBody?,
        youTubeVideoId: RequestBody?,
        isVideoLink: RequestBody?,
        groupPassword: RequestBody?,
        groupName: RequestBody?,
        uniquecode: RequestBody?,
        file: MultipartBody.Part?,
    ) = safeApiCall {
        api.publicPostWatchTogether(
            user_id,
            media,
            category,
            lat,
            lng,
            caption,
            country,
            datatype,
            hyperlink,
            mediaType,
            mediaRatio,
            youTubeVideoId,
            isVideoLink,
            groupPassword,
            groupName,
            uniquecode,
            file,
        )
    }

    suspend fun publicPostGalleryVideos(
        user_id: RequestBody?,
        media: RequestBody?,
        category: RequestBody?,
        lat: RequestBody?,
        lng: RequestBody?,
        caption: RequestBody?,
        country: RequestBody?,
        datatype: RequestBody?,
        hyperlink: RequestBody?,
        mediaType: RequestBody?,
        mediaRatio: RequestBody?,
        youTubeVideoId: RequestBody?,
        isVideoLink: RequestBody?,
        groupPassword: RequestBody?,
        groupName: RequestBody?,
        uniquecode: RequestBody?,
        file: MultipartBody.Part?,
    ) = safeApiCall {
        api.publicPostGalleryVideos(
            user_id,
            media,
            category,
            lat,
            lng,
            caption,
            country,
            datatype,
            hyperlink,
            mediaType,
            mediaRatio,
            youTubeVideoId,
            isVideoLink,
            groupPassword,
            groupName,
            uniquecode,
            file,
        )
    }

}