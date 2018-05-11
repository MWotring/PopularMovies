package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<ArrayList<MovieData>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mMoviesList;
    private MovieAdapter mMovieAdapter;
    private TextView mError;
    private static final int MOVIE_LOADER_ID = 13;

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

        int loaderId = MOVIE_LOADER_ID;

        LoaderManager.LoaderCallbacks<ArrayList<MovieData>> callbacks = MainActivity.this;

        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callbacks);

       // loadMovieData("popular");

    }

    private void loadMovieData(String orderBy) {
        showMovieGrid();
        Context context = this;
        boolean network = NetworkUtils.isNetworkConnectionPresent(context);

        if(network == true) {
            //new FetchMoviesTask().execute(orderBy);
        } else {
            showErrorMsg();
            Log.v(TAG, "Network check has failed.");
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
        public void onClick(MovieData movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity
                .putExtra("MovieData", movie);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public Loader<ArrayList<MovieData>> onCreateLoader(int id, Bundle loaderArgs) {
        return new MovieAsyncTaskLoader(getApplicationContext());
    }



    @Override
    public void onLoadFinished(Loader<ArrayList<MovieData>> loader, ArrayList<MovieData> movieData) {
        //another possible loading indicator spot
        mMovieAdapter.setMovieData(movieData);
        if (movieData == null) {
            showErrorMsg();
        } else {
            //show movie data view
        }

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieData>> loader) {
        invalidateData();
    }

    private void invalidateData() {mMovieAdapter.setMovieData(null);}


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

    private static class MovieAsyncTaskLoader extends AsyncTaskLoader<ArrayList<MovieData>>  {

        ArrayList<MovieData> mMovieData;

        public MovieAsyncTaskLoader(Context context) {
            super(context);
        }

        @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    //adding a loading indicator is a possibiity here
                    forceLoad();
                }
            }

            @Override
            public ArrayList<MovieData> loadInBackground() {
                String orderPref = "popular";

                //preferences call needs to be made here to get movies of correct category
                //after preferences are implemented
                URL movieRequestUrl = NetworkUtils.buildUrl(orderPref);

                try {
                    String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                    ArrayList<MovieData> MovieDataStrings = MovieDBJsonUtils.getMovieDataFromJson(getContext(), jsonMoviesResponse);
                    return MovieDataStrings;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }

            public void deliverResult(ArrayList<MovieData> data) {
                mMovieData = data;
                super.deliverResult(data);
            }


    }
}

