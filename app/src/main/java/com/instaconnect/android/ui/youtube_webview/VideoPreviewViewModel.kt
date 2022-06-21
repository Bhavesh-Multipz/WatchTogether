package com.instaconnect.android.ui.youtube_webview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.data.model.PublicPost
import com.instaconnect.android.network.Resource
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class VideoPreviewViewModel constructor(private val repository: VideoPreviewRepository) : BaseViewModel(repository) {

    private val _youtubeVideoDetailsResponse: MutableLiveData<Resource<YoutubeVideoDetails>> = MutableLiveData()
    val youtubeVideoDetailsResponse: LiveData<Resource<YoutubeVideoDetails>>
        get() = _youtubeVideoDetailsResponse

    private val _uploadPostResponse: MutableLiveData<Resource<PublicPost>> = MutableLiveData()
    val uploadPostResponse: LiveData<Resource<PublicPost>>
        get() = _uploadPostResponse

    private val _uploadYoutubePostResponse: MutableLiveData<Resource<PublicPost>> = MutableLiveData()
    val uploadYoutubePostResponse: LiveData<Resource<PublicPost>>
        get() = _uploadYoutubePostResponse

    suspend fun publicPostYoutubeVideo(
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
    ) = viewModelScope.launch {
        _uploadYoutubePostResponse.value = Resource.Loading
        _uploadYoutubePostResponse.value = repository.publicPostYoutube(
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

    suspend fun getYoutubeVideoDetails(
        url: String,
    ) = viewModelScope.launch {
        _youtubeVideoDetailsResponse.value = Resource.Loading
        _youtubeVideoDetailsResponse.value = repository.getYoutubeVideoDetails(url)
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
    ) = viewModelScope.launch {
        _uploadPostResponse.value = Resource.Loading
        _uploadPostResponse.value = repository.publicPostWatchTogether(
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
