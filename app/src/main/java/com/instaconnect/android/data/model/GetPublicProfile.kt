package com.instaconnect.android.data.model

class GetPublicProfile {
    var response: Response? = null

    inner class Response {
        var userimage: String? = null
        var username: String? = null
        var code: String? = null
        var message: String? = null
    }
}