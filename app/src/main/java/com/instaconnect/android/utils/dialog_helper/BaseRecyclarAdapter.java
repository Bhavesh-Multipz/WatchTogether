package com.instaconnect.android.utils.dialog_helper;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.instaconnect.android.base.BaseViewHolder;
import com.instaconnect.android.widget.RecyclerClickListener;


public abstract class BaseRecyclarAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public RecyclerClickListener listener;

    public void OnClickListener(RecyclerClickListener listener) {
        this.listener = listener;
    }

    public <T extends ViewDataBinding> T getDataBinding(LayoutInflater inflater, @LayoutRes int id, ViewGroup parent, boolean attachParent) {
        return DataBindingUtil.inflate(inflater, id, parent, attachParent);
    }
}
