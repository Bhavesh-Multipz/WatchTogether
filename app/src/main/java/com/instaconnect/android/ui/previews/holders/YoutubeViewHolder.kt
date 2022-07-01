package com.instaconnect.android.ui.previews.holders

import android.content.Context
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.instaconnect.android.ui.public_chat.videoList.VideoListAdapter
import com.allattentionhere.autoplayvideos.ExoViewHolder
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.allattentionhere.autoplayvideos.ExoVideoImage
import com.allattentionhere.autoplayvideos.ExoMediaPlayer
import com.instaconnect.android.widget.BasicTextView
import androidx.cardview.widget.CardView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar
import com.allattentionhere.autoplayvideos.VideoRecyclerView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.instaconnect.android.InstaConnectApp
import com.instaconnect.android.R
import com.instaconnect.android.rxBus.BusMessage
import com.instaconnect.android.rxBus.RxBus
import com.instaconnect.android.utils.Util
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBarListener
import java.lang.Exception

class YoutubeViewHolder(
    view: View,
    private val context: Context,
    lifecycle: Lifecycle,
    var videoListAdapter: VideoListAdapter
) : ExoViewHolder(view) {
    @JvmField
    var userPic: ImageView
    @JvmField
    val tvUserName: TextView
    @JvmField
    val tvText: TextView
    val mVisibilityPercents: TextView
    @JvmField
    val ivOption: View
    @JvmField
    val tvDate: TextView
    val tvTime: TextView
    @JvmField
    val ivAudio: ImageView
    @JvmField
    val lin_view: LinearLayout
    @JvmField
    val tvViewCount: TextView
    @JvmField
    val tv_follow: TextView
    @JvmField
    val iv_favourite: ImageView
    @JvmField
    val iv_like: ImageView
    @JvmField
    val tv_like_count: TextView
    @JvmField
    val lin_comment: LinearLayout
    @JvmField
    val tv_comment_count: TextView
    @JvmField
    val lin_like: LinearLayout
    @JvmField
    val youTubePlayerView: YouTubePlayerView
    val llComment: LinearLayout
    @JvmField
    var mYouTubePlayer: YouTubePlayer? = null
    val mPlayer: ExoVideoImage

    var isPlayingOrNot = false
    var isLoading = false
    var isVisible = false
    val exoMediaPlayer: ExoMediaPlayer
    @JvmField
    var img_togather: ImageView
    @JvmField
    var iv_imageBlur: ImageView
    @JvmField
    var tvLbl: BasicTextView
    @JvmField
    var relFooter: LinearLayout
    @JvmField
    var relHeader: LinearLayout
    var relYoutube: RelativeLayout
    var relYoutubeGridView: RelativeLayout
    @JvmField
    var ivYoutubeClick: ImageView
    var iv_youtubeGridImage: ImageView? = null
    var videoCardView: CardView
    @JvmField
    var tvYoutubeGridViewCaption: TextView

    var tvYoutubeGridCaption: TextView
    @JvmField
    var currentPosition = 0
    @JvmField
    var mute_unmute_button: ImageView
    var youtube_player_seekbar: YouTubePlayerSeekBar
    @JvmField
    var relLabels: RelativeLayout
    lateinit var relMain : RelativeLayout
    override fun videoStarted() {
        super.videoStarted()
        if (VideoRecyclerView.isMute()) {
            ivAudio.setImageResource(R.drawable.mute)
        } else {
            ivAudio.setImageResource(R.drawable.audio_play)
        }
        setTime()
    }

    override fun muteVideo() {
        super.muteVideo()
        RxBus.instance!!.publish(BusMessage.MUTE.name, true)
    }

    override fun unmuteVideo() {
        super.unmuteVideo()
        RxBus.instance!!.publish(BusMessage.MUTE.name, false)
    }

    private var timer: CountDownTimer? = null
    private fun setTime() {
        timer = object : CountDownTimer(6000, 1000) {
            override fun onTick(l: Long) {
                (context as AppCompatActivity).runOnUiThread {
                    try {
                        if (playerView.player != null) {
                            if (playerView.player.currentPosition in 3001..3999) {
                                videoListAdapter.increaseYoutubePostView(
                                    currentPosition,
                                    this@YoutubeViewHolder
                                )
                            }
                            val time =
                                Util.formateMilliSeccond(playerView.player.duration - playerView.player.currentPosition)
                            if (tvTime.text.toString().length < 6) {
                                tvTime.text = time
                                tvTime.visibility = View.VISIBLE
                            } else tvTime.visibility = View.GONE
                        } else {
                            tvTime.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFinish() {
                (context as AppCompatActivity).runOnUiThread {
                    try {
                        tvTime.text = ""
                        tvTime.visibility = View.GONE
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                timer!!.cancel()
            }
        }.start()
    }

    init {
        relMain = view.findViewById(R.id.main)
        tvText = view.findViewById(R.id.tvText)
        mVisibilityPercents = view.findViewById<View>(R.id.visibility_percents) as TextView
        userPic = view.findViewById<View>(R.id.iv_user_image) as ImageView
        ivOption = view.findViewById(R.id.iv_option)
        ivAudio = view.findViewById<View>(R.id.ivAudio) as ImageView
        ivAudio.visibility = View.GONE
        tvYoutubeGridCaption = view.findViewById(R.id.tvYoutubeGridCaption)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvDate = view.findViewById(R.id.tvdate)
        tvTime = view.findViewById(R.id.tv_timer)
        lin_view = view.findViewById(R.id.lin_view)
        tvViewCount = view.findViewById(R.id.tvViewCount)
        tv_follow = view.findViewById(R.id.tv_follow)
        iv_favourite = view.findViewById(R.id.iv_favourite)
        tv_like_count = view.findViewById(R.id.tv_like_count)
        tv_comment_count = view.findViewById(R.id.tv_comment_count)
        lin_comment = view.findViewById(R.id.lin_comment)
        lin_like = view.findViewById(R.id.lin_like)
        iv_like = view.findViewById(R.id.iv_like)
        img_togather = view.findViewById(R.id.img_togather)
        youTubePlayerView = view.findViewById(R.id.youtubeplayer)
        mPlayer = view.findViewById(R.id.player)
        //        mPlayer.setVisibility(View.GONE);
        lifecycle.addObserver(youTubePlayerView)
        llComment = view.findViewById(R.id.llComment)
        iv_imageBlur = view.findViewById(R.id.iv_imageBlur)
        tvLbl = view.findViewById(R.id.tvLbl)
        relFooter = view.findViewById(R.id.relFooter)
        relHeader = view.findViewById(R.id.relHeader)
        relYoutube = view.findViewById(R.id.relYoutube)
        ivYoutubeClick = view.findViewById(R.id.btnClick)
        relYoutubeGridView = view.findViewById(R.id.relYoutubeGridView)
        videoCardView = view.findViewById(R.id.videoCardView)
        tvYoutubeGridViewCaption = view.findViewById(R.id.tvYoutubeGridViewCaption)
        youtube_player_seekbar = view.findViewById(com.pierfrancescosoffritti.androidyoutubeplayer.R.id.youtube_player_seekbar)
        mute_unmute_button = view.findViewById(R.id.mute_unmute_button)
        relLabels = view.findViewById(R.id.relLabels)
        iv_youtubeGridImage = view.findViewById(R.id.iv_youtubeGridImage)
        /*iv_youtubeGridImage = view.findViewById(R.id.iv_youtubeGridImage);*/
        // lifecycle.addObserver(youTubePlayerView);
        exoMediaPlayer = ExoMediaPlayer(context, InstaConnectApp.instance!!.getDownloadCache())
        isPlayingOrNot = false
        isLoading = false
        isVisible = false
        youTubePlayerView.addYouTubePlayerListener(youtube_player_seekbar)
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
            }
        })
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
            }

            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerState) {
                if (state == PlayerState.ENDED) {
                }
            }
        })
        youtube_player_seekbar.youtubePlayerSeekBarListener =
            object : YouTubePlayerSeekBarListener {
                override fun seekTo(time: Float) {
                    mYouTubePlayer!!.seekTo(time)
                }
            }

        /*RxBus.getInstance().subscribe(BusMessage.MUTE.name(), this, Boolean.class, new Consumer<Boolean>() {
            @Override
            public void accept(final Boolean mute) throws Exception {
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mute)
                            ivAudio.setImageResource(R.drawable.mute);
                        else
                            ivAudio.setImageResource(R.drawable.audio_play);
                    }
                });
            }
        });*/
    }
}