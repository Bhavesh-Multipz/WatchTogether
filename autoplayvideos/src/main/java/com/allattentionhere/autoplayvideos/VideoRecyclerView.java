package com.allattentionhere.autoplayvideos;

import static com.allattentionhere.autoplayvideos.AAH_Utils.getString;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.allattentionhere.autoplayvideos.recyclerview.BasicRecyclerView;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class VideoRecyclerView extends BasicRecyclerView {

    private Activity context;
    private boolean playOnlyFirstVideo = false;
    private boolean downloadVideos = false;
    private boolean checkForMp4 = true;
    private float visiblePercent = 100.0f;
    private String downloadPath = Environment.getExternalStorageDirectory() + "/Video";
    private static boolean isMute = true;
    private boolean isForeground = true;

    public VideoRecyclerView(Context context) {
        super(context);
    }

    public VideoRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public VideoRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public void setActivity(Activity context) {
        this.context = context;
    }

    public void onScroll() {
        try {
            if (!isForeground() || isProgress)
                return;
            isProgress = true;
            //onScrollHandler.removeCallbacks(onScrollRunnable);
            onScrollHandler.post(onScrollRunnable);
        } catch (Exception e) {
            e.printStackTrace();
            isProgress = false;
        }
    }

    private Runnable onScrollRunnable = new Runnable() {
        @Override
        public void run() {
            playAvailableVideos(0);

        }
    };
    private Handler onScrollHandler = new Handler();

    private boolean isProgress = false;

    public void playAvailableVideos(int newState) {

        if (newState == 0) {

            int firstVisiblePosition = ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
            int lastVisiblePosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

            if (firstVisiblePosition >= 0) {
                Rect rect_parent = new Rect();
                getGlobalVisibleRect(rect_parent);
                if (playOnlyFirstVideo) {
                    boolean foundFirstVideo = false;
                    for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                        final RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(i);
                        try {
                            ExoViewHolder cvh = (ExoViewHolder) holder;
                            if (i >= 0 && cvh != null && cvh.getVideoUrl() != null && !cvh.getVideoUrl().isEmpty() && !cvh.getVideoUrl().equalsIgnoreCase("null") && (cvh.getVideoUrl().endsWith(".mp4") || !checkForMp4)) {
                                int[] location = new int[2];
                                cvh.getExoVideoImage().getLocationOnScreen(location);
                                Rect rect_child = new Rect(location[0], location[1], location[0] + cvh.getExoVideoImage().getWidth(), location[1] + cvh.getExoVideoImage().getHeight());
                                float rect_parent_area = (rect_child.right - rect_child.left) * (rect_child.bottom - rect_child.top);
                                float x_overlap = Math.max(0, Math.min(rect_child.right, rect_parent.right) - Math.max(rect_child.left, rect_parent.left));
                                float y_overlap = Math.max(0, Math.min(rect_child.bottom, rect_parent.bottom) - Math.max(rect_child.top, rect_parent.top));
                                float overlapArea = x_overlap * y_overlap;
                                float percent = (overlapArea / rect_parent_area) * 100.0f;
                                if (!foundFirstVideo && percent >= visiblePercent) {
                                    foundFirstVideo = true;
                                    if (getString(context, cvh.getVideoUrl()) != null && new File(getString(context, cvh.getVideoUrl())).exists()) {
                                        ((ExoViewHolder) holder).setVideoUrl(getString(context, cvh.getVideoUrl()));
                                    } else {
                                        ((ExoViewHolder) holder).setVideoUrl(cvh.getVideoUrl());
                                    }
                                    if (downloadVideos) {
                                        startDownloadInBackground(cvh.getVideoUrl());
                                    }
                                    ((ExoViewHolder) holder).playVideo();
                                } else {
                                    ((ExoViewHolder) holder).stopVideo();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                        final RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(i);
                        try {
                            ExoViewHolder cvh = (ExoViewHolder) holder;

                            if (i >= 0 && cvh != null && !cvh.getVideoUrl().isEmpty() && (cvh.getVideoUrl().endsWith(".mp4") || !checkForMp4)) {
                                int[] location = new int[2];
                                cvh.getExoVideoImage().getLocationOnScreen(location);
                                Rect rect_child = new Rect(location[0], location[1], location[0] + cvh.getExoVideoImage().getWidth(), location[1] + cvh.getExoVideoImage().getHeight());
                                float rect_parent_area = (rect_child.right - rect_child.left) * (rect_child.bottom - rect_child.top);
                                float x_overlap = Math.max(0, Math.min(rect_child.right, rect_parent.right) - Math.max(rect_child.left, rect_parent.left));
                                float y_overlap = Math.max(0, Math.min(rect_child.bottom, rect_parent.bottom) - Math.max(rect_child.top, rect_parent.top));
                                float overlapArea = x_overlap * y_overlap;
                                float percent = (overlapArea / rect_parent_area) * 100.0f;
                                if (percent >= visiblePercent) {
                                    if (getString(context, cvh.getVideoUrl()) != null && new File(getString(context, cvh.getVideoUrl())).exists()) {
                                        ((ExoViewHolder) holder).setVideoUrl(getString(context, cvh.getVideoUrl()));
                                    } else {
                                        ((ExoViewHolder) holder).setVideoUrl(cvh.getVideoUrl());
                                    }
                                    if (downloadVideos) {
                                        startDownloadInBackground(cvh.getVideoUrl());
                                    }
                                    ((ExoViewHolder) holder).playVideo();
                                } else {
                                    ((ExoViewHolder) holder).stopVideo();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }
        isProgress = false;
    }

    public void setPlayOnlyFirstVideo(boolean playOnlyFirstVideo) {
        this.playOnlyFirstVideo = playOnlyFirstVideo;
    }

    @Override
    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
        return super.getGlobalVisibleRect(r, globalOffset);
    }

    public void startDownloadInBackground(String url) {
        if (!AAH_Utils.isConnected(context)) return;
        /* Starting Download Service */
        if ((AAH_Utils.getString(context, url) == null || !(new File(getString(context, url)).exists())) && url != null && !url.equalsIgnoreCase("null")) {
            Intent intent = new Intent(Intent.ACTION_SYNC, null, context, AAH_DownloadService.class);
            /* Send optional extras to Download IntentService */
            intent.putExtra("url", url);
            intent.putExtra("path", downloadPath);
            intent.putExtra("requestId", 101);
            context.startService(intent);
        }
    }

    public void setDownloadVideos(boolean downloadVideos) {
        this.downloadVideos = downloadVideos;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public void preDownload(List<String> urls) {
        if (!AAH_Utils.isConnected(context)) return;
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(urls);
        urls.clear();
        urls.addAll(hashSet);
        for (int i = 0; i < urls.size(); i++) {
            if ((AAH_Utils.getString(context, urls.get(i)) == null || !(new File(getString(context, urls.get(i))).exists())) && urls.get(i) != null && !urls.get(i).equalsIgnoreCase("null")) {
                Intent intent = new Intent(Intent.ACTION_SYNC, null, context, AAH_DownloadService.class);
                intent.putExtra("url", urls.get(i));
                intent.putExtra("path", downloadPath);
                intent.putExtra("requestId", 101);
                context.startService(intent);
            }
        }
    }

    public void setCheckForMp4(boolean checkForMp4) {
        this.checkForMp4 = checkForMp4;
    }

    public void stopVideos() {
        final HandlerThread handlerThread = new HandlerThread("stopVideos", android.os.Process.THREAD_PRIORITY_BACKGROUND + android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int childCount = getChildCount(), i = 0; i < childCount; ++i) {
                        final ViewHolder holder = getChildViewHolder(getChildAt(i));
                        if (holder instanceof ExoViewHolder) {
                            final ExoViewHolder cvh = (ExoViewHolder) holder;
                            if (cvh != null && cvh.getVideoUrl() != null && !cvh.getVideoUrl().equalsIgnoreCase("null") && !cvh.getVideoUrl().isEmpty() && (cvh.getVideoUrl().endsWith(".mp4") || !checkForMp4)) {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (cvh != null)
                                            cvh.stopVideo();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    handlerThread.quit();
                }
            }
        });
    }

    public void pauseVideos() {
        final HandlerThread handlerThread = new HandlerThread("pauseVideos", android.os.Process.THREAD_PRIORITY_BACKGROUND + android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int childCount = getChildCount(), i = 0; i < childCount; ++i) {
                        final ViewHolder holder = getChildViewHolder(getChildAt(i));
                        if (holder instanceof ExoViewHolder) {
                            final ExoViewHolder cvh = (ExoViewHolder) holder;
                            if (cvh != null && cvh.getVideoUrl() != null && !cvh.getVideoUrl().equalsIgnoreCase("null") && !cvh.getVideoUrl().isEmpty() && (cvh.getVideoUrl().endsWith(".mp4") || !checkForMp4)) {
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        cvh.pauseVideo();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    handlerThread.quit();
                }
            }
        });
    }

    public void setVisiblePercent(float visiblePercent) {
        this.visiblePercent = visiblePercent;
    }

    public static boolean isMute() {
        return isMute;
    }

    public static void setMute(boolean mute) {
        isMute = mute;
    }

    public boolean isForeground() {
        return isForeground;
    }

    public void setForeground(boolean foreground) {
        isForeground = foreground;
    }
}
