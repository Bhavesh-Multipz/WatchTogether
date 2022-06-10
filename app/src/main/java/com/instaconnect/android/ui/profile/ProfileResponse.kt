package com.instaconnect.android.ui.profile

class ProfileResponse {
    var response: Response? = null

    inner class Response {
        var username: String? = null
        var image: String? = null
        var code: String? = null
        var message: String? = null
    }
}