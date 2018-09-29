package com.example.android.popular_movies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popular_movies.R;
import com.example.android.popular_movies.SpannableUtilities;
import com.example.android.popular_movies.model.Review;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to manage the Reviews RecyclerView
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private List<Review> mReviewList;
    @BindString(R.string.movie_detail_review_by) String mDetailReviewByLabel;

    class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_review_author) TextView mReviewAuthorTextView;
        @BindView(R.id.tv_review_content) TextView mReviewContentTextView;
        @BindView(R.id.tv_review_see_more) TextView mReviewSeeMoreTextView;
        @BindView(R.id.tv_review_collapse) TextView mReviewCollapseTextView;

        ReviewsAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.reviews;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ButterKnife.bind(this, view);
        return new ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewsAdapterViewHolder reviewsAdapterViewHolder, int position) {
        Review review = mReviewList.get(position);
        reviewsAdapterViewHolder.mReviewAuthorTextView.append(SpannableUtilities.makeBold(mDetailReviewByLabel));
        reviewsAdapterViewHolder.mReviewAuthorTextView.append(review.getAuthor());
        reviewsAdapterViewHolder.mReviewContentTextView.setText(review.getContent());
        reviewsAdapterViewHolder.mReviewSeeMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsAdapterViewHolder.mReviewContentTextView.setMaxLines(Integer.MAX_VALUE);
                reviewsAdapterViewHolder.mReviewCollapseTextView.setVisibility(View.VISIBLE);
                reviewsAdapterViewHolder.mReviewSeeMoreTextView.setVisibility(View.INVISIBLE);
            }
        });
        reviewsAdapterViewHolder.mReviewCollapseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewsAdapterViewHolder.mReviewContentTextView.setMaxLines(4);
                reviewsAdapterViewHolder.mReviewCollapseTextView.setVisibility(View.INVISIBLE);
                reviewsAdapterViewHolder.mReviewSeeMoreTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mReviewList) return 0;
        return mReviewList.size();
    }


    public void setReviewsData(List<Review> reviewList) {
        if (null == mReviewList) {
            mReviewList = reviewList;
        } else {
            mReviewList.addAll(reviewList);
        }
        notifyDataSetChanged();
    }

    public List<Review> getReviewsData() {
        return mReviewList;
    }

}
