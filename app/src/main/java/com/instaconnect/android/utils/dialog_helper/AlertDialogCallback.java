package com.instaconnect.android.utils.dialog_helper;

import android.content.DialogInterface;

public interface AlertDialogCallback {
    void onPositiveButton(DialogInterface dialog);
    void onNegativeButton(DialogInterface dialog);
}