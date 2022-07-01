package com.instaconnect.android.ui.block_user

import com.instaconnect.android.utils.models.User
import java.util.ArrayList

class BlockUserResponse {
    var response: Response? = null

    inner class Response {
        var blocklist: ArrayList<User>? = null
        var code: String? = null
        var message: String? = null
    }
}