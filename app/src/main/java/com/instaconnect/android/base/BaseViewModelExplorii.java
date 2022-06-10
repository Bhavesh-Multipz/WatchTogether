package com.instaconnect.android.base;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModel;

import com.instaconnect.android.utils.dialog_helper.SchedulerProvider;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseViewModelExplorii<N> extends ViewModel {

    private N bridge;
    private final ObservableBoolean mIsLoading = new ObservableBoolean(false);
    private CompositeDisposable mCompositeDisposable;
    private final SchedulerProvider mSchedulerProvider;

    public BaseViewModelExplorii(SchedulerProvider schedulerProvider) {
        this.mSchedulerProvider = schedulerProvider;
    }

    public void setBridge(N bridge) {
        this.bridge = bridge;
    }

    public N getBridge() {
        return bridge;
    }

    public void onViewCreated() {
        this.mCompositeDisposable = new CompositeDisposable();
    }

    public void onDestroyView() {
        mCompositeDisposable.dispose();
        //RxBus.getInstance().unregister(this);
    }

    public SchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public ObservableBoolean getIsLoading() {
        return mIsLoading;
    }

    public void setIsLoading(boolean isLoading) {
        mIsLoading.set(isLoading);
    }

}