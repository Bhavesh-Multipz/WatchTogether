package com.instaconnect.android.utils.dialog_helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.instaconnect.android.utils.models.DialogItem
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseViewHolder
import com.instaconnect.android.databinding.RvDialogItemBinding
import javax.inject.Inject
import com.instaconnect.android.utils.dialog_helper.ListDialogModel
import java.lang.Exception
import java.util.ArrayList

class ListDialogAdapter     // Provide a suitable constructor (depends on the kind of dataset)
    (private val mContext: Context) : BaseRecyclarAdapter() {
    private val dialogItems = ArrayList<DialogItem>()
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        val binding: RvDialogItemBinding =
            DataBindingUtil.inflate(inflater, R.layout.rv_dialog_item, parent, false)
        return ViewHolder(binding)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return dialogItems.size
    }

    fun setData(mDialogItems: ArrayList<DialogItem>) {
        dialogItems.addAll((mDialogItems))
        notifyDataSetChanged()
    }

    fun insertItem(mDialogItems: DialogItem) {
        dialogItems.add(mDialogItems)
        notifyItemInserted(dialogItems.size - 1)
    }

    fun deleteItem(position: Int) {
        dialogItems.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(var mBinding: RvDialogItemBinding) : BaseViewHolder<ViewDataBinding?>(
        mBinding
    ) {
        @Inject
        var listDialogModel: ListDialogModel? = null
        override fun onDetached() {}
        override fun onViewRecycled() {}
        override fun onBind(position: Int) {
            itemView.setOnClickListener(View.OnClickListener { view ->
                listener.onClick(
                    view,
                    itemView,
                    adapterPosition
                )
            })
            mBinding.viewModel = listDialogModel

            /*if (dialogItems.get(position).getTxtColor() != -1) {
                try {
                    mBinding.tvItemName.setTextColor(ContextCompat.getColor(mContext, dialogItems.get(position).getTxtColor()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/mBinding.tvItemName.text = dialogItems.get(position).name
            if (dialogItems[position].resId != -1) {
                try {
                    mBinding.ivItemImage.setImageResource(dialogItems[position].resId)
                    mBinding.ivItemImage.visibility = View.VISIBLE
                } catch (e: Exception) {
                    mBinding.ivItemImage.visibility = View.GONE
                }
            } else {
                mBinding.ivItemImage.visibility = View.GONE
            }

            /*if (dialogItems.get(position).isUnderLine())
                mBinding.tvItemName.setBackgroundResource(R.drawable.layout_square_rounded_white_glass);
            else
                mBinding.tvItemName.setBackgroundResource(R.drawable.layout_square_rounded_white_glass);*/

            /*if (dialogItems.get(position).isSelected && !dialogItems.get(position).getName().equals("Cancel"))
                mBinding.tvItemName.setTextColor(Color.parseColor("#2cb0bb"));
            else if (!dialogItems.get(position).isSelected && !dialogItems.get(position).getName().equals("Cancel"))
                mBinding.tvItemName.setTextColor(ContextCompat.getColor(mContext, dialogItems.get(position).getTxtColor()));
            else if (dialogItems.get(position).getName().equals("Cancel"))
                mBinding.tvItemName.setTextColor(ContextCompat.getColor(mContext,R.color.red));*/if (dialogItems[position].isDivider) mBinding.divider.visibility =
                View.VISIBLE else mBinding.divider.visibility = View.GONE
        }
    }
}