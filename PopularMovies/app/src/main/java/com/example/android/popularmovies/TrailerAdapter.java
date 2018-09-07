package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private static String TAG = TrailerAdapter.class.getSimpleName();

    private ArrayList<HashMap<String, String>> mTrailerData;

    private final Context mContext;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String youTubeKey);
    }

    public TrailerAdapter(@NonNull Context context, OnItemClickListener listener) {
        mContext = context;
        this.listener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForItem = R.layout.trailer_view_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForItem, viewGroup, shouldAttachToParentImmediately);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);

        return viewHolder;
    }

    //Displays the data
    @Override
    public void onBindViewHolder(TrailerViewHolder viewHolder, int position) {
        HashMap<String, String> trailerDataMap = mTrailerData.get(position);
        String name = trailerDataMap.get(mContext.getString(R.string.name_string));
        String youtubeKey = trailerDataMap.get(mContext.getString(R.string.key_string));
        Log.d(TAG, "Trailer bind view holder got name " + name);
        viewHolder.mTrailerTitle.setText(name);
        viewHolder.bind(listener, youtubeKey);
    }

    @Override
    public int getItemCount() {
        if (mTrailerData == null) return 0;
        return mTrailerData.size();
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        TextView mTrailerTitle;

        public TrailerViewHolder(View itemView) {
            super(itemView);

            mTrailerTitle = (TextView) itemView.findViewById(R.id.trailer_item);
        }

        public void bind(final OnItemClickListener listener, final String youTubeKey) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(youTubeKey);
                }
            });
        }
    }

    public void setTrailerData(ArrayList<HashMap<String, String>> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }

}

//https://antonioleiva.com/recyclerview-listener/