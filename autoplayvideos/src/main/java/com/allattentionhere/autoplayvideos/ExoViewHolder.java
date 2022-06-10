package com.allattentionhere.autoplayvideos;

import static com.allattentionhere.autoplayvideos.VideoRecyclerView.isMute;
import static com.allattentionhere.autoplayvideos.VideoRecyclerView.setMute;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;

public class ExoViewHolder extends RecyclerView.ViewHolder implements ExoMediaPlayer.ExoMediaPlayerBus, Player.EventListener {
    private ExoVideoImage exoVideoImage;
    private String imageUrl;
    private String videoUrl;
    private boolean isInit = false;
    private ExoMediaPlayer mediaPlayer;
    private Activity activity;
    private boolean isPlaybackCanceled = false;

    public ExoViewHolder(View x) {
        super(x);
        exoVideoImage = (ExoVideoImage) x.findViewWithTag("exoVideoImage");
    }

    public void playVideo() {
        if (mediaPlayer == null)
            return;
        setPlaybackCanceled(false);
        if (!isInit() || mediaPlayer.getPlayer().getPlaybackState() == Player.STATE_IDLE) {
            final Uri uri = Uri.parse(videoUrl);
            mediaPlayer.initPlayer(uri, ExoViewHolder.this.getPlayerView(), true, Player.REPEAT_MODE_OFF, ExoViewHolder.this, ExoViewHolder.this);
        } else {
            mediaPlayer.play(true);
        }
        setInit(true);
    }

    public void videoStarted() {
        if (exoVideoImage != null)
            exoVideoImage.getImageView().setVisibility(View.GONE);
    }

    public void showThumb() {
        if (exoVideoImage != null)
            exoVideoImage.getImageView().setVisibility(View.VISIBLE);
    }

    public void initVideoView(Activity _act, ExoMediaPlayer exoMediaPlayer, MediaListener listener) {
        this.listener = listener;
        this.mediaPlayer = exoMediaPlayer;
        this.activity = _act;
//        this.isInit = false;
        setPlaybackCanceled(false);
    }

    @Override
    public void onUnplug() {
        showThumb();
    }

    @Override
    public void onPlug() {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        if (isMute())
            mediaPlayer.mute();
        else
            mediaPlayer.unMute();
    }

    private boolean isVideoStarted = false;

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (isMute())
            mediaPlayer.mute();
        else
            mediaPlayer.unMute();

        if (isPlaybackCanceled) {
            stopVideo();
        } else if (playbackState == Player.STATE_IDLE) {
             //showThumb();
        }else if (playbackState == Player.STATE_ENDED) {
            mediaPlayer.getPlayer().seekToDefaultPosition();
            mediaPlayer.getPlayer().setPlayWhenReady(true);
            videoRepeat();
        } else if (playbackState == Player.STATE_READY) {
            if (!isVideoStarted)
                videoStarted();
            setVideoStarted(true);
        }


//        if (playbackState == Player.STATE_ENDED) {
//            ((Activity) this.itemView.getContext()).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(activity, "video repeat... 1", Toast.LENGTH_SHORT).show();
////                    mediaPlayer.getPlayer().setRepeatMode(Player.REPEAT_MODE_ALL);
//
//                    // mediaPlayer.getPlayer().stop(true);
//                    // isPlaybackCanceled = true;
//
//
//                }
//            });
//
//        }

    }


    protected void videoRepeat(){

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    public interface MediaListener {
        boolean onError();
    }

    private MediaListener listener;

    public void setListener(MediaListener listener) {
        this.listener = listener;
    }

    public void pauseVideo() {
        try {
            if (mediaPlayer == null)
                return;
            mediaPlayer.play(false);
            setPlaybackCanceled(true);
            setVideoStarted(false);
        } catch (Exception e) {
            e.printStackTrace();
            handleError();
        }
    }

    public void stopVideo() {
        try {
            showThumb();
            if (mediaPlayer == null)
                return;
            mediaPlayer.stop(false);
            setPlaybackCanceled(true);
            setVideoStarted(false);
        } catch (Exception e) {
            e.printStackTrace();
            handleError();
        }
    }

    public boolean isVideoStarted() {
        return isVideoStarted;
    }

    public void setVideoStarted(boolean videoStarted) {
        isVideoStarted = videoStarted;
    }

    public void muteVideo() {
        setMute(true);
        if (mediaPlayer == null)
            return;
        mediaPlayer.mute();
    }

    public void unmuteVideo() {
        setMute(false);
        if (mediaPlayer == null)
            return;
        mediaPlayer.unMute();
    }

    public ExoVideoImage getExoVideoImage() {
        return exoVideoImage;
    }

    public PercentageCropImageView getImageView() {
//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) exoVideoImage.getPlayer().getLayoutParams();
//        exoVideoImage.setLayoutParams(layoutParams);
        return exoVideoImage.getImageView();
    }

    public String getImageUrl() {
        return imageUrl + "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setExoVideoImage(ExoVideoImage exoVideoImage) {
        this.exoVideoImage = exoVideoImage;
    }

    public String getVideoUrl() {
        return videoUrl + "";
    }

    public boolean isPlaying() {
        return (mediaPlayer != null && mediaPlayer.isPlaying());
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public PlayerView getPlayerView() {
        return this.exoVideoImage.getPlayer();
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public boolean isPlaybackCanceled() {
        return isPlaybackCanceled;
    }

    public void setPlaybackCanceled(boolean playbackCanceled) {
        isPlaybackCanceled = playbackCanceled;
    }

    public void handleError() {
        this.isPlaybackCanceled = false;
        setInit(false);
        setVideoStarted(false);
        if (mediaPlayer != null)
            mediaPlayer.release();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (listener != null)
                    listener.onError();
            }
        });

    }

    public void clearViews() {
        if (mediaPlayer != null)
            mediaPlayer.release();
        getPlayerView().getVideoSurfaceView().invalidate();
        getPlayerView().invalidate();
        setMediaPlayer(null);
        setInit(false);
        setVideoStarted(false);
    }

    public ExoMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(ExoMediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }
}