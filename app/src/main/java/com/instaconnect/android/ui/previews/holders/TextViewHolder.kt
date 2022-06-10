package com.instaconnect.android.ui.previews.holders

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.allattentionhere.autoplayvideos.ExoViewHolder
import androidx.cardview.widget.CardView
import com.instaconnect.android.R
import com.instaconnect.android.widget.RelativeViewLayout

class TextViewHolder(view: View) : ExoViewHolder(view) {
    val userPic: ImageView
    val lin_view: LinearLayout
    val ivAudio: ImageView
    val tvViewCount: TextView
    val tvUserName: TextView
    val tvText: TextView
    val mVisibilityPercents: TextView
    val ivOption: View
    val tvDate: TextView
    val tv_follow: TextView
    val iv_favourite: ImageView
    val iv_like: ImageView
    val tv_like_count: TextView
    val tv_comment_count: TextView
    val tvViews: TextView
    val lin_like: LinearLayout
    val lin_comment: LinearLayout
    val llComment: LinearLayout
    val textCard: CardView
    val relText: RelativeViewLayout
    val relText2: RelativeViewLayout
    val relFooter: RelativeViewLayout
    val relHeader: RelativeViewLayout

    init {
        tvText = view.findViewById(R.id.tvText)
        mVisibilityPercents = view.findViewById<View>(R.id.visibility_percents) as TextView
        userPic = view.findViewById<View>(R.id.iv_user_image) as ImageView
        ivOption = view.findViewById(R.id.iv_option)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvDate = view.findViewById(R.id.tvdate)
        lin_view = view.findViewById(R.id.lin_view)
        ivAudio = view.findViewById(R.id.ivAudio)
        tvViewCount = view.findViewById(R.id.tvViewCount)
        tv_follow = view.findViewById(R.id.tv_follow)
        iv_favourite = view.findViewById(R.id.iv_favourite)
        tv_like_count = view.findViewById(R.id.tv_like_count)
        tv_comment_count = view.findViewById(R.id.tv_comment_count)
        lin_like = view.findViewById(R.id.lin_like)
        iv_like = view.findViewById(R.id.iv_like)
        lin_comment = view.findViewById(R.id.lin_comment)
        tvViews = view.findViewById(R.id.tvViews)
        lin_view.visibility = View.GONE
        llComment = view.findViewById(R.id.llComment)
        textCard = view.findViewById(R.id.textCard)
        relText = view.findViewById(R.id.relText)
        relText2 = view.findViewById(R.id.relText2)
        relFooter = view.findViewById(R.id.relFooter)
        relHeader = view.findViewById(R.id.relHeader)
    }
}