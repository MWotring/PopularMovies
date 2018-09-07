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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.example.android.popularmovies.loaders.ReviewLoader;
import com.example.android.popularmovies.loaders.TrailerLoader;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks {
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
    private static final int ID_TRAILER_LOADER = 812;
    private static final int ID_REVIEW_LOADER = 110;

    TextView mTitleTextView;
    ImageView mPosterView;
    TextView mSynopsisTextView;
    ImageView mFavoriteImageView;
    TextView mUserRatingTextView;
    TextView mReleaseDateTextView;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    String mTitle;
    String mSynopsis;
    String mPosterPath;
    String mRating;
    String mReleaseDate;
    String mApiId;
    Context mContext;
    String mFavorite;
    Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mContext = this;
        mTitleTextView = findViewById(R.id.original_title_txt);
        mPosterView = findViewById(R.id.poster_thumbnail);
        mSynopsisTextView = findViewById(R.id.movie_synopsis);
        mUserRatingTextView = findViewById(R.id.movie_rated);
        mReleaseDateTextView = findViewById(R.id.movie_released);
        mFavoriteImageView = findViewById(R.id.favorite_iv);

        mFavoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFavorite.equals(mContext.getString(R.string.true_string))) {
                    setFavoriteValue(mContext.getString(R.string.false_string));
                    set_favorite_image();
                } else {
                    setFavoriteValue(mContext.getString(R.string.true_string));
                    set_favorite_image();
                }
            }

        });

        RecyclerView trailersList = findViewById(R.id.rv_movie_trailers);
        RecyclerView reviewsList = findViewById(R.id.rv_movie_reviews);

        Intent intentThatStartedDetails = getIntent();
        mApiId = intentThatStartedDetails.getStringExtra(
                mContext.getString(R.string.intent_movie_api_id_string));

        if (mApiId == null) {
            throw new NullPointerException("Movie api id cannot be null");
        } else {

            Bundle bundle = new Bundle();
            bundle.putString(mContext.getString(R.string.intent_movie_api_id_string), mApiId);
            getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, bundle, this);
            getSupportLoaderManager().initLoader(ID_TRAILER_LOADER, null, this);
            getSupportLoaderManager().initLoader(ID_REVIEW_LOADER, null, this);
            LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this);
            LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
            trailersList.setLayoutManager(trailerLayoutManager);
            reviewsList.setLayoutManager(reviewLayoutManager);
            mTrailerAdapter = new TrailerAdapter(this);
            mReviewAdapter = new ReviewAdapter(this);
            trailersList.setAdapter(mTrailerAdapter);
            reviewsList.setAdapter(mReviewAdapter);
        }
    }

    private void setFavoriteValue(String valueToSet) {
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

    private void set_favorite_image() {
        if (mFavorite.equals(mContext.getString(R.string.true_string))) {
            mFavoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_24px);
        } else {
            mFavoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24px);
        }
    }

    @Override
    public Loader onCreateLoader(int loaderId, Bundle bundle) {

        switch (loaderId) {

            case ID_DETAIL_LOADER:
                String apiId = bundle.getString(
                        mContext.getString(R.string.intent_movie_api_id_string));
                String selection = MovieContract.MovieEntry.COLUMN_MOVIE_API_ID + "=?";
                String[] selectionArgs = new String[]{apiId};

                return new CursorLoader(this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        DETAIL_MOVIE_PROJECTION,
                        selection,
                        selectionArgs,
                        null);

            case ID_TRAILER_LOADER:
                return new TrailerLoader(this, mApiId);
            case ID_REVIEW_LOADER:
                return new ReviewLoader(this, mApiId);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();

        if (id == ID_DETAIL_LOADER) {
            Cursor cursorData = (Cursor) data;
            mCursor = cursorData;
            boolean cursorHasValidData = false;
            if (cursorData != null && cursorData.moveToFirst()) {
                cursorHasValidData = true;
            }
            if (!cursorHasValidData) return;

            mTitle = cursorData.getString(INDEX_MOVIE_NAME);
            mSynopsis = cursorData.getString(INDEX_MOVIE_SYNOPSIS);
            mRating = cursorData.getString(INDEX_MOVIE_RATING);
            mReleaseDate = cursorData.getString(INDEX_MOVIE_RELEASE_DATE);
            mPosterPath = cursorData.getString(INDEX_MOVIE_POSTER);
            mFavorite = cursorData.getString(INDEX_MOVIE_FAVORITE);

            mTitleTextView.setText(mTitle);
            mSynopsisTextView.setText(mSynopsis);
            mUserRatingTextView.setText(
                    mContext.getString(R.string.rating_description) + mRating);
            mReleaseDateTextView.setText(
                    mContext.getString(R.string.release_date_description) + mReleaseDate);

            URL posterUrl = NetworkUtils.buildPosterUrl(mPosterPath);

            Context context = mPosterView.getContext();
            Picasso.with(context)
                    .load(posterUrl.toString())
                    .into(mPosterView);

        } else if (id == ID_TRAILER_LOADER) {
            mTrailerAdapter.setTrailerData((ArrayList<HashMap<String, String>>) data);
        } else if (id == ID_REVIEW_LOADER) {
            mReviewAdapter.setReviewData((ArrayList<HashMap<String, String>>) data);
        }
        set_favorite_image();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        int id = loader.getId();

        switch (id) {
            case ID_DETAIL_LOADER:
                mCursor = null;
            case ID_REVIEW_LOADER:
                mReviewAdapter.setReviewData(null);
            case ID_TRAILER_LOADER:
                mTrailerAdapter.setTrailerData(null);
        }

    }
}

//https://stackoverflow.com/questions/15414206/use-different-asynctask-loaders-in-one-activity
//https://stackoverflow.com/questions/15643907/multiple-loaders-in-same-activity