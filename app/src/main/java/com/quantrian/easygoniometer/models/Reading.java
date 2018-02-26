package com.quantrian.easygoniometer.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vinnie on 2/15/2018.
 */

public class Reading implements Parcelable {

    //public String id;
    public int flexion;
    public int extension;
    public long date;
    public int flex_min;
    public int flex_max;
    public int ext_min;
    public int ext_max;

    public Reading(int flexion, int extension, long date){
        this.flexion = flexion;
        this.extension = extension;
        this.date = date;
    }

    protected Reading(Parcel in) {
        //id = in.readString();
        flexion= in.readInt();
        extension = in.readInt();
        date = in.readLong();
    }

    public static final Creator<Reading> CREATOR = new Creator<Reading>() {
        @Override
        public Reading createFromParcel(Parcel in) {
            return new Reading(in);
        }

        @Override
        public Reading[] newArray(int size) {
            return new Reading[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(flexion);
        parcel.writeInt(extension);
        parcel.writeLong(date);
    }
}
