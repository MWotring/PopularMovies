package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by megan.wotring on 2/11/18.
 */

public final class MovieDBJsonUtils {

    private static final String TAG = MovieDBJsonUtils.class.getSimpleName();

    public static String[] getPosterUrlsFromJson(Context context, String movieJsonString)
            throws JSONException {

        /* All results are children of the "results" object */
        final String MDB_RESULTS = "results";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_ID = "id";
        final String MDB_TITLE = "title";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE_DATE = "release_date";


        String[] parsedMovieData = null;
        JSONObject movieJson = new JSONObject(movieJsonString);

        JSONArray movieArray = movieJson.getJSONArray(MDB_RESULTS);
        parsedMovieData = new String[movieArray.length()];

        for (int i = 0; i< movieArray.length(); i++){
            String posterPath;
            String id;
            String title;
            String overview;
            String releaseDate;

            /* get a single movie's data */
            JSONObject movie = movieArray.getJSONObject(i);
            posterPath = movie.getString(MDB_POSTER_PATH);
            id = movie.getString(MDB_ID);
            title = movie.getString(MDB_TITLE);
            overview = movie.getString(MDB_OVERVIEW);
            releaseDate = movie.getString(MDB_RELEASE_DATE);
            Log.d(TAG, "getSimpleWeatherStringsFromJson: " + posterPath);
            parsedMovieData[i] = posterPath;
        }
        return parsedMovieData;
    }
}
