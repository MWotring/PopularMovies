package com.example.android.popularmovies.data;

/**
 * Created by megan.wotring on 2/12/18.
 */

public class MovieData {
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
}
