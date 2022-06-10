package com.instaconnect.android.ui.public_chat.videoList

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.allattentionhere.autoplayvideos.ExoVideosAdapter
import com.allattentionhere.autoplayvideos.ExoViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.danikula.videocache.HttpProxyCacheServer
import com.instaconnect.android.R
import com.instaconnect.android.network.ApiEndPoint
import com.instaconnect.android.ui.fragment.worldwide.Post.PostsList
import com.instaconnect.android.ui.fragment.worldwide.VideoViewHolder
import com.instaconnect.android.ui.previews.holders.ImageViewHolder
import com.instaconnect.android.ui.previews.holders.TextViewHolder
import com.instaconnect.android.ui.previews.holders.YoutubeViewHolder
import com.instaconnect.android.utils.DateUtil
import com.instaconnect.android.utils.DoubleClickListener
import com.instaconnect.android.utils.ScreenUtils
import com.instaconnect.android.utils.Util
import com.instaconnect.android.utils.helper_classes.GlideHelper
import com.instaconnect.android.utils.models.User
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.thefinestartist.finestwebview.FinestWebView
import java.net.URL
import java.text.DecimalFormat
import java.util.regex.Pattern

class VideoListAdapter(
    val context: Context,
    private val proxyCacheServer: HttpProxyCacheServer,
    val listListener: VideoListListener,
    val user: User,
    val lifecycle: Lifecycle,
    var currentLayoutType: String,
    private val videoCountIncreaseListener: VideoCountIncreaseListener
) : ExoVideosAdapter() {
    val postsLists = ArrayList<PostsList>()
    private var hyperlinkIcon: Drawable =
        ContextCompat.getDrawable(context, R.drawable.external_link)!!
    var imageHeight = 0
    var imageWidth = 0
    var isWatchTogether = false
    var screenHeight = 0
    var screenWidth = 0
    var videoWidth = 0
    var videoHeight = 0
    var isMute = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExoViewHolder {
        val resultView: View
        Log.e("ViewType", "$viewType...")
        when (viewType) {
            VIDEO -> {
                resultView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_public_video_new, parent, false)
                return VideoViewHolder(resultView, context, this)
            }
            YOUTUBE -> {
                resultView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_public_youtube_video_new, parent, false)
                return YoutubeViewHolder(resultView, context, lifecycle, this)
            }
            YOUTUBE_WATCH_TOGATHER -> {
                resultView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.rv_public_youtube_video_watchtogather, parent, false)
                return YoutubeViewHolder(resultView, context, lifecycle, this)
            }
            IMAGE -> {
                resultView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.rv_public_image, parent, false)
                return ImageViewHolder(resultView)
            }
            else -> {
                resultView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.rv_public_text, parent, false)
                return TextViewHolder(resultView)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: ExoViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is YoutubeViewHolder) {
            holder.isLoading = false
            holder.isPlayingOrNot = false
            if (holder.mYouTubePlayer != null) {
                holder.mYouTubePlayer!!.mute()
                holder.mute_unmute_button.setImageResource(R.drawable.ic_mute)
                isMute = true
                holder.mYouTubePlayer!!.pause()

            }
        }
    }

    override fun onViewAttachedToWindow(holder: ExoViewHolder) {
        super.onViewAttachedToWindow(holder)

        if (holder is YoutubeViewHolder) {
            if (holder.isLoading && holder.mYouTubePlayer == null) {
                try {
                    if (postsLists[holder.bindingAdapterPosition].groupPassword!!.isEmpty()) {
                        holder.youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                super.onReady(youTubePlayer)
                                try {
                                    holder.mYouTubePlayer = youTubePlayer
                                    holder.mYouTubePlayer!!.cueVideo(postsLists[(holder.currentPosition)].youTubeVideoId!!, 0f)
                                    if (holder.isPlaying) {
                                        holder.isLoading = false
                                    } else {
                                        holder.isPlayingOrNot = true
                                        holder.mYouTubePlayer!!.pause()
                                        holder.mYouTubePlayer!!.mute()
                                        holder.mYouTubePlayer!!.loadVideo(postsLists[holder.currentPosition].youTubeVideoId!!, 0f)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace();
                                }
                            }
                        })
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "onViewAttachedToWindow: ccccccc" + e.message);
                    e.printStackTrace();
                }

            } else if (holder.mYouTubePlayer != null) {
                if (postsLists[holder.bindingAdapterPosition].groupPassword!!.isEmpty()) {
                    if (holder.isPlaying) {
                        holder.isLoading = false
                    } else {
                        holder.isPlayingOrNot = true
                        holder.mYouTubePlayer!!.pause()
                        holder.mYouTubePlayer!!.mute()
                        postsLists[holder.currentPosition].youTubeVideoId?.let {
                            holder.mYouTubePlayer!!.loadVideo(
                                it, 0f
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(viewHolder: ExoViewHolder, position: Int) {
        Log.d("viewholder", "onBindViewHolder")
        when (viewHolder) {
            is VideoViewHolder -> {
                setVideoView(viewHolder, position, videoCountIncreaseListener)
            }
            is YoutubeViewHolder -> {
                setYoutubeView(viewHolder, position)
            }
            is ImageViewHolder -> {
                setImageView(viewHolder, position)
            }
        }
    }

    override fun onViewRecycled(holder: ExoViewHolder) {
        super.onViewRecycled(holder)
        Log.d("viewholder", "onViewRecycled" + holder.itemId)
    }

    private fun setVideoView(
        viewHolder: VideoViewHolder,
        position: Int,
        videoCountIncreaseListener: VideoCountIncreaseListener?
    ) {
        viewHolder.currentPosition = position
        viewHolder.tvDate.text = DateUtil.formatByDay((postsLists[position].date)!!)
        viewHolder.tvViewCount.text = prettyCount(postsLists[position].totalViews!!.toInt())
        viewHolder.tv_comment_count.text = prettyCount(postsLists[position].commentTotal)
        viewHolder.tv_like_count.text = prettyCount(postsLists[position].reaction!!.likes)
        viewHolder.tvText.post {
            hyperlinkIcon.setBounds(
                0,
                0,
                viewHolder.tvText.lineHeight,
                viewHolder.tvText.lineHeight
            )
            val myText: String? = postsLists[position].caption
            val textLength: Int = myText!!.length
            val sb = SpannableString("$myText     ")
            if (postsLists[position].hyperlink != null && postsLists[position].hyperlink!!.trim { it <= ' ' }
                    .isNotEmpty()) {
                val imageSpan = ImageSpan(hyperlinkIcon, ImageSpan.ALIGN_BOTTOM)
                sb.setSpan(
                    imageSpan,
                    textLength + 2,
                    textLength + 3,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            if (postsLists[position].caption != null && postsLists[position].caption != "") {
                viewHolder.tvText.text = sb
                /*if ((postsLists[position].mediaType == "web")) {
                    viewHolder.tvText.text = sb
                } else {
                    viewHolder.tvText.text = sb
                }*/
            } else {
                viewHolder.tvText.height = 5
            }
            viewHolder.tvText.movementMethod = LinkMovementMethod.getInstance()
        }
        if ((postsLists[position].userId == user.phone)) {
            viewHolder.tv_follow.visibility = View.GONE
        }
        if (postsLists[position].favourite!!.status == 0) {
            viewHolder.iv_favourite.setImageResource(R.drawable.ic_unfavourites)
        } else {
            viewHolder.iv_favourite.setImageResource(R.drawable.ic_favourites)
        }
        if (postsLists[position].reaction!!.yourReaction.equals("0", ignoreCase = true)) {
            viewHolder.iv_like.setImageResource(R.drawable.ic_unlike)
        } else {
            viewHolder.iv_like.setImageResource(R.drawable.ic_like)
        }
        if ((!postsLists[position].video.equals("", ignoreCase = true)
                    && !postsLists[position].thumbnail.equals("", ignoreCase = true)
                    && !postsLists[position].mediaType.equals("", ignoreCase = true))
        ) {
            viewHolder.lin_view.visibility = View.VISIBLE
        } else {
            viewHolder.lin_view.visibility = View.GONE
        }
        if (postsLists[position].follow!!.status == 0) {
            if (postsLists[position].isPrivate
                    .equals("0", true)
            ) viewHolder.tv_follow.text = "Following" else if (postsLists[position].isPrivate
                    .equals("1", true)
            ) viewHolder.tv_follow.text = "Requested"
        } else if (postsLists[position].follow!!.status == 1) viewHolder.tv_follow.text =
            "Requested" else if (postsLists[position].follow!!.status == 2) viewHolder.tv_follow.text =
            "Following" else if (postsLists[position].follow!!.status == 3) viewHolder.tv_follow.text =
            "+follow"
        viewHolder.imageView.cropYCenterOffsetPct = 0f
        viewHolder.tvUserName.text = postsLists.get(position).username
        GlideHelper.loadFromUrl(
            context,
            postsLists[position].userimage,
            R.drawable.gridview_image,
            viewHolder.userPic
        )
        if (postsLists[position].thumbnail!!.contains("http")) {
            viewHolder.imageUrl = postsLists[position].thumbnail
            if ((postsLists[position].mediaType == "web")) {
                viewHolder.videoUrl = postsLists[position].hyperlink
            } else {
                viewHolder.videoUrl = proxyCacheServer.getProxyUrl(postsLists[position].video)
            }
        } else {
            viewHolder.imageUrl =
                ApiEndPoint.UPLOADS_BASE_URL + postsLists[position].thumbnail
            if ((postsLists[position].mediaType == "web")) {
                viewHolder.videoUrl = postsLists[position].hyperlink
            } else {
                viewHolder.videoUrl =
                    ApiEndPoint.UPLOADS_BASE_URL + postsLists[position].video
            }
        }
        if (postsLists[viewHolder.bindingAdapterPosition].groupPassword!!.isEmpty()) {
            viewHolder.initVideoView(context as AppCompatActivity, viewHolder.exoMediaPlayer, null)
        }

        //load image/thumbnail into ImageView
        if (((postsLists[position].thumbnail != null) && !postsLists[position].thumbnail!!.isEmpty() &&
                    !postsLists[position].thumbnail!!.contains("http"))
        ) {
            if (postsLists.get(position).mediaType != "web") {
                if (postsLists[position].thumbnail!!.contains("http")) {
                    GlideHelper.loadFromUrl(
                        context, postsLists[position].thumbnail,
                        R.drawable.gridview_image, viewHolder.imageView
                    )
                } else {
                    GlideHelper.loadFromUrl(
                        context,
                        ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].thumbnail,
                        R.drawable.gridview_image,
                        viewHolder.imageView
                    )
                }
            } else {
                if (postsLists[position].thumbnail!!.contains("http")) {
                    GlideHelper.loadFromUrl(
                        context, postsLists[position].thumbnail,
                        0, viewHolder.imageView
                    )
                } else {
                    GlideHelper.loadFromUrl(
                        context,
                        ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].thumbnail,
                        0,
                        viewHolder.imageView
                    )
                }
            }
            if (postsLists[position].blurBitmap == null) {
                getBitmapFromUrl(
                    position,
                    ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].thumbnail,
                    viewHolder.iv_imageBlur
                )
            } else {
                viewHolder.iv_imageBlur.setImageBitmap(postsLists[position].blurBitmap)
            }
        } else if (postsLists[position].thumbnail != null && !postsLists[position].thumbnail!!.isEmpty()) {
            if (postsLists.get(position).mediaType != "web") {
                if (postsLists[position].thumbnail!!.contains("http")) {
                    GlideHelper.loadFromUrl(
                        context, postsLists[position].thumbnail,
                        R.drawable.gridview_image, viewHolder.imageView
                    )
                } else {
                    GlideHelper.loadFromUrl(
                        context,
                        ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].thumbnail,
                        R.drawable.gridview_image,
                        viewHolder.imageView
                    )
                }
            } else {
                if (postsLists[position].thumbnail!!.contains("http")) {
                    GlideHelper.loadFromUrl(
                        context, postsLists[position].thumbnail,
                        R.drawable.gridview_image, viewHolder.imageView
                    )
                } else {
                    GlideHelper.loadFromUrl(
                        context,
                        ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].thumbnail,
                        R.drawable.gridview_image,
                        viewHolder.imageView
                    )
                }
            }
            if (postsLists[position].blurBitmap == null) {
                getBitmapFromUrl(position, postsLists[position].thumbnail, viewHolder.iv_imageBlur)
            } else {
                viewHolder.iv_imageBlur.setImageBitmap(postsLists[position].blurBitmap)
            }
        } else {
            GlideHelper.loadFromUrl(
                context, postsLists[position].thumbnail,
                R.drawable.gridview_image, viewHolder.imageView
            )
            if (postsLists[position].blurBitmap == null) {
                getBitmapFromUrl(position, postsLists[position].thumbnail, viewHolder.iv_imageBlur)
            } else {
                viewHolder.iv_imageBlur.setImageBitmap(postsLists[position].blurBitmap)
            }
        }
        if (isMute) {
            viewHolder.ivAudio.setImageResource(R.drawable.mute)
        } else {
            viewHolder.ivAudio.setImageResource(R.drawable.audio_play)
        }
        viewHolder.userPic.setOnClickListener { v: View? -> }
        viewHolder.tvUserName.setOnClickListener { v: View? -> }

        //to mute/un-mute video (optional)
        viewHolder.ivAudio.setOnClickListener { v: View? ->
            if (isMute) {
                viewHolder.unmuteVideo()
                viewHolder.ivAudio.setImageResource(R.drawable.audio_play)
            } else {
                viewHolder.muteVideo()
                viewHolder.ivAudio.setImageResource(R.drawable.mute)
            }
        }
        viewHolder.tvText.setOnClickListener(View.OnClickListener {
            listListener.onItemSingleClick(
                postsLists[position], viewHolder.imageView, VIDEO, position
            )
        })
        viewHolder.itemView.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                if (((postsLists[position].mediaType == "web") && postsLists[position].groupPassword != "" && (postsLists[position].hyperlink != null))
                ) {
                    listListener.onPrivateRoomVideoClick(
                        postsLists[position], postsLists[position].mediaType,
                        postsLists[position].groupPassword,
                        postsLists[position].hyperlink, VIDEO
                    )
                } else {
                    val totalViewCount = postsLists[position].totalViews!!.toInt()
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewHolder.tvViewCount.text = prettyCount(totalViewCount + 1)
                    }, 500)
                    listListener.onItemSingleClick(
                        postsLists[position],
                        viewHolder.imageView,
                        VIDEO,
                        position
                    )
                }
            }

            override fun onDoubleClick(v: View?) {
                listListener.onItemViewClick(
                    postsLists[viewHolder.bindingAdapterPosition],
                    viewHolder.imageView,
                    VIDEO
                )
            }
        })

        viewHolder.tv_follow.setOnClickListener {
            listListener.onItemClick(
                viewHolder.bindingAdapterPosition,
                postsLists[viewHolder.bindingAdapterPosition],
                viewHolder.tv_follow
            )
        }

        viewHolder.ivOption.setOnClickListener {
            listListener.onOptionClick(
                viewHolder.bindingAdapterPosition,
                postsLists[viewHolder.bindingAdapterPosition],
                VIDEO
            )
        }

        viewHolder.iv_favourite.setOnClickListener {
            listListener.onItemClick(
                viewHolder.bindingAdapterPosition,
                postsLists[viewHolder.bindingAdapterPosition],
                viewHolder.iv_favourite
            )
        }

        viewHolder.lin_like.setOnClickListener {
            listListener.onItemClick(
                viewHolder.bindingAdapterPosition,
                postsLists[viewHolder.bindingAdapterPosition],
                viewHolder.lin_like
            )
        }

        viewHolder.lin_comment.setOnClickListener {
            listListener.onItemClick(
                viewHolder.bindingAdapterPosition,
                postsLists[viewHolder.bindingAdapterPosition],
                viewHolder.lin_comment
            )
        }

        if ((currentLayoutType == "Grid")) {
            Log.d("TAG", "currentLayoutType: Grid : tvText - GONE")
            if ((postsLists[position].mediaType == "web")) {
                viewHolder.mPlayer.visibility = View.GONE
                viewHolder.relWatchTogetherView.visibility = View.VISIBLE
                viewHolder.ivVideoGif.visibility = View.GONE
                viewHolder.iv_imageBlur.visibility = View.GONE
                val params = RelativeLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    550
                )
                params.setMargins(4, 4, 4, 4)
                viewHolder.relWatchTogetherView.layoutParams = params
                viewHolder.tvWatchTogetherCaption.text = postsLists[position].caption
                if (postsLists[position].groupPassword!!.isEmpty()) {
                    // watch together
                    if (postsLists[position].caption!!.isEmpty()) {
                        viewHolder.iv_videoGridImage.setImageResource(R.drawable.ic_grid_watch_together_glass)
                    } else {
                        viewHolder.iv_videoGridImage.setImageResource(R.drawable.ic_watch_with_name)
                    }
                } else {
                    // private room
                    if (postsLists[position].caption!!.isEmpty()) {
                        viewHolder.iv_videoGridImage.setImageResource(R.drawable.ic_grid_private_room_glass)
                    } else {
                        viewHolder.iv_videoGridImage.setImageResource(R.drawable.ic_private_witt_name)
                    }
                }
            } else {
                viewHolder.mPlayer.visibility = View.GONE
                viewHolder.relWatchTogetherView.visibility = View.VISIBLE

                viewHolder.iv_imageBlur.visibility = View.GONE
                viewHolder.ivVideoGif.visibility = View.VISIBLE
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    550
                )
                params.setMargins(4, 4, 4, 4)
                viewHolder.ivVideoGif.layoutParams = params
                viewHolder.ivVideoGif.setPadding(0, 4, 4, 0)
                if (postsLists[position].gif!!.contains("http")) {
                    Glide.with(context)
                        .asGif()
                        .load(postsLists[position].gif)
                        .placeholder(R.drawable.gridview_image)
                        .into(viewHolder.ivVideoGif)
                } else {
                    Log.e(
                        "ivVideoGif",
                        ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].gif + "..."
                    )
                    Glide.with(context!!)
                        .asGif()
                        .load(ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].gif)
                        .placeholder(R.drawable.gridview_image)
                        .into(viewHolder.ivVideoGif)
                }
            }
            hideVideoLayouts(viewHolder)
        } else {
            val params2 = RelativeLayout.LayoutParams(
                0,
                0
            )
            viewHolder.ivVideoGif.layoutParams = params2
            viewHolder.mPlayer.visibility = View.VISIBLE
            viewHolder.relWatchTogetherView.visibility = View.GONE
            viewHolder.ivVideoGif.visibility = View.GONE

            if ((postsLists[position].mediaType == "web")) {
                screenWidth = ScreenUtils.getScreenWidth(context)
                val videoHeight = screenWidth * 9 / 16
                val params = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    videoHeight
                )
                viewHolder.mPlayer.layoutParams = params
            } else {
                val params = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                viewHolder.mPlayer.layoutParams = params
            }
            if ((postsLists[position].mediaType == "web")) {
                viewHolder.img_togather.visibility = View.VISIBLE
                viewHolder.isWatchTogetherVideo = true
            } else {
                viewHolder.img_togather.visibility = View.GONE
                viewHolder.isWatchTogetherVideo = false
            }
            showVideoLayouts(viewHolder, postsLists[position], position)
            if (!postsLists[position].groupPassword!!.isEmpty()) {
                viewHolder.ivAudio.visibility = View.GONE
            }

        }
    }

    fun getBitmapFromUrl(position: Int, url: String?, iv_imageBlur: ImageView) {
        if (!(context as Activity).isFinishing) {
            Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        val blur = Util.blur(context, resource)
                        Handler().postDelayed(object : Runnable {
                            override fun run() {
                                try {
                                    postsLists[position].blurBitmap = blur
                                    iv_imageBlur.setImageBitmap(blur)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }, 1000)
                    }
                })
        }
    }

    fun setLayout(grid: String) {
        currentLayoutType = grid
    }

    inner class DownloadImage(position: Int) : AsyncTask<String?, Void?, Bitmap?>() {
        var position: Int

        override fun onPostExecute(result: Bitmap?) {
            postsLists[position].blurBitmap = Util.blur(context, (result)!!)
            //            notifyDataSetChanged();
        }

        init {
            this.position = position
        }

        override fun doInBackground(vararg params: String?): Bitmap? {
            val urldisplay = params[0]
            var mIcon11: Bitmap? = null
            try {
                val `in` = URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.d("Error", e.stackTrace.toString())
            }
            return mIcon11
        }
    }

    fun increasePostView(position: Int, videoViewHolder: VideoViewHolder) {
        val totalViews = postsLists[position].totalViews
        videoViewHolder.tvViewCount.text = prettyCount(totalViews!!.toInt() + 1)
        videoCountIncreaseListener.increaseViewCount(postsLists[position].id, position)
    }

    fun increaseYoutubePostView(position: Int, youtubeViewHolder: YoutubeViewHolder) {
        val totalViews = postsLists[position].totalViews
        youtubeViewHolder.tvViewCount.text =
            prettyCount(totalViews!!.toInt() + 1)
        videoCountIncreaseListener.increaseViewCount(postsLists[position].id, position)
    }

    /*private fun goToOtherUserProfileActivity(user_id: String) {
        val intent = Intent(context, OtherUserProfileActivity::class.java)
        intent.putExtra("USER_ID", user_id)
        context.startActivity(intent)
    }*/

    private fun hideVideoLayouts(viewHolder: VideoViewHolder) {
        viewHolder.tvDate.visibility = View.GONE
        viewHolder.tvViewCount.visibility = View.GONE
        viewHolder.tv_comment_count.visibility = View.GONE
        viewHolder.tv_like_count.visibility = View.GONE
        viewHolder.tv_follow.visibility = View.GONE
        viewHolder.iv_favourite.visibility = View.GONE
        viewHolder.iv_like.visibility = View.GONE
        viewHolder.lin_view.visibility = View.GONE
        viewHolder.tvText.visibility = View.GONE
        viewHolder.tvUserName.visibility = View.GONE
        viewHolder.ivAudio.visibility = View.GONE
        viewHolder.lin_like.visibility = View.GONE
        viewHolder.lin_comment.visibility = View.GONE
        viewHolder.ivOption.visibility = View.GONE
        viewHolder.userPic.visibility = View.GONE
        viewHolder.linearFooter.visibility = View.GONE
        viewHolder.img_togather.visibility = View.GONE
    }

    private fun showVideoLayouts(viewHolder: VideoViewHolder, post: PostsList, position: Int) {
        viewHolder.tvDate.visibility = View.VISIBLE
        viewHolder.tv_comment_count.visibility = View.VISIBLE
        viewHolder.tvViewCount.visibility = View.VISIBLE
        viewHolder.tv_like_count.visibility = View.VISIBLE
        viewHolder.tv_follow.visibility = View.GONE // montu
        viewHolder.iv_favourite.visibility = View.GONE // montu
        viewHolder.iv_like.visibility = View.VISIBLE
        viewHolder.lin_view.visibility = View.VISIBLE

        if (postsLists[position].caption!!.isEmpty()) {
            viewHolder.tvText.visibility = View.GONE
        } else {
            viewHolder.tvText.visibility = View.VISIBLE
        }
        viewHolder.tvUserName.visibility = View.VISIBLE
        viewHolder.ivAudio.visibility = View.VISIBLE
        viewHolder.lin_like.visibility = View.VISIBLE
        viewHolder.lin_comment.visibility = View.VISIBLE
        viewHolder.ivOption.visibility = View.VISIBLE
        viewHolder.userPic.visibility = View.VISIBLE
        viewHolder.linearFooter.visibility = View.GONE
        if ((post.mediaType == "web")) {
            viewHolder.lin_view.visibility = View.GONE
        } else {
            viewHolder.lin_view.visibility = View.VISIBLE
        }
    }

    private fun setYoutubeView(viewHolder: YoutubeViewHolder, position: Int) {
        viewHolder.currentPosition = position
        viewHolder.tvDate.text = DateUtil.formatByDay((postsLists.get(position).date)!!)
        viewHolder.tvViewCount.text = prettyCount(postsLists.get(position).totalViews!!.toInt())
        viewHolder.tv_comment_count.text = prettyCount(postsLists.get(position).commentTotal)
        viewHolder.tv_like_count.text = prettyCount(postsLists.get(position).reaction!!.likes)
        viewHolder.tvText.post {
            hyperlinkIcon.setBounds(
                0,
                0,
                viewHolder.tvText.lineHeight,
                viewHolder.tvText.lineHeight
            )
            val myText = postsLists[position].caption
            val textLength = myText!!.length
            val sb = SpannableString("$myText     ")
            if (postsLists[position].caption != null && postsLists[position].caption!!.trim { it <= ' ' }
                    .isNotEmpty()) {
                val imageSpan = ImageSpan(hyperlinkIcon, ImageSpan.ALIGN_BOTTOM)
                sb.setSpan(
                    imageSpan,
                    textLength + 2,
                    textLength + 3,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
            if (postsLists[position].caption != null && postsLists[position].caption != "") {
                viewHolder.tvText.text = sb
                /*if ((postsLists[position].mediaType == "web")) {
                    viewHolder.tvText.text = sb
                } else {
                    viewHolder.tvText.text = sb
                }*/
            } else {
                viewHolder.tvText.height = 40
            }
            viewHolder.tvText.movementMethod = LinkMovementMethod.getInstance()
        }
        viewHolder.mute_unmute_button.setOnClickListener { v: View? ->
            if (viewHolder.mYouTubePlayer != null) {
                if (isMute) {
                    viewHolder.mYouTubePlayer!!.unMute()
                    viewHolder.mute_unmute_button.setImageResource(R.drawable.ic_unmute)
                    isMute = false
                } else {
                    viewHolder.mYouTubePlayer!!.mute()
                    viewHolder.mute_unmute_button.setImageResource(R.drawable.ic_mute)
                    isMute = true
                }
            }
        }
        viewHolder.ivAudio.visibility = View.GONE
        if ((postsLists[position].userId == user.phone)) {
            viewHolder.tv_follow.visibility = View.GONE
        }

//        if (postsLists.get(position).getYouTubeVideoId() != null)
//            viewHolder.mYouTubePlayer.loadVideo(postsLists.get(position).getYouTubeVideoId(), 0);
        if (postsLists[position].favourite!!.status == 0) {
            viewHolder.iv_favourite.setImageResource(R.drawable.ic_unfavourites)
        } else {
            viewHolder.iv_favourite.setImageResource(R.drawable.ic_favourites)
        }
        if (postsLists[position].reaction!!.yourReaction.equals("0", ignoreCase = true)) {
            viewHolder.iv_like.setImageResource(R.drawable.ic_unlike)
        } else {
            viewHolder.iv_like.setImageResource(R.drawable.ic_like)
        }
        viewHolder.lin_view.visibility = View.GONE
        if (postsLists[position].follow!!.status == 0) {
            if (postsLists[position].isPrivate
                    .equals("0", true)
            ) viewHolder.tv_follow.text = "Following" else if (postsLists[position].isPrivate
                    .equals("1", true)
            ) viewHolder.tv_follow.text = "Requested"
        } else if (postsLists[position].follow!!.status == 1) viewHolder.tv_follow.text =
            "Requested" else if (postsLists[position].follow!!.status == 2) viewHolder.tv_follow.text =
            "Following" else if (postsLists[position].follow!!.status == 3) viewHolder.tv_follow.text =
            "+follow"
        viewHolder.tvUserName.text = postsLists.get(position).username
        GlideHelper.loadFromUrl(
            context,
            postsLists[position].userimage,
            R.drawable.gridview_image,
            viewHolder.userPic
        )
        //  viewHolder.tvDate.setText(DateUtil.formatByDay(postsLists.get(position).getDate()));
        if (postsLists[position].thumbnail != null && !postsLists[position].thumbnail!!.isEmpty()) GlideHelper.loadFromUrl(
            context, ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].thumbnail,
            R.drawable.gridview_image, viewHolder.imageView
        )
        if (postsLists[position].blurBitmap == null) {
            if (postsLists[position].thumbnail!!.contains("http")) {
                getBitmapFromUrl(position, postsLists[position].thumbnail, viewHolder.iv_imageBlur)
            } else {
                getBitmapFromUrl(
                    position,
                    ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].thumbnail,
                    viewHolder.iv_imageBlur
                )
            }
        } else {
            viewHolder.iv_imageBlur.setImageBitmap(postsLists[position].blurBitmap)
        }
        viewHolder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if ((postsLists[position].groupPassword == "")) {
                    listListener.onItemSingleClick(
                        postsLists[position],
                        viewHolder.imageView,
                        YOUTUBE,
                        position
                    )
                } else {
                    // password protected watch together youtube vdo
                    listListener.onPrivateRoomVideoClick(
                        postsLists[position],
                        postsLists[position].mediaType,
                        postsLists[position].groupPassword,
                        postsLists[position].hyperlink,
                        YOUTUBE
                    )
                }
            }
        })
        viewHolder.tvText.setOnClickListener {
            if ((postsLists[position].groupPassword == "")) {
                listListener.onItemSingleClick(
                    postsLists[position],
                    viewHolder.imageView,
                    YOUTUBE,
                    position
                )
            } else {
                // password protected watch together youtube vdo
                listListener.onPrivateRoomVideoClick(
                    postsLists[position],
                    postsLists[position].mediaType,
                    postsLists[position].groupPassword,
                    postsLists[position].hyperlink,
                    YOUTUBE
                )
            }
        }

        //to mute/un-mute video (optional)
        viewHolder.ivAudio.setOnClickListener {
            if (isMute) {
                if (viewHolder.mYouTubePlayer != null) {
                    viewHolder.mYouTubePlayer!!.unMute()
                }
                viewHolder.unmuteVideo()
                viewHolder.ivAudio.setImageResource(R.drawable.audio_play)
            } else {
                if (viewHolder.mYouTubePlayer != null) {
                    viewHolder.mYouTubePlayer!!.mute()
                }
                viewHolder.muteVideo()
                viewHolder.ivAudio.setImageResource(R.drawable.mute)
            }
        }

        viewHolder.relLabels.setOnClickListener {
            if ((postsLists[position].groupPassword == "")) {
                listListener.onItemSingleClick(
                    postsLists[position],
                    viewHolder.imageView,
                    YOUTUBE,
                    position
                )
            } else {
                // password protected watch together youtube vdo
                listListener.onPrivateRoomVideoClick(
                    postsLists[position],
                    postsLists[position].mediaType,
                    postsLists[position].groupPassword,
                    postsLists[position].hyperlink,
                    YOUTUBE
                )
            }
        }

        viewHolder.ivYoutubeClick.setOnClickListener {
            /*if (currentLayoutType.equals("Grid")) {
                listListener.onItemSingleClick(postsLists.get(position), viewHolder.getImageView(), GRID_VIEW, position);
            } else {

            }*/if ((postsLists.get(position).groupPassword == "")) {
            listListener.onItemSingleClick(
                postsLists.get(position),
                viewHolder.getImageView(),
                YOUTUBE,
                position
            )
        } else {
            // password protected watch together youtube vdo
            listListener.onPrivateRoomVideoClick(
                postsLists.get(position),
                postsLists.get(position).mediaType,
                postsLists.get(position).groupPassword,
                postsLists.get(position).hyperlink,
                YOUTUBE
            )
        }
        }

        viewHolder.tv_follow.setOnClickListener {
//                viewHolder.tv_follow.setTextColor(context.getResources().getColor(R.color.following_text_clr));
            listListener.onItemClick(
                viewHolder.bindingAdapterPosition,
                postsLists[viewHolder.bindingAdapterPosition],
                viewHolder.tv_follow
            )
        }

        viewHolder.ivOption.setOnClickListener { view: View? ->
            listListener.onOptionClick(
                viewHolder.bindingAdapterPosition,
                postsLists[viewHolder.bindingAdapterPosition],
                VIDEO
            )
        }

        viewHolder.iv_favourite.setOnClickListener { view: View? ->
            listListener.onItemClick(
                viewHolder.bindingAdapterPosition,
                postsLists[viewHolder.bindingAdapterPosition],
                viewHolder.iv_favourite
            )
        }

        viewHolder.lin_like.setOnClickListener {
            if (postsLists[position].favourite!!.status == 1) {
                postsLists[position].favourite!!.status = 0
            } else {
                postsLists[position].favourite!!.status = 1
            }
            listListener.onItemClick(
                viewHolder.bindingAdapterPosition,
                postsLists[viewHolder.bindingAdapterPosition],
                viewHolder.lin_like
            )
        }

        viewHolder.lin_comment.setOnClickListener {
            listListener.onItemClick(
                viewHolder.bindingAdapterPosition,
                postsLists[viewHolder.bindingAdapterPosition],
                viewHolder.lin_comment
            )
        }

        if ((currentLayoutType == "Grid")) {
            val params2 = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                550
            )
            params2.setMargins(4, 4, 4, 4)
            viewHolder.ivYoutubeClick.layoutParams = params2
            if (!postsLists[position].groupPassword!!.isEmpty()) {
                viewHolder.youTubePlayerView.visibility = View.GONE
                viewHolder.ivYoutubeClick.setImageResource(R.drawable.ic_grid_private_room_glass)
            } else {
                viewHolder.youTubePlayerView.visibility = View.VISIBLE
                GlideHelper.loadFromUrl(
                    context, "",
                    0, viewHolder.ivYoutubeClick
                )
            }
            viewHolder.iv_imageBlur.visibility = View.GONE
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                550
            )
            params.setMargins(4, 4, 4, 4)
            viewHolder.youTubePlayerView.layoutParams = params
            hideYoutubeVideoLayouts(viewHolder, postsLists[position])

        } else {

            viewHolder.tvYoutubeGridViewCaption.visibility = View.GONE
            if (!postsLists[position].groupPassword!!.isEmpty()) {
                viewHolder.youTubePlayerView.visibility = View.GONE
                viewHolder.ivYoutubeClick.visibility = View.VISIBLE
                val params2 = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    600
                )
                params2.setMargins(0, 0, 0, 0)
                viewHolder.ivYoutubeClick.layoutParams = params2
                viewHolder.ivYoutubeClick.setImageResource(R.drawable.ic_grid_private_room_glass)
            } else {
                GlideHelper.loadFromUrl(
                    context, "",
                    0, viewHolder.ivYoutubeClick
                )
                viewHolder.youTubePlayerView.visibility = View.VISIBLE
                viewHolder.ivYoutubeClick.visibility = View.GONE
            }

            //  viewHolder.iv_imageBlur.setVisibility(View.VISIBLE); // montu
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            viewHolder.youTubePlayerView.layoutParams = params
            showYoutubeVideoLayouts(viewHolder, position)
        }
    }

    private fun showYoutubeVideoLayouts(viewHolder: YoutubeViewHolder?, position: Int) {
        if (viewHolder != null) {
            viewHolder.tvDate.visibility = View.VISIBLE
            viewHolder.tvViewCount.visibility = View.VISIBLE
            viewHolder.tv_comment_count.visibility = View.VISIBLE
            viewHolder.tv_like_count.visibility = View.VISIBLE
            viewHolder.tv_follow.visibility = View.GONE // montu
            viewHolder.iv_favourite.visibility = View.GONE // montu
            viewHolder.iv_like.visibility = View.VISIBLE
            if (postsLists[position].caption!!.isEmpty()) {
                viewHolder.tvText.visibility = View.GONE
            } else {
                viewHolder.tvText.visibility = View.VISIBLE
            }
            viewHolder.tvUserName.visibility = View.VISIBLE
            viewHolder.lin_like.visibility = View.VISIBLE
            viewHolder.lin_comment.visibility = View.VISIBLE
            viewHolder.relHeader.visibility = View.VISIBLE
            viewHolder.ivOption.visibility = View.VISIBLE
            viewHolder.mute_unmute_button.visibility = View.VISIBLE
            viewHolder.ivAudio.visibility = View.GONE
            viewHolder.tvLbl.visibility = View.GONE
            if (viewHolder.userPic != null) viewHolder.userPic.visibility = View.VISIBLE
            /* if (viewHolder.relFooter != null)
                viewHolder.relFooter.setVisibility(View.VISIBLE);*/
            // montu
            if (viewHolder.img_togather != null) if ((postsLists[position].mediaType == "web")) {
                viewHolder.img_togather.visibility = View.VISIBLE
                viewHolder.ivYoutubeClick.visibility = View.VISIBLE
            } else {
                viewHolder.img_togather.visibility = View.GONE
                viewHolder.ivYoutubeClick.visibility = View.GONE
            }
        }
    }

    private fun hideYoutubeVideoLayouts(viewHolder: YoutubeViewHolder?, postsList: PostsList) {
        if (viewHolder != null) {
            viewHolder.tvDate.visibility = View.GONE
            viewHolder.tvViewCount.visibility = View.GONE
            viewHolder.tv_comment_count.visibility = View.GONE
            viewHolder.tv_like_count.visibility = View.GONE
            viewHolder.tv_follow.visibility = View.GONE
            viewHolder.iv_favourite.visibility = View.GONE
            viewHolder.iv_like.visibility = View.GONE
            viewHolder.tvText.visibility = View.GONE
            viewHolder.tvUserName.visibility = View.GONE
            viewHolder.lin_like.visibility = View.GONE
            viewHolder.lin_comment.visibility = View.GONE
            viewHolder.ivOption.visibility = View.GONE
            viewHolder.relHeader.visibility = View.GONE
            viewHolder.ivAudio.visibility = View.GONE
            viewHolder.tvLbl.visibility = View.GONE
            viewHolder.mute_unmute_button.visibility = View.GONE
            if ((postsList.mediaType == "web")) {
                viewHolder.ivYoutubeClick.visibility = View.VISIBLE
            } else {
                viewHolder.ivYoutubeClick.visibility = View.GONE
            }
            viewHolder.userPic.visibility = View.GONE
            viewHolder.relFooter.visibility = View.GONE
            viewHolder.img_togather.visibility = View.GONE
        }
    }

    private fun setImageView(viewHolder: ImageViewHolder, position: Int) {
//        viewHolder.mCover.setCropYCenterOffsetPct(0f);
        viewHolder.ivAudio.visibility = View.GONE
        Log.e("ImageViewDate", (postsLists[position].date)!!)
        viewHolder.tvDate.text = DateUtil.formatByDay((postsLists.get(position).date)!!)
        //   manageComment(postsLists.get(viewHolder.getbindingAdapterPosition()),viewHolder.llComment);
        if ((postsLists[position].userId == user.phone)) {
            viewHolder.tv_follow.visibility = View.GONE
        }
        val displayMetrics = context!!.resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels
        viewHolder.tvUserName.text = postsLists.get(position).username
        GlideHelper.loadFromUrl(
            context,
            postsLists[position].userimage,
            R.drawable.placeholder,
            viewHolder.userPic
        )
        //  viewHolder.tvDate.setText(DateUtil.formatByDay(postsLists.get(position).getDate()));
        if (postsLists[position].blurBitmap == null) {
            if (postsLists[position].image!!.contains("http")) {
                getBitmapFromUrl(position, postsLists[position].image, viewHolder.iv_imageBlur)
            } else {
                getBitmapFromUrl(
                    position,
                    ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].image,
                    viewHolder.iv_imageBlur
                )
            }
        } else {
            viewHolder.iv_imageBlur.setImageBitmap(postsLists[position].blurBitmap)
        }
        viewHolder.tvText.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                if (postsLists[viewHolder.bindingAdapterPosition].hyperlink != null && !postsLists[viewHolder.bindingAdapterPosition].hyperlink!!.isEmpty()) {
                    var link = postsLists[viewHolder.bindingAdapterPosition].hyperlink
                    if (!link!!.contains("http")) link = "http://$link"
                    FinestWebView.Builder(context as AppCompatActivity)
                        .webViewJavaScriptEnabled(true)
                        .webViewJavaScriptCanOpenWindowsAutomatically(true).iconDefaultColor(
                            context.resources.getColor(R.color.white)
                        )
                        .show(link)
                }
            }
        })
        viewHolder.itemView.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                if ((currentLayoutType == "Grid")) {
                    listListener.onItemSingleClick(
                        postsLists[position],
                        viewHolder.mCover,
                        GRID_VIEW,
                        position
                    )
                } else {
                    listListener.onItemSingleClick(
                        postsLists[viewHolder.bindingAdapterPosition],
                        viewHolder.mCover,
                        IMAGE,
                        position
                    )
                }
            }

            override fun onDoubleClick(v: View?) {
                listListener.onItemViewClick(
                    postsLists[viewHolder.bindingAdapterPosition],
                    viewHolder.mCover,
                    IMAGE
                )
            }
        })
        viewHolder.ivOption.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                listListener.onOptionClick(
                    viewHolder.bindingAdapterPosition,
                    postsLists[viewHolder.bindingAdapterPosition],
                    IMAGE
                )
            }
        })
        viewHolder.iv_favourite.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                listListener.onItemClick(
                    viewHolder.bindingAdapterPosition,
                    postsLists[viewHolder.bindingAdapterPosition],
                    viewHolder.iv_favourite
                )
            }
        })
        viewHolder.userPic.setOnClickListener(
            { v: View? -> }
        )
        viewHolder.tvUserName.setOnClickListener(
            { v: View? -> }
        )
        if ((currentLayoutType == "Grid")) {
            viewHolder.iv_imageBlur.visibility = View.GONE
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                550
            )
            params.setMargins(4, 4, 4, 4)
            viewHolder.mCover.layoutParams = params
            viewHolder.mCover.scaleType = ImageView.ScaleType.CENTER_CROP
            viewHolder.mCover.adjustViewBounds = false
            if (postsLists[position].image!!.contains("http")) {
                GlideHelper.loadFromUrl(
                    context,
                    postsLists[position].image,
                    R.drawable.placeholder,
                    viewHolder.mCover
                )
            } else {
                GlideHelper.loadFromUrl(
                    context,
                    ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].image,
                    R.drawable.placeholder,
                    viewHolder.mCover
                )
            }
            hideImageLayout(viewHolder)
        } else {
            //viewHolder.iv_imageBlur.setVisibility(View.VISIBLE); // montu
            val params = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.addRule(RelativeLayout.BELOW, R.id.relHeader)
            viewHolder.mCover.layoutParams = params
            viewHolder.mCover.scaleType = ImageView.ScaleType.FIT_CENTER
            viewHolder.mCover.adjustViewBounds = true
            if (postsLists[position].image!!.contains("http")) {
                GlideHelper.loadFromUrl(
                    context,
                    postsLists[position].image,
                    R.drawable.placeholder,
                    viewHolder.mCover
                )
            } else {
                GlideHelper.loadFromUrl(
                    context,
                    ApiEndPoint.UPLOADS_BASE_URL.toString() + postsLists[position].image,
                    R.drawable.placeholder,
                    viewHolder.mCover
                )
            }
            viewHolder.tvText.post(object : Runnable {
                override fun run() {
                    hyperlinkIcon.setBounds(
                        0,
                        0,
                        viewHolder.tvText.lineHeight,
                        viewHolder.tvText.lineHeight
                    )
                    val myText = postsLists[position].caption
                    val textLength = myText!!.length
                    val sb = SpannableString("$myText     ")
                    if (postsLists[position].hyperlink != null && !postsLists[position].hyperlink!!.isEmpty()) {
                        val imageSpan = ImageSpan(hyperlinkIcon, ImageSpan.ALIGN_BOTTOM)
                        sb.setSpan(
                            imageSpan,
                            textLength + 2,
                            textLength + 3,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                    viewHolder.tvText.visibility =
                        if (postsLists.get(position).caption!!.isEmpty()) View.INVISIBLE else View.VISIBLE
                    viewHolder.tvText.text = sb
                    viewHolder.tvText.movementMethod = LinkMovementMethod.getInstance()
                    viewHolder.tvViewCount.text =
                        prettyCount(postsLists.get(position).totalViews!!.toInt())
                    viewHolder.tv_comment_count.text =
                        prettyCount(postsLists.get(position).commentTotal)
                    viewHolder.tv_like_count.text = prettyCount(
                        postsLists.get(position).reaction!!.likes
                    )
                    Log.e(
                        "follow_image",
                        postsLists[position].isPrivate
                            .toString() + ".." + postsLists[position].follow!!.status
                    )
                    if (postsLists[position].favourite!!.status == 0) {
                        viewHolder.iv_favourite.setImageResource(R.drawable.ic_unfavourites)
                    } else {
                        viewHolder.iv_favourite.setImageResource(R.drawable.ic_favourites)
                    }
                    if (postsLists[position].reaction!!.yourReaction.equals(
                            "0",
                            ignoreCase = true
                        )
                    ) {
                        viewHolder.iv_like.setImageResource(R.drawable.ic_unlike)
                    } else {
                        viewHolder.iv_like.setImageResource(R.drawable.ic_like)
                    }

                    /*if (postsLists.get(position).isPrivate.equalsIgnoreCase("0") && postsLists.get(position).getFollow().getStatus() == 2) {
                        viewHolder.tv_follow.setText("Following");
                    } else if (postsLists.get(position).isPrivate.equalsIgnoreCase("1") && postsLists.get(position).getFollow().getStatus() == 1) {
                        viewHolder.tv_follow.setText("Requested");
                    } else {
                        viewHolder.tv_follow.setText("+follow");
                    }*/if (postsLists[position].follow!!.status == 0) {
                        if (postsLists[position].isPrivate
                                .equals("0", true)
                        ) viewHolder.tv_follow.text =
                            "Following" else if (postsLists[position].isPrivate
                                .equals("1", true)
                        ) viewHolder.tv_follow.text = "Requested"
                    } else if (postsLists[position].follow!!.status == 1) viewHolder.tv_follow.text =
                        "Requested" else if (postsLists[position].follow!!.status == 2) viewHolder.tv_follow.text =
                        "Following" else if (postsLists[position].follow!!.status == 3) viewHolder.tv_follow.text =
                        "+follow"
                    viewHolder.tv_follow.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            listListener.onItemClick(
                                viewHolder.bindingAdapterPosition,
                                postsLists[viewHolder.bindingAdapterPosition],
                                viewHolder.tv_follow
                            )
                        }
                    })
                    viewHolder.lin_like.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            listListener.onItemClick(
                                viewHolder.bindingAdapterPosition,
                                postsLists[viewHolder.bindingAdapterPosition],
                                viewHolder.lin_like
                            )
                        }
                    })
                    viewHolder.lin_comment.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(view: View) {
                            listListener.onItemClick(
                                viewHolder.bindingAdapterPosition,
                                postsLists[viewHolder.bindingAdapterPosition],
                                viewHolder.lin_comment
                            )
                        }
                    })
                }
            })
            showImageLayout(viewHolder)
        }
    }

    private fun showImageLayout(viewHolder: ImageViewHolder) {
        viewHolder.tvDate.visibility = View.VISIBLE
        viewHolder.tvViewCount.visibility = View.VISIBLE
        viewHolder.tv_comment_count.visibility = View.VISIBLE
        viewHolder.tv_like_count.visibility = View.VISIBLE
        //    viewHolder.tv_follow.setVisibility(View.VISIBLE); // montu
        viewHolder.tv_follow.visibility = View.GONE
        //     viewHolder.iv_favourite.setVisibility(View.VISIBLE); // montu
        viewHolder.iv_favourite.visibility = View.GONE
        viewHolder.iv_like.visibility = View.VISIBLE
//        viewHolder.tvText.visibility = View.VISIBLE
        viewHolder.tvUserName.visibility = View.VISIBLE
        viewHolder.lin_like.visibility = View.VISIBLE
        viewHolder.lin_comment.visibility = View.VISIBLE
        viewHolder.ivOption.visibility = View.VISIBLE
        viewHolder.userPic.visibility = View.VISIBLE
        viewHolder.imageLinear.visibility = View.VISIBLE
        viewHolder.relHeader.visibility = View.VISIBLE
    }

    private fun hideImageLayout(viewHolder: ImageViewHolder) {
        viewHolder.tvDate.visibility = View.GONE
        viewHolder.tvViewCount.visibility = View.GONE
        viewHolder.tv_comment_count.visibility = View.GONE
        viewHolder.tv_like_count.visibility = View.GONE
        viewHolder.tv_follow.visibility = View.GONE
        viewHolder.iv_favourite.visibility = View.GONE
        viewHolder.iv_like.visibility = View.GONE
        viewHolder.tvText.visibility = View.GONE
        viewHolder.tvUserName.visibility = View.GONE
        viewHolder.lin_like.visibility = View.GONE
        viewHolder.lin_comment.visibility = View.GONE
        viewHolder.ivOption.visibility = View.GONE
        viewHolder.userPic.visibility = View.GONE
        viewHolder.imageLinear.visibility = View.GONE
        viewHolder.relHeader.visibility = View.GONE
    }

    /*private void setTextView(final TextViewHolder viewHolder, final int position) {
        //   manageComment(postsLists.get(viewHolder.getbindingAdapterPosition()),viewHolder.llComment);
        viewHolder.tvViewCount.setVisibility(View.GONE);
        viewHolder.ivAudio.setVisibility(View.GONE);
        viewHolder.lin_view.setVisibility(View.GONE);

        if (postsLists.get(position).getCaption().contains("!@#$")) {
            String replacedString = postsLists.get(position).getCaption().replace("!@#$", "1*12/");
            String[] separated = replacedString.split("1*12/");
            String title = "", body = "";
            if (separated.length > 0) {
                title = separated[0];
            }

            if (separated.length > 1) {
                title = separated[0];
                body = separated[1];
            }
            viewHolder.tvText.setText("");
            SpannableString ss1 = new SpannableString(title);
            ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, ss1.length(), 0);
            viewHolder.tvText.append(ss1);

            if (!body.isEmpty() && !title.isEmpty()) {
                viewHolder.tvText.append("\n");
            }
            viewHolder.tvText.append(body);
            viewHolder.tvText.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            viewHolder.tvText.setText(postsLists.get(position).getCaption());
        }

        // viewHolder.tvViewCount.setText(postsLists.get(position).getTotalViews());
        viewHolder.tv_comment_count.setText(prettyCount(postsLists.get(position).getCommentTotal()));
        viewHolder.tv_like_count.setText(prettyCount(postsLists.get(position).getReaction().getLikes()));
        viewHolder.tvText.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.tvUserName.setText(postsLists.get(position).getUsername());
        GlideHelper.loadFromUrl(context, postsLists.get(position).getUserimage(), R.drawable.placeholder, viewHolder.userPic);
        viewHolder.tvDate.setText(DateUtil.formatByDay(postsLists.get(position).getDate()));

        if (postsLists.get(position).getFavourite().getStatus() == 0) {
            viewHolder.iv_favourite.setImageResource(R.drawable.ic_unfavourites);
        } else {
            viewHolder.iv_favourite.setImageResource(R.drawable.ic_favourites);
        }

        if (postsLists.get(position).getReaction().getYourReaction().equalsIgnoreCase("0")) {
            viewHolder.iv_like.setImageResource(R.drawable.ic_unlike);
        } else {
            viewHolder.iv_like.setImageResource(R.drawable.ic_like);
        }

        Log.e("follow_text", postsLists.get(position).isPrivate + ".." + postsLists.get(position).getFollow().getStatus());

        if (postsLists.get(position).getFollow().getStatus() == 0) {
            if (postsLists.get(position).isPrivate.equalsIgnoreCase("0")) {
                viewHolder.tv_follow.setText("Following");
                viewHolder.tv_follow.setTextColor(context.getResources().getColor(R.color.following_text_clr));
            } else if (postsLists.get(position).isPrivate.equalsIgnoreCase("1")) {
                viewHolder.tv_follow.setText("Requested");
                viewHolder.tv_follow.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }

        } else if (postsLists.get(position).getFollow().getStatus() == 1) {
            viewHolder.tv_follow.setText("Requested");
            viewHolder.tv_follow.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        } else if (postsLists.get(position).getFollow().getStatus() == 2) {
            viewHolder.tv_follow.setText("Following");
            viewHolder.tv_follow.setTextColor(context.getResources().getColor(R.color.following_text_clr));
        } else if (postsLists.get(position).getFollow().getStatus() == 3) {
            viewHolder.tv_follow.setText("+follow");
            viewHolder.tv_follow.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }


        viewHolder.userPic.setOnClickListener(
                v ->  {
                    //goToOtherUserProfileActivity(postsLists.get(position).getUserId());
                }
        );

        viewHolder.tvUserName.setOnClickListener(
                v ->  {
                    //goToOtherUserProfileActivity(postsLists.get(position).getUserId());
                }
        );

        viewHolder.tvText.setOnClickListener(new DoubleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (currentLayoutType.equals("Grid")) {
                    listListener.onTextItemClick(postsLists.get(position), viewHolder.tvText, GRID_VIEW, position);
                } else {
                    listListener.onItemSingleClick(postsLists.get(viewHolder.getbindingAdapterPosition()), viewHolder.userPic, TEXT, position);
                }
            }

            @Override
            public void onDoubleClick(View v) {
                listListener.onItemViewClick(postsLists.get(viewHolder.getbindingAdapterPosition()), viewHolder.userPic, TEXT);
            }
        });

        viewHolder.ivOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listListener.onOptionClick(viewHolder.getbindingAdapterPosition(), postsLists.get(viewHolder.getbindingAdapterPosition()), TEXT);
            }
        });

        viewHolder.tv_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (viewHolder.tv_follow.getText().equals(R.string.following)) {
                    viewHolder.tv_follow.setTextColor(context.getResources().getColor(R.color.following_text_clr));
                } else {
                    viewHolder.tv_follow.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }
                listListener.onItemClick(viewHolder.getbindingAdapterPosition(), postsLists.get(viewHolder.getbindingAdapterPosition()), viewHolder.tv_follow);
            }
        });

        viewHolder.iv_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listListener.onItemClick(viewHolder.getbindingAdapterPosition(), postsLists.get(viewHolder.getbindingAdapterPosition()), viewHolder.iv_favourite);
            }
        });
        viewHolder.lin_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listListener.onItemClick(viewHolder.getbindingAdapterPosition(), postsLists.get(viewHolder.getbindingAdapterPosition()), viewHolder.lin_like);
            }
        });
        viewHolder.lin_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listListener.onItemClick(viewHolder.getbindingAdapterPosition(), postsLists.get(viewHolder.getbindingAdapterPosition()), viewHolder.lin_comment);
            }
        });

        if (currentLayoutType.equals("Grid")) {
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.tvText.getLayoutParams();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 550);

            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 550);

            params.setMargins(13, 13, 13, 13);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            viewHolder.textCard.setLayoutParams(params);
            viewHolder.tvText.setLayoutParams(params2);
            viewHolder.tvText.setGravity(Gravity.CENTER);
            hideTextLayout(viewHolder);
        } else {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            params2.addRule(RelativeLayout.CENTER_IN_PARENT);
            viewHolder.textCard.setLayoutParams(params2);

            viewHolder.tvText.setGravity(Gravity.START);
            params.addRule(RelativeLayout.BELOW, R.id.relHeader);
            params.setMargins(26, 0, 0, 0);
            viewHolder.tvText.setLayoutParams(params);

            showTextLayout(viewHolder);
            if (postsLists.get(position).getUserId().equals(user.getPhone())) {
                viewHolder.tv_follow.setVisibility(View.GONE);
            } else {
                viewHolder.tv_follow.setVisibility(View.GONE); //montu
            }
        }
    }

    private void showTextLayout(TextViewHolder viewHolder) {
        viewHolder.tvDate.setVisibility(View.VISIBLE);
        viewHolder.tvViewCount.setVisibility(View.VISIBLE);
        viewHolder.tv_comment_count.setVisibility(View.VISIBLE);
        viewHolder.tv_like_count.setVisibility(View.VISIBLE);
        viewHolder.tv_follow.setVisibility(View.GONE);  // montu
        viewHolder.iv_favourite.setVisibility(View.GONE); // montu
        viewHolder.iv_like.setVisibility(View.VISIBLE);
        viewHolder.tvUserName.setVisibility(View.VISIBLE);
        viewHolder.lin_like.setVisibility(View.VISIBLE);
        viewHolder.lin_comment.setVisibility(View.VISIBLE);
        viewHolder.ivOption.setVisibility(View.VISIBLE);
        viewHolder.relHeader.setVisibility(View.VISIBLE);
        //   viewHolder.relFooter.setVisibility(View.VISIBLE);  // montu
        viewHolder.userPic.setVisibility(View.VISIBLE);
//        viewHolder.mVisibilityPercents.setVisibility(View.VISIBLE);
    }

    private void hideTextLayout(TextViewHolder viewHolder) {
        viewHolder.tvDate.setVisibility(View.GONE);
        viewHolder.tvViewCount.setVisibility(View.GONE);
        viewHolder.tv_comment_count.setVisibility(View.GONE);
        viewHolder.tv_like_count.setVisibility(View.GONE);
        viewHolder.tv_follow.setVisibility(View.GONE);
        viewHolder.iv_favourite.setVisibility(View.GONE);
        viewHolder.iv_like.setVisibility(View.GONE);
        viewHolder.tvUserName.setVisibility(View.GONE);
        viewHolder.lin_like.setVisibility(View.GONE);
        viewHolder.lin_comment.setVisibility(View.GONE);
        viewHolder.ivOption.setVisibility(View.GONE);
        viewHolder.relHeader.setVisibility(View.GONE);
        viewHolder.relFooter.setVisibility(View.GONE);
        viewHolder.userPic.setVisibility(View.GONE);
        viewHolder.mVisibilityPercents.setVisibility(View.GONE);
    }*/
    private fun manageComment(postsList: PostsList, llComment: LinearLayout) {
        if (postsList.commentVisible) {
            postsList.commentVisible = true
        } else {
            llComment.postDelayed(object : Runnable {
                override fun run() {
                    val anim = AnimationUtils.loadLayoutAnimation(context, R.anim.scale_down)
                    llComment.layoutAnimation = anim
                    llComment.visibility = View.VISIBLE
                }
            }, 3000)
        }
    }

    override fun getItemCount(): Int {
        return postsLists.size
    }

    override fun getItemViewType(position: Int): Int {
        Log.e("viewTypeName", postsLists[position].username + ".." + postsLists[position].mediaType)
        if (postsLists[position].mediaType.equals("youTube", ignoreCase = true)) {
            //   Log.e("viewType","Youtube");
            return YOUTUBE
        } else if ((postsLists[position].mediaType.equals("web", ignoreCase = true)
                    && !postsLists[position].youTubeVideoId!!.trim { it <= ' ' }.isEmpty() &&
                    postsLists.get(position).youTubeVideoId != "empty") //               && !postsLists.get(position).getGroupName().trim().isEmpty()
        ) {
            return YOUTUBE
        } else if (postsLists[position].mediaType.equals("web", ignoreCase = true)) {
            return VIDEO
        } else if (postsLists[position].video != null && !postsLists[position].video!!.isEmpty()) {
            //  Log.e("viewType","Video");
            return VIDEO
        } else return if (postsLists.get(position).image != null && !postsLists.get(position).image!!.isEmpty()) {
            IMAGE
        } else {
            TEXT
        }
    }

    fun setData(lists: List<PostsList>) {
        val iter = lists.iterator()
        postsLists.addAll(lists)
        notifyDataSetChanged()
    }

    fun clear() {
        postsLists.clear()
        notifyDataSetChanged()
    }

    fun delete(position: Int) {
        postsLists.removeAt(position)
        notifyItemRemoved(position)
    }

    interface VideoListListener {
        fun onItemViewClick(post: PostsList?, imageView: ImageView?, type: Int)
        fun onItemSingleClick(post: PostsList?, imageView: ImageView?, type: Int, position: Int)
        fun onTextItemClick(post: PostsList?, textView: TextView?, type: Int, position: Int)
        fun onOptionClick(position: Int, post: PostsList?, type: Int)
        fun onItemClick(position: Int, post: PostsList?, view: View?)
        fun onPrivateRoomVideoClick(
            post: PostsList?,
            mediaType: String?,
            password: String?,
            hyperLink: String?,
            type: Int
        )
    }

    interface VideoCountIncreaseListener {
        fun increaseViewCount(postId: String?, position: Int)
    }

    companion object {
        val TEXT = 1
        val IMAGE = 2
        val VIDEO = 3
        val YOUTUBE = 4
        val YOUTUBE_WATCH_TOGATHER = 5
        val GRID_VIEW = 6
        val context: Context? = null
        fun extractYTId(ytUrl: String): String? {
            if (ytUrl.contains("youtu.be")) {
                var vId: String? = null
                val pattern = Pattern.compile(
                    "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                    Pattern.CASE_INSENSITIVE
                )
                val matcher = pattern.matcher(ytUrl)
                if (matcher.matches()) {
                    vId = matcher.group(1)
                }
                return vId
            } else {
                val pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
                val compiledPattern = Pattern.compile(pattern)
                val matcher = compiledPattern.matcher(ytUrl)
                return if (matcher.find()) {
                    matcher.group()
                } else null
            }
        }

        fun prettyCount(number: Number?): String {
            val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
            val numValue = number!!.toLong()
            val value = Math.floor(Math.log10(numValue.toDouble())).toInt()
            val base = value / 3
            return if (value >= 3 && base < suffix.size) {
                DecimalFormat("#0.0")
                    .format(numValue / Math.pow(10.0, (base * 3).toDouble())) + suffix.get(base)
            } else {
                DecimalFormat("#,##0").format(numValue)
            }
        }
    }
}