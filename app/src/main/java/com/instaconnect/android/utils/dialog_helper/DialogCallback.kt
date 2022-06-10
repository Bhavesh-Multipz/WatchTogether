package com.instaconnect.android.utils.dialog_helper

import android.app.Dialog
import android.view.View

interface DialogCallback {
    fun onCallback(dialog: Dialog?, v: View?, position: Int)
    fun onDismiss()
}