package com.instaconnect.android.data.model

class PublicPost {
    var response: Response? = null

    inner class Response {
        var userId: String? = null
            get() = field
            set(user_id) {
                field = user_id
            }
        var media: String? = null
        var mediaType: String? = null
        var caption: String? = null
        var youTubeVideoId: String? = null
        var lat: String? = null
        var lng: String? = null
        var category: String? = null
        var country: String? = null
        var date: String? = null
        var id = 0
        var roomName: String? = null
        var totalViews: String? = null
        var hyperlink: String? = null
        var group_name: String? = null
        var image: String? = null
        var video: String? = null
        var thumbnail: String? = null
        var userimage: String? = null
        var username: String? = null
        var code: String? = null
        var message: String? = null
    }
}