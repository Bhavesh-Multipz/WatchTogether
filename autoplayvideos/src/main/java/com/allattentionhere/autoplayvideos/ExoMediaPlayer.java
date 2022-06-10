package com.allattentionhere.autoplayvideos;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.offline.DownloadAction;
import com.google.android.exoplayer2.offline.ProgressiveDownloadAction;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.dash.offline.DashDownloadAction;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.offline.HlsDownloadAction;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.offline.SsDownloadAction;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;


public class ExoMediaPlayer {

    private static final String DOWNLOAD_ACTION_FILE = "actions";
    private static final String DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions";
    private static final int MAX_SIMULTANEOUS_DOWNLOADS = 2;
    private static final DownloadAction.Deserializer[] DOWNLOAD_DESERIALIZERS =
            new DownloadAction.Deserializer[]{
                    DashDownloadAction.DESERIALIZER,
                    HlsDownloadAction.DESERIALIZER,
                    SsDownloadAction.DESERIALIZER,
                    ProgressiveDownloadAction.DESERIALIZER
            };
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private String userAgent;
    private File downloadDirectory;
    private Cache downloadCache;
    private Context context;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    int height;
    int width;

    public ExoMediaPlayer(Context context, Cache downloadCache) {
        this.context = context;
        this.downloadCache = downloadCache;
        init(context);
    }

    private CacheDataSourceFactory buildReadOnlyCacheDataSource(
            DefaultDataSourceFactory upstreamFactory, Cache cache) {
        return new CacheDataSourceFactory(
                cache,
                upstreamFactory,
                new FileDataSourceFactory(),
                /* cacheWriteDataSinkFactory= */ null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                /* exoMediaPlayerBus= */ null);
    }

    private void init(Context context) {
        userAgent = Util.getUserAgent(context, "ExoPlayerDemo");
        mediaDataSourceFactory = buildDataSourceFactory(true);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
    }

    private File getDownloadDirectory() {
        if (downloadDirectory == null) {
            downloadDirectory = context.getExternalFilesDir(null);
            if (downloadDirectory == null) {
                downloadDirectory = context.getFilesDir();
            }
        }
        return downloadDirectory;
    }

    /**
     * Returns a {@link DataSource.Factory}.
     */
    private DataSource.Factory buildDataSourceFactory(TransferListener<? super DataSource> listener) {
        DefaultDataSourceFactory upstreamFactory =
                new DefaultDataSourceFactory(context, listener, buildHttpDataSourceFactory(listener));
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache());
    }

    /**
     * Returns a {@link HttpDataSource.Factory}.
     */
    private HttpDataSource.Factory buildHttpDataSourceFactory(
            TransferListener<? super DataSource> listener) {

        return new DefaultHttpDataSourceFactory(userAgent, listener);
    }


    /**
     * Returns whether extension renderers should be used.
     */
//    private boolean useExtensionRenderers() {
//        return "withExtensions".equals(BuildConfig.FLAVOR);
//    }

    private File downloadContentDirectory;
    private synchronized Cache getDownloadCache() {
        if (downloadCache == null) {
            downloadCache = new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor());
        }
        return downloadCache;
    }

    @SuppressWarnings("unchecked")
    private MediaSource buildMediaSource(Uri uri, @Nullable String overrideExtension) {
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(true))
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(true))
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                if (uri.toString().contains("mp4")){
                    return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
                } else {
                    return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                            .createMediaSource(uri);
                }
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    public void initPlayer(Uri uri, PlayerView playerView, boolean autoPlay, int repeatMode) {
        initPlayer(uri, null, autoPlay, repeatMode, null, null);
    }

    public void initPlayer(Uri uri, PlayerView playerView, boolean autoPlay) {
        initPlayer(uri, null, autoPlay, Player.REPEAT_MODE_OFF, null, null);
    }

    public void initPlayer(Uri uri, PlayerView playerView) {
        initPlayer(uri, null, false, Player.REPEAT_MODE_OFF, null, null);
    }

    public void initPlayer(Uri uri, ExoMediaPlayerBus exoBus) {
        initPlayer(uri, null, false, Player.REPEAT_MODE_OFF, null, exoBus);
    }

    public void initPlayer(Uri uri, PlayerView playerView, ExoMediaPlayerBus exoBus) {
        initPlayer(uri, playerView, false, Player.REPEAT_MODE_OFF, null, exoBus);
    }

    public void initPlayer(Uri uri, PlayerView playerView, boolean autoPlay, Player.EventListener listener) {
        initPlayer(uri, playerView, autoPlay, Player.REPEAT_MODE_OFF, listener, null);
    }

    public void initPlayer(Uri uri, PlayerView playerView, Player.EventListener listener, ExoMediaPlayerBus exoBus) {
        initPlayer(uri, playerView, false, Player.REPEAT_MODE_OFF, listener, exoBus);
    }

    public void initPlayer(Uri uri, PlayerView playerView, boolean startAutoPlay, ExoMediaPlayerBus exoBus) {
        initPlayer(uri, playerView, startAutoPlay, Player.REPEAT_MODE_OFF, null, exoBus);
    }

    public void initPlayer(Uri uri, PlayerView playerView, boolean startAutoPlay, int repeatMode, Player.EventListener listener) {
        initPlayer(uri, playerView, startAutoPlay, repeatMode, listener, null);
    }

    public void initPlayer(Uri uri, PlayerView playerView, boolean startAutoPlay, Player.EventListener listener, ExoMediaPlayerBus exoBus) {
        initPlayer(uri, playerView, startAutoPlay, Player.REPEAT_MODE_OFF, listener, exoBus);
    }

    private ExoMediaPlayerBus exoMediaPlayerBus;

    public void initPlayer(Uri uri, PlayerView playerView, boolean startAutoPlay, int repeatMode, Player.EventListener listener, ExoMediaPlayerBus exoBus) {
        if (player != null)
            unPlug();

        // a factory to create an AdaptiveVideoTrackSelection
        TrackSelection.Factory adaptiveTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        TrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(context),
                trackSelector,
                new DefaultLoadControl());

        if (playerView != null)
            playerView.setPlayer(player);

        if (listener != null)
            player.addListener(listener);

        if (exoBus != null)
            this.exoMediaPlayerBus = exoBus;

        player.setPlayWhenReady(startAutoPlay);
        player.setRepeatMode(repeatMode);
        player.prepare(buildMediaSource(uri, MediaSourceUtil.getExtension(uri)), true, false);
    }


    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public boolean isPlaying() {
        return (player != null && player.getPlayWhenReady());
    }

    public void release() {
        if (player != null) {
            player.release();
        }
    }

    public void stop(boolean reset) {
        if (player != null) {
            player.stop(reset);
        }
    }

    public void play(boolean play) {
        if (player != null) {
            player.setPlayWhenReady(play);
        }
    }

    public void mute() {
        if (player != null) {
            player.setVolume(0f);
        }
    }

    public void unMute() {
        if (player != null) {
            player.setVolume(1f);
        }
    }

    public void unPlug() {
        if (player != null) {
            stop(true);
        }
        if (exoMediaPlayerBus != null)
            exoMediaPlayerBus.onUnplug();
    }

    public interface ExoMediaPlayerBus {
        void onUnplug();

        void onPlug();
    }

    public void setExoMediaPlayerBus(ExoMediaPlayerBus exoMediaPlayerBus) {
        this.exoMediaPlayerBus = exoMediaPlayerBus;
    }

    public void addListener(Player.EventListener listener) {
        if (player != null) {
            player.addListener(listener);
        }
    }
}
