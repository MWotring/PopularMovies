package com.example.android.popularmovies.utilities;

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
import java.util.Scanner;

/**
 * Created by megan.wotring on 2/11/18.
 */

public final class NetworkUtils {

    /* Remember to  take this out before pushing to github!!*/
    private static final String MOVIE_API_KEY = "";

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIE_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w342";
    private static final String MOVIES_API_BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String KEY_PARAM = "api_key";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    private static final String YOUTUBE_VIDEO_QUERY = "v";
    private static final String MOVIE_VIDEO_URL = "videos";

    /**
     * Builds the URL used to talk to the movie api using a sort of popular or highest rated.
     *
     * @param sortOrder The order that will be queried for, can be popular or top_rated
     * @return The URL to use to query the movie server.
     */
    public static URL buildUrl(String sortOrder) {
        Uri builtUri = Uri.parse(MOVIES_API_BASE_URL).buildUpon()
                .appendEncodedPath(sortOrder)
                .appendQueryParameter(KEY_PARAM, MOVIE_API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static Uri buildYoutubeUrl(String key) {
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_QUERY, key)
                .build();

        Log.v(TAG, "Built URI " + builtUri);
        return builtUri;
    }

    public static URL buildMovieUrl(String movieApiId, String endpoint) {
        URL url = null;

        if (!endpoint.equals("favorite")) {
            Uri builtUri = Uri.parse(MOVIE_IMAGE_BASE_URL).buildUpon()
                    .appendEncodedPath(movieApiId)
                    .appendEncodedPath(endpoint).build();

            try {
                url = new URL(builtUri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "Built URI " + url);
        }
        return url;
    }

    public static URL buildPosterUrl(String posterPath){
        Uri builtUri = Uri.parse(MOVIE_IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(posterPath).build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildDetailURL(String apiId, String key) {
        Uri builtUri = Uri.parse(MOVIES_API_BASE_URL).buildUpon()
                .appendEncodedPath(apiId)
                .appendEncodedPath(key)
                .appendQueryParameter(KEY_PARAM, MOVIE_API_KEY)
                .build();
        Log.d(TAG, builtUri.toString());
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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

    public static boolean isNetworkConnectionPresent(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
