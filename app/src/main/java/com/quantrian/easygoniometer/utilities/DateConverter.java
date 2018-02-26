package com.quantrian.easygoniometer.utilities;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Vinnie on 2/25/2018.
 */

public class DateConverter {

    private static final DateFormat mDataFormat = new SimpleDateFormat("MMM dd YYYY 'at' hh:mm aaa", Locale.ENGLISH);

    public static String secToString(long timestamp){
        Date date = new Date();

        try{
            date.setTime(timestamp*1000);

            return mDataFormat.format(date);
        }
        catch(Exception ex){
            return "xx";
        }
    }
}
