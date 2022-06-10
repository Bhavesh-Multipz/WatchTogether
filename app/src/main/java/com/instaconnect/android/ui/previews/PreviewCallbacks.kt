package com.instaconnect.android.ui.previews

import com.instaconnect.android.data.model.db.ChatMessage
import com.instaconnect.android.utils.models.Trending

interface PreviewCallbacks {
    fun onPreviewActivityCallback(type: String?, chatMessage: ChatMessage?, flag: Int)
    fun onPreviewActivityCallback(
        type: String?,
        chatMessage: ChatMessage?,
        flag: Int,
        trending: Trending?
    )
}