package com.instaconnect.android.ui.watch_together_room

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.allattentionhere.autoplayvideos.MediaSourceUtil
import com.allattentionhere.autoplayvideos.VideoRecyclerView
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.instaconnect.android.InstaConnectApp
import com.instaconnect.android.R
import com.instaconnect.android.data.model.*
import com.instaconnect.android.databinding.ActivityWatchTogetherVideoBinding
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.ui.trending_websites.TrendingWebsitesActivity
import com.instaconnect.android.utils.*
import com.instaconnect.android.utils.Utils.toast
import com.instaconnect.android.utils.heart_view.HeartsRenderer
import com.instaconnect.android.utils.heart_view.HeartsView
import com.instaconnect.android.utils.helper_classes.GlideHelper
import com.instaconnect.android.ytextractor.VideoMeta
import com.instaconnect.android.ytextractor.YouTubeExtractor
import com.instaconnect.android.ytextractor.YtFile
import gun0912.tedimagepicker.util.ToastUtil
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class WatchTogetherVideoActivity : AppCompatActivity(), Player.EventListener, View.OnClickListener,
    AddFriendToVideoListAdapter.AddFriendListListener {
    private val permissionsRequestCode = 111
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
    private var comingFrom: String? = "Home"
    private var myPostReaction = ""
    private var actualPostId = ""
    private var currentUserAvtar = ""
    private val screenWidth = 0
    var reviewManager: ReviewManager? = null
    private lateinit var binding: ActivityWatchTogetherVideoBinding
    private var isScroll: Boolean? = true
    private var chatDataList: ArrayList<MessageDataItem>? = null
    var chatAdapter: WatchTogetherChatAdapter? = null
    var roomLiveUsersAdapter: RoomLiveUsersAdapter? = null
    var liveUsersList: ArrayList<LiveUsersItem> = ArrayList()
    private var viewUtil: ViewUtil? = null
    private var isRated: String = "0"
    private lateinit var managePermissions: ManagePermissions
    var currentWindow = 0
    var playBackPosition: Long = 0
    var proxyCacheServer: HttpProxyCacheServer? = null

    var list = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchTogetherVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

        chatDataList = ArrayList()
        viewUtil = ViewUtil(this)
        videoId = intent.getStringExtra("VIDEO_ID")
        postId = intent.getStringExtra("POST_ID")!!
        if (intent.getStringExtra("COMING_FROM") != null) {
            comingFrom = intent.getStringExtra("COMING_FROM")!!
        }

        userId = Prefrences.getPreferences(this, Constants.PREF_USER_ID)
        currentUserAvtar = Prefrences.getUser()!!.avatar
        isRated = Prefrences.getPreferences(this, Constants.IS_RATED)!!
        viewModel = ViewModelProvider(
            this,
            WatchTogetherViewModelFactory(
                WatchTogetherVideoRepository(
                    MyApi.getInstance()
                )
            )
        )[WatchTogetherVideoViewModel::class.java]

        managePermissions = ManagePermissions(this, list.toList(), permissionsRequestCode)
        mute = VideoRecyclerView.isMute()
        proxyCacheServer = InstaConnectApp.instance!!.getProxy(this)
        forBackgroundVideo()
        setChatAdapter()
        setUpLiveUserAdapter()
        getCurrentUserData()

        val handler1 = Handler()
        handler1.postDelayed({
            /*if (videoId != null && postId != null && postId != "web" && !videoId!!.contains("http")) {
                binding.youtubeplayer.visibility = View.GONE
                binding.exoPlayer.visibility = View.VISIBLE
                binding.relExo.visibility = View.GONE
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
            } else {*/
            /*var hyperLink: String
            if (videoId != null && !videoId!!.contains("http")) {
                //hyperLink = extractYTId(videoId);
                binding.youtubeplayer.visibility = View.GONE
                binding.exoPlayer.visibility = View.VISIBLE
                binding.relExo.visibility = View.VISIBLE
                Log.d("TAG", "run: $videoId")
                binding.youtubeplayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId!!, 0f)
                        binding.relVideoNotWorking.visibility = View.GONE
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
            } else {*/
            binding.youtubeplayer.visibility = View.GONE
            binding.exoPlayer.visibility = View.VISIBLE

            show()
            binding.progressBar.visibility = View.VISIBLE

            val handler = Handler(Looper.myLooper()!!)
            handler.postDelayed({ playVideo() }, 100)

            binding.exoPlayer.setControllerVisibilityListener { visibility ->
                Log.e("control_visibility", "$visibility....")
                if (visibility == 0) {
                    show()
                } else {
                    hide()
                }
            }
//                } }

        }, 100)

        setUserData()
        setListeners()

        if (SocketConnector.getInstance() != null) {
            if (SocketConnector.getSocket()!!.connected()) {
                emitMessages()
                onMessages()
                getPostReaction()
                onCurrentMessage()
                onJoinPostRoom()
                onGetPostLiveUsers()
                getPostReactionSocket()

            } else {
                ToastUtil.showToast("Socket not connected")
            }
        }

        // post reaction response handler
        viewModel.postReactionResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null) {
                        myPostReaction = "1"
                        binding.tvTotalLike.text = java.lang.String.valueOf(it.value.response!!.reaction!!.likes)
                    }
                }
                is Resource.Failure -> {
                    ToastUtil.showToast(it.toString())
                }
                else -> {}
            }
        }


        // user rating response handler
        viewModel.userRatingResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null) {
                        Prefrences.savePreferencesString(this, Constants.IS_RATED, "1")
                    }
                }
                is Resource.Failure -> {
                    ToastUtil.showToast(it.toString())
                }
                else -> {}
            }
        }

        // delete post response handler
        viewModel.deletePostResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    finish()
                    Toast.makeText(context, "Room deleted", Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {}

                is Resource.Failure -> {}
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)
                if (isPermissionsGranted) {

                } else {
                    this.toast("Permissions denied.")
                }
                return
            }
        }
    }

    private fun getPostReactionSocket() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("postId", actualPostId)
            jsonObject.put("userId", userId)
            SocketConnector.getSocket()!!.emit("getPostReactionSocket", jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun onJoinPostRoom() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("post_id", actualPostId)
            jsonObject.put("image", currentUserAvtar)
            jsonObject.put("username", user_name)
            jsonObject.put("user_id", userId)

            if (!actualPostId.isNullOrBlank() && !userId.isNullOrBlank())
                SocketConnector.getSocket()!!.emit("joinroom", jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun onGetPostLiveUsers() {

        SocketConnector.getSocket()!!.on(
            "availbleUsers"
        ) { args ->

            val data = args[0] as JSONObject
            try {

                var liveUser: RoomLiveUsersResponse =
                    Gson().fromJson(
                        data.toString(),
                        object : TypeToken<RoomLiveUsersResponse>() {}.type
                    )

                if (actualPostId == liveUser.postId) {
                    liveUsersList.clear()
                    liveUser.liveUsers.forEach {
                        if (actualPostId == it.postId) {
                            if (liveUsersList.contains(it)) {

                            } else {
                                liveUsersList.add(it)
                            }
                        }
                    }

                    runOnUiThread {

                        setUpLiveUserAdapter()
                        binding.tvTotalWatch.text = liveUsersList.size.toString()
                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
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

                if (actualPostId == chatData.postId) {
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

            if (actualPostId == chatData.postId) {
                chatDataList!!.clear()
                chatDataList!!.addAll(chatData.messageData!!)
                notifyList()
                scrollViewToLastPos(true)
            }
        }
    }

    private fun getPostReaction() {
        SocketConnector.getSocket()!!.on(
            "getPostReaction"
        ) { args ->
            val data = args[0] as JSONObject
            val reaction: PostReactionSocketResponse =
                Gson().fromJson(
                    data.toString(),
                    object : TypeToken<PostReactionSocketResponse?>() {}.type
                )

            if (actualPostId == reaction.postId) {
                if (userId == "${reaction.userId}") {
                    myPostReaction = "${reaction.messageData!!.yourReaction}"
                }
                runOnUiThread { binding.tvTotalLike.text = "${reaction.messageData!!.likes}" }
            }

            if (binding.tvTotalLike.text.toString() != "${reaction.messageData!!.likes}") {
                showLikesAnimation(reaction.messageData.likes)
            }
        }
    }

    private fun showLikesAnimation(likes: Int?) {
        runOnUiThread {
            for (i in 1..likes!!) {
                callHeart(100)
            }
        }
    }

    // initialize inbox data
    private fun emitMessages() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("post_id", actualPostId)
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
                isScroll = false
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
//        binding.tvTotalLike.text = total_likes
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
        binding.relVideoNotWorking.setOnClickListener(this)

        binding.viewLayer.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                showHideControls()
            }

            override fun onDoubleClick(v: View?) {}
        })

        window.decorView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            window.decorView.getWindowVisibleDisplayFrame(r)

            val height = window.decorView.height
            if (height - r.bottom > height * 0.1399) {

            } else {
                scrollViewToLastPos(true)
            }
        }
    }

    private fun showHideControls() {
        if (binding.fl.visibility === View.VISIBLE) {
            hide()
        } else {
            show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.youtubeplayer.release()
        binding.youtubeplayer2.release()
        simpleExoPlayer!!.release()

        InstaConnectApp.instance!!.mediaPlayer()!!.release()
    }

    override fun onStart() {
        super.onStart()
        binding.blurLayout.startBlur()
    }

    override fun onResume() {
        super.onResume()

        if (mute) {
            VideoRecyclerView.setMute(true)
            InstaConnectApp.instance!!.mediaPlayer()!!.mute()
            binding.ivAudio.setImageResource(R.drawable.mute)
        } else {
            VideoRecyclerView.setMute(false)
            InstaConnectApp.instance!!.mediaPlayer()!!.unMute()
            binding.ivAudio.setImageResource(R.drawable.audio_play)
        }

        Handler().postDelayed({
            if (ViewUtil.isActivityDead(this@WatchTogetherVideoActivity)) return@postDelayed
            if (mute) {
                VideoRecyclerView.setMute(true)
            } else {
                VideoRecyclerView.setMute(false)
            }
        }, 2000)
    }

    override fun onStop() {
        super.onStop()
        mute = true
        binding.blurLayout.pauseBlur()
    }

    private fun onLeavePostRoom() {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("post_id", actualPostId)

            SocketConnector.getSocket()!!.emit("leaveroom", jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.iv_send -> {
                viewUtil!!.hideKeyboard()
                sendTextMessage()
            }

            R.id.relVideoNotWorking -> {
                startActivity(Intent(this, TrendingWebsitesActivity::class.java))
            }

            R.id.iv_endStreaming -> {
                if (isRated == "0") {
                    feedbackDialog()
                } else {
                    closeDialog()
                }
            }

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
                if (myPostReaction == "0") {
                    myPostReaction = "1"
                    postLike(actualPostId, myPostReaction, userId)
                    /*viewModel.viewModelScope.launch {
                        viewModel.addPostReaction(actualPostId, myPostReaction, userId!!)
                    }*/
                }
                showHeart()
            }

            R.id.iv_streamUser -> {
                val addFriendDialog = AddFriendToWatchVideoDialog(this@WatchTogetherVideoActivity, actualPostId, this, viewModel)
                addFriendDialog.show(supportFragmentManager, "AddFriendDialog")
            }
        }
    }

    private fun feedbackDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_feedback_from_room)
        val tvYes = dialog.findViewById<TextView>(R.id.tvYes)
        val tvNo = dialog.findViewById<TextView>(R.id.tvNo)
        val tvDismiss = dialog.findViewById<TextView>(R.id.tvDismiss)

        val imageView: ImageView = dialog.findViewById(R.id.img_bg)
        val relMain: View = dialog.findViewById(R.id.rel_main)
        val vto: ViewTreeObserver = relMain.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width: Int = relMain.measuredWidth
                val height: Int = relMain.measuredHeight
                imageView.layoutParams.height = height
                imageView.layoutParams.width = width
                /*imageView.setImageBitmap(
                    BlurKit.getInstance()!!.fastBlur(imageView, 8, 0.12.toFloat())
                )*/
            }
        })


        tvYes.setOnClickListener { v: View? ->
            showRateApp()
            dialog.dismiss()
        }
        tvNo.setOnClickListener { v: View? ->
            closeDialog()
            dialog.dismiss()
        }
        tvDismiss.setOnClickListener { v: View? ->

            closeDialog()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showRateApp() {
        reviewManager = ReviewManagerFactory.create(this)
        val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo: ReviewInfo = task.result
                val flow: Task<Void> = reviewManager!!.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener {
                    Toast.makeText(this, "Already Submitted", Toast.LENGTH_SHORT).show()

                }
            } else {
                Toast.makeText(this, "InAppReviewFailed", Toast.LENGTH_SHORT).show()
            }
        }

        request.addOnSuccessListener {
            viewModel.viewModelScope.launch {
                viewModel.userRating(userId!!, packageManager.getPackageInfo(packageName, 0).versionName, "1")
            }
        }
    }

    private fun closeDialog() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_close_live_room)
        val tvYes = dialog.findViewById<TextView>(R.id.tvYes)
        val tvNo = dialog.findViewById<TextView>(R.id.tvNo)

        val imageView: ImageView = dialog.findViewById(R.id.img_bg)
        val relMain: View = dialog.findViewById(R.id.rel_main)
        val vto: ViewTreeObserver = relMain.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                relMain.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width: Int = relMain.measuredWidth
                val height: Int = relMain.measuredHeight
                imageView.layoutParams.height = height
                imageView.layoutParams.width = width
                /*imageView.setImageBitmap(
                    BlurKit.getInstance()!!.fastBlur(imageView, 8, 0.12.toFloat())
                )*/
            }
        })

        tvNo.setOnClickListener { v: View? ->
            dialog.dismiss()
        }
        tvYes.setOnClickListener { v: View? ->

            if (comingFrom.equals("DeletePost")) {
                //deletePost()
            }
            onLeavePostRoom()
            if (comingFrom == "Home") {
                finish()
            } else {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun deletePost() {
        viewModel.viewModelScope.launch {
            viewModel.deletePost(actualPostId)
        }
    }

    private fun postLike(actualPostId: String, myPostReaction: String, userId: String?) {
        if (SocketConnector.getInstance() != null) {
            if (SocketConnector.getSocket()!!.connected()) {
                val jsonObject = JSONObject()
                try {
                    jsonObject.put("reaction", myPostReaction)
                    jsonObject.put("postId", actualPostId)
                    jsonObject.put("userId", userId)

                    SocketConnector.getSocket()!!.emit("addPostReaction", jsonObject)
                    binding.etMessageBox.setText("")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
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
                    jsonObject.put("message", Objects.requireNonNull(binding.etMessageBox.text)
                        .toString().trim { it <= ' ' })

                    SocketConnector.getSocket()!!.emit("sendMessage", jsonObject)
                    binding.etMessageBox.setText("")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun callHeart(mils: Int) {
        Handler().postDelayed({
            showHeart()
        }, mils.toLong())
    }

    private fun showHeart() {
        heartAnimation()
        binding.heartView.emitHeart(model!!)
    }

    private fun heartAnimation() {
        val config = HeartsRenderer.Config(3f, 0.8f, 1.5f)
        binding.heartView.applyConfig(config)
        val bitmap = BitmapFactory.decodeResource(context!!.resources, R.drawable.ic_heart)
        model = HeartsView.Companion.Model(0, bitmap)
    }

    private fun getRandomNumber(min: Int, max: Int): Float {
        val random = Random()
        return min + random.nextFloat() * (max - min)
    }

    private fun playVideo() {
        if (videoId!!.contains("www.youtube") || videoId!!.contains("m.youtube.com") || videoId!!.contains(
                "watch?v") || videoId!!.length == 11
        ) {
            playYoutubeVideo(videoId!!)
            binding.youtubeplayer.visibility = View.GONE
            binding.exoPlayer.visibility = View.VISIBLE
            binding.relExo.visibility = View.VISIBLE
            simpleExoPlayer!!.addListener(this)
            binding.exoPlayer.player = simpleExoPlayer
            simpleExoPlayer!!.playWhenReady = true
        } else {
            simpleExoPlayer!!.addListener(this)
            simpleExoPlayer!!.prepare()
            simpleExoPlayer!!.setMediaSource(buildMediaSource(Uri.parse(videoId)!!,
                MediaSourceUtil.getExtension(Uri.parse(videoId))!!))
            simpleExoPlayer!!.playWhenReady = true
        }

    }

    @SuppressLint("StaticFieldLeak")
    private fun playYoutubeVideo(youtubeLink: String) {
        object : YouTubeExtractor(this@WatchTogetherVideoActivity) {

            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, videoMeta: VideoMeta?) {

                if (ytFiles != null) {

                    val audioTag = 140 // audio tab for m4a


                    val downloadableUrl = 22 // downloadable url
                    val url144 = 160
                    val url240 = 133
                    val url360 = 134
                    val url480 = 135
                    val url720 = 136
                    val audioSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                        .createMediaSource(MediaItem.fromUri(proxyCacheServer!!.getProxyUrl(ytFiles.get(audioTag).url)))
                    var videoSource: ProgressiveMediaSource? = null

                    if (ytFiles.get(url360) != null) {
                        videoSource = ProgressiveMediaSource.Factory(
                            DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(proxyCacheServer!!.getProxyUrl(ytFiles.get(url360).url)))
                    } else if (ytFiles.get(url240) != null) {
                        videoSource = ProgressiveMediaSource.Factory(
                            DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(proxyCacheServer!!.getProxyUrl(ytFiles.get(url240).url)))
                    } else if (ytFiles.get(url144) != null) {
                        videoSource = ProgressiveMediaSource.Factory(
                            DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(proxyCacheServer!!.getProxyUrl(ytFiles.get(url144).url)))
                    } else if (ytFiles.get(url480) != null) {
                        videoSource = ProgressiveMediaSource.Factory(
                            DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(proxyCacheServer!!.getProxyUrl(ytFiles.get(url480).url)))
                    } else if (ytFiles.get(url720) != null) {
                        videoSource = ProgressiveMediaSource.Factory(
                            DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(proxyCacheServer!!.getProxyUrl(ytFiles.get(url720).url)))
                    } else if (ytFiles.get(downloadableUrl) != null) {
                        videoSource = ProgressiveMediaSource.Factory(
                            DefaultHttpDataSource.Factory())
                            .createMediaSource(MediaItem.fromUri(proxyCacheServer!!.getProxyUrl(ytFiles.get(downloadableUrl).url)))

                    }

                    if (videoSource != null) {
                        simpleExoPlayer!!.setMediaSource(
                            MergingMediaSource(
                                true, videoSource, audioSource), true
                        )

                        simpleExoPlayer!!.prepare()
                        simpleExoPlayer!!.playWhenReady = true
                        simpleExoPlayer!!.seekTo(currentWindow, playBackPosition)
                    }

                }
            }
        }.extract(youtubeLink, true, true)

    }

    private fun buildMediaSource(uri: Uri, overrideExtension: String): MediaSource {
        @C.ContentType val type = Util.inferContentType(uri, overrideExtension)
        return when (type) {
            C.TYPE_HLS -> HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri)
            C.TYPE_OTHER -> if (uri.toString().contains("mp4") || uri.toString().contains(".MOV")) {
                ProgressiveMediaSource.Factory(dataSourceFactory!!).createMediaSource(uri)
            } else {
                if (uri.toString().contains(".MOV") || uri.toString().contains("mp4")) {
                    ProgressiveMediaSource.Factory(dataSourceFactory!!).createMediaSource(uri)
                } else {
                    HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
                        .createMediaSource(uri)
                }
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
        userAgent = Util.getUserAgent(context!!, "ExoPlayer")
        // Create the player
        simpleExoPlayer = SimpleExoPlayer.Builder(this).build()
        binding.exoPlayer.player = simpleExoPlayer
        binding.exoPlayer.keepScreenOn = true
        dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "ExoPlayer"))
        extractorsFactory = DefaultExtractorsFactory()

    }

    private fun setUpLiveUserAdapter() {
        var linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvLiveUsers.layoutManager = linearLayoutManager
        roomLiveUsersAdapter = RoomLiveUsersAdapter(this, liveUsersList) {}
        binding.rvLiveUsers.adapter = roomLiveUsersAdapter
    }

    private fun setChatAdapter() {
        var linearLayoutManager = LinearLayoutManager(context)
        val recyclerChat: RecyclerView = binding.recyclerChat
        recyclerChat.layoutManager = linearLayoutManager
        chatAdapter = WatchTogetherChatAdapter(this, chatDataList!!) {}
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

    override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {
        Log.d("TAG", "onTimelineChanged: ")
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
        Log.d("TAG", "onTracksChanged: ")
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        Log.d("TAG", "onLoadingChanged: $isLoading")
    }


    override fun onPause() {
        super.onPause()
        InstaConnectApp.instance!!.mediaPlayer()!!.play(false)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        /*if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
            binding.play.visibility = View.GONE
            binding.pause.visibility = View.VISIBLE

        }*/
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

    override fun onPlayerError(error: ExoPlaybackException) {
        Log.d("TAG", "onPlayerError: $error")
        binding.progressBar.visibility = View.GONE
        binding.relVideoNotWorking.visibility = View.GONE
    }

    override fun onPositionDiscontinuity(reason: Int) {
        Log.d("TAG", "onPositionDiscontinuity: ")
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
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

