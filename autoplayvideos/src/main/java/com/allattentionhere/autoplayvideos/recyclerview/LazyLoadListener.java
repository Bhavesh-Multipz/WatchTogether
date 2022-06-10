package com.allattentionhere.autoplayvideos.recyclerview;

/**
 * Created by jatin on 6/8/2017.
 */

public interface LazyLoadListener {
    boolean onScrollNext(int page, int totalItemsCount);
    boolean onScrollPrev(int page, int totalItemsCount);
    void onScrolling();
    void onScrollStateChage(int upDown); //0 for up - 1 for down

}
