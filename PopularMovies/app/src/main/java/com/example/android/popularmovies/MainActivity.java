package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import com.example.android.popularmovies.utilities.NetworkUtils;

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
    private Context mContext;
    private Cursor mMovieDataCursor;
    private static final int ID_MOVIE_LOADER = 13;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    private int mPosition = RecyclerView.NO_POSITION;
    private String mSelection;
    private String[] mSelectionArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
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

    private void showErrorMsg() {
        mMoviesList.setVisibility(View.INVISIBLE);
        mError.setVisibility(View.VISIBLE);
    }

    private void showMovieGrid() {
        mMoviesList.setVisibility(View.VISIBLE);
        mError.setVisibility(View.INVISIBLE);
    }

    private void setCorrectErrorMessage() {
        if (MoviePreferences.getPrefSortBy(this)
                .equals(mContext.getString(R.string.favorite_string))) {
            Log.d(TAG, "Set fav error msg");
            mError.setText(R.string.fave_error_msg);
        } else {
            Log.d(TAG, "set network error msg");
            mError.setText(R.string.error_msg);
        }
    }

    @Override
    public void onClick(String movieApiId) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity
                .putExtra(mContext.getString(R.string.intent_movie_api_id_string), movieApiId);
            startActivity(intentToStartDetailActivity);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle loaderArgs) {
        Log.d(TAG, "onCreateLoader called with id " + id);
        switch (id) {
            case ID_MOVIE_LOADER:
                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;
                String prefSortBy = MoviePreferences.getPrefSortBy(this);
                if (prefSortBy.equals(mContext.getString(R.string.favorite_string))) {
                    mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE + "=?";
                    mSelectionArgs = new String[]{mContext.getString(R.string.true_string)};
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
            Log.d(TAG, "Grid should display now with count" + mMovieDataCursor.getCount());
        } else {
            Log.d(TAG, "Error should display");
            setCorrectErrorMessage();
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
        if (!prefSortBy.equals(getString(R.string.favorite_string))) {
            MovieSyncUtils.initialize(this);
        }
        getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER, null, this);
        PREFERENCES_HAVE_BEEN_UPDATED = true;
        setCorrectErrorMessage();
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
}

