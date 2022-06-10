package com.instaconnect.android.data.model.db

import com.instaconnect.android.utils.DateUtil.Companion.uniqueCurrentTimeMS
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.instaconnect.android.utils.DateUtil
import com.instaconnect.android.utils.Model

@Entity(tableName = "contacts")
class Contacts : Model() {
    @ColumnInfo(name = "name")
    var name = ""

    @ColumnInfo(name = "user_name")
    var userName = ""

    @PrimaryKey
    var uid = uniqueCurrentTimeMS()

    @ColumnInfo(name = "phone")
    var phone = ""

    @ColumnInfo(name = "status")
    var isStatus = false

    @ColumnInfo(name = "isAdmin")
    var isAdmin = false
    var header = ""
    var avatar = ""
    var isChecked = false
    var isBlocked = false

    @Ignore
    var hashCode = 0
    override fun equals(obj: Any?): Boolean {
        val blog = obj as Contacts?
        return if (phone == blog!!.phone) {
            hashCode = blog.hashCode
            true
        } else {
            hashCode = super.hashCode()
            false
        }
    }
}