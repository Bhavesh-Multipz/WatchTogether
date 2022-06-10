package com.instaconnect.android.ui.watch_together_room

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.instaconnect.android.R
import com.instaconnect.android.data.model.MessageDataItem
import com.instaconnect.android.databinding.RvWatchTogetherChatMessageBinding
import com.instaconnect.android.utils.helper_classes.GlideHelper
import java.util.*



class WatchTogetherChatAdapter(
    private val context: Context,
    private val chatList : ArrayList<MessageDataItem>,
    private val serviceItemClick: ((item: MessageDataItem) -> Unit)? = null
) :
    RecyclerView.Adapter<WatchTogetherChatAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: RvWatchTogetherChatMessageBinding
    ) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = RvWatchTogetherChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatData = chatList[position]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.binding.tvUserName.text = Html.fromHtml("<b>${chatData.username} :</b> ${chatData.message}",0)
        } else{
            holder.binding.tvUserName.text = Html.fromHtml("<b>${chatData.username} :</b> ${chatData.message}")
        }
        GlideHelper.loadFromUrl(
            context, chatData.userProfile,
            R.drawable.loader, holder.binding.ivUserImage
        )
    }

    override fun getItemCount(): Int = chatList.size

    fun getList(): List<MessageDataItem> {
        return chatList
    }

    fun updateList(newPortfolioList: ArrayList<MessageDataItem>) {
        chatList.clear()
        chatList.addAll(newPortfolioList)
        notifyDataSetChanged()
    }
}
