package com.example.android.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by megan.wotring on 2/12/18.
 */

public class MovieData implements Parcelable{
    private String userRating;
    private String id;
    private String title;
    private String posterPath;
    private String overview;
    private String releaseDate;

    public MovieData(String id, String title, String posterPath, String overview, String releaseDate, String userRating) {
        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.userRating = userRating;
    }

    private MovieData(Parcel in) {
        title = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();
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
        return posterPath;
    }

    public void setPosterPath(String poster_path) {
        this.posterPath = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String release_date) {
        this.releaseDate = release_date;
    }

    public String getUserRating() {
        return userRating;
    }

    public void setUserRating(String user_rating) {
        this.userRating = user_rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(posterPath);
        parcel.writeString(overview);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);
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