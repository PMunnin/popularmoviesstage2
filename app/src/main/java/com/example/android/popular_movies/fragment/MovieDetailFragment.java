package com.example.android.popular_movies.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popular_movies.MovieDbJson;
import com.example.android.popular_movies.R;
import com.example.android.popular_movies.SpannableUtilities;
import com.example.android.popular_movies.Utilities;
import com.example.android.popular_movies.adapter.ReviewsAdapter;
import com.example.android.popular_movies.adapter.VideosAdapter;
import com.example.android.popular_movies.data.FavouriteMoviesContract;
import com.example.android.popular_movies.model.Movie;
import com.example.android.popular_movies.model.Review;
import com.example.android.popular_movies.model.Video;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MovieDetailFragment extends Fragment {

    private Context mContext;
    private Movie mMovie;
    private VideosAdapter mVideosAdapter;
    private ReviewsAdapter mReviewsAdapter;

    @BindView(R.id.iv_movie_detail_backdrop) ImageView mMovieBackdropImageView;
    @BindView(R.id.pb_movie_detail_poster) ProgressBar mMoviePosterProgressBar;
    @BindView(R.id.tv_movie_detail_vote_average) TextView mMovieVoteAverageTextView;
    @BindView(R.id.tv_movie_detail_release_date) TextView mMovieReleaseDateTextView;
    @BindView(R.id.tv_movie_detail_overview) TextView mMovieOverviewTextView;
    @BindView(R.id.tv_movie_detail_poster_error) TextView mMoviePosterErrorTextView;

    @BindView(R.id.rv_videos) RecyclerView mVideosRecyclerView;
    @BindView(R.id.rv_reviews) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.iv_movie_favourite) ImageView mMovieFavouriteImageView;

    @BindString(R.string.movie_detail_vote_avg) String mDetailVoteAvgLabel;
    @BindString(R.string.movie_detail_release) String mDetailReleaseDateLabel;
    @BindString(R.string.movie_detail_overview) String mDetailOverviewLabel;

    @BindString(R.string.movie_unfavourited_toast) String mFavOffToastMsg;
    @BindString(R.string.movie_favourited_toast) String mFavOnToastMsg;

    public static final String PARCELABLE_MOVIE_KEY = "movieObject";
    private static final String BUNDLE_VIDEOS_KEY = "videoList";
    private static final String BUNDLE_REVIEWS_KEY = "reviewList";

    private static final String DETAIL_ELEMENT_VIDEOS = "videos";
    private static final String DETAIL_ELEMENT_REVIEWS = "reviews";

    public MovieDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();
        mMovie = null;
        if (getArguments().containsKey(PARCELABLE_MOVIE_KEY)) {
            mMovie = getArguments().getParcelable(PARCELABLE_MOVIE_KEY);
        }

        if (null != mMovie) {
            View rootView = inflater.inflate(R.layout.details_movies, container, false);
            ButterKnife.bind(this, rootView);
            getActivity().setTitle(mMovie.getOriginalTitle());

            if (checkFavourite(mMovie.getId())) {
                mMovieFavouriteImageView.setBackgroundResource(R.drawable.ic_star);
            }

            Picasso.with(mContext)
                    .load(mMovie.buildBackdropPath(mContext))
                    .into(mMovieBackdropImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            mMoviePosterProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            mMoviePosterProgressBar.setVisibility(View.GONE);
                            mMoviePosterErrorTextView.setRotation(-20);
                            mMoviePosterErrorTextView.setVisibility(View.VISIBLE);
                        }
                    });

            mMovieVoteAverageTextView.append(SpannableUtilities.makeBold(mDetailVoteAvgLabel));
            mMovieVoteAverageTextView.append(mMovie.getVoteAverage());
            mMovieReleaseDateTextView.append(SpannableUtilities.makeBold(mDetailReleaseDateLabel));
            mMovieReleaseDateTextView.append(mMovie.getReleaseDate());
            mMovieOverviewTextView.append(SpannableUtilities.makeBold(mDetailOverviewLabel));
            mMovieOverviewTextView.append(mMovie.getOverview());

            LinearLayoutManager videosLinearLayoutManager = new LinearLayoutManager(mContext);
            mVideosRecyclerView.setLayoutManager(videosLinearLayoutManager);

            mVideosRecyclerView.setHasFixedSize(true);
            mVideosAdapter = new VideosAdapter();
            mVideosRecyclerView.setAdapter(mVideosAdapter);

            LinearLayoutManager reviewsLinearLayoutManager = new LinearLayoutManager(mContext);
            mReviewsRecyclerView.setLayoutManager(reviewsLinearLayoutManager);

            mReviewsRecyclerView.setHasFixedSize(true);
            mReviewsAdapter = new ReviewsAdapter();
            mReviewsRecyclerView.setAdapter(mReviewsAdapter);

            if (null != savedInstanceState) {
                ArrayList<Video> videoList = savedInstanceState.getParcelableArrayList(BUNDLE_VIDEOS_KEY);
                mVideosAdapter.setVideosData(videoList);
                ArrayList<Review> reviewList = savedInstanceState.getParcelableArrayList(BUNDLE_REVIEWS_KEY);
                mReviewsAdapter.setReviewsData(reviewList);
            } else {
                loadElements(DETAIL_ELEMENT_VIDEOS, mMovie.getId());
                loadElements(DETAIL_ELEMENT_REVIEWS, mMovie.getId());
            }
            return rootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        List<Video> videosList = mVideosAdapter.getVideosData();
        if (null != videosList) {
            ArrayList<Video> videoArrayList = new ArrayList<>(videosList);
            outState.putParcelableArrayList(BUNDLE_VIDEOS_KEY, videoArrayList);
        }

        List<Review> reviewsList = mReviewsAdapter.getReviewsData();
        if (null != reviewsList) {
            ArrayList<Review> reviewArrayList = new ArrayList<>(reviewsList);
            outState.putParcelableArrayList(BUNDLE_REVIEWS_KEY, reviewArrayList);
        }
    }

    public void loadElements(String element, int movieId) {
        if (Utilities.isOnline(mContext)) {
            String method;
            switch (element) {
                case DETAIL_ELEMENT_VIDEOS:
                    method = Utilities.getMoviedbMethodVideos(movieId);
                    String[] videos = new String[]{method};
                    new FetchVideosTask().execute(videos);
                    break;
                case DETAIL_ELEMENT_REVIEWS:
                    method = Utilities.getMoviedbMethodReviews(movieId);
                    String[] reviews = new String[]{method};
                    new FetchReviewsTask().execute(reviews);
                    break;
            }
        }
    }

    private boolean checkFavourite(int movieId) {
        boolean favourite = false;
        String[] selectionArgs = {String.valueOf(movieId)};
        Uri uri = FavouriteMoviesContract.FavouriteMovieEntry.buildFavouriteUriWithMovieId(movieId);
        Cursor cursor = getActivity().getContentResolver().query(uri,
                null,
                FavouriteMoviesContract.FavouriteMovieEntry.COLUMN_MOVIE_ID + "=?",
                selectionArgs,
                null);
        if (null != cursor && cursor.getCount() != 0) {
            favourite = true;
            cursor.close();
        }
        return favourite;
    }

    @OnClick(R.id.iv_movie_favourite)
    public void favouriteMovie() {
        if (checkFavourite(mMovie.getId())) {
            Uri removeFavouriteUri = FavouriteMoviesContract.FavouriteMovieEntry.buildFavouriteUriWithMovieId(mMovie.getId());
            getActivity().getContentResolver().delete(removeFavouriteUri, null, null);
            Toast.makeText(getActivity().getBaseContext(), mFavOffToastMsg, Toast.LENGTH_LONG).show();
            mMovieFavouriteImageView.setBackgroundResource(R.drawable.ic_star_black);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavouriteMoviesContract.FavouriteMovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
            contentValues.put(FavouriteMoviesContract.FavouriteMovieEntry.COLUMN_BACKDROP_PATH, mMovie.getBackdropPath());
            contentValues.put(FavouriteMoviesContract.FavouriteMovieEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
            contentValues.put(FavouriteMoviesContract.FavouriteMovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            contentValues.put(FavouriteMoviesContract.FavouriteMovieEntry.COLUMN_TITLE, mMovie.getOriginalTitle());
            contentValues.put(FavouriteMoviesContract.FavouriteMovieEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
            contentValues.put(FavouriteMoviesContract.FavouriteMovieEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAverage());
            Uri favouriteUri = getActivity().getContentResolver().insert(FavouriteMoviesContract.FavouriteMovieEntry.CONTENT_URI, contentValues);

            if (null != favouriteUri) {
                Toast.makeText(getActivity().getBaseContext(), mFavOnToastMsg, Toast.LENGTH_LONG).show();
                mMovieFavouriteImageView.setBackgroundResource(R.drawable.ic_star);
            }
        }
    }

    public class FetchVideosTask extends AsyncTask<String[], Void, List<Video>> {

        private final String TAG = FetchVideosTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Video> doInBackground(String[]... params) {
            String method = params[0][0];
            Map<String, String> mapping = new HashMap<>();

            mapping.put(Utilities.getMoviedbLanguageQueryParam(), MovieGridFragment.getMovieLocale());

            URL url = Utilities.buildUrl(method, mapping);

            try {
                String response = Utilities.getResponseFromHttpUrl(url);
                Log.d(TAG, response);
                JSONObject responseJson = new JSONObject(response);

                return MovieDbJson.getVideosListFromJson(responseJson);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Video> videoList) {
            if (!(videoList.isEmpty())) {
                mVideosAdapter.setVideosData(videoList);
            }
        }
    }

    public class FetchReviewsTask extends AsyncTask<String[], Void, List<Review>> {

        private final String TAG = FetchReviewsTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Review> doInBackground(String[]... params) {
            String method = params[0][0];
            Map<String, String> mapping = new HashMap<>();

            mapping.put(Utilities.getMoviedbLanguageQueryParam(), MovieGridFragment.getMovieLocale());

            URL url = Utilities.buildUrl(method, mapping);

            try {
                String response = Utilities.getResponseFromHttpUrl(url);
                Log.d(TAG, response);
                JSONObject responseJson = new JSONObject(response);

                return MovieDbJson.getReviewsListFromJson(responseJson);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Review> reviewList) {
            if (!(reviewList.isEmpty())) {
                mReviewsAdapter.setReviewsData(reviewList);
            }
        }
    }

}
