package com.instaconnect.android.ui.watch_together_room

import android.content.Context
import android.net.Uri
import android.util.Log
import com.instaconnect.android.ui.watch_together_room.AddFriendToVideoListAdapter.AddFriendListListener
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.instaconnect.android.R
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import android.widget.TextView
import com.instaconnect.android.data.model.FriendListModel
import de.hdodenhof.circleimageview.CircleImageView

class AddFriendToVideoListAdapter(
    var mUserList: MutableList<FriendListModel.User>,
    var context: Context,
    var listListener: AddFriendListListener
) : RecyclerView.Adapter<AddFriendToVideoListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_add_friend_to_watch_video_new, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = mUserList[position].username
        holder.inviteFriendImage().setOnClickListener { v ->
            listListener.onAddFriendClick(holder.bindingAdapterPosition, mUserList[holder.bindingAdapterPosition], v)
            holder.inviteFriendImage().setTextColor(ContextCompat.getColor(context, R.color.gray))
            holder.inviteFriendImage().isEnabled = false
        }
        Glide.with(context).load(Uri.parse(mUserList[position].profileUrl)).centerCrop()
            .placeholder(R.drawable.ic_friend_placeholder).into(holder.profileView)
    }

    override fun getItemCount(): Int {
        return mUserList.size
    }

    fun clear() {
        mUserList.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.txt_name)
        val profileView: CircleImageView = view.findViewById(R.id.iv_profile)
        private val tvInvite: TextView = view.findViewById(R.id.tvInvite)
        fun inviteFriendImage(): TextView {
            return tvInvite
        }

        init {
            // Define click listener for the ViewHolder's View
        }
    }

    fun addUser(userList: List<FriendListModel.User>?) {
        mUserList.addAll(userList!!)
        notifyDataSetChanged()
    }

    interface AddFriendListListener {
        fun onAddFriendClick(position: Int, user: FriendListModel.User?, view: View?)
        fun onFriendView(position: Int, user: FriendListModel.User?, view: View?)
    }
}