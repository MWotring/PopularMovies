package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 4;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIES_TABLE =
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_API_ID + " INTEGER NOT NULL UNIQUE, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_NAME + " STRING NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS + " STRING NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " STRING NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_POSTER + " STRING NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_SORT + " STRING NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE + " STRING NOT NULL" + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
