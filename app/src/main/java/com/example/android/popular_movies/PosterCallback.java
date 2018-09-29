package com.example.android.popular_movies;

import android.view.View;

import com.example.android.popular_movies.adapter.MoviesAdapt;
import com.squareup.picasso.Callback;

public class PosterCallback extends Callback.EmptyCallback {

    private MoviesAdapt.MoviesAdapterViewHolder mViewHolder;


    public PosterCallback(MoviesAdapt.MoviesAdapterViewHolder viewHolder) {
        this.mViewHolder = viewHolder;
    }

    @Override
    public void onSuccess() {
        mViewHolder.mMoviePosterProgressBar.setVisibility(View.GONE);
        mViewHolder.mMoviePosterErrorTextView.setVisibility(View.GONE);
    }

    @Override
    public void onError() {
        mViewHolder.mMoviePosterProgressBar.setVisibility(View.GONE);
        mViewHolder.mMoviePosterErrorTextView.setRotation(-20);
        mViewHolder.mMoviePosterErrorTextView.setVisibility(View.VISIBLE);
    }
}