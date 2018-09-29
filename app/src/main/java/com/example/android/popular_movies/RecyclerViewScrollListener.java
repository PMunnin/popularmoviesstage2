package com.example.android.popular_movies;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private int mVisibleThreshold = 3;
    private int mCurrentPage;
    private int mPreviousTotalItemCount = 0;
    private boolean mLoading = true;
    private int mStartingPageIndex = 1;

    private RecyclerView.LayoutManager mLayoutManager;


    protected RecyclerViewScrollListener(GridLayoutManager layoutManager, int page) {
        this.mLayoutManager = layoutManager;
        mVisibleThreshold = mVisibleThreshold * layoutManager.getSpanCount();
        mCurrentPage = page;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {

        if (dy > 0) {
            int totalItemCount = mLayoutManager.getItemCount();
            int lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();

            if (totalItemCount < mPreviousTotalItemCount) {
                this.mCurrentPage = this.mStartingPageIndex;
                this.mPreviousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.mLoading = true;
                }
            }
            if (mLoading && (totalItemCount > mPreviousTotalItemCount)) {
                mLoading = false;
                mPreviousTotalItemCount = totalItemCount;
            }

            if (!mLoading && (lastVisibleItemPosition + mVisibleThreshold) > totalItemCount) {
                mCurrentPage++;
                onLoadMore(mCurrentPage, totalItemCount, view);
                mLoading = true;
            }
        }
    }


    public void resetState() {
        this.mCurrentPage = this.mStartingPageIndex;
        this.mPreviousTotalItemCount = 0;
        this.mLoading = true;
    }


    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

}