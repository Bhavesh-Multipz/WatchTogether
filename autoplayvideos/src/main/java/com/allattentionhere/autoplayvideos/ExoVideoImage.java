package com.allattentionhere.autoplayvideos;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;


public class ExoVideoImage extends FrameLayout {
    private PlayerView playerView;
    private PercentageCropImageView iv;

    private Context context;
    int height;
    int width;

    public ExoVideoImage(Context context) {
        super(context);
        this.context = context;
        inflate();
        init();
    }

    public ExoVideoImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        inflate();
        init();
    }

    public ExoVideoImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflate();
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ExoVideoImage(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        inflate();
        init();
    }

    private void inflate() {
        LayoutInflater.from(getContext()).inflate(R.layout.exo_video_image, this);
    }

    public PlayerView getPlayer() {
        return playerView;
    }

    public PercentageCropImageView getImageView() {
        return iv;
    }


    private void init() {
        this.setTag("exoVideoImage");
        playerView = findViewById(R.id.exo_player);
        iv = findViewById(R.id.ivCover);
    }
}
