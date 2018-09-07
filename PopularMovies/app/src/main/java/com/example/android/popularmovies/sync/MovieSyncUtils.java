package com.example.android.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MoviePreferences;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;


import java.util.concurrent.TimeUnit;

public class MovieSyncUtils {
    private static final String TAG = MovieSyncUtils.class.getSimpleName();

    private static final int SYNC_INTERVAL_HOURS = 24;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 24;

    private static final String MOVE_SYNC_TAG = "movie-sync";

    static void scheduleFirebaseJobDispatcherSync(@NonNull Context context) {
        Log.d(TAG, "scheduleFirebaseSync");
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);

        Job movieSyncJob = firebaseJobDispatcher.newJobBuilder()
                .setService(MovieFirebaseJobService.class)
                .setTag(MOVE_SYNC_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        firebaseJobDispatcher.schedule(movieSyncJob);

    }

    synchronized public static void initialize(@NonNull final Context context) {

        Log.d(TAG, "initialize method in MovieSyncUtils");
        final String prefSortBy = MoviePreferences.getPrefSortBy(context);
        scheduleFirebaseJobDispatcherSync(context);

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;

                String[] projectionColumns = {MovieContract.MovieEntry._ID};
                String selectionStatement = MovieContract.MovieEntry.COLUMN_MOVIE_SORT + "=?";
                String[] selectionArgs = new String[]{prefSortBy};

                Cursor cursor = context.getContentResolver().query(
                        movieQueryUri,
                        projectionColumns,
                        selectionStatement,
                        selectionArgs,
                        null);

                if (cursor == null || cursor.getCount() == 0
                        || prefSortBy.equals(context.getString(R.string.favorite_string))) {
                    startImmediateSync(context);
                }
                cursor.close();
            }
        });
        checkForEmpty.start();
    }


    public static void startImmediateSync(@NonNull final Context context) {
        Log.d(TAG, "startImmediateSync in MovieSyncUtils");
        Intent intentToSyncImmediately = new Intent(context, MovieSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
