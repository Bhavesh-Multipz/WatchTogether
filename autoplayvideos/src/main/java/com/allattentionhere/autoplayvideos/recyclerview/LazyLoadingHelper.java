package com.allattentionhere.autoplayvideos.recyclerview;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;


public class LazyLoadingHelper extends RecyclerView.OnScrollListener {

    private String TAG = LazyLoadingHelper.class.getSimpleName();
    private int prevPage = 0;
    private int nextPage =0;
    private boolean isLoading = false;
    private boolean prevScrollEnabled = false;
    private boolean nextScrollEnabled = false;
    private LazyLoadListener lazyLoadListener;
    int dy;
    String scroll_event = "ScrollDown";

    public LazyLoadingHelper(LazyLoadListener listeners) {
        this.lazyLoadListener = listeners;
        this.prevScrollEnabled = true;
        this.nextScrollEnabled = true;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        this.dy = dy;

        int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
        int totalItemCount = recyclerView.getLayoutManager().getItemCount();

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int pastVisibleItems = linearLayoutManager.findFirstCompletelyVisibleItemPosition();

        int pastVisiblesItems=-1;
        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            int[] firstVisibleItems = manager.findFirstVisibleItemPositions(null);
            if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                pastVisiblesItems = firstVisibleItems[0];
            }
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            pastVisiblesItems = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        } else {
            pastVisiblesItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }

        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && !isLoading && nextScrollEnabled) {
            isLoading = true;
            nextPage++;
            isLoading = lazyLoadListener.onScrollNext(nextPage, totalItemCount);
            Log.d(TAG, " onScrollNext : Page= " + String.valueOf(nextPage) + " totalItemCount=" + String.valueOf(totalItemCount));
        }

        if ( pastVisiblesItems==0 && !isLoading && prevScrollEnabled) {
            isLoading = true;
            prevPage++;
            isLoading = lazyLoadListener.onScrollPrev(prevPage, totalItemCount);
            Log.d(TAG, " onScrollPrev : Page= " + String.valueOf(prevPage) + " pastVisiblesItems =" + String.valueOf(pastVisiblesItems));
        }

//        if (pastVisiblesItems  == 0) {
//            recyclerView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    lazyLoadListener.onScrollStateChage(0); // for scroll up
//                }
//            },300);
//        }

        if (pastVisibleItems == 0) {
            lazyLoadListener.onScrollStateChage(1);
        }

        lazyLoadListener.onScrolling();
    }


    // Defines the process for actually isLoading more data based on page
    // Returns true if more data is being loaded; returns false if there is no more data to load.


    @Override
    public void onScrollStateChanged(RecyclerView view, int scrollState) {
        // Don't take any action on changed

//        if (dy==0){
//            lazyLoadListener.onScrollStateChage(1);
//        }

        if(dy > 0)
        {
            if(scroll_event.equalsIgnoreCase("ScrollDown")) {
                Log.e("ScrollEvent", "ScrollUp");
                lazyLoadListener.onScrollStateChage(0); // for scroll up
                scroll_event = "ScrollUp";
            }
        }

        if(dy < 0)
        {
            if(scroll_event.equalsIgnoreCase("ScrollUp")) {
                Log.e("ScrollEvent","ScrollDown");
                scroll_event = "ScrollDown";
                lazyLoadListener.onScrollStateChage(1); // for scroll down
            }

        }
    }


    public void reset(){
        this.nextPage =0;
        this.prevPage =0;
        this.isLoading =false;
        this.prevScrollEnabled=true;
        this.nextScrollEnabled=true;
    }

    public int getPrevPage() {
        return this.prevPage;
    }

    public void setPrevPage(int page) {
        this.prevPage = page;
    }

    public int getNextPage() {
        return this.nextPage;
    }

    public void setNextPage(int page) {
        this.nextPage = page;
    }

    public boolean isPrevScrollEnabled() {
        return this.prevScrollEnabled;
    }

    public void setPrevScroll(boolean scrollingEnabled) {
        this.prevScrollEnabled = scrollingEnabled;
    }

    public boolean isNextScrollEnabled() {
        return this.nextScrollEnabled;
    }

    public void setNextScroll(boolean scrollingEnabled) {
        this.nextScrollEnabled = scrollingEnabled;
    }
    public boolean isLoading() {
        return this.isLoading;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }
}