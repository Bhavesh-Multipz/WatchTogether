// Generated by data binding compiler. Do not edit!
package com.allattentionhere.autoplayvideos.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.allattentionhere.autoplayvideos.PercentageCropImageView;
import com.allattentionhere.autoplayvideos.R;
import com.google.android.exoplayer2.ui.PlayerView;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class ExoVideoImageBinding extends ViewDataBinding {
  @NonNull
  public final PlayerView exoPlayer;

  @NonNull
  public final PercentageCropImageView ivCover;

  protected ExoVideoImageBinding(Object _bindingComponent, View _root, int _localFieldCount,
      PlayerView exoPlayer, PercentageCropImageView ivCover) {
    super(_bindingComponent, _root, _localFieldCount);
    this.exoPlayer = exoPlayer;
    this.ivCover = ivCover;
  }

  @NonNull
  public static ExoVideoImageBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.exo_video_image, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static ExoVideoImageBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<ExoVideoImageBinding>inflateInternal(inflater, R.layout.exo_video_image, root, attachToRoot, component);
  }

  @NonNull
  public static ExoVideoImageBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.exo_video_image, null, false, component)
   */
  @NonNull
  @Deprecated
  public static ExoVideoImageBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<ExoVideoImageBinding>inflateInternal(inflater, R.layout.exo_video_image, null, false, component);
  }

  public static ExoVideoImageBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static ExoVideoImageBinding bind(@NonNull View view, @Nullable Object component) {
    return (ExoVideoImageBinding)bind(component, view, R.layout.exo_video_image);
  }
}