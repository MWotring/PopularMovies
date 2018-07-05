package com.example.android.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MoviePreferences;
import com.example.android.popularmovies.utilities.MovieDBJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MovieSyncTask {
    private static final String TAG = MovieSyncTask.class.getSimpleName();

    synchronized public static void syncMovies(Context context) {
        String jsonMoviesResponse;
        ContentValues[] movieDataContentValues;

        try {
            String prefSortBy = MoviePreferences.getPrefSortBy(context);
            URL movieRequestUrl = NetworkUtils.buildUrl(prefSortBy);

            jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);


            movieDataContentValues = MovieDBJsonUtils.getMovieDataFromJson(context, jsonMoviesResponse);

            if (movieDataContentValues != null && movieDataContentValues.length != 0) {
                ContentResolver resolver = context.getContentResolver();

                //TODO: delete old data that matches the sort preference

                /* Insert new movies to db for sort pref */
                int rowsAdded = resolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movieDataContentValues);
                 Log.d(TAG, "Bulk insert added movies numbered to " + rowsAdded);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}