package com.instaconnect.android.utils.dialog_helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.instaconnect.android.R;
import com.instaconnect.android.base.BaseViewHolder;
import com.instaconnect.android.databinding.RvPopUpItemBinding;
import com.instaconnect.android.utils.models.DialogItem;

import java.util.ArrayList;

public class PopUpWindowAdapter extends BaseRecyclarAdapter {

    private Context mContext;
    private ArrayList<DialogItem> dialogItems =new ArrayList<>();

    // Provide a suitable constructor (depends on the kind of dataset)
    public PopUpWindowAdapter(Context context) {
        mContext = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RvPopUpItemBinding binding= DataBindingUtil.inflate(inflater, R.layout.rv_pop_up_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dialogItems.size();
    }

    public void setData(ArrayList<DialogItem> mDialogItems)
    {
        dialogItems.addAll(mDialogItems);
        notifyDataSetChanged();
    }

    public void insertItem(DialogItem mDialogItems)
    {
        dialogItems.add(mDialogItems);
        notifyItemInserted(dialogItems.size()-1);
    }

    public void deleteItem(int position)
    {
        dialogItems.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends BaseViewHolder {

        RvPopUpItemBinding mBinding;
        public ViewHolder(RvPopUpItemBinding binding) {
            super(binding);
            this.mBinding = binding;
        }

        @Override
        public void onBind(int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v, itemView, getAdapterPosition());
                }
            });

            mBinding.tvItemName.setText(dialogItems.get(position).getName());

            if(dialogItems.get(position).getTxtColor()!=-1){
                mBinding.tvItemName.setTextColor(ContextCompat.getColor(mContext, dialogItems.get(position).getTxtColor()));
            }

            if(dialogItems.get(position).getResId()!=-1) {
                mBinding.ivItemImage.setImageResource(dialogItems.get(position).getResId());
            }else {
                mBinding.ivItemImage.setVisibility(View.GONE);
            }
        }

        @Override
        public void onDetached() {

        }

        @Override
        public void onViewRecycled() {

        }

    }

}