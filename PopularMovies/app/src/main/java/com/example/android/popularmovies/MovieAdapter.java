package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.data.MovieData;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by megan.wotring on 2/1/18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private static final String TAG = MovieAdapter.class.getSimpleName();
    private ArrayList<MovieData> mMoviesData;
    private final MovieAdapterOnClickHandler mClickHandler;
    private final Context mContext;
    private Cursor mCursor;

    public interface MovieAdapterOnClickHandler {
        void onClick(String movieData);
    }

    public MovieAdapter(@NonNull Context context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutIdForGridItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForGridItem, parent, shouldAttachToParentImmediately);
        MovieViewHolder viewHolder = new MovieViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String posterPath = mCursor.getString(MainActivity.INDEX_MOVIE_POSTER);

        URL posterUrl = NetworkUtils.buildPosterUrl(posterPath);

        Context context = holder.listItemPosterView.getContext();
        Picasso.with(context)
                .load(posterUrl.toString())
                .into(holder.listItemPosterView);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView listItemPosterView;

        public MovieViewHolder(View itemView){
            super(itemView);

            listItemPosterView = (ImageView) itemView.findViewById(R.id.movie_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String movieApiId = mCursor.getString(MainActivity.INDEX_MOVIE_API_ID);

            mClickHandler.onClick(movieApiId);
        }


    }

    public void setMovieData(ArrayList<MovieData> moviesData) {
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}
