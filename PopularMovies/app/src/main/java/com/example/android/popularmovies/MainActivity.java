package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MoviePreferences;
import com.example.android.popularmovies.sync.MovieSyncUtils;
import com.example.android.popularmovies.utilities.MovieDBJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_API_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
    };

    public static final int INDEX_MOVIE_API_ID = 0;
    public static final int INDEX_MOVIE_POSTER = 1;


    private RecyclerView mMoviesList;
    private MovieAdapter mMovieAdapter;
    private TextView mError;
    private Cursor mMovieDataCursor;
    private static final int ID_MOVIE_LOADER = 13;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    private int mPosition = RecyclerView.NO_POSITION;
    private String mSelection;
    private String[] mSelectionArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_grid);

        mError = findViewById(R.id.network_error_message);

        mMoviesList = findViewById(R.id.recyclerview_movies);
        int numberOfColumns = 3;
        mMoviesList.setHasFixedSize(true);
        mMoviesList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        mMovieAdapter = new MovieAdapter(this, this);
        mMoviesList.setAdapter(mMovieAdapter);

        int loaderId = ID_MOVIE_LOADER;

        LoaderManager.LoaderCallbacks<Cursor> callbacks = MainActivity.this;

        Bundle bundleForLoader = null;

        getSupportLoaderManager().restartLoader(loaderId, bundleForLoader, callbacks);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        MovieSyncUtils.initialize(this);
    }

    //May not need this method any longer
    private void loadMovieData(String orderBy) {
        showMovieGrid();
        Context context = this;
        boolean network = NetworkUtils.isNetworkConnectionPresent(context);
        Log.d(TAG, "load movieData method, orderby " + orderBy);

        if (orderBy.equals(getString(R.string.order_by_favorite_value))) {
            new MovieAsyncTaskLoader(this).loadInBackground();
            Log.d(TAG, "loadMovieData for favorites");
        }
        else if(network) {
            Log.d(TAG, "NOT going to call api from loadMovieData");
           // new MovieQueryAsyncTask(context).execute(orderBy);
        } else {
           // showErrorMsg();
            new MovieAsyncTaskLoader(this).loadInBackground();
            Log.v(TAG, "Network check has failed. Loading from DB");
        }
    }

        private void showErrorMsg() {
            mMoviesList.setVisibility(View.INVISIBLE);
            mError.setVisibility(View.VISIBLE);
        }

        private void showMovieGrid() {
            mMoviesList.setVisibility(View.VISIBLE);
            mError.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(String movieApiId) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity
                .putExtra("MovieApiId", movieApiId);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle loaderArgs) {
        Log.d(TAG, "onCreateLoader called with id " + id);
        switch (id) {
            case ID_MOVIE_LOADER:
                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;
                String prefSortBy = MoviePreferences.getPrefSortBy(this);
                if (prefSortBy.equals("favorite")) {
                    mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE + "=?";
                    mSelectionArgs = new String[]{"true"};
                } else {
                    mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_SORT + "=?";
                    mSelectionArgs = new String[]{prefSortBy};
                }
                return new CursorLoader(this,
                        movieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        mSelection,
                        mSelectionArgs,
                        null
                        );
                default:
                    throw new RuntimeException("Loader not implemented: " + id);
        }
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor movieData) {
        Log.d(TAG, "onLoadFinished called");
        mMovieDataCursor = movieData;
        mMovieAdapter.swapCursor(movieData);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mMoviesList.smoothScrollToPosition(mPosition);
        if (mMovieDataCursor.getCount() != 0) {
            showMovieGrid();
        } else {
            showErrorMsg();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mMovieAdapter != null) {
            mMovieAdapter.swapCursor(null);
            Log.d(TAG, "onLoaderReset valid adapter");
        } else {
            Log.d(TAG, "OnLoaderRest: adapter is null");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        String prefSortBy = MoviePreferences.getPrefSortBy(this);
        if (!prefSortBy.equals(getString(R.string.order_by_favorite_value))) {
            MovieSyncUtils.initialize(this);
        }
        getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, this);
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d(TAG, "Preferences have been updated");
            getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private static class MovieAsyncTaskLoader extends AsyncTaskLoader<Cursor>  {

        Cursor mMovieData;

        public MovieAsyncTaskLoader(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
        Log.d(TAG, "AsyncTaskLoader onStartLoading");
            if (mMovieData != null) {
                deliverResult(mMovieData);
            } else {
                forceLoad();
            }
        }

        @Override
        public Cursor loadInBackground() {
            ContentResolver resolver = getContext().getContentResolver();
            Log.d(TAG, "loadInBack in AsyncTaskLoader " );

                try {
                    Cursor cursor = resolver.query(MovieContract.MovieEntry.CONTENT_URI,
                            MAIN_MOVIE_PROJECTION, null, null, null,null);
                    return cursor;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
        }

        @Override
        public void deliverResult(Cursor cursor) {
            mMovieData = cursor;
            super.deliverResult(cursor);
        }

    }

}

