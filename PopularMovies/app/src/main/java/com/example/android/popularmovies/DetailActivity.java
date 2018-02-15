package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();
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
        Bundle extras = intentThatStartedDetails.getExtras();
        if (extras != null) {
                mTitle = extras.getString("title");
                mTitleTextView.setText(mTitle);

                mPosterPath = extras.getString("posterPath");

                mSynopsis = extras.getString("overview");
                mSynopsisTextView.setText("Plot Synopsis: " + mSynopsis);


                mRating = extras.getString("rating");
                mUserRatingTextView.setText("Average film rating: " + mRating);


                mReleaseDate = extras.getString("releaseDate");
                mReleaseDateTextView.setText("Film Release date: " + mReleaseDate);

        }
        URL posterUrl = NetworkUtils.buildPosterUrl(mPosterPath);

        Context context = mPosterView.getContext();
        Picasso.with(context)
                .load(posterUrl.toString())
                .into(mPosterView);
    }

}