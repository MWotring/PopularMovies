package com.example.android.popularmovies.sync;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

public class MovieFirebaseJobService extends  JobService {
    private static final String TAG = MovieFirebaseJobService.class.getSimpleName();
    private AsyncTask<Void, Void, Void> mFetchMoviesTask;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d(TAG, "onStartJob in MovieFirebaseJobService");

        mFetchMoviesTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                MovieSyncTask.syncMovies(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };
        mFetchMoviesTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mFetchMoviesTask != null) {
            mFetchMoviesTask.cancel(true);
        }
        return true;
    }
}
