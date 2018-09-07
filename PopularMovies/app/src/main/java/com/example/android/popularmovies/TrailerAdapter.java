package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    private static String TAG = TrailerAdapter.class.getSimpleName();

    private ArrayList<HashMap<String, String>> mTrailerData;

    private final Context mContext;

    public TrailerAdapter(@NonNull Context context) {
        mContext = context;
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
        Log.d(TAG, "Trailer bind view holder got name " + name);
        viewHolder.mTrailerTitle.setText(name);
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    HashMap<String, String> trailerData = mTrailerData.get(adapterPosition);
                    String youtubeKey = trailerData.get(mContext.getString(R.string.key_string));
                    String site = trailerData.get(mContext.getString(R.string.site_string));
                    Log.d(TAG, "Reached onClick with site" + site);

                    if (site.equals(mContext.getString(R.string.youtube_string))) {
                        Uri youtubeUri = NetworkUtils.buildYoutubeUrl(youtubeKey);
                        Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);
                        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                            mContext.startActivity(intent);
                            Log.d(TAG, "Tried to open " + youtubeUri.toString());
                        }
                    } else {
                        Log.d(TAG, "Unable to handle non-youtube video source");
                    }
                }
            });
        }


    }

    public void setTrailerData(ArrayList<HashMap<String, String>> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }

}
