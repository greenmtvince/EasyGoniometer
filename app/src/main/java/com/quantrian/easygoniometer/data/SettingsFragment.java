package com.quantrian.easygoniometer.data;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import com.quantrian.easygoniometer.R;
import com.takisoft.fix.support.v7.preference.DatePickerPreference;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_user_info);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();

        int count = preferenceScreen.getPreferenceCount();

        for (int i =0;i<count;i++){
            Preference p = preferenceScreen.getPreference(i);
            String value = sharedPreferences.getString(p.getKey(),"");
            setPreferenceSummary(p, value);
        }
        setDateSummary(sharedPreferences);

    }

    private void setPreferenceSummary(Preference preference, String value){
        if (preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >=0){
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (preference instanceof EditTextPreference){
            preference.setSummary(value);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("PREF", "onSharedPreferenceChanged: "+ key);
        Preference preference = findPreference(key);
        if (null != preference){
            String value = sharedPreferences.getString(preference.getKey(),"");
            setPreferenceSummary(preference,value);
        }
    }

    public void setDateSummary(final SharedPreferences sharedPreferences){
        final String surgeryDateKey = "surgery_date";
        String surgeryDateValue = sharedPreferences.getString(surgeryDateKey,"");

        final DatePickerPreference datePickerPreference = (DatePickerPreference) findPreference(surgeryDateKey);
        datePickerPreference.setSummary(surgeryDateValue);
        String[] dateArray = surgeryDateValue.split("/");

        datePickerPreference.setDate(Integer.parseInt(dateArray[2]),Integer.parseInt(dateArray[0])-1,Integer.parseInt(dateArray[1]));

        datePickerPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //
                //DatePickerPreference p = (DatePickerPreference.) preference;
                //Date d = p.getDate();
                DatePickerPreference.DateWrapper wrapper = (DatePickerPreference.DateWrapper) o;
                //wrapper.
                String date = String.format("%d/%d/%d",wrapper.month+1,wrapper.day,wrapper.year);
                Log.d("PREF", "onPreferenceChange: " + date);
                sharedPreferences.edit().putString(surgeryDateKey,date).apply();
                datePickerPreference.setSummary(date);

                return true;
            }
        });
    }
}
