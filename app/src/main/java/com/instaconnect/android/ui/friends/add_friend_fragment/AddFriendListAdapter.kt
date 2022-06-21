package com.instaconnect.android.ui.friends.add_friend_fragment

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

class AddFriendListAdapter(
    var mUserList: MutableList<FriendListModel.User>,
    var context: Context,
    var listListener: AddFriendListListener
) : RecyclerView.Adapter<AddFriendListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_add_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = mUserList[position].username
        Glide.with(context).load(mUserList[position].profileUrl).placeholder(R.drawable.ic_friend_placeholder)
            .into(holder.profileView)
        holder.addFriendImage.setOnClickListener { v: View? ->
            if (mUserList[position].is_friended != 1) {
                mUserList[position].is_friended = 1
                listListener.onAddFriendClick(holder.absoluteAdapterPosition, mUserList[holder.absoluteAdapterPosition], v)
            }
        }
        holder.profileView.setOnClickListener { v ->
            if (mUserList[position].is_friended != 1) {
                mUserList[position].is_friended = 1
                listListener.onAddFriendClick(holder.absoluteAdapterPosition, mUserList[holder.absoluteAdapterPosition], holder.addFriendImage)
            }
        }
        if (mUserList[position].is_friended == 1) {
            holder.addFriendImage.visibility = View.GONE
        } else {
            holder.addFriendImage.visibility = View.VISIBLE
        }
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
        private val iv_profile: RoundImageView = view.findViewById(R.id.iv_profile)
        val addFriendImage: ImageView = view.findViewById(R.id.iv_add_friend)
        val profileView: RoundImageView
            get() = iv_profile

    }

    fun addUser(userList: List<FriendListModel.User>?) {
        mUserList.addAll(userList!!)
        notifyDataSetChanged()
    }

    interface AddFriendListListener {
        fun onAddFriendClick(position: Int, user: FriendListModel.User?, view: View?)
        fun onFriendView(position: Int, user: FriendListModel.User?, view: View?)
    }

    /*init {
        this.smackManager = smackManager
        try {
            val presence = Presence(Presence.Type.available)
            smackManager.getSmackConnection().sendPacket(presence)
            roster = Roster.getInstanceFor(smackManager.getSmackConnection())
        } catch (e: SmackException.NotConnectedException) {
            e.printStackTrace()
        }
    }*/
}