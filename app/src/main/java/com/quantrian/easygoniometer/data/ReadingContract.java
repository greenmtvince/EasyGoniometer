package com.quantrian.easygoniometer.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Vinnie on 2/15/2018.
 */

public class ReadingContract {
    public static final String AUTHORITY = "com.quantrian.easygoniometer";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);

    public static final String PATH_READINGS = "readings";

    public static final class ReadingEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_READINGS).build();

        public static final String TABLE_NAME = "readings";
        public static final String COLUMN_FLEXION = "flexion";
        public static final String COLUMN_EXTENSION = "extension";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_FLEX_MAX = "flex_max";
        public static final String COLUMN_FLEX_MIN = "flex_min";
        public static final String COLUMN_EXT_MAX = "ext_max";
        public static final String COLUMN_EXT_MIN = "ext_min";
    }
}
