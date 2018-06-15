package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class MovieProvider extends ContentProvider {
    private static final String TAG = MovieProvider.class.getSimpleName();
    public static final int CODE_MOVIES = 100;
    public static final int CODE_WITH_API_ID = 101;
    public static final int CODE_WITH_SORT = 102;
    public static final int CODE_WITH_FAVORITE = 103;

    public static final UriMatcher sUriMatcher = buildUriMatcher();
    MovieDBHelper movieDBHelper;
    private String mAPiId;
    private String mSelection;
    private String[] mSelectionArgs;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, CODE_MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/*", CODE_WITH_API_ID);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_SORTINGS + "/*", CODE_WITH_SORT);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVORITES + "/*", CODE_WITH_FAVORITE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        movieDBHelper = new MovieDBHelper(context);
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] contentValues) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsInserted = 0;
        Log.d(TAG, "bulkInsert method with match " + match);

        switch (match) {
            case CODE_MOVIES:
                db.beginTransaction();
                try {
                    for (ContentValues value : contentValues) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
                default: return super.bulkInsert(uri, contentValues);
        }

    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = movieDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        Log.d(TAG, "Query method with " + uri + " match is " + match);

        switch (match) {
            case CODE_MOVIES:
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_WITH_API_ID:
                mAPiId = uri.getPathSegments().get(1);
                mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_API_ID + "=?";
                mSelectionArgs = new String[]{mAPiId};

                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_WITH_SORT:
                String sort_pref = uri.getPathSegments().get(1);
                mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_SORT + "=?";
                mSelectionArgs = new String[]{sort_pref};

                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);

                //IS THIS RIGHT?? return all favorites of true or false as requested
            case CODE_WITH_FAVORITE:
                String fav_value = uri.getPathSegments().get(1);
                mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE + "=?";
                mSelectionArgs = new String[]{fav_value};
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);

            default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numTasksDeleted;

        Log.d(TAG, "Delete method with " + uri + " match is " + match);

        switch (match) {
            case CODE_WITH_API_ID:
                mAPiId = uri.getPathSegments().get(1);
                numTasksDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, "movie_api_id=?", new String[]{mAPiId});
                break;
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numTasksDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numTasksDeleted;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        Log.d(TAG, "Insert method with " + uri + " match is " + match);

        switch (match) {
            case CODE_WITH_API_ID:
                long id = db.insert(MovieContract.MovieEntry.TABLE_NAME,
                        null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new UnsupportedOperationException("Unknown Uri: " + uri);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnUri;
        }
        return returnUri;
    }

    //values are the columns to update, selection is the column to select on, selectionArgs are value to compare to
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        Log.d(TAG, "Update method with " + uri + " match is " + match);

        switch (match) {
            case CODE_MOVIES:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CODE_WITH_API_ID:
                mAPiId = uri.getPathSegments().get(1);
                mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_API_ID + "=?";
                mSelectionArgs = new String[]{mAPiId};

                 rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                         values,
                         mSelection,
                         mSelectionArgs);

            case CODE_WITH_FAVORITE:
                mAPiId = uri.getPathSegments().get(1);
                mSelection = MovieContract.MovieEntry.COLUMN_MOVIE_API_ID + "=?";
                mSelectionArgs = new String[]{mAPiId};

                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                        values,
                        mSelection,
                        mSelectionArgs);

             default:
                 throw new IllegalArgumentException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


}
