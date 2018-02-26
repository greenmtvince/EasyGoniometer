package com.quantrian.easygoniometer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vinnie on 2/15/2018.
 */

public class ReadingDbHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "readings.db";

    private static final int DATABASE_VERSION = 1;

    public ReadingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_READING_TABLE = "CREATE TABLE " +  ReadingContract.ReadingEntry.TABLE_NAME + " (" +
                ReadingContract.ReadingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReadingContract.ReadingEntry.COLUMN_EXTENSION + " INTEGER NOT NULL, " +
                ReadingContract.ReadingEntry.COLUMN_FLEXION + " INTEGER NOT NULL, " +
                ReadingContract.ReadingEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                ReadingContract.ReadingEntry.COLUMN_FLEX_MAX + " INTEGER, " +
                ReadingContract.ReadingEntry.COLUMN_FLEX_MIN + " INTEGER, " +
                ReadingContract.ReadingEntry.COLUMN_EXT_MAX + " INTEGER, " +
                ReadingContract.ReadingEntry.COLUMN_EXT_MIN + " INTEGER " +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_READING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReadingContract.ReadingEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
