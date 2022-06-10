package com.instaconnect.android.ui.watch_together_room

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.allattentionhere.autoplayvideos.MediaSourceUtil
import com.allattentionhere.autoplayvideos.VideoRecyclerView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.instaconnect.android.InstaConnectApp
import com.instaconnect.android.R
import com.instaconnect.android.data.model.ChatDataNewResponse
import com.instaconnect.android.data.model.FriendListModel
import com.instaconnect.android.data.model.MessageDataItem
import com.instaconnect.android.databinding.ActivityWatchTogetherVideoBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.utils.*
import com.instaconnect.android.utils.heart_view.HeartsRenderer
import com.instaconnect.android.utils.heart_view.HeartsView
import com.instaconnect.android.utils.helper_classes.GlideHelper
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import gun0912.tedimagepicker.util.ToastUtil
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.prefs.Preferences
import kotlin.collections.ArrayList

class WatchTogetherVideoActivity : AppCompatActivity(), Player.EventListener, View.OnClickListener,
    AddFriendToVideoListAdapter.AddFriendListListener {
    private lateinit var viewModel: WatchTogetherVideoViewModel
    private var videoId: String? = null
    private var postId = ""
    private var roomName = ""
    private var userId: String? = null
    private var userImage: String? = null
    private var user_name: String? = null
    private var total_views: String? = null
    private var total_likes: String? = null
    var mute = false
    private var simpleExoPlayer: SimpleExoPlayer? = null
    private var userAgent: String? = null
    var dataSourceFactory: DataSource.Factory? = null
    var extractorsFactory: ExtractorsFactory? = null
    private var context: Context? = null
    private var model: HeartsView.Companion.Model? = null
    private var postUserId: String? = null
    private var groupName: String? = null
    private var to: String? = null
    private var from: String? = null
    private var myPostReaction = ""
    private var actualPostId = ""
    private val screenWidth = 0
    private lateinit var binding: ActivityWatchTogetherVideoBinding
    private var isScroll: Boolean? = true
    private var chatDataList: ArrayList<MessageDataItem>? = null
    var chatAdapter: WatchTogetherChatAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchTogetherVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        chatDataList = ArrayList()
        videoId = intent.getStringExtra("VIDEO_ID")
        postId = intent.getStringExtra("POST_ID")!!
        userId = Prefrences.getPreferences(this, Constants.PREF_USER_ID)
        userImage = Prefrences.getUser()!!.avatar

        viewModel = ViewModelProvider(
            this,
            WatchTogetherViewModelFactory(
                WatchTogetherVideoRepository(
                    MyApi.getInstance()
                )
            )
        )[WatchTogetherVideoViewModel::class.java]

        mute = VideoRecyclerView.isMute()
        forBackgroundVideo()
        setChatAdapter()
        getCurrentUserData()

        val handler1 = Handler()
        handler1.postDelayed({
            if (videoId != null && postId != null && postId != "web" && !videoId!!.contains("http")) {
                binding.youtubeplayer.visibility = View.VISIBLE
                binding.exoPlayer.visibility = View.GONE
                binding.youtubeplayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId!!, 0f)
                    }

                    override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                        super.onError(youTubePlayer, error)
                        Log.d("TAG", "onError: " + error.name)
                    }

                    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerState) {
                        if (state == PlayerState.ENDED) {
//                            youtubeVideoEnded()
                        }
                    }
                })
            } else {
                var hyperLink: String
                if (videoId != null && !videoId!!.contains("http")) {
                    //hyperLink = extractYTId(videoId);
                    binding.youtubeplayer.visibility = View.VISIBLE
                    binding.exoPlayer.visibility = View.GONE
                    Log.d("TAG", "run: $videoId")
                    binding.youtubeplayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.loadVideo(videoId!!, 0f)
                        }

                        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                            super.onError(youTubePlayer, error)
                            Log.d("TAG", "onError: " + error.name)
                        }

                        override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerState) {
                            if (state == PlayerState.ENDED) {
//                                youtubeVideoEnded()
                            }
                        }
                    })
                } else {
                    binding.youtubeplayer.visibility = View.GONE
                    binding.exoPlayer.visibility = View.VISIBLE
                    show()
                    binding.progressBar.visibility = View.VISIBLE
                    if (videoId!!.contains("http")) {
                        val handler = Handler()
                        handler.postDelayed({ playVideo() }, 100)
                    }
                    binding.exoPlayer.setControllerVisibilityListener(PlayerControlView.VisibilityListener { visibility ->
                        Log.e("control_visibilyt", "$visibility....")
                        if (visibility == 0) {
                            show()
                        } else {
                            hide()
                        }
                    })
                }
            }
        }, 1000)

        setUserData()
        setListeners()

        if (SocketConnector.getInstance() != null) {
            if (SocketConnector.getSocket()!!.connected()) {
                emitMessages()
                onMessages()
                onCurrentMessage()
            } else {
                ToastUtil.showToast("Socket not connected")
            }
        }
    }

    private fun onCurrentMessage() {
        SocketConnector.getSocket()!!.on(
            "getPostMessage"
        ) { args ->

            val data = args[0] as JSONObject
            try {
                val result = data.getJSONObject("result")

                val chatData: ChatDataNewResponse =
                    Gson().fromJson(
                        data.toString(),
                        object : TypeToken<ChatDataNewResponse?>() {}.type
                    )

                if(actualPostId == chatData.postId){
                    val messageData: MessageDataItem =
                        Gson().fromJson(result.toString(), object : TypeToken<MessageDataItem?>() {}.type)

                    chatDataList!!.add(messageData)
                    notifyList()
                    scrollViewToLastPos(true)
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    // retrieve inbox data
    private fun onMessages() {
        SocketConnector.getSocket()!!.on(
            "setMessages"
        ) { args ->
            val data = args[0] as JSONObject

            val chatData: ChatDataNewResponse =
                Gson().fromJson(
                    data.toString(),
                    object : TypeToken<ChatDataNewResponse?>() {}.type
                )

            chatDataList!!.clear()
            if(actualPostId == chatData.postId){
                chatDataList!!.addAll(chatData.messageData!!)
                notifyList()
                scrollViewToLastPos(false)
            }

        }
    }

    // initialize inbox data
    private fun emitMessages() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("post_id", actualPostId)
            //jsonObject.put("page", currentPage)
            SocketConnector.getSocket()!!.emit("getMessages", jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun scrollViewToLastPos(isSmoothScroll: Boolean) {
        if (chatDataList != null && chatDataList!!.size > 0) {
            runOnUiThread {
                if (isSmoothScroll) binding.recyclerChat.smoothScrollToPosition(chatDataList!!.size - 1) else binding.recyclerChat.scrollToPosition(
                    chatDataList!!.size - 1
                )
//                binding.relNewMsg.setVisibility(View.GONE)
//                count = 0
                isScroll = false
//                binding.relNewMsg.setVisibility(View.GONE)
            }
        }
    }

    private fun setUserData() {
        actualPostId = intent.getStringExtra("ACTUAL_POST_ID")!!
        postUserId = intent.getStringExtra("USER_ID")
        roomName = intent.getStringExtra("ROOM_NAME")!!
        user_name = intent.getStringExtra("USER_NAME")
        userImage = intent.getStringExtra("USER_IMAGE")
        total_views = intent.getStringExtra("TOTAL_VIEWS")
        total_likes = intent.getStringExtra("TOTAL_LIKES")
        groupName = intent.getStringExtra("GROUP_NAME")
        myPostReaction = intent.getStringExtra("POST_REACTION")!!
        from = Prefrences.getUser()!!.phone
        to = roomName
        val totalViews = total_views!!.toInt()
        binding.tvUserName.text = user_name
        binding.tvTotalWatch.text = (totalViews + 1).toString()
        binding.tvTotalLike.text = total_likes
        GlideHelper.loadFromUrl(
            context, userImage,
            R.drawable.loader, binding.ivUserImage
        )
    }

    private fun setListeners() {
        binding.ivEndStreaming.setOnClickListener(this)
        binding.play.setOnClickListener(this)
        binding.pause.setOnClickListener(this)
        binding.ivAudio.setOnClickListener(this)
        binding.ivLike.setOnClickListener(this)
        binding.ivSend.setOnClickListener(this)
        binding.ivStreamUser.setOnClickListener(this)

        binding.viewLayer.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                showHideControls()
            }

            override fun onDoubleClick(v: View?) {}
        })
    }

    private fun showHideControls() {
        if (binding.fl.visibility === View.VISIBLE) {
            hide()
        } else {
            show()
        }
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.iv_send -> {
                sendTextMessage()
            }
            R.id.iv_endStreaming ->
//              deleteWatchTogetherPost(postId);
                finish()
            R.id.play -> {
                InstaConnectApp.instance!!.mediaPlayer()!!.play(true)
                binding.play.visibility = View.GONE
                binding.pause.visibility = View.VISIBLE
                simpleExoPlayer!!.playWhenReady = true
            }

            R.id.pause -> {
                InstaConnectApp.instance!!.mediaPlayer()!!.play(false)
                binding.play.visibility = View.VISIBLE
                binding.pause.visibility = View.GONE
                simpleExoPlayer!!.playWhenReady = false
            }

            R.id.ivAudio -> if (VideoRecyclerView.isMute()) {
                VideoRecyclerView.setMute(false)
                simpleExoPlayer!!.volume = 1f
                InstaConnectApp.instance!!.mediaPlayer()!!.unMute()
                binding.ivAudio.setImageResource(R.drawable.audio_play)
                //RxBus.getInstance().publish(BusMessage.MUTE.name(), false)
            } else {
                VideoRecyclerView.setMute(true)
                simpleExoPlayer!!.volume = 0f
                InstaConnectApp.instance!!.mediaPlayer()!!.mute()
                binding.ivAudio.setImageResource(R.drawable.mute)
                //RxBus.getInstance().publish(BusMessage.MUTE.name(), true)
            }

            R.id.iv_like -> {
                if (myPostReaction != null && myPostReaction == "0") {
                    myPostReaction = "1"
//                    callAddPostReaction(userId, myPostReaction, actualPostId)
                }
                showHeart()
                callHeart(100)
                callHeart(300)

            }

            R.id.iv_streamUser -> {
                val addFriendDialog = AddFriendToWatchVideoDialog(this@WatchTogetherVideoActivity, actualPostId, this, viewModel)
                addFriendDialog.show(supportFragmentManager, "AddFriendDialog")
            }
        }
    }

    private fun sendTextMessage() {
        if (Utils.isConnected(this)) {
            if (binding.etMessageBox.text.toString() != "") {
                isScroll = false
                emitSendMsg()
            }

            notifyList()
            binding.etMessageBox.setText("")
        }
    }

    private fun notifyList() {
        runOnUiThread(chatAdapter!!::notifyDataSetChanged)
    }

    //Send message
    private fun emitSendMsg() {
        if (SocketConnector.getInstance() != null) {
            if (SocketConnector.getSocket()!!.connected()) {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("post_id", actualPostId)
                    jsonObject.put("user_id", userId)
                    jsonObject.put("message", Objects.requireNonNull(binding.etMessageBox.text).toString()
                        .trim { it <= ' ' })

                    SocketConnector.getSocket()!!.emit("sendMessage", jsonObject)
                    binding.etMessageBox.setText("")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun callHeart(mils: Int) {
        Handler().postDelayed({ showHeart() }, mils.toLong())
    }

    private fun showHeart() {
        heartAnimation()
        binding.heartView.emitHeart(model!!)
    }

    private fun heartAnimation() {
        val config = HeartsRenderer.Config(3f, 0.8f, getRandomNumber(1, 2))
        binding.heartView.applyConfig(config)
        val bitmap = BitmapFactory.decodeResource(context!!.resources, R.drawable.ic_heart)
        model = HeartsView.Companion.Model(0, bitmap)
    }

    private fun getRandomNumber(min: Int, max: Int): Float {
        val random = Random()
        return min + random.nextFloat() * (max - min)
    }

    private fun playVideo() {
        //InstaConnectApp.getInstance().mediaPlayer().initPlayer(Uri.parse(videoId), player, true, this);
        simpleExoPlayer!!.addListener(this)
        simpleExoPlayer!!.prepare(buildMediaSource(Uri.parse(videoId), MediaSourceUtil.getExtension(Uri.parse(videoId))!!))
        binding.exoPlayer.requestFocus()
        simpleExoPlayer!!.playWhenReady = true
    }

    private fun buildMediaSource(uri: Uri, overrideExtension: String): MediaSource? {
        @C.ContentType val type = Util.inferContentType(uri, overrideExtension)
        return when (type) {
            C.TYPE_HLS -> HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
            C.TYPE_OTHER -> if (uri.toString().contains("mp4")) {
                ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            } else {
                HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri)
            }
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    private fun hide() {
        binding.fl.visibility = View.GONE
        binding.ivAudio.visibility = View.GONE
    }

    private fun show() {
        if (videoId != null && videoId!!.contains("http")) {
            binding.fl.visibility = View.VISIBLE
            binding.ivAudio.visibility = View.VISIBLE
            binding.ivAudio.visibility = View.VISIBLE
        }
        /*new Handler().postDelayed(this::hide, 3000);*/
    }

    private fun forBackgroundVideo() {
        userAgent = Util.getUserAgent(context, "ExoPlayer")
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory: TrackSelection.Factory =
            AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        // Create a default LoadControl
        val loadControl: LoadControl = DefaultLoadControl()
        // Create the player
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
        binding.exoPlayer.player = simpleExoPlayer
        binding.exoPlayer.keepScreenOn = true
        dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayer"))
        extractorsFactory = DefaultExtractorsFactory()
        val videoSource: MediaSource = ExtractorMediaSource(
            Uri.parse(videoId),
            dataSourceFactory, extractorsFactory, null, null
        )
    }

    private fun setChatAdapter() {
        var linearLayoutManager = LinearLayoutManager(context)
        val recyclerChat: RecyclerView = binding.recyclerChat
        recyclerChat.layoutManager = linearLayoutManager
        chatAdapter = WatchTogetherChatAdapter(this, chatDataList!!){

        }
        recyclerChat.adapter = chatAdapter

    }

    private fun getCurrentUserData() {


        /* val call: Call<GetPublicProfile> =
             dataManager.apiHelper().getPublicProfile(dataManager.prefHelper().getUser().getPhone())
         call.enqueue(object : Callback<GetPublicProfile?> {
             override fun onResponse(
                 call: Call<GetPublicProfile?>,
                 response: Response<GetPublicProfile?>
             ) {
                 if (response.body() != null && response.body().getResponse().getCode()
                         .equals("200")
                 ) {
                     userCurrentImage = response.body().getResponse().getUserimage()
                     userCurrentName = response.body().getResponse().getUsername()
                     //watchTogetherVideoModel.saveProfile(dataManager.prefHelper().getUser().getPhone(), userCurrentName, userCurrentImage);
                 }
             }

             override fun onFailure(call: Call<GetPublicProfile?>, t: Throwable) {}
         })*/
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        Log.d("TAG", "onTimelineChanged: ")
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        Log.d("TAG", "onTracksChanged: ")
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        Log.d("TAG", "onLoadingChanged: $isLoading")
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
            binding.play.visibility = View.GONE
            binding.pause.visibility = View.VISIBLE
        }
        if (playbackState == Player.STATE_READY) {
            binding.progressBar.visibility = View.GONE
            hide()
        } else {
            show()
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        Log.e("TAG", "$repeatMode..")
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        Log.d("TAG", "onShuffleModeEnabledChanged: ")
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        Log.d("TAG", "onPlayerError: ")
        binding.progressBar.visibility = View.GONE
    }

    override fun onPositionDiscontinuity(reason: Int) {
        Log.d("TAG", "onPositionDiscontinuity: ")
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        Log.d("TAG", "onPlaybackParametersChanged: ")
    }

    override fun onSeekProcessed() {
        Log.e("onProcessed", "onProcessed")
    }

    override fun onAddFriendClick(position: Int, user: FriendListModel.User?, view: View?) {

    }

    override fun onFriendView(position: Int, user: FriendListModel.User?, view: View?) {

    }


}

