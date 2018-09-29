package com.example.android.popular_movies.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.popular_movies.R;
import com.example.android.popular_movies.model.Video;
import com.squareup.picasso.Picasso;


import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter to manage the Videos RecyclerView
 */
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder> {

    private List<Video> mVideoList;
    private Context mContext;
    @BindString(R.string.movie_detail_youtube_service) String mDetailVideoYoutubeThumb;
    @BindString(R.string.movie_detail_youtube_vendor) String mDetailVideoYoutubeVendor;
    @BindString(R.string.movie_detail_youtube_link) String mDetailVideoYoutubeVideoLink;

    class VideosAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_video_thumb) ImageView mVideoThumbImageView;
        @BindView(R.id.tv_video_name) TextView mVideoNameTextView;
        Context mContext;

        VideosAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public VideosAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        mContext = viewGroup.getContext();
        int layoutIdForListItem = R.layout.videos;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        ButterKnife.bind(this, view);
        return new VideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideosAdapterViewHolder videosAdapterViewHolder, int position) {
        final Video video = mVideoList.get(position);
        Picasso.with(videosAdapterViewHolder.mContext)
                .load(buildVideoUrl(video.getKey()))
                .into(videosAdapterViewHolder.mVideoThumbImageView);
        videosAdapterViewHolder.mVideoNameTextView.setText(video.getName());

        videosAdapterViewHolder.mVideoThumbImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mDetailVideoYoutubeVendor + video.getKey())
                );
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mDetailVideoYoutubeVideoLink + video.getKey())
                );
                try {
                    view.getContext().startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    view.getContext().startActivity(webIntent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == mVideoList) return 0;
        return mVideoList.size();
    }

    private String buildVideoUrl(String videoKey) {
        return mDetailVideoYoutubeThumb.replace("#", videoKey);
    }

    public void setVideosData(List<Video> videoList) {
        if (null == mVideoList) {
            mVideoList = videoList;
        } else {
            mVideoList.addAll(videoList);
        }
        notifyDataSetChanged();
    }

    public List<Video> getVideosData() {
        return mVideoList;
    }

}
