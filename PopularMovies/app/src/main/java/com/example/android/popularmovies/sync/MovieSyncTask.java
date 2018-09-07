package com.example.android.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MoviePreferences;
import com.example.android.popularmovies.utilities.MovieDBJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import java.net.URL;

public class MovieSyncTask {
    private static final String TAG = MovieSyncTask.class.getSimpleName();

    synchronized public static void syncMovies(Context context) {
        String jsonMoviesResponse;
        ContentValues[] movieDataContentValues;
        String prefSortBy = MoviePreferences.getPrefSortBy(context);
        if (!prefSortBy.equals(context.getString(R.string.favorite_string))) {

            try {

                URL movieRequestUrl = NetworkUtils.buildUrl(prefSortBy);

                jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);


                movieDataContentValues = MovieDBJsonUtils.getMovieDataFromJson(context, jsonMoviesResponse);

                if (movieDataContentValues != null && movieDataContentValues.length != 0) {
                    ContentResolver resolver = context.getContentResolver();

                    /* Insert new movies to db for sort pref */
                    int rowsAdded = resolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movieDataContentValues);
                    Log.d(TAG, "Bulk insert added movies numbered to " + rowsAdded);


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}