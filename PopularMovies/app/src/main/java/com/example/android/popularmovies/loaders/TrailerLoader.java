package com.example.android.popularmovies.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.DetailActivity;
import com.example.android.popularmovies.utilities.MovieDBJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class TrailerLoader extends AsyncTaskLoader<ArrayList<HashMap<String, String>>> {
    private static final String TAG = DetailActivity.class.getSimpleName();
    private ArrayList<HashMap<String, String>> mTrailerData;
    private String mApiId;

    public TrailerLoader(Context context, String movieApiId) {
        super(context);
        this.mApiId = movieApiId;
    }

    @Override
    protected void onStartLoading() {
        if (mTrailerData == null) {
            forceLoad();
        }
    }

    @Override
    public ArrayList<HashMap<String, String>> loadInBackground() {
        String jsonMoviesResponse;
        ArrayList<HashMap<String, String>> movieDataArrayList;

        try {
            URL movieTrailerRequestUrl = NetworkUtils.buildDetailURL(mApiId, "videos");
            Log.d(TAG, "URL for trailers: " + movieTrailerRequestUrl);
            jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(movieTrailerRequestUrl);
            Log.d(TAG, "API resp trailer: " + jsonMoviesResponse);
            movieDataArrayList = MovieDBJsonUtils.getTrailerDataFromJson(getContext(), jsonMoviesResponse);
            return movieDataArrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deliverResult(ArrayList<HashMap<String, String>> deliveredValues) {
        mTrailerData = deliveredValues;
        Log.d(TAG, "trailer values delivered");
        if(deliveredValues == null) {
            Log.d(TAG, "trailer values are null :((");
            return;
        }
        super.deliverResult(deliveredValues);
    }
}
