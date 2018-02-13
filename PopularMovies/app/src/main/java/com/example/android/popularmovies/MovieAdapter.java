package com.example.android.popularmovies;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieData;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * Created by megan.wotring on 2/1/18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();
    private MovieData[] mMoviesData;

    public MovieAdapter() {

    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForGridItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        MovieData movie = mMoviesData[position];
        Log.d(TAG, "Poster@@@@@@: " + movie.getPoster_path());

        URL posterUrl = NetworkUtils.buildPosterUrl(movie.getPoster_path());

        Context context = holder.listItemPosterView.getContext();
        Picasso.with(context).load(posterUrl.toString()).into(holder.listItemPosterView);
        //holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesData) return 0;
        return mMoviesData.length;
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView listItemPosterView;
        TextView listItemNumberView;

        public MovieViewHolder(View itemView){
            super(itemView);

            listItemPosterView = (ImageView) itemView.findViewById(R.id.movie_image);
        }

    }

    public void setMovieData(MovieData[] moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}
