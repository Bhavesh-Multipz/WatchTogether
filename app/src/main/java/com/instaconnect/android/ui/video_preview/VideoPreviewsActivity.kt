package com.instaconnect.android.ui.video_preview

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ui.PlayerControlView
import com.instaconnect.android.InstaConnectApp
import com.instaconnect.android.R
import com.instaconnect.android.data.model.db.ChatMessage
import com.instaconnect.android.databinding.ActivityVideoPreviewsBinding
import com.instaconnect.android.fileHelper.DataManager
import com.instaconnect.android.network.MyApi
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.home.HomeActivity
import com.instaconnect.android.ui.previews.PreviewCallbacks
import com.instaconnect.android.ui.watch_together_room.WatchTogetherVideoActivity
import com.instaconnect.android.ui.youtube_webview.VideoPreviewRepository
import com.instaconnect.android.ui.youtube_webview.VideoPreviewViewModel
import com.instaconnect.android.ui.youtube_webview.VideoPreviewViewModelFactory
import com.instaconnect.android.utils.*
import com.instaconnect.android.utils.dialog_helper.DialogCallback
import com.instaconnect.android.utils.models.Trending
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import io.alterac.blurkit.BlurKit
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class VideoPreviewsActivity : AppCompatActivity(), View.OnClickListener, PlayerControlView.VisibilityListener,
    ProgressRequestBody.UploadCallbacks {


    private var viewUtil: ViewUtil? = null
    private lateinit var binding: ActivityVideoPreviewsBinding
    private lateinit var viewModel: VideoPreviewViewModel
    private var thumbImage: String? = null
    private var mediaRatioF = 0f
    private var chatMessage: ChatMessage? = null
    var mYouTubePlayer: YouTubePlayer? = null
    private var countryName: String? = null
    private var progressDialog: ProgressDialog? = null
    private var enteredPassword: String? = null
    private val trending: Trending? = null
    private var appDialogUtil: AppDialogUtil? = null

    companion object {
        private var previewCallbacks: PreviewCallbacks? = null
        var caller = ""
    }


    fun setPreviewCallbacks(chatCallbacks: PreviewCallbacks, callers: String) {
        VideoPreviewsActivity.previewCallbacks = chatCallbacks
        VideoPreviewsActivity.caller = callers
    }

    fun setPreviewCallbacks(chatCallbacks: PreviewCallbacks) {
        VideoPreviewsActivity.previewCallbacks = chatCallbacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoPreviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            VideoPreviewViewModelFactory(
                VideoPreviewRepository(
                    MyApi.getInstance()
                )
            )
        )[VideoPreviewViewModel::class.java]
        appDialogUtil = AppDialogUtil(this)
        chatMessage = intent.getSerializableExtra(Constants.CHAT_MESSAGE_MODEL) as ChatMessage

        setEventListener()
        viewUtil = ViewUtil(this)
        if (chatMessage!!.videoType != "null" && !chatMessage!!.videoType.contains("http")
        ) {
            //Youtube Video
            binding.player.visibility = View.GONE
            binding.youtubePlayer.visibility = View.VISIBLE
            binding.youtubePlayer.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    mYouTubePlayer = youTubePlayer
                    mYouTubePlayer!!.loadVideo(chatMessage!!.videoType, 0f)
                    mYouTubePlayer!!.mute()
                }
            })
        } else {
            //Other Video
            binding.player.visibility = View.VISIBLE
            binding.youtubePlayer.visibility = View.GONE
            binding.player.setControllerVisibilityListener(this)
            binding.player.overlayFrameLayout
                .setOnClickListener { viewUtil!!.hideKeyboard() }
        }

        viewModel.youtubeVideoDetailsResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value != null) {
                        thumbImage = it.value.thumbnailUrl
                        val thumbHeight: Int = it.value.thumbnailHeight
                        val thumbWidth: Int = it.value.thumbnailWidth
                        mediaRatioF = thumbWidth.toFloat() / thumbHeight
                        //                        chatMessage.setMediaRatio(String.valueOf(mediaRatioF));
                    }
                }
                Resource.Loading -> {}
                is Resource.Failure -> {
                }
            }
        }

        viewModel.uploadPostResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null && it.value.response!!.code.equals("200")) {
                        val intent = Intent(this@VideoPreviewsActivity,
                            WatchTogetherVideoActivity::class.java)
                        if (it.value.response!!.youTubeVideoId.equals("empty")) {
                            intent.putExtra("VIDEO_ID", it.value.response!!.hyperlink)
                        } else {
                            intent.putExtra("VIDEO_ID", it.value.response!!.youTubeVideoId)
                        }
                        intent.putExtra("POST_ID", java.lang.String.valueOf(it.value.response!!.id))
                        intent.putExtra("ROOM_NAME", it.value.response!!.group_name)
                        intent.putExtra("USER_ID", it.value.response!!.userId)
                        intent.putExtra("USER_NAME", it.value.response!!.username)
                        intent.putExtra("USER_IMAGE", it.value.response!!.userimage)
                        intent.putExtra("TOTAL_VIEWS", "0")
                        intent.putExtra("TOTAL_LIKES", "0")
                        intent.putExtra("GROUP_NAME", it.value.response!!.group_name)
                        intent.putExtra("ACTUAL_POST_ID", java.lang.String.valueOf(it.value.response!!.id))
                        intent.putExtra("POST_REACTION", "0")
                        startActivity(intent)
                        InstaConnectApp.instance!!.mediaPlayer()!!.release()
                        finish()
                    }
                }
                Resource.Loading -> {}
                is Resource.Failure -> {
                    // uploadCallbacks.onError(ChatMessage())
                }
            }
        }

        viewModel.uploadPostResponse.observe(this) {
            when (it) {
                is Resource.Success -> {
                    if (it.value.response != null && it.value.response!!.code.equals("200")) {
                        finish()
                    }
                }
                Resource.Loading -> {}
                is Resource.Failure -> {
                }
            }
        }

        getYoutubeVideoDetails(chatMessage!!.videoType)

    }

    private fun getYoutubeVideoDetails(videoId: String?) {
        if (videoId != null) {
            viewModel.viewModelScope.launch {
                viewModel.getYoutubeVideoDetails("http://www.youtube.com/watch?v=$videoId")
            }
        }
    }

    private fun setEventListener() {
        binding.ivClose.setOnClickListener(this)
        binding.ivSend.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_close -> {
                finish()
            }

            R.id.iv_send -> {
                if (chatMessage!!.postType == "My Community") {
                    if (chatMessage!!.captureType != "Video") {
                        if (chatMessage!!.captureType == "WatchTogether") {
                            chatMessage!!.caption = binding.etCaption.text.toString()
                            openDialogForSetRoomPassword()
                        } else {
                            chatMessage!!.caption = binding.etCaption.text.toString()

                            publicPostYoutube(chatMessage!!.caption,
                                "My Community",
                                HomeActivity.userLocation!!.latitude,
                                HomeActivity.userLocation!!.longitude,
                                getCountry(),
                                "http://www.youtube.com/watch?v=" + chatMessage!!.videoType,
                                "youTube",
                                chatMessage!!.videoType,
                                this,
                                thumbImage,
                                mediaRatioF)
                        }
                    } else {
                        chatMessage!!.data = ""
                        chatMessage!!.caption = binding.etCaption.text.toString()
                        previewCallbacks?.onPreviewActivityCallback(
                            chatMessage!!.dataType,
                            chatMessage,
                            1)
                        finish()
                    }
                } else {
                    if (chatMessage!!.captureType != null && chatMessage!!.captureType == "WatchTogether") {
                        chatMessage!!.caption = binding.etCaption.text.toString()
                        openDialogForSetRoomPassword()
                    }/* else if (caller.equals(ChatActivity::class.java.getName(),
                            ignoreCase = true) || VideoPreviewsActivity.caller.equals(
                            ChatRoomActivity::class.java.getName(), ignoreCase = true)
                    ) {
                        chatMessage.setData("")
                        chatMessage.setCaption(getViewDataBinding().etCaption.getText().toString())
                        if (VideoPreviewsActivity.previewCallbacks != null) VideoPreviewsActivity.previewCallbacks.onPreviewActivityCallback(
                            chatMessage.getDataType(),
                            chatMessage,
                            1)
                        finish()
                    }*/ else {
                        post(0)
                    }
                }
            }
        }

    }

    fun publicPostYoutube(
        caption: String?, category: String?, lati: Double, longi: Double,
        countrySelected: String?, hyperLink: String?, mediaType: String?,
        videoId: String?, uploadCallbacks: ProgressRequestBody.UploadCallbacks, thumbImage: String?, mediaRatioF: Float,
    ) {
        val user_id: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), Prefrences.getPreferences(this, Constants.PREF_USER_ID)!!)
        val media = RequestBody.create(MediaType.parse("multipart/form-data"), "1")
        val lat = RequestBody.create(MediaType.parse("multipart/form-data"), lati.toString())
        val lng = RequestBody.create(MediaType.parse("multipart/form-data"), longi.toString())
        val rCat = RequestBody.create(MediaType.parse("multipart/form-data"), category)
        val videoCaption = RequestBody.create(MediaType.parse("multipart/form-data"), caption)
        val country = RequestBody.create(MediaType.parse("multipart/form-data"), countrySelected)
        val youtubeVideoId = RequestBody.create(MediaType.parse("multipart/form-data"), videoId)
        val hyperLinkBody = RequestBody.create(MediaType.parse("multipart/form-data"), hyperLink)
        val dataType = RequestBody.create(MediaType.parse("multipart/form-data"), "video")
        val mediaTypeBody = RequestBody.create(MediaType.parse("multipart/form-data"), mediaType)
        val mediaRatioBody = RequestBody.create(MediaType.parse("multipart/form-data"), mediaRatioF.toString())
        DownloadFileHelper(this, thumbImage) { saveFilePath ->
            Log.e("TAG", "onTaskCompleted: $saveFilePath")
            val thamb = RequestBody.create(MediaType.parse("image/*"), File(saveFilePath))
            val thoumbBody = MultipartBody.Part.createFormData("thumb_image", File(saveFilePath).name, thamb)

            viewModel.viewModelScope.launch {
                viewModel.publicPostYoutubeVideo(user_id, media, rCat, lat, lng,
                    videoCaption, country, dataType, hyperLinkBody, mediaTypeBody, mediaRatioBody,
                    youtubeVideoId, thoumbBody)
            }
        }.execute()
    }

    private fun openDialogForSetRoomPassword() {
        val dialog = Dialog(this@VideoPreviewsActivity, R.style.CustomDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_add_password_for_watch_together_video)
        val edtPassword = dialog.findViewById<EditText>(R.id.edtPassword)
        val tvSkip = dialog.findViewById<TextView>(R.id.tvSkip)
        val tvSet = dialog.findViewById<TextView>(R.id.tvSet)
        tvSkip.setOnClickListener { v: View? ->
            // public room - no password
            dialog.dismiss()
            if (chatMessage!!.postType == "My Community") {
                publicPostWatchTogether(chatMessage!!.caption,
                    chatMessage!!.postType,
                    HomeActivity.userLocation!!.latitude,
                    HomeActivity.userLocation!!.longitude,
                    getCountry(),
                    "http://www.youtube.com/watch?v=" + chatMessage!!.videoThumb,
                    "web",
                    "empty",
                    this,
                    thumbImage,
                    mediaRatioF,
                    "")
            } else {
                post(0)
            }
        }
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
        tvSet.setOnClickListener { v: View? ->
            // private room - with password
            enteredPassword = edtPassword.text.toString()
            if (enteredPassword!!.isEmpty()) {
                Toast.makeText(this@VideoPreviewsActivity, "Please enter password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (chatMessage!!.postType == "My Community") {
                    publicPostWatchTogether(chatMessage!!.caption,
                        chatMessage!!.postType,
                        HomeActivity.userLocation!!.latitude,
                        HomeActivity.userLocation!!.longitude,
                        getCountry(),
                        "http://www.youtube.com/watch?v=" + chatMessage!!.videoType,
                        "web",
                        "empty",
                        this,
                        thumbImage,
                        mediaRatioF,
                        enteredPassword)
                } else {
                    post(0)
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun post(filter: Int) {
        if (shouldShowHyperLink(filter)) {
            //showHyperLink();
            /*callCategoryDialog(type, chatMessage);*/ // for category dialog
            chatMessage!!.postType = "worldwide"
            if (chatMessage!!.postType != "My Community" && chatMessage!!.captureType != null
                && chatMessage!!.captureType == "WatchTogether"
            ) {
                // showHyperLink(this);
                postWithoutHyperLink(this)
            } else {
                if (chatMessage!!.captureType != null && chatMessage!!.captureType == "Video") {
                    showHyperLink(this)
                } else {
                    postWithoutHyperLink(this)
                }
                //showHyperLink(this);
            }
        } else {
            chatMessage!!.data = ""
            chatMessage!!.caption = binding.etCaption.text.toString()
            if (previewCallbacks != null) previewCallbacks!!.onPreviewActivityCallback(chatMessage!!.dataType, chatMessage, 1)
            finish()
        }
    }

    private fun showHyperLink(uploadCallbacks: ProgressRequestBody.UploadCallbacks) {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Uploading...")
        progressDialog!!.show()
        appDialogUtil!!.createAddHyperlinkDialog(object : DialogCallback {

            override fun onCallback(dialog: Dialog?, v: View?, position: Int) {
                if (position == 1) {
                    if (chatMessage!!.captureType != null && chatMessage!!.captureType.equals("WatchTogether")) {
                        chatMessage!!.caption = binding.etCaption.getText().toString()
                        if (chatMessage!!.videoType.contains("http")) {
                            publicPostWatchTogether(chatMessage!!.caption,
                                chatMessage!!.postType,
                                HomeActivity.userLocation!!.latitude,
                                HomeActivity.userLocation!!.longitude,
                                getCountry(),
                                "http://www.youtube.com/watch?v=" + chatMessage!!.videoType,
                                "web",
                                "empty",
                                uploadCallbacks,
                                thumbImage,
                                mediaRatioF,
                                enteredPassword)
                        } else {
                            // new changes for youtube watch together video , when youtube video watch together youtubevideoId = videoId and hyperLink = youtubevideoId
                            publicPostWatchTogether(chatMessage!!.caption,
                                chatMessage!!.postType,
                                HomeActivity.userLocation!!.latitude,
                                HomeActivity.userLocation!!.longitude,
                                getCountry(),
                                "http://www.youtube.com/watch?v=" + chatMessage!!.videoType,
                                "web",
                                chatMessage!!.videoType,
                                uploadCallbacks,
                                thumbImage,
                                mediaRatioF,
                                enteredPassword)
                        }
                    } else {
                        val link = (v as EditText).text.toString()
                        chatMessage!!.data = link
                        chatMessage!!.caption = binding.etCaption.text.toString()
                        if (previewCallbacks != null) previewCallbacks!!.onPreviewActivityCallback(chatMessage!!.dataType,
                            chatMessage,
                            0,
                            trending)
                        finish()
                    }
                } else if (position == 2) {
                    if (chatMessage!!.captureType != null && chatMessage!!.captureType == "WatchTogether") {
                        chatMessage!!.caption = binding.etCaption.text.toString()
                        if (chatMessage!!.videoType.contains("http")) {
                            publicPostWatchTogether(chatMessage!!.caption,
                                chatMessage!!.postType,
                                HomeActivity.userLocation!!.latitude,
                                HomeActivity.userLocation!!.longitude,
                                getCountry(),
                                chatMessage!!.videoType,
                                "web",
                                "empty",
                                uploadCallbacks,
                                thumbImage,
                                mediaRatioF,
                                enteredPassword)
                        } else {
                            // new changes for youtube watch together video, when youtube video watch together youtubevideoId = videoId and hyperLink = youtubevideoId
                            publicPostWatchTogether(chatMessage!!.caption,
                                chatMessage!!.postType,
                                HomeActivity.userLocation!!.latitude,
                                HomeActivity.userLocation!!.longitude,
                                getCountry(),
                                "http://www.youtube.com/watch?v=" + chatMessage!!.videoType,
                                "web",
                                chatMessage!!.videoType,
                                uploadCallbacks,
                                thumbImage,
                                mediaRatioF,
                                enteredPassword)
                        }
                    } else {
                        chatMessage!!.data = ""
                        chatMessage!!.caption = binding.etCaption.text.toString()
                        if (previewCallbacks != null) previewCallbacks!!.onPreviewActivityCallback(chatMessage!!.dataType,
                            chatMessage,
                            0,
                            trending)
                    }
                }
            }

            override fun onDismiss() {}
        })
    }

    private fun postWithoutHyperLink(uploadCallbacks: ProgressRequestBody.UploadCallbacks) {
        if (chatMessage!!.captureType != null && chatMessage!!.captureType == "WatchTogether") {
            progressDialog = ProgressDialog(this)
            progressDialog!!.setMessage("Uploading...")
            progressDialog!!.show()
            chatMessage!!.caption = binding.etCaption.text.toString()
            if (chatMessage!!.videoType.contains("http")) {
                publicPostWatchTogether(chatMessage!!.caption,
                    chatMessage!!.postType,
                    HomeActivity.userLocation!!.latitude,
                    HomeActivity.userLocation!!.longitude,
                    getCountry(),
                    chatMessage!!.videoType,
                    "web",
                    "empty",
                    uploadCallbacks,
                    thumbImage,
                    mediaRatioF,
                    enteredPassword)
            } else {
                // new changes for youtube watch together video, when youtube video watch together youtubevideoId = videoId and hyperLink = youtubevideoId
                publicPostWatchTogether(chatMessage!!.caption,
                    chatMessage!!.postType,
                    HomeActivity.userLocation!!.latitude,
                    HomeActivity.userLocation!!.longitude,
                    getCountry(),
                    "http://www.youtube.com/watch?v=" + chatMessage!!.videoType,
                    "web",
                    chatMessage!!.videoType,
                    uploadCallbacks,
                    thumbImage,
                    mediaRatioF,
                    enteredPassword)
            }
        } else {
            chatMessage!!.data = ""
            chatMessage!!.caption = binding.etCaption.text.toString()
            if (previewCallbacks != null) {
                progressDialog = ProgressDialog(this)
                progressDialog!!.setMessage("Uploading...")
                progressDialog!!.show()
                previewCallbacks!!.onPreviewActivityCallback(chatMessage!!.dataType, chatMessage, 0, trending)
            }
        }
    }

    fun publicPostWatchTogether(
        caption: String?, category: String?, lati: Double, longi: Double,
        countrySelected: String?, hyperLink: String?, mediaType: String?,
        videoId: String?, uploadCallbacks: ProgressRequestBody.UploadCallbacks, thumbImage: String?,
        mediaRatioF: Float, groupPassword: String?,
    ) {
        var thumbImage = thumbImage
        val user_id: RequestBody =
            RequestBody.create(MediaType.parse("multipart/form-data"), Prefrences.getPreferences(this, Constants.PREF_USER_ID)!!)
        val media = RequestBody.create(MediaType.parse("multipart/form-data"), "1")
        val lat = RequestBody.create(MediaType.parse("multipart/form-data"), lati.toString())
        val lng = RequestBody.create(MediaType.parse("multipart/form-data"), longi.toString())
        val rCat = RequestBody.create(MediaType.parse("multipart/form-data"), category)
        val videoCaption = RequestBody.create(MediaType.parse("multipart/form-data"), caption)
        val country = RequestBody.create(MediaType.parse("multipart/form-data"), countrySelected)
        val youtubeVideoId = RequestBody.create(MediaType.parse("multipart/form-data"), videoId)
        val hyperLinkBody = RequestBody.create(MediaType.parse("multipart/form-data"), hyperLink)
        val dataType = RequestBody.create(MediaType.parse("multipart/form-data"), "video")
        val mediaTypeBody = RequestBody.create(MediaType.parse("multipart/form-data"), mediaType)
        val mediaRatioBody = RequestBody.create(MediaType.parse("multipart/form-data"), mediaRatioF.toString())
        val videoLinkBody = RequestBody.create(MediaType.parse("multipart/form-data"), "YES")
        val groupPasswordBody: RequestBody = if (groupPassword == null) {
            RequestBody.create(MediaType.parse("multipart/form-data"), "")
        } else {
            RequestBody.create(MediaType.parse("multipart/form-data"), groupPassword)
        }
        val groupNameBody = RequestBody.create(MediaType.parse("multipart/form-data"), "01121993" + System.currentTimeMillis())
        val uniqueCode = RequestBody.create(MediaType.parse("multipart/form-data"), "01121993" + Util.createUniqueCode())
        if (thumbImage == null) {
            thumbImage = "http://99.79.19.208/webapp/dev/uploads/1608380830thumbimage.jpeg"
        }

        DownloadFileHelper(this, thumbImage) { saveFilePath ->
            val thumb: RequestBody
            var thumbBody: MultipartBody.Part? = null
            if (saveFilePath != null) {
                thumb = RequestBody.create(MediaType.parse("image/*"), File(saveFilePath))
                thumbBody = MultipartBody.Part.createFormData("thumb_image", File(saveFilePath).name, thumb)
            }

            viewModel.viewModelScope.launch {
                viewModel.publicPostWatchTogether(
                    user_id, media, rCat, lat, lng,
                    videoCaption, country, dataType, hyperLinkBody, mediaTypeBody, mediaRatioBody,
                    youtubeVideoId, videoLinkBody, groupPasswordBody, groupNameBody, uniqueCode, thumbBody
                )
            }
        }.execute()
    }

    private fun shouldShowHyperLink(filter: Int): Boolean {
        return true
    }

    fun getCountry(): String? {
        if (countryName == null) countryName = LocationUtil.getCountryName(this, HomeActivity.userLocation)
        return if (countryName == null) resources.configuration.locale.displayCountry else countryName
    }

    override fun onVisibilityChange(visibility: Int) {

    }

    override fun onProgressUpdate(percentage: Int, chatMessage: ChatMessage) {

    }

    override fun onError(chatMessage: ChatMessage) {

    }

    override fun onFinish(chatMessage: ChatMessage) {

    }
}