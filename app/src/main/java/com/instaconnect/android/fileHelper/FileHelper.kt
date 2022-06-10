package com.instaconnect.android.fileHelper

import java.io.File

interface FileHelper {
    fun createFileDatabase()
    fun createPrimaryDir()
    fun createImageDir()
    fun createVideoDir()
    fun createAudioDir()
    fun createLocationDir()
    fun createThumbnailDir()
    fun createEmojiDir()
    fun createStickerDir()
    fun createBackgroundDir()
    fun createAvatar(fileName: String?): File?
    fun createAvatar(): File?
    val primaryDir: String?
    val chatBgDir: String?
    val imageDir: String?
    val videoDir: String?
    val audioDir: String?
    val thumbDir: String?
    val locationDir: String?
    val emojiDir: String?
    val stickerDir: String?
    fun createImageFile(fileName: String?): File?
    fun createChatBgFile(fileName: String?): File?
    fun createImageFile(): File?
    fun createVideoFile(fileName: String?): File?
    fun createVideoFile(): File?
    fun createAudioFile(fileName: String?): File?
    fun createAudioFile(): File?
    fun createLoactionFile(fileName: String?): File?
    fun createThumbFile(fileName: String?): File?
    fun createThumbFile(): File?
    fun getImageFile(filename: String?): File?
    fun getVideoFile(filename: String?): File?
    fun getThumbFile(filename: String?): File?
    fun getAudioFile(filename: String?): File?
    fun getEmojiFile(filename: String?): File?
    fun getLoactionFile(filename: String?): File?
    fun getStickerFile(filename: String?): File?
    fun getAvatarFile(filename: String?): File?
    val userDir: String?
    fun getChatBgFile(filename: String?): File?
    fun createUserDir()
    fun createUniqueFilename(): String?
}