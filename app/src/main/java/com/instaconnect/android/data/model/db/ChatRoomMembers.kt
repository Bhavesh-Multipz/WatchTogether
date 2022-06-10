package com.instaconnect.android.data.model.db

import com.instaconnect.android.utils.DateUtil.Companion.uniqueCurrentTimeMS
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.instaconnect.android.utils.DateUtil

@Entity(tableName = "chat_room_member")
class ChatRoomMembers {
    @ColumnInfo(name = "room_uid")
    var roomUId: String? = null

    @PrimaryKey
    var uid = uniqueCurrentTimeMS()

    @ColumnInfo(name = "member_phone")
    var memberPhone: String? = null

    @ColumnInfo(name = "member_image")
    var memberImage: String? = null

    constructor() {}

    @ColumnInfo(name = "date_joined")
    var dateJoined: String? = null

    @ColumnInfo(name = "response")
    var response: String? = null

    @ColumnInfo(name = "is_group_admin")
    var isGroupAdmin = false

    constructor(
        roomUId: String?,
        uid: Long,
        memberPhone: String?,
        dateJoined: String?,
        response: String?,
        isGroupAdmin: Boolean
    ) {
        this.roomUId = roomUId
        this.uid = uid
        this.memberPhone = memberPhone
        this.dateJoined = dateJoined
        this.response = response
        this.isGroupAdmin = isGroupAdmin
    }
}