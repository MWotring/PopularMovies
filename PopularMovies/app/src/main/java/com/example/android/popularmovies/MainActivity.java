package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieData;
import com.example.android.popularmovies.utilities.MovieDBJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NUM_LIST_ITEMS = 20;
    private RecyclerView mMoviesList;
    private MovieAdapter mMovieAdapter;
    private TextView mError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_grid);

        mError = (TextView) findViewById(R.id.network_error_message);

        mMoviesList = (RecyclerView) findViewById(R.id.recyclerview_movies);
        int numberOfColumns = 3;
        mMoviesList.setHasFixedSize(true);
        mMoviesList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        mMovieAdapter = new MovieAdapter();
        mMoviesList.setAdapter(mMovieAdapter);
        loadMovieData("popularity.desc");

    }

    private void loadMovieData(String orderBy) {
        showMovieGrid();

        new FetchMoviesTask().execute(orderBy);
    }

    private void showErrorMsg() {
        mMoviesList.setVisibility(View.INVISIBLE);
        mError.setVisibility(View.VISIBLE);
    }

    private void showMovieGrid() {
        mMoviesList.setVisibility(View.VISIBLE);
        mError.setVisibility(View.INVISIBLE);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]> {

        @Override
        protected MovieData[] doInBackground(String... params) {
            /* if no preference default popular */
            String orderPref;
            if(params.length == 0) {
                orderPref = "popularity.desc"; /* alt vote_average.desc */
            } else {
                orderPref = params[0];
            }

            URL movieRequestUrl = NetworkUtils.buildUrl(orderPref);
            try {
                String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                Log.v(TAG, "Got response " + jsonMoviesResponse);
                MovieData[] MovieDataObjects = MovieDBJsonUtils.getMovieDataFromJson(MainActivity.this, jsonMoviesResponse);
                return MovieDataObjects;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieData[] movieData) {
            if (movieData != null){
                showMovieGrid();
                Log.v(TAG, "onPostExecute");
                mMovieAdapter.setMovieData(movieData);
            } else {
                showErrorMsg();
            }
        }
    }
}

