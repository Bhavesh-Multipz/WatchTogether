package com.instaconnect.android.ui.fragment.worldwide

import android.content.Context
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.allattentionhere.autoplayvideos.ExoMediaPlayer
import com.allattentionhere.autoplayvideos.ExoVideoImage
import com.allattentionhere.autoplayvideos.ExoViewHolder
import com.allattentionhere.autoplayvideos.VideoRecyclerView.isMute
import com.instaconnect.android.InstaConnectApp
import com.instaconnect.android.R
import com.instaconnect.android.ui.public_chat.videoList.VideoListAdapter
import com.instaconnect.android.utils.Util
import com.instaconnect.android.widget.BasicTextView
import com.instaconnect.android.widget.RelativeViewLayout
import dagger.android.ContributesAndroidInjector

class VideoViewHolder(
    view: View,
    private val context: Context,
    var videoListAdapter: VideoListAdapter
) : ExoViewHolder(view) {
    val mPlayer: ExoVideoImage
    val userPic: ImageView
    val tvUserName: TextView
    val tvText: TextView
    val mVisibilityPercents: TextView
    val ivOption: View
    val tvDate: TextView
    val tvTime: TextView
    val ivAudio: ImageView
    val lin_view: LinearLayout
    val tvViewCount: TextView
    val tv_follow: TextView
    val iv_favourite: ImageView
    val iv_like: ImageView
    val tv_like_count: TextView
    val lin_comment: LinearLayout
    val linearFooter: LinearLayout
    val tv_comment_count: TextView
    val lin_like: LinearLayout
    val exoMediaPlayer: ExoMediaPlayer
    val llComment: LinearLayout
    val tvLbl: BasicTextView
    val img_togather: ImageView
    val iv_imageBlur: ImageView
    val relVideoHeader: RelativeLayout
    val videoCardView: CardView
    public var currentPosition = 0
    var isWatchTogetherVideo = false
    var listener: VideoListAdapter.VideoCountIncreaseListener? = null
    var relMain: RelativeViewLayout
    var relWatchTogetherView: RelativeLayout
    var tvWatchTogetherCaption: TextView
    var ivVideoGif: ImageView
    var iv_videoGridImage: ImageView

    override fun videoStarted() {
        super.videoStarted()
        if (isMute()) {
            ivAudio.setImageResource(R.drawable.mute)
        } else {
            ivAudio.setImageResource(R.drawable.audio_play)
        }
        setTime()
    }

    override fun muteVideo() {
        super.muteVideo()
        //RxBus.getInstance().publish(BusMessage.MUTE.name(), true)


    }

    override fun unmuteVideo() {
        super.unmuteVideo()
        //RxBus.getInstance().publish(BusMessage.MUTE.name(), false)
    }

    private var timer: CountDownTimer? = null
    override fun videoRepeat() {
        super.videoRepeat()
        videoListAdapter.increasePostView(currentPosition, this)
    }

    private fun setTime() {
        timer = object : CountDownTimer(6000, 1000) {
            override fun onTick(l: Long) {
                (context as AppCompatActivity).runOnUiThread(Runnable {
                    try {
                        if (playerView.player != null) {
                            if (playerView.player!!.currentPosition in 3001..3999) {
                                videoListAdapter.increasePostView(
                                    currentPosition,
                                    this@VideoViewHolder
                                )
                            }
                            val time: String =
                                Util.formateMilliSeccond(playerView.player!!.duration - playerView.player!!.currentPosition)
                            if (tvTime.text.toString().length < 6) {
                                tvTime.text = time
                                if (!isWatchTogetherVideo) tvTime.visibility = View.VISIBLE
                            } else tvTime.visibility = View.GONE
                        } else {
                            tvTime.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
            }

            override fun onFinish() {
                (context as AppCompatActivity).runOnUiThread(Runnable {
                    try {
                        tvTime.text = ""
                        tvTime.visibility = View.GONE
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
                timer!!.cancel()
            }
        }.start()
    }

    init {
        tvText = view.findViewById(R.id.tvText)
        mVisibilityPercents = view.findViewById<View>(R.id.visibility_percents) as TextView
        userPic = view.findViewById<View>(R.id.iv_user_image) as ImageView
        ivOption = view.findViewById(R.id.iv_option)
        ivAudio = view.findViewById<View>(R.id.ivAudio) as ImageView
        tvUserName = view.findViewById(R.id.tvUserName)
        tvDate = view.findViewById(R.id.tvdate)
        tvTime = view.findViewById(R.id.tv_timer)
        mPlayer = view.findViewById(R.id.player)
        lin_view = view.findViewById(R.id.lin_view)
        tvViewCount = view.findViewById(R.id.tvViewCount)
        tv_follow = view.findViewById(R.id.tv_follow)
        iv_favourite = view.findViewById(R.id.iv_favourite)
        tv_like_count = view.findViewById(R.id.tv_like_count)
        tv_comment_count = view.findViewById(R.id.tv_comment_count)
        lin_comment = view.findViewById(R.id.lin_comment)
        lin_like = view.findViewById(R.id.lin_like)
        iv_like = view.findViewById(R.id.iv_like)
        relMain = view.findViewById(R.id.relMain)
        exoMediaPlayer = ExoMediaPlayer(context, InstaConnectApp.instance!!.getDownloadCache())
        llComment = view.findViewById(R.id.llComment)
        tvLbl = view.findViewById(R.id.tvLbl)
        linearFooter = view.findViewById(R.id.linearFooter)
        img_togather = view.findViewById(R.id.img_togather)
        relVideoHeader = view.findViewById(R.id.relVideoHeader)
        videoCardView = view.findViewById(R.id.videoCardView)
        iv_imageBlur = view.findViewById(R.id.iv_imageBlur)
        relWatchTogetherView = view.findViewById(R.id.relWatchTogetherView)
        tvWatchTogetherCaption = view.findViewById(R.id.tvWatchTogetherCaption)
        ivVideoGif = view.findViewById(R.id.ivVideoGif)
        iv_videoGridImage = view.findViewById(R.id.iv_videoGridImage)
        /*RxBus.getInstance().subscribe(
            BusMessage.MUTE.name(),
            this,
            Boolean::class.java,
            object : Consumer<Boolean?>() {
                @Throws(Exception::class)
                fun accept(mute: Boolean) {
                    (context as AppCompatActivity).runOnUiThread(Runnable {
                        if (mute) ivAudio.setImageResource(
                            R.drawable.mute
                        ) else ivAudio.setImageResource(R.drawable.audio_play)
                    })
                }
            })*/
    }
}