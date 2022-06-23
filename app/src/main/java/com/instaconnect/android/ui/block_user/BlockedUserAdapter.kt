package com.instaconnect.android.ui.block_user

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.instaconnect.android.ui.public_chat.videoList.VideoListAdapter.Companion.prettyCount
import com.instaconnect.android.utils.models.MessagelistItem
import androidx.recyclerview.widget.RecyclerView
import com.instaconnect.android.R
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.base.BaseIntent
import com.instaconnect.android.ui.watch_together_room.WatchTogetherVideoActivity
import com.instaconnect.android.ui.public_chat.videoList.VideoListAdapter
import com.instaconnect.android.utils.Constants
import io.alterac.blurkit.BlurKit

class BlockedUserAdapter internal constructor(var context: Context, var mNotificationLists: MutableList<MessagelistItem>) :
    RecyclerView.Adapter<BlockedUserAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification_new, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationData = mNotificationLists[position]
        holder.textView.text = notificationData.message
        holder.itemView.setOnClickListener { v: View? ->
            if (notificationData.notifyType == Constants.NOTIFICATION_TYPE_SEND_REQUEST) {
                val intent = Intent(context, HomeActivity::class.java)
                intent.putExtra("from", "notification")
                intent.putExtra("type", notificationData.notifyType)
                context.startActivity(intent)
            } else if (notificationData.notifyType == Constants.NOTIFICATION_TYPE_SEND_INVITATION || notificationData.notifyType == Constants.NOTIFICATION_TYPE_POST_LIKED) {
                handleSendInvitationItem(notificationData)
            }
        }
    }

    private fun handleSendInvitationItem(notificationData: MessagelistItem) {
        if (notificationData.postDetailArr == null) {
            Toast.makeText(context, "This post is not available", Toast.LENGTH_SHORT).show()
        } else {
            val groupPassword = notificationData.postDetailArr.groupPassword
            val dialog = Dialog(context, R.style.CustomDialogTheme)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.dialog_watch_video_notification)
            val ivPostProtected = dialog.findViewById<ImageView>(R.id.ivPostProtected)
            val tvNotificationDesc = dialog.findViewById<TextView>(R.id.tvNotificationDesc)
            tvNotificationDesc.text = notificationData.message
            val tvJoin = dialog.findViewById<TextView>(R.id.tvJoin)
            val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
            if (groupPassword.isEmpty()) {
                ivPostProtected.setImageResource(R.drawable.ic_unlock)
            } else {
                ivPostProtected.setImageResource(R.drawable.ic_lock)
            }
            tvJoin.setOnClickListener { v: View? ->
                if (groupPassword.isEmpty()) {
                    goToWatchPartyRoom(notificationData)
                } else {
                    verifyVideoPasswordDialog(groupPassword, notificationData)
                }
                dialog.dismiss()
            }
            tvCancel.setOnClickListener { v: View? -> dialog.dismiss() }
            val imageView = dialog.findViewById<ImageView>(R.id.img_bg)
            val relMain = dialog.findViewById<View>(R.id.rel_main)
            val vto = relMain.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val width = relMain.measuredWidth
                    val height = relMain.measuredHeight
                    Log.e("View Height", "$width...$height")
                    imageView.layoutParams.height = height
                    imageView.layoutParams.width = width
                    imageView.setImageBitmap(BlurKit.getInstance().fastBlur(imageView, 8, 0.12.toFloat()))
                }
            })
            dialog.show()
        }
    }

    private fun goToWatchPartyRoom(messagelistItem: MessagelistItem) {
        val intent = BaseIntent((context as Activity), WatchTogetherVideoActivity::class.java, false)
        intent.putExtra("USER_ID", messagelistItem.postDetailArr.userId)
        if (messagelistItem.postDetailArr.youTubeVideoId.toString() == "empty") {
            // for other type of video
            intent.putExtra("VIDEO_ID", messagelistItem.postDetailArr.hyperlink)
        } else {
            // for youtube video
            intent.putExtra("VIDEO_ID", messagelistItem.postDetailArr.youTubeVideoId)
        }
        intent.putExtra("POST_ID", messagelistItem.postDetailArr.mediaType)
        intent.putExtra("ACTUAL_POST_ID", messagelistItem.postDetailArr.id)
        intent.putExtra("ROOM_NAME", messagelistItem.postDetailArr.groupName)
        intent.putExtra("USER_NAME", messagelistItem.postDetailArr.username)
        intent.putExtra("USER_IMAGE", messagelistItem.postDetailArr.userimage)
        intent.putExtra("TOTAL_VIEWS", messagelistItem.postDetailArr.totalViews)
        intent.putExtra("GROUP_NAME", messagelistItem.postDetailArr.groupName)
        intent.putExtra("POST_REACTION", messagelistItem.postDetailArr.yourReaction)
        intent.putExtra("TOTAL_LIKES", prettyCount(messagelistItem.postDetailArr.likes.toString().toInt()))
        context.startActivity(intent)
    }

    private fun verifyVideoPasswordDialog(groupPassword: String, messagelistItem: MessagelistItem) {
        val dialog = Dialog(context, R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_private_room_video_password_new)
        val edtPassword = dialog.findViewById<EditText>(R.id.edtPassword)
        val tvCancel = dialog.findViewById<TextView>(R.id.tvCancel)
        val tvOk = dialog.findViewById<TextView>(R.id.tvOk)
        val imageView = dialog.findViewById<ImageView>(R.id.img_bg)
        val relMain = dialog.findViewById<View>(R.id.rel_main)
        val vto = relMain.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = relMain.measuredWidth
                val height = relMain.measuredHeight
                Log.e("View Height", "$width...$height")
                imageView.layoutParams.height = height
                imageView.layoutParams.width = width
                imageView.setImageBitmap(BlurKit.getInstance().fastBlur(imageView, 8, 0.12.toFloat()))
            }
        })
        tvCancel.setOnClickListener { v: View? -> dialog.dismiss() }
        tvOk.setOnClickListener { v: View? ->
            val enteredPassword = edtPassword.text.toString()
            if (enteredPassword.isEmpty()) {
                Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
            } else {
                if (enteredPassword == groupPassword) {
                    goToWatchPartyRoom(messagelistItem)
                    dialog.dismiss()
                } else {
                    Toast.makeText(context, "You've entered wrong password!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    override fun getItemCount(): Int {
        return mNotificationLists.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.txt_notification)
    }

    fun clear() {
        mNotificationLists.clear()
        notifyDataSetChanged()
    }

    fun addUser(userList: List<MessagelistItem>?) {
        mNotificationLists.addAll(userList!!)
        notifyDataSetChanged()
    }
}