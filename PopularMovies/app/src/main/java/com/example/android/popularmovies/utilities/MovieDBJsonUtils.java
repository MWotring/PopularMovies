package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.data.MovieData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by megan.wotring on 2/11/18.
 */

public final class MovieDBJsonUtils {

    private static final String TAG = MovieDBJsonUtils.class.getSimpleName();

    public static ArrayList<MovieData> getMovieDataFromJson(Context context, String movieJsonString)
            throws JSONException {

        /* All results are children of the "results" object */
        final String MDB_RESULTS = "results";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_ID = "id";
        final String MDB_TITLE = "original_title";
        final String MDB_OVERVIEW = "overview";
        final String MDB_VOTE_AVG = "vote_average";
        final String MDB_RELEASE_DATE = "release_date";

        /* from String to json object */
        JSONObject moviesJsons = new JSONObject(movieJsonString);

        /* separate individual movie jsons into an array of jsons */
        JSONArray movieArray = moviesJsons.getJSONArray(MDB_RESULTS);

        /* An array of movie objects so we can access them as needed */
       // MovieData[] movieDataArray = new MovieData[movieArray.length()];
        ArrayList<MovieData> movieDataArrayList = new ArrayList<>();

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

            MovieData movie = new MovieData(id, title, posterPath, overview, releaseDate, userRating);
            movieDataArrayList.add(movie);
        }
        return movieDataArrayList;
    }
}
