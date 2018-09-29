package com.example.android.popular_movies.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popular_movies.R;

public class Movie implements Parcelable {
    private int id;
    private String backdropPath;
    private String posterPath;
    private String overview;
    private String originalTitle;
    private String releaseDate;
    private String voteAverage;

    private static final String MOVIEDB_POSTER_IMG_URL = "http://image.tmdb.org/t/p/";


    public Movie(int id, String backdropPath, String posterPath, String overview, String originalTitle,
                 String releaseDate, String voteAverage) {
        this.id = id;
        this.backdropPath = backdropPath;
        this.posterPath = posterPath;
        this.overview = overview;
        this.originalTitle = originalTitle;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    private Movie(Parcel parcel) {
        id = parcel.readInt();
        backdropPath = parcel.readString();
        posterPath = parcel.readString();
        overview = parcel.readString();
        originalTitle = parcel.readString();
        releaseDate = parcel.readString();
        voteAverage = parcel.readString();
    }

    public String buildBackdropPath(Context context) {
        String backdropWidth = context.getResources().getString(R.string.backdrop_size);
        return MOVIEDB_POSTER_IMG_URL + backdropWidth + getBackdropPath();
    }


    public String buildPosterPath(Context context) {
        String posterWidth = context.getResources().getString(R.string.poster_size);
        return MOVIEDB_POSTER_IMG_URL + posterWidth + getPosterPath();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(backdropPath);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(originalTitle);
        parcel.writeString(releaseDate);
        parcel.writeString(voteAverage);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }

    };


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }
}
