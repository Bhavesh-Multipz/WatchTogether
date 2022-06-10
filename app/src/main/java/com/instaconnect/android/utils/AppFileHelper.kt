package com.instaconnect.android.utils

import android.content.Context
import com.instaconnect.android.fileHelper.FileHelper
import android.os.Environment
import android.util.Log
import com.instaconnect.android.utils.AppFileHelper
import java.io.File
import java.util.*

class AppFileHelper(private val context: Context) : FileHelper {
    override fun createFileDatabase() {
        createPrimaryDir()
        createImageDir()
        createVideoDir()
        createAudioDir()
        createLocationDir()
        createThumbnailDir()
        createEmojiDir()
        createStickerDir()
        createUserDir()
        createBackgroundDir()
    }

    override fun createPrimaryDir() {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), PRIMARY_DIR
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create InstaMessage directory")
            }
        }
    }

    override fun createImageDir() {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), IMAGE_DIR
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory")
            }
        }
    }

    override fun createVideoDir() {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), VIDEO_DIR
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory")
            }
        }
    }

    override fun createAudioDir() {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), AUDIO_DIR
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory")
            }
        }
    }

    override fun createLocationDir() {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), LOCATION_DIR
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory")
            }
        }
    }

    override fun createThumbnailDir() {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), THUMB_DIR
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory")
            }
        }
    }

    override fun createEmojiDir() {
        val mediaStorageDir = File(context.filesDir, EMOJI_DIR)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory")
            }
        }
    }

    override fun createStickerDir() {
        val mediaStorageDir = File(context.filesDir, STICKER_DIR)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory")
            }
        }
    }

    override fun createUserDir() {
        val mediaStorageDir = File(context.filesDir, USER_DIR)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory")
            }
        }
    }

    override fun createBackgroundDir() {
        val mediaStorageDir = File(context.filesDir, CHAT_BG_DIR)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("App", "failed to create directory")
            }
        }
    }

    override val primaryDir: String
        get() = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString() + "/" + PRIMARY_DIR
    override val imageDir: String
        get() = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString() + "/" + IMAGE_DIR
    override val videoDir: String
        get() = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString() + "/" + VIDEO_DIR
    override val audioDir: String
        get() = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString() + "/" + AUDIO_DIR
    override val thumbDir: String
        get() = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString() + "/" + THUMB_DIR
    override val locationDir: String
        get() = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        ).toString() + "/" + LOCATION_DIR
    override val emojiDir: String
        get() = context.filesDir.toString() + "/" + EMOJI_DIR
    override val chatBgDir: String
        get() = context.filesDir.toString() + "/" + CHAT_BG_DIR
    override val stickerDir: String
        get() = context.filesDir.toString() + "/" + STICKER_DIR
    override val userDir: String
        get() = context.filesDir.toString() + "/" + USER_DIR

    override fun createImageFile(fileName: String?): File? {
        return File(imageDir, fileName.toString() + ".jpeg")
    }

    override fun createImageFile(): File? {
        return File(imageDir, createUniqueFilename() + ".jpeg")
    }

    override fun createVideoFile(fileName: String?): File? {
        return File(videoDir, fileName.toString() + ".mp4")
    }

    override fun createVideoFile(): File? {
        return File(videoDir, createUniqueFilename() + ".mp4")
    }

    override fun createChatBgFile(filename: String?): File? {
        return File(chatBgDir, "$filename.png")
    }

    override fun createAudioFile(fileName: String?): File? {
        return File(audioDir, fileName.toString() + ".m4a")
    }

    override fun createAudioFile(): File? {
        return File(audioDir, createUniqueFilename() + ".m4a")
    }

    override fun createLoactionFile(fileName: String?): File? {
        return File(locationDir, fileName.toString() + ".jpeg")
    }

    override fun createThumbFile(fileName: String?): File? {
        return File(thumbDir, fileName.toString() + ".jpeg")
    }

    override fun createThumbFile(): File? {
        return File(thumbDir, createUniqueFilename() + ".jpeg")
    }

    override fun createAvatar(fileName: String?): File? {
        return File(userDir, fileName.toString() + ".jpeg")
    }

    override fun createAvatar(): File? {
        return File(userDir, createUniqueFilename() + ".jpeg")
    }

    override fun getImageFile(filename: String?): File? {
        return File("$imageDir/$filename.jpeg")
    }

    override fun getVideoFile(filename: String?): File? {
        return File("$videoDir/$filename.mp4")
    }

    override fun getChatBgFile(filename: String?): File? {
        return File("$chatBgDir/$filename.png")
    }

    override fun getThumbFile(filename: String?): File? {
        return File("$thumbDir/$filename.jpeg")
    }

    override fun getAudioFile(filename: String?): File? {
        return File("$audioDir/$filename.m4a")
    }

    override fun getEmojiFile(filename: String?): File? {
        return File("$emojiDir/$filename.png")
    }

    override fun getLoactionFile(filename: String?): File? {
        return File("$locationDir/$filename.png")
    }

    override fun getStickerFile(filename: String?): File? {
        return File("$stickerDir/$filename.png")
    }

    override fun getAvatarFile(filename: String?): File? {
        return File("$userDir/$filename.jpeg")
    }

    override fun createUniqueFilename(): String? {
        val min = 100
        val max = 1000000000
        val r = Random()
        val random = r.nextInt(max - min + 1) + min
        val time = System.currentTimeMillis()
        return (random + time).toString()
    }

    companion object {
        const val PRIMARY_DIR = "InstaConnect"
        const val IMAGE_DIR = PRIMARY_DIR + "/image"
        const val VIDEO_DIR = PRIMARY_DIR + "/video"
        const val AUDIO_DIR = PRIMARY_DIR + "/audio"
        const val THUMB_DIR = PRIMARY_DIR + "/thumbnail"
        const val LOCATION_DIR = PRIMARY_DIR + "/location"
        const val EMOJI_DIR = "emoji"
        const val STICKER_DIR = "sticker"
        const val USER_DIR = "user"
        const val CHAT_BG_DIR = "background"
    }
}