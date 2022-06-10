package com.instaconnect.android.data.model.db

import com.instaconnect.android.utils.DateUtil.Companion.uniqueCurrentTimeMS
import androidx.room.PrimaryKey
import com.instaconnect.android.utils.DateUtil
import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "chat_rooms")
class ChatRooms {
    @PrimaryKey
    var uid = uniqueCurrentTimeMS()

    @ColumnInfo(name = "room_uid")
    var roomUId = ""

    @ColumnInfo(name = "room_admin")
    var roomAdmin = ""

    @ColumnInfo(name = "room_image")
    var roomImage = ""

    @ColumnInfo(name = "room_name")
    var roomName = ""
}