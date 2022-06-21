package com.instaconnect.android.ui.friends.friend_request_fragment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instaconnect.android.R
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.widget.RoundImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class FriendRequestListAdapter(
    var userList: MutableList<FriendListModel.User>,
    var context: Context,
    var listener: FriendRequestListener
) : RecyclerView.Adapter<FriendRequestListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_friend_request_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = userList[position].username
        holder.timeTextView.text = covertTimeToText(userList[position].datecreated)
        Glide.with(context).load(userList[position].profileUrl).placeholder(R.drawable.ic_person_glass).into(holder.profileView)
        holder.acceptTextView.setOnClickListener {
            listener.onItemClick(holder.absoluteAdapterPosition,
                userList[holder.absoluteAdapterPosition],
                holder.acceptTextView,
                "1")
        }
        holder.declineTextView.setOnClickListener {
            listener.onItemClick(holder.absoluteAdapterPosition,
                userList[holder.absoluteAdapterPosition],
                holder.acceptTextView,
                "2")
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun addUser(list: List<FriendListModel.User>?) {
        userList.addAll(list!!)
        notifyDataSetChanged()
    }

    fun removeUser(position: Int) {
        userList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clear() {
        userList.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val profileView: RoundImageView
        val acceptTextView: TextView
        val declineTextView: TextView
        val timeTextView: TextView

        init {

            // Define click listener for the ViewHolder's View
            textView = view.findViewById(R.id.tv_name)
            profileView = view.findViewById(R.id.img_profile)
            acceptTextView = view.findViewById(R.id.tv_accept)
            declineTextView = view.findViewById(R.id.tv_decline)
            timeTextView = view.findViewById(R.id.txt_time)
        }
    }

    interface FriendRequestListener {
        fun onItemClick(position: Int, user: FriendListModel.User?, view: View?, isAccept: String?)
    }

    fun covertTimeToText(dataDate: String?): String? {
        var convTime: String? = null
        val prefix = ""
        val suffix = "Ago"
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val pasTime = dateFormat.parse(dataDate)
            val nowTime = Date()
            val dateDiff = nowTime.time - pasTime.time
            val second = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
            val minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
            val hour = TimeUnit.MILLISECONDS.toHours(dateDiff)
            val day = TimeUnit.MILLISECONDS.toDays(dateDiff)
            if (second > 1 && second < 60) {
                convTime = "$second s"
            } else if (minute < 60) {
                convTime = "$minute m"
            } else if (hour < 24) {
                convTime = "$hour h"
            } else if (day >= 7) {
                convTime = if (day > 360) {
                    (day / 360).toString() + " Y"
                } else if (day > 30) {
                    (day / 30).toString() + " M"
                } else {
                    (day / 7).toString() + " W"
                }
            } else if (day < 7) {
                convTime = "$day D"
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.e("ConvTimeE", e.message!!)
        }
        return convTime
    }
}