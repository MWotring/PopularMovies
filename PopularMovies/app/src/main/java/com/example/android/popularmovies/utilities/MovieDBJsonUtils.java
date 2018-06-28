package com.example.android.popularmovies.utilities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MoviePreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by megan.wotring on 2/11/18.
 */

public final class MovieDBJsonUtils {

    private static final String TAG = MovieDBJsonUtils.class.getSimpleName();

    public static int getMovieDataFromJson(Context context, String movieJsonString)
            throws JSONException {

        /* All results are children of the "results" object */
        final String MDB_RESULTS = "results";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_ID = "id";
        final String MDB_TITLE = "original_title";
        final String MDB_OVERVIEW = "overview";
        final String MDB_VOTE_AVG = "vote_average";
        final String MDB_RELEASE_DATE = "release_date";

        String orderPref = MoviePreferences.getPrefSortBy(context);
        String not_favorite = context.getResources().getString(R.string.not_favorite_value);

        /* from String to json object */
        JSONObject moviesJsons = new JSONObject(movieJsonString);

        /* separate individual movie jsons into an array of jsons */
        JSONArray movieArray = moviesJsons.getJSONArray(MDB_RESULTS);
        Log.d(TAG, "JSON util with results " + movieArray);

        ContentValues[] movieDataContentValues = new ContentValues[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++){
            /* items to be pulled from json */
            String posterPath;
            String id;
            String title;
            String overview;
            String releaseDate;
            String userRating;

            Log.d(TAG, "Json: " + movieArray.getString(i));
            /* get a single movie's data */
            JSONObject movieJson = movieArray.getJSONObject(i);
            posterPath = movieJson.getString(MDB_POSTER_PATH);
            id = movieJson.getString(MDB_ID);
            title = movieJson.getString(MDB_TITLE);
            overview = movieJson.getString(MDB_OVERVIEW);
            releaseDate = movieJson.getString(MDB_RELEASE_DATE);
            userRating = movieJson.getString(MDB_VOTE_AVG);

            ContentValues movieData = new ContentValues();
            movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_API_ID, id);
            movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, title);
            movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, overview);
            movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, userRating);
            movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
            movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, posterPath);
            movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_SORT, orderPref);
            movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, not_favorite);

            movieDataContentValues[i] = movieData;
        }
        ContentResolver resolver = context.getContentResolver();
        int rowsAdded = resolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movieDataContentValues);
        Log.d(TAG, "Bulk insert added movies numbered to " + rowsAdded);

        return rowsAdded;
    }
}
