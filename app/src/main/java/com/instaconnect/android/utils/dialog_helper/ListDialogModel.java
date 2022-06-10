package com.instaconnect.android.utils.dialog_helper;

import com.instaconnect.android.base.BaseViewModelExplorii;

import javax.inject.Inject;

public class ListDialogModel extends BaseViewModelExplorii<ListDialogBridge> {

    @Inject
    public ListDialogModel(SchedulerProvider provider) {
        super(provider);
    }

    public void showToast(){
           getBridge().getViewUtil().toast("Click");
    }

}