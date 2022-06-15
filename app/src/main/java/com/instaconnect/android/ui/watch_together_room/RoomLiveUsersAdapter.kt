package com.instaconnect.android.ui.watch_together_room

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instaconnect.android.R
import com.instaconnect.android.data.model.LiveUsersItem
import com.instaconnect.android.databinding.RvStreamUsersProfileBinding
import com.instaconnect.android.utils.helper_classes.GlideHelper


class RoomLiveUsersAdapter(
    private val context: Context,
    private val liveUsersList: ArrayList<LiveUsersItem>,
    private val serviceItemClick: ((item: LiveUsersItem) -> Unit)? = null,
) :
    RecyclerView.Adapter<RoomLiveUsersAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: RvStreamUsersProfileBinding,
    ) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = RvStreamUsersProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatData = liveUsersList[position]

        GlideHelper.loadFromUrl(
            context, chatData.image,
            R.drawable.loader, holder.binding.ivUserImage
        )
    }

    override fun getItemCount(): Int = liveUsersList.size
}
