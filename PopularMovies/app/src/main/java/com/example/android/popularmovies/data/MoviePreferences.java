package com.example.android.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MoviePreferences {
    public static final String DEFAULT_SORT = "popular";

    static public void setPrefSortBy(Context context, String sortBy) {
        //later tater
    }

    static public void resetPrefSortBy(Context context) {
        //maybe later
    }

    public static String getPrefSortBy(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String keyForSort = "sort_by";
        String defaultSort = "popular";
        return prefs.getString(keyForSort, defaultSort);
    }

    public static String getDefaultSortBy() {
        return DEFAULT_SORT;
    }
}
