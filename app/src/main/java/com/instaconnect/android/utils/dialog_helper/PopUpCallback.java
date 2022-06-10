package com.instaconnect.android.utils.dialog_helper;

import android.view.View;
import android.widget.PopupWindow;

public interface PopUpCallback {
    void onCallback(PopupWindow popupWindow, View v, int position);
}