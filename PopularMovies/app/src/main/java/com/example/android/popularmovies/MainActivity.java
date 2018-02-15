package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieData;
import com.example.android.popularmovies.utilities.MovieDBJsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
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

        mMovieAdapter = new MovieAdapter(this);
        mMoviesList.setAdapter(mMovieAdapter);
        loadMovieData("popular");

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

    @Override
    public void onClick(MovieData movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity
                .putExtra("MovieData", movie);
        startActivity(intentToStartDetailActivity);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieData[]> {

        @Override
        protected MovieData[] doInBackground(String... params) {
            /* if no preference default sort by popular */
            String orderPref;
            if(params.length == 0) {
                orderPref = "popular";
            } else {
                orderPref = params[0];
            }

            URL movieRequestUrl = NetworkUtils.buildUrl(orderPref);
            try {
                String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
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
                mMovieAdapter.setMovieData(movieData);
            } else {
                showErrorMsg();
            }
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

        if(id == R.id.movie_rating){
            loadMovieData("top_rated");
            return true;
        }
        else if (id == R.id.movie_popular) {
            loadMovieData("popular");
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}

