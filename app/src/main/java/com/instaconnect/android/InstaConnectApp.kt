package com.instaconnect.android

import android.content.Context
import android.os.Build
import android.os.StrictMode
import androidx.core.provider.FontRequest
import com.allattentionhere.autoplayvideos.ExoMediaPlayer
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.firebase.FirebaseApp
import com.instaconnect.android.utils.SocketConnector
import io.alterac.blurkit.BlurKit
import java.io.File

class InstaConnectApp : BaseApp() {
    private val TAG = InstaConnectApp::class.java.name
    private var exoMediaPlayer: ExoMediaPlayer? = null
    private val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
    private var downloadDirectory: File? = null
        private get() {
            if (field == null) {
                field = getExternalFilesDir(null)
                if (field == null) {
                    field = filesDir
                }
            }
            return field
        }

    private var proxy: HttpProxyCacheServer? = null

    fun getProxy(context: Context): HttpProxyCacheServer? {
        var app: InstaConnectApp = context.getApplicationContext() as InstaConnectApp
        return if (app.proxy == null) app.newProxy().also { app.proxy = it } else app.proxy
    }

    private fun newProxy(): HttpProxyCacheServer? {
        return HttpProxyCacheServer.Builder(this)
            .maxCacheSize(500000000) // 500 mb for cache
            .build()
    }

    private var downloadCache: com.google.android.exoplayer2.upstream.cache.Cache? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        BlurKit.init(this)
        FirebaseApp.initializeApp(this)
        loadEmojiCompat()
        disableDeathOnFileUriExposure()
        SocketConnector.initSocket(instance)
        /*DaggerAppComponent.builder()
            .application(this)
            .build()
            .inject(this)*/
        downloadCache = getDownloadCache()
        exoMediaPlayer = ExoMediaPlayer(this, downloadCache)
    }

    @Synchronized
    fun getDownloadCache(): com.google.android.exoplayer2.upstream.cache.Cache {
        if (downloadCache == null) {
            downloadCache =
                SimpleCache(File(downloadDirectory, DOWNLOAD_CONTENT_DIRECTORY), NoOpCacheEvictor())
        }
        return downloadCache!!
    }

    private fun disableDeathOnFileUriExposure() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
                m.invoke(null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadEmojiCompat() {
        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs
        )
        val config: androidx.emoji.text.EmojiCompat.Config =
            androidx.emoji.text.FontRequestEmojiCompatConfig(this, fontRequest)
        androidx.emoji.text.EmojiCompat.init(config)
    }

    fun mediaPlayer(): ExoMediaPlayer? {
        return exoMediaPlayer
    }

    companion object {
        var instance: InstaConnectApp? = null
            private set
    }
}
