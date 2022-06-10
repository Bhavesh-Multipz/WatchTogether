package com.instaconnect.android.data.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.instaconnect.android.fileHelper.FileUtils
import com.instaconnect.android.utils.DateUtil
import com.instaconnect.android.utils.Model

@Entity(tableName = "chat_message")
class ChatMessage : Model() {
    @ColumnInfo(name = "unique_code")
    var uniqueCode = ""

    @ColumnInfo(name = "capture_type")
    var captureType = ""

    @ColumnInfo(name = "video_thumb")
    var videoThumb = ""

    @ColumnInfo(name = "media_ratio")
    var mediaRatio = ""

    @ColumnInfo(name = "post_type")
    var postType = ""

    @ColumnInfo(name = "video_type")
    var videoType = "null"

    @PrimaryKey
    var uid: Long = DateUtil.uniqueCurrentTimeMS()

    @ColumnInfo(name = "thread_id")
    var threadId = "null"

    @ColumnInfo(name = "caption")
    var caption = ""

    @ColumnInfo(name = "contact")
    var contact = ""

    @ColumnInfo(name = "contact_name")
    var contactName = ""

    @ColumnInfo(name = "contact_user_name")
    var contactUserName = ""

    @ColumnInfo(name = "data")
    var data = ""

    @ColumnInfo(name = "data_type")
    var dataType = ""

    @ColumnInfo(name = "date")
    var date = ""

    @ColumnInfo(name = "downloaded")
    var isDownloaded = false

    @ColumnInfo(name = "from_user_id")
    var fromUserId = ""

    @ColumnInfo(name = "receipt_status")
    var receiptStatus = ""

    @ColumnInfo(name = "latitude")
    var latitude = ""

    @ColumnInfo(name = "longitude")
    var longitude = ""

    @ColumnInfo(name = "location_image")
    var locationImage = ""

    @ColumnInfo(name = "message_body")
    var messageBody = ""

    @ColumnInfo(name = "message_thumb")
    var messageThumb = ""

    @ColumnInfo(name = "to_user_id")
    var toUserId = ""

    @ColumnInfo(name = "uploaded")
    var isUploaded = true

    @ColumnInfo(name = "file_size")
    var fileSize = ""

    @ColumnInfo(name = "in_progress")
    var isInProgress = false

    @ColumnInfo(name = "chat_mode")
    var chatMode = ""

    @ColumnInfo(name = "chat_type")
    var chatType = ""

    @ColumnInfo(name = "room_uid")
    var roomUId = "null"

    @ColumnInfo(name = "room_name")
    var roomName = "unknown"

    @ColumnInfo(name = "room_image")
    var roomImage = ""

    @ColumnInfo(name = "room_admin")
    var roomAdmin = ""

    @ColumnInfo(name = "filePath")
    var filePath = ""

    @ColumnInfo(name = "thumbnailPath")
    var thumbnailPath = ""
    var isSelected = false
    val dateAgo: String
        get() = DateUtil.getDaysAgo(date)
    val relativeFileSize: String
        get() {
            var file_size = ""
            try {
                file_size = FileUtils.getFileSize(java.lang.Long.valueOf(fileSize))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file_size
        }
}