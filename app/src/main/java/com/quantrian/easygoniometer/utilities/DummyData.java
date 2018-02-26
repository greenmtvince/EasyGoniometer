package com.quantrian.easygoniometer.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.quantrian.easygoniometer.data.ReadingContract;
import com.quantrian.easygoniometer.data.ReadingDbHelper;
import com.quantrian.easygoniometer.models.Reading;

import java.util.Calendar;

/**
 * Created by Vinnie on 2/24/2018.
 */

public class DummyData {

    private Context mContext;
    private ReadingDbHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    public DummyData(Context context){
        mContext=context;

        mDbHelper = new ReadingDbHelper(mContext);

        mDatabase = mDbHelper.getWritableDatabase();

        PopulateDb();
        mDatabase.close();
    }

    private Reading[] makeData(){
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -5);

        Reading[] outArray = new Reading[5];
        outArray[0] =new Reading(85,10,dummyDate(cal,0));
        outArray[1] =new Reading(90,8,dummyDate(cal,1));
        outArray[2] =new Reading(92,7,dummyDate(cal,1));
        outArray[3] =new Reading(90,4,dummyDate(cal,1));
        outArray[4] =new Reading(98,3,dummyDate(cal,1));

        //Log.d("WTFROFLBBQ", "buildTestData: size = " +outArray.length);

        return outArray;
    }

    private long dummyDate(Calendar cal, int addor){

        cal.add(Calendar.DATE, +addor);
        //Log.d("TIMEOUT", "dummyDate: "+ cal.getTime());
        long timeOut = cal.getTime().getTime()/1000;
        //Log.d("TIMEOUT", "dummyDate: "+timeOut);
        return timeOut;
    }

    private void PopulateDb(){
        clearTable();
        Reading[] values = makeData();
        for (Reading r : values){
            insertReading(r);
        }
    }

    private long insertReading(Reading reading){
        ContentValues cv = new ContentValues();

        cv.put(ReadingContract.ReadingEntry.COLUMN_FLEXION,reading.flexion);
        cv.put(ReadingContract.ReadingEntry.COLUMN_EXTENSION,reading.extension);
        cv.put(ReadingContract.ReadingEntry.COLUMN_DATE,reading.date);



        return mDatabase.insert(ReadingContract.ReadingEntry.TABLE_NAME, null,cv);
    }

    private void clearTable(){
        mDatabase.delete(ReadingContract.ReadingEntry.TABLE_NAME,null,null);
    }

    public long getTableSize(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, ReadingContract.ReadingEntry.TABLE_NAME);
        db.close();
        return count;
    }
}
