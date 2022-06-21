package com.instaconnect.android.utils

import android.os.Handler
import android.os.Looper
import com.instaconnect.android.data.model.db.ChatMessage
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class ProgressRequestBody(private val mFile: File, type: Int,private val chatMessage: ChatMessage?) : RequestBody() {
    var length = 0
    private var mListener: UploadCallbacks? = null
    private val type: Int
    private var mUploaded: Long = 0
    private var mTotal: Long = 0
    private var progress = 0


    override fun contentType(): MediaType? {
        return when (type) {
            IMAGE -> MediaType.parse("image/*")
            VIDEO -> MediaType.parse("video/*")
            else -> MediaType.parse("audio/*")
        }
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val `in` = FileInputStream(mFile)
        var uploaded: Long = 0
        length++
        val handler = Handler(Looper.getMainLooper())
        try {
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {

                // update progress on UI thread
                if (mListener != null) {
                    mUploaded = uploaded
                    mTotal = fileLength
                    progress = (100 * mUploaded / mTotal).toInt()
                    handler.post { mListener!!.onProgressUpdate(progress, chatMessage!!) }
                }
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
            handler.post { mListener?.onFinish(chatMessage!!) }
        } catch (e: Exception) {
            e.printStackTrace()
            if (mListener != null) {
                handler.post {
                    //mListener.onError(chatMessage);
                }
            }
        }
    }

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int, chatMessage: ChatMessage)
        fun onError(chatMessage: ChatMessage)
        fun onFinish(chatMessage: ChatMessage)
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
        var IMAGE = 1
        var VIDEO = 2
        var AUDIO = 3
    }

    init {
        mListener = mListener
        this.type = type
    }
}