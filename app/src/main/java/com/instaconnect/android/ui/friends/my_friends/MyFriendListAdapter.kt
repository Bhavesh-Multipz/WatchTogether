package com.instaconnect.android.ui.friends.my_friends

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instaconnect.android.R
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.widget.RoundImageView

class MyFriendListAdapter(
    var userList: ArrayList<FriendListModel.User>,
    var context: Context,
    var listListener: MyFriendListListener
) : RecyclerView.Adapter<MyFriendListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.rv_item_my_friend_new, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = userList[position].username
        Glide.with(context).load(userList[position].profileUrl).placeholder(R.drawable.ic_friend_placeholder)
            .into(holder.profileView)
        holder.iv_unfriend.setOnClickListener { v: View? ->
            listListener.onUnfriendClick(holder.absoluteAdapterPosition,
                userList[holder.absoluteAdapterPosition])
        }
        holder.profileView.setOnClickListener { v: View? ->
            listListener.onUnfriendClick(holder.absoluteAdapterPosition,
                userList[holder.absoluteAdapterPosition])
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun clear() {
        userList.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.txt_name)
        val profileView: RoundImageView = view.findViewById(R.id.iv_profile)
        val onlineOffline = view.findViewById<ImageView>(R.id.iv_onlilne_offline)
        val iv_unfriend: ImageView = view.findViewById(R.id.iv_unfriend)

    }

    fun addUser(list: List<FriendListModel.User>?) {
        userList.addAll(list!!)
        notifyDataSetChanged()
    }

    interface MyFriendListListener {
        fun onMyFriendClick(position: Int, user: FriendListModel.User?, view: View?)
        fun onFriendView(position: Int, user: FriendListModel.User?, view: View?)
        fun onUnfriendClick(position: Int, user: FriendListModel.User?)
    }
}