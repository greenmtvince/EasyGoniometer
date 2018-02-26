package com.quantrian.easygoniometer.utilities;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.quantrian.easygoniometer.data.ReadingContract;
import com.quantrian.easygoniometer.models.Reading;

import java.util.ArrayList;

/**
 * Created by Vinnie on 2/24/2018.
 */

public class FetchReadings extends AsyncTask<Void, Void, Cursor> {
    private TaskCompleteListener<ArrayList<Reading>> mListener;
    private Context mContext;

    public FetchReadings(Context context, TaskCompleteListener<ArrayList<Reading>> listener){
        mContext = context;
        mListener = listener;
    }

    @Override
    protected Cursor doInBackground(Void... voids) {

        try{
            return mContext.getContentResolver().query(ReadingContract.ReadingEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    ReadingContract.ReadingEntry.COLUMN_DATE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(Cursor result){
        if (mListener!=null){
            super.onPostExecute(result);
            mListener.onTaskComplete(getAllReadings(result));
        }
    }

    private ArrayList<Reading> getAllReadings(Cursor c){
        ArrayList<Reading> readings = new ArrayList<>();

        if (c.moveToFirst()){
            while (!c.isAfterLast()){
                int extension = c.getInt(c.getColumnIndex(ReadingContract.ReadingEntry.COLUMN_EXTENSION));
                int flexion = c.getInt(c.getColumnIndex(ReadingContract.ReadingEntry.COLUMN_FLEXION));
                long date = c.getLong(c.getColumnIndex(ReadingContract.ReadingEntry.COLUMN_DATE));

                readings.add(new Reading(flexion,extension,date));
                c.moveToNext();
            }
        }
        return readings;
    }
}
