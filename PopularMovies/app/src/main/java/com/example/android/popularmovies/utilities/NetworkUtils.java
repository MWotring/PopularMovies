package com.example.android.popularmovies.utilities;

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

    private static final String MOVIES_API_BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String MOVIE_API_BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String KEY_PARAM = "api_key";
    private static final String SORT_PARAM = "sort_by";

    /**
     * Builds the URL used to talk to the movie api using a sort of popular or highest rated.
     *
     * @param sortOrder The order that will be queried for, can be popularity.desc or vote_average.desc
     * @return The URL to use to query the movie server.
     */
    public static URL buildUrl(String sortOrder) {
        Uri builtUri = Uri.parse(MOVIES_API_BASE_URL).buildUpon()
                .appendQueryParameter(SORT_PARAM, sortOrder)
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

}
