package com.example.android.popular_movies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public final class Utilities {

    // TODO: Substitute actual key before submit
    private static final String MOVIEDB_API_KEY = "c59908e2f58d39bbdf816d3151a80219";
    private static final String MOVIEDB_API_KEY_QUERY_PARAM = "api_key";
    private static final String MOVIEDB_LANGUAGE_QUERY_PARAM = "language";
    private static final String MOVIEDB_PAGE_QUERY_PARAM = "page";
    private static final String MOVIEDB_API_URL = "https://api.themoviedb.org/3";
    private static final String MOVIEDB_METHOD_POPULAR = "/movie/popular";
    private static final String MOVIEDB_METHOD_RATED = "/movie/top_rated";
    private static final String MOVIEDB_METHOD_VIDEOS = "/movie/#/videos";
    private static final String MOVIEDB_METHOD_REVIEWS = "/movie/#/reviews";

    private static final String TAG = Utilities.class.getSimpleName();

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

     public static URL buildUrl(String method, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(MOVIEDB_API_URL + method).buildUpon();
        Log.v(TAG, "Parse url '" + MOVIEDB_API_URL + "' with method '" + method + "'");
        builder.appendQueryParameter(MOVIEDB_API_KEY_QUERY_PARAM, MOVIEDB_API_KEY);
        Log.v(TAG, "Append api key");
        for (Map.Entry<String, String> param : params.entrySet()) {
            builder.appendQueryParameter(param.getKey(), param.getValue());
            Log.v(TAG, "Append param '" + param.getKey() + "' with value '" + param.getValue() + "'");
        }

        Uri uri = builder.build();
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getMoviedbLanguageQueryParam() { return MOVIEDB_LANGUAGE_QUERY_PARAM; }

    public static String getMoviedbPageQueryParam() {
        return MOVIEDB_PAGE_QUERY_PARAM;
    }

    public static String getMoviedbMethodPopular() {
        return MOVIEDB_METHOD_POPULAR;
    }

    public static String getMoviedbMethodRated() {
        return MOVIEDB_METHOD_RATED;
    }

    public static String getMoviedbMethodVideos(int movieId) {
        return MOVIEDB_METHOD_VIDEOS.replace("#", String.valueOf(movieId));
    }

    public static String getMoviedbMethodReviews(int movieId) {
        return MOVIEDB_METHOD_REVIEWS.replace("#", String.valueOf(movieId));
    }
}
