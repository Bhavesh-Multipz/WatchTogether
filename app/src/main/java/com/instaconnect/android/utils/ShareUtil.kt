package com.instaconnect.android.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.instaconnect.android.R
import gun0912.tedimagepicker.util.ToastUtil.context

class ShareUtil(private val context: Context) {
    companion object{
        fun shareTextUrl(title: String?, link: String?, activity: Activity) {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "text/plain"
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            // Add data to the intent, the receiving app will decide
            // what to do with it.
            share.putExtra(Intent.EXTRA_SUBJECT, title)
            share.putExtra(Intent.EXTRA_TEXT, link)
            activity.startActivity(Intent.createChooser(share, context.getString(R.string.app_name)))
        }
    }

}