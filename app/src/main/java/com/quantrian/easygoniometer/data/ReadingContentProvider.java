package com.quantrian.easygoniometer.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Vinnie on 2/24/2018.
 */

public class ReadingContentProvider extends ContentProvider{
    public static final int READINGS = 100;
    public static final int READINGS_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ReadingContract.AUTHORITY, ReadingContract.PATH_READINGS, READINGS);
        uriMatcher.addURI(ReadingContract.AUTHORITY, ReadingContract.PATH_READINGS+"/#", READINGS_WITH_ID);
        return uriMatcher;
    }

    private ReadingDbHelper mReadingDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mReadingDbHelper = new ReadingDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mReadingDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match){
            case READINGS:
                returnCursor = db.query(ReadingContract.ReadingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " +uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(),uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mReadingDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case READINGS:
                long id = db.insert(ReadingContract.ReadingEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ReadingContract.ReadingEntry.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unkown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
