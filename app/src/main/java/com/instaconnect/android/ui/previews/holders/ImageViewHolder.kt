package com.instaconnect.android.ui.previews.holders

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.allattentionhere.autoplayvideos.ExoViewHolder
import com.allattentionhere.autoplayvideos.PercentageCropImageView
import com.instaconnect.android.R

class ImageViewHolder(view: View) : ExoViewHolder(view) {
    @JvmField
    val userPic: ImageView
    @JvmField
    val tvUserName: TextView
    @JvmField
    val mCover: PercentageCropImageView
    val mVisibilityPercents: TextView
    @JvmField
    val ivOption: View
    @JvmField
    val tvDate: TextView
    @JvmField
    val tvText: TextView
    val lin_view: LinearLayout
    @JvmField
    val ivAudio: ImageView
    @JvmField
    val tvViewCount: TextView
    @JvmField
    val tv_follow: TextView
    @JvmField
    val iv_favourite: ImageView
    @JvmField
    val iv_like: ImageView
    @JvmField
    val iv_imageBlur: ImageView
    @JvmField
    val tv_like_count: TextView
    @JvmField
    val tv_comment_count: TextView
    @JvmField
    val lin_like: LinearLayout
    @JvmField
    val lin_comment: LinearLayout
    val llComment: LinearLayout
    @JvmField
    val imageLinear: LinearLayout
    val relImagePost: RelativeLayout
    @JvmField
    val relHeader: RelativeLayout

    init {
        tvText = view.findViewById(R.id.tvText)
        mCover = view.findViewById<View>(R.id.cover) as PercentageCropImageView
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
        lin_view.visibility = View.GONE
        llComment = view.findViewById(R.id.llComment)
        imageLinear = view.findViewById(R.id.imageLinear)
        relImagePost = view.findViewById(R.id.relImagePost)
        iv_imageBlur = view.findViewById(R.id.iv_imageBlur)
        relHeader = view.findViewById(R.id.relHeader)
    }
}