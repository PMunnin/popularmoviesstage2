package com.example.android.popular_movies.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popular_movies.DetailsActivity;
import com.example.android.popular_movies.PosterCallback;
import com.example.android.popular_movies.R;
import com.example.android.popular_movies.fragment.MovieDetailFragment;
import com.example.android.popular_movies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Manage the RecyclerView
 */

public class MoviesAdapt extends RecyclerView.Adapter<MoviesAdapt.MoviesAdapterViewHolder> {

    private List<Movie> mMovieList;

    private static final String INTENT_MOVIE_KEY = "movieObject";
    private static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_BACKDROP_PATH = 1;
    private static final int INDEX_POSTER_PATH = 2;
    private static final int INDEX_OVERVIEW = 3;
    private static final int INDEX_TITLE = 4;
    private static final int INDEX_RELEASE_DATE = 5;
    private static final int INDEX_VOTE_AVERAGE = 6;

    /**
     * Inner class to represent the ViewHolder for the Adapter
     */
    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cv_popular_movie) CardView mPopularMovieCardView;
        @BindView(R.id.iv_poster) ImageView mMoviePosterImageView;
        @BindView(R.id.pb_poster) public ProgressBar mMoviePosterProgressBar;
        @BindView(R.id.tv_poster_error) public TextView mMoviePosterErrorTextView;
        @BindView(R.id.tv_title) TextView mMovieTitleTextView;
        @BindString(R.string.selected_configuration) String mSelectedConfiguration;
        @BindString(R.string.large) String mLarge;
        @BindString(R.string.large_land) String mLargeLand;
        @BindString(R.string.xlarge) String mXlarge;
        @BindString(R.string.xlarge_land) String mXlargeLand;
        Context mContext;
        FragmentManager mFragmentManager;
        private String[] mTwoPaneConfigurations;

        /**
         * Constructor to the ViewHolder class
         *
         * @param view the we are going to inflate
         */
        MoviesAdapterViewHolder(View view, FragmentManager fragmentManager) {
            super(view);
            ButterKnife.bind(this, view);
            mTwoPaneConfigurations = new String[]{
                    mLarge, mLargeLand,
                    mXlarge, mXlargeLand,
            };
            mContext = view.getContext();
            mFragmentManager = fragmentManager;
        }

        /**
         * Checks if we're using a two pane layout or not
         *
         * @param selectedConfiguration the selected configuration string
         * @return true or false
         */
        private boolean checkTwoPane(String selectedConfiguration) {
            return Arrays.asList(mTwoPaneConfigurations).contains(selectedConfiguration);
        }
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movies_card;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view, ((FragmentActivity)context).getSupportFragmentManager());
    }

    @Override
    public void onBindViewHolder(final MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
        final Movie movie = mMovieList.get(position);

        Picasso.with(moviesAdapterViewHolder.mContext)
                .load(movie.buildPosterPath(moviesAdapterViewHolder.mContext))
                .into(moviesAdapterViewHolder.mMoviePosterImageView, new PosterCallback(moviesAdapterViewHolder));
        moviesAdapterViewHolder.mMovieTitleTextView.setText(movie.getOriginalTitle());

        moviesAdapterViewHolder.mPopularMovieCardView.setTag(R.id.card_view_item, position);

        moviesAdapterViewHolder.mPopularMovieCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moviesAdapterViewHolder.checkTwoPane(moviesAdapterViewHolder.mSelectedConfiguration)) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(MovieDetailFragment.PARCELABLE_MOVIE_KEY, movie);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    moviesAdapterViewHolder.mFragmentManager.beginTransaction()
                            .replace(R.id.movie_detail_cont, fragment)
                            .commit();
                } else {
                    Intent movieDetailIntent = new Intent(view.getContext(), DetailsActivity.class);
                    int position = (int) view.getTag(R.id.card_view_item);
                    movieDetailIntent.putExtra(INTENT_MOVIE_KEY, mMovieList.get(position));
                    view.getContext().startActivity(movieDetailIntent);
                }
            }
        });

        if (position == 0 && moviesAdapterViewHolder.checkTwoPane(moviesAdapterViewHolder.mSelectedConfiguration)) {
            moviesAdapterViewHolder.mPopularMovieCardView.performClick();
        }
    }

    /**
     * This method behaves like a swapCursor equivalent, we have to put the elements in the
     * List<Movie> structure
     *
     * @param newCursor the cursor that we want to load into the adapter
     */
    public void loadCursorIntoAdapter(Cursor newCursor) {
        if (null != newCursor) {
            newCursor.moveToFirst();
            List<Movie> movieList = new ArrayList<>();
            try {
                do {
                    movieList.add(createMovieFromCursor(newCursor));
                } while (newCursor.moveToNext());
            } finally {
                newCursor.close();
            }
            mMovieList = new ArrayList<>(movieList);
        } else {
            clearMovieList();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null != mMovieList) {
            return mMovieList.size();
        } else {
            return 0;
        }
    }

    /**
     * Reset the Movie List, new search, for example
     */
    public void clearMovieList() {
        if (null != mMovieList) {
            mMovieList.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * Setter method for the Movie List Object
     *
     * @param movieList the List containing Movie Objects
     */
    public void setMoviesData(List<Movie> movieList) {
        if (null == mMovieList) {
            mMovieList = movieList;
        } else {
            mMovieList.addAll(movieList);
        }
        notifyDataSetChanged();
    }

    /**
     * Getter method for the Movie List Object
     *
     * @return the Movie List
     */
    public List<Movie> getMoviesData() {
        return mMovieList;
    }

    /**
     * This utility method creates a Movie element from a Cursor
     *
     * @param cursor the cursor containing the movie data
     * @return a Movie instance
     */
    private Movie createMovieFromCursor(Cursor cursor) {
        return new Movie(
                cursor.getInt(INDEX_MOVIE_ID),
                cursor.getString(INDEX_BACKDROP_PATH),
                cursor.getString(INDEX_POSTER_PATH),
                cursor.getString(INDEX_OVERVIEW),
                cursor.getString(INDEX_TITLE),
                cursor.getString(INDEX_RELEASE_DATE),
                cursor.getString(INDEX_VOTE_AVERAGE)
        );
    }
}