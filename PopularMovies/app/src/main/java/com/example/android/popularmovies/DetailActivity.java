package com.example.android.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DetailActivity.class.getSimpleName();

    public static final String[] DETAIL_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_NAME,
            MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_MOVIE_RATING,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE
    };

    public static final int INDEX_MOVIE_NAME = 0;
    public static final int INDEX_MOVIE_SYNOPSIS = 1;
    public static final int INDEX_MOVIE_RATING = 2;
    public static final int INDEX_MOVIE_RELEASE_DATE = 3;
    public static final int INDEX_MOVIE_POSTER = 4;
    public static final int INDEX_MOVIE_FAVORITE = 5;

    private static final int ID_DETAIL_LOADER = 620;

    TextView mTitleTextView;
    ImageView mPosterView;
    TextView mSynopsisTextView;
    TextView mUserRatingTextView;
    TextView mReleaseDateTextView;
    String mTitle;
    String mSynopsis;
    String mPosterPath;
    String mRating;
    String mReleaseDate;
    String mApiId;
    String mFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleTextView = findViewById(R.id.original_title_txt);
        mPosterView = findViewById(R.id.poster_thumbnail);
        mSynopsisTextView = findViewById(R.id.movie_synopsis);
        mUserRatingTextView = findViewById(R.id.movie_rated);
        mReleaseDateTextView = findViewById(R.id.movie_released);

        Intent intentThatStartedDetails = getIntent();
        mApiId = intentThatStartedDetails.getStringExtra("MovieApiId");

        if (mApiId == null) {
            throw new NullPointerException("Movie api id cannot be null");
        } else {

            Bundle bundle = new Bundle();
            bundle.putString("MovieApiId", mApiId);
            getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, bundle, this);
        }
        setFavorite("true");
    }

    private void setFavorite(String valueToSet) {
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, valueToSet);
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_API_ID + "=?";
        String[] selectionArgs = new String[]{mApiId};
        int updatedRows;

        updatedRows = resolver.update(MovieContract.MovieEntry.CONTENT_URI,
                values,
                selection,
                selectionArgs);
        Log.d(TAG, "Rows were updated " + updatedRows);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case ID_DETAIL_LOADER:
                String apiId = bundle.getString("MovieApiId");
                String selection = MovieContract.MovieEntry.COLUMN_MOVIE_API_ID + "=?";
                String[] selectionArgs = new String[]{apiId};

                return new CursorLoader(this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        DETAIL_MOVIE_PROJECTION,
                        selection,
                        selectionArgs,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) return;

        mTitle = data.getString(INDEX_MOVIE_NAME);
        mSynopsis = data.getString(INDEX_MOVIE_SYNOPSIS);
        mRating = data.getString(INDEX_MOVIE_RATING);
        mReleaseDate = data.getString(INDEX_MOVIE_RELEASE_DATE);
        mPosterPath = data.getString(INDEX_MOVIE_POSTER);
        mFavorite = data.getString(INDEX_MOVIE_FAVORITE);

        mTitleTextView.setText(mTitle);
        mSynopsisTextView.setText("Synopsis: " + mSynopsis);
        mUserRatingTextView.setText("Average viewer rating: " + mRating);
        mReleaseDateTextView.setText("Film release date: " + mReleaseDate);

        URL posterUrl = NetworkUtils.buildPosterUrl(mPosterPath);

        Context context = mPosterView.getContext();
        Picasso.with(context)
                .load(posterUrl.toString())
                .into(mPosterView);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}