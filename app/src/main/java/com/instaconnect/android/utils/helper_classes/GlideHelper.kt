package com.instaconnect.android.utils.helper_classes

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Key
import com.bumptech.glide.request.RequestOptions
import com.instaconnect.android.fileHelper.DataManager
import java.io.File
import java.lang.Exception
import java.lang.NullPointerException
import java.security.MessageDigest

class GlideHelper {

    companion object{
        fun loadAvatar(
            context: Context,
            dataManager: DataManager,
            uniqueCode: String,
            @DrawableRes placeholder: Int,
            view: ImageView
        ) {
            val file = dataManager.fileHelper()!!.getAvatarFile(uniqueCode)
            Log.e("UserAvatar", file!!.absolutePath + "....UniqueCode " + uniqueCode)
            if (file != null) {
                val signature = StringSignature(file.lastModified().toString())
                //Glide.with(context)
                Glide.with(context.applicationContext)
                    .load(Uri.fromFile(file))
                    .apply(
                        RequestOptions()
                            .placeholder(placeholder)
                    )
                    .apply(RequestOptions().signature(signature))
                    .into(view)
            } else {
                view.setImageResource(placeholder)
            }
        }

        fun loadFromPath(
            context: Context,
            path: String?,
            @DrawableRes placeholder: Int,
            view: ImageView
        ) {
            println("IN FILE PATH")
            if (path != null && !path.isEmpty()) {
                val new_path: String = if (path.contains("http")) path else "file://$path"
                println("NEW FILE PATH:$new_path")
                val file = File(new_path)
                val signature = StringSignature(file.lastModified().toString())
                //Glide.with(context)
                Glide.with(context.applicationContext)
                    .load(Uri.parse(new_path))
                    .apply(
                        RequestOptions()
                            .placeholder(placeholder)
                    )
                    .apply(RequestOptions().signature(signature))
                    .into(view)
            } else {
                view.setImageResource(placeholder)
            }
        }

        fun loadFromUrl(
            context: Context?,
            url: String?,
            @DrawableRes placeholder: Int,
            view: ImageView
        ) {
            println("IN FILE URL")
            if (context == null) return
            if (url != null && !url.isEmpty()) {
                //Glide.with(context)
                Glide.with(context.applicationContext)
                    .load(url)
                    .thumbnail(Glide.with(context.applicationContext).load(placeholder))
                    .apply(
                        RequestOptions()
                            .placeholder(placeholder)
                    )
                    .into(view)
            } else {
                view.setImageResource(placeholder)
            }
        }
    }

    fun loadFromFile(
        context: Context,
        file: File?,
        @DrawableRes placeholder: Int,
        view: ImageView
    ) {
        println("IN FILE FILE")
        if (file != null) {
            val signature = StringSignature(file.lastModified().toString())
            //Glide.with(context)
            Glide.with(context.applicationContext)
                .load(Uri.fromFile(file))
                .apply(
                    RequestOptions()
                        .placeholder(placeholder)
                )
                .apply(RequestOptions().signature(signature))
                .into(view)
        } else {
            view.setImageResource(placeholder)
        }
    }



    fun loadImage(context: Context, path: String?, @DrawableRes placeholder: Int, view: ImageView) {
        println("IN FILE LOAD IMAGE")
        val file = File(path)
        if (file != null && file.exists()) {
            val signature = StringSignature(file.lastModified().toString())
            //Glide.with(context)
            Glide.with(context.applicationContext)
                .load(Uri.fromFile(file))
                .apply(
                    RequestOptions()
                        .placeholder(placeholder)
                )
                .apply(RequestOptions().signature(signature))
                .into(view)
        } else {
            view.setImageResource(placeholder)
        }
    }

    fun loadThumbnail(
        context: Context,
        dataManager: DataManager,
        uniqueCode: String?,
        @DrawableRes placeholder: Int,
        view: ImageView
    ) {
        println("IN FILE THUMBNAIL")
        val file = dataManager.fileHelper()!!.getThumbFile(uniqueCode)
        if (file != null) {
            val signature = StringSignature(file.lastModified().toString())
            //Glide.with(context)
            Glide.with(context.applicationContext)
                .load(Uri.fromFile(file))
                .apply(
                    RequestOptions()
                        .placeholder(placeholder)
                )
                .apply(RequestOptions().signature(signature))
                .into(view)
        } else {
            view.setImageResource(placeholder)
        }
    }

    fun loadEmoji(
        context: Context,
        dataManager: DataManager,
        uniqueCode: String?,
        @DrawableRes placeholder: Int,
        view: ImageView
    ) {
        val file = dataManager.fileHelper()!!.getEmojiFile(uniqueCode)
        if (file != null) {
            val signature = StringSignature(file.lastModified().toString())
            //Glide.with(context)
            Glide.with(context.applicationContext)
                .load(Uri.fromFile(file))
                .apply(
                    RequestOptions()
                        .placeholder(placeholder)
                )
                .apply(RequestOptions().signature(signature))
                .into(view)
        } else {
            view.setImageResource(placeholder)
        }
    }

    fun loadSticker(
        context: Context,
        dataManager: DataManager,
        uniqueCode: String?,
        @DrawableRes placeholder: Int,
        view: ImageView
    ) {
        val file = dataManager.fileHelper()!!.getStickerFile(uniqueCode)
        if (file != null) {
            val signature = StringSignature(file.lastModified().toString())
            //Glide.with(context)
            Glide.with(context.applicationContext)
                .load(Uri.fromFile(file))
                .apply(
                    RequestOptions()
                        .placeholder(placeholder)
                )
                .apply(RequestOptions().signature(signature))
                .into(view)
        } else {
            view.setImageResource(placeholder)
        }
    }

    fun loadLocation(
        context: Context,
        dataManager: DataManager,
        uniqueCode: String?,
        @DrawableRes placeholder: Int,
        view: ImageView
    ) {
        val file = dataManager.fileHelper()!!.getLoactionFile(uniqueCode)
        if (file != null) {
            val signature = StringSignature(file.lastModified().toString())
            //Glide.with(context)
            Glide.with(context.applicationContext)
                .load(Uri.fromFile(file))
                .apply(
                    RequestOptions()
                        .placeholder(placeholder)
                )
                .apply(RequestOptions().signature(signature))
                .into(view)
        } else {
            view.setImageResource(placeholder)
        }
    }

    internal class StringSignature(signature: String?) : Key {
        private val signature: String
        override fun equals(o: Any?): Boolean {
            if (this === o) {
                return true
            }
            if (o == null || javaClass != o.javaClass) {
                return false
            }
            val that = o as StringSignature
            return signature == that.signature
        }

        override fun hashCode(): Int {
            return signature.hashCode()
        }

        override fun updateDiskCacheKey(messageDigest: MessageDigest) {
            try {
                messageDigest.update(signature.toByteArray(charset(Key.STRING_CHARSET_NAME)))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        init {
            if (signature == null) {
                throw NullPointerException("Signature cannot be null!")
            }
            this.signature = signature
        }
    }
}