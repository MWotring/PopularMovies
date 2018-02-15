package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by megan.wotring on 2/12/18.
 */

public class MovieData implements Parcelable{
    private String user_rating;
    private String id;
    private String title;
    private String poster_path;
    private String overview;
    private String release_date;

    public MovieData(String id, String title, String posterPath, String overview, String releaseDate, String userRating) {
        this.id = id;
        this.title = title;
        this.poster_path = posterPath;
        this.overview = overview;
        this.release_date = releaseDate;
        this.user_rating = userRating;
    }

    private MovieData(Parcel in) {
        title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        user_rating = in.readString();
        release_date = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public void setPosterPath(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    public String getUserRating() {
        return user_rating;
    }

    public void setUserRating(String user_rating) {
        this.user_rating = user_rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(poster_path);
        parcel.writeString(overview);
        parcel.writeString(user_rating);
        parcel.writeString(release_date);
    }

    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {

        @Override
        public MovieData createFromParcel(Parcel parcel) {
            return new MovieData(parcel);
        }

        @Override
        public MovieData[] newArray(int i) {
            return new MovieData[i];
        }

    };
}

//super helpful tutorial https://www.sitepoint.com/transfer-data-between-activities-with-android-parcelable/