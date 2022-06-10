package com.allattentionhere.autoplayvideos;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ExoVideosAdapter extends RecyclerView.Adapter<ExoViewHolder> {

    public ExoVideosAdapter() {
    }

    @Override
    public ExoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExoViewHolder(new View(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(ExoViewHolder holder, int position) {
    }

    @Override
    public void onViewDetachedFromWindow(final ExoViewHolder holder) {
        if (holder != null && holder.getExoVideoImage()!=null) {
            holder.stopVideo();
        }
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(ExoViewHolder holder) {
        if (holder != null && holder.getExoVideoImage()!=null) {
            holder.clearViews();
        }
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }
}