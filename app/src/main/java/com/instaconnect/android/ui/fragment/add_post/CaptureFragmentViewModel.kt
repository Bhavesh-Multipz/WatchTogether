package com.instaconnect.android.ui.fragment.add_post

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.Camera
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.instaconnect.android.base.BaseViewModel
import com.instaconnect.android.data.model.db.ChatMessage
import com.instaconnect.android.fileHelper.DataManager
import com.instaconnect.android.fileHelper.FileUtils
import com.instaconnect.android.network.Resource
import com.instaconnect.android.ui.video_preview.VideoPreviewsActivity
import com.instaconnect.android.utils.AppFileHelper
import com.instaconnect.android.utils.BitmapUtil
import com.instaconnect.android.utils.Constants
import com.instaconnect.android.utils.Util
import kotlinx.coroutines.launch
import java.io.File

class CaptureFragmentViewModel(private val repository: CaptureFragmentRepository) : BaseViewModel(repository),
    View.OnTouchListener, SurfaceHolder.Callback {

    private var postType: String? = null
    private var captureType: String? = null
    var videoFile: File? = null
    private var surfaceHolder: SurfaceHolder? = null
    private var appFileHelper: AppFileHelper? = null
    private val camera: Camera? = null
    var dataManager: DataManager? = null
    private var fileName: String? = null

    private val _getWebLinksResponse: MutableLiveData<Resource<WebLinkResponse>> = MutableLiveData()
    val getWebLinksResponse: LiveData<Resource<WebLinkResponse>>
        get() = _getWebLinksResponse

    suspend fun getWebLinks(
        device_type: String,
    ) = viewModelScope.launch {
        _getWebLinksResponse.value = Resource.Loading
        _getWebLinksResponse.value = repository.getWebLinks(device_type)
    }


    fun init(surfaceView: SurfaceView, surfaceHolder: SurfaceHolder, appFileHelper: AppFileHelper) {
        this.surfaceHolder = surfaceHolder
        this.appFileHelper = appFileHelper
        surfaceView.setOnTouchListener(this)
        setSurfaceHolder()
        fileName = appFileHelper.createUniqueFilename()
        videoFile = appFileHelper.createVideoFile(fileName)
    }

    private fun setSurfaceHolder() {
        surfaceHolder!!.addCallback(this)
        surfaceHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    fun onPickMediaResult(fileUtils: FileUtils, data: Intent, context: Context) {
        val selectedMedia = data.data
        val mime: String = fileUtils.getMimeType(selectedMedia!!)!!
        if (mime.toLowerCase().contains("video")) {
            val videoPath: String = fileUtils.getPath(selectedMedia)!!
            // FileUtils.copyFile(new File(videoPath), videoFile);
            videoFile = File(videoPath)
            showVideoPreview(context)
            surfaceDestroyed(surfaceHolder!!)
        } /*else if (mime.toLowerCase().contains("image")) {
            val imagepath: String = fileUtils.getPath(selectedMedia!!)!!
            showImagePreview(imagepath, false)
            surfaceDestroyed(surfaceHolder)
        }*/
    }

    private fun showVideoPreview(context: Context) {
        Thread {
            val thumb: Bitmap = BitmapUtil.createThumbnailAtTime(videoFile!!.path, 1)!!
            val videoThumb: File = appFileHelper!!.createThumbFile(fileName)!!
            BitmapUtil.bitmapToFile(thumb, videoThumb.path)
            val chatMessage = ChatMessage()
            chatMessage.messageBody  = videoFile!!.path
            chatMessage.filePath = videoFile!!.path
            chatMessage.thumbnailPath = videoThumb.path
            chatMessage.messageThumb = videoThumb.path
            chatMessage.fileSize = videoFile!!.length().toString()
            chatMessage.dataType = Constants.VIDEO_TYPE
            chatMessage.postType = postType!!
            chatMessage.captureType = captureType!!
            chatMessage.uniqueCode = Util.createUniqueCode()
            val intent = Intent(context, VideoPreviewsActivity::class.java)
            intent.putExtra(Constants.CHAT_MESSAGE_MODEL, chatMessage)
            context.startActivity(intent)

        }.start()
    }

    fun setPostType(postType: String) {
        this.postType = postType
    }

    fun setCaptureType(captureType: String) {
        this.captureType = captureType
    }

    override fun onTouch(view: View?, p1: MotionEvent?): Boolean {

        // Get the pointer ID
        /*val params: Camera.Parameters = camera!!.parameters
        val action: Int = event.getAction()

        if (event.getPointerCount() > 1) {
            // handle multi-touch events
            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mDist = getFingerSpacing(event)
            } else if (action == MotionEvent.ACTION_MOVE
                && params.isZoomSupported
            ) {
                camera!!.cancelAutoFocus()
                handleZoom(event, params)
            }
        } else {
            // handle single touch events
            if (action == MotionEvent.ACTION_UP) {
                handleFocus(event, params)
                view!!.performClick()
            }
        }*/
        return true
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
    }
}