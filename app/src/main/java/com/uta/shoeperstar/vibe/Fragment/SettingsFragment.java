package com.uta.shoeperstar.vibe.Fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.uta.shoeperstar.vibe.Activity.SettingsActivity;
import com.uta.shoeperstar.vibe.R;


public class SettingsFragment extends PreferenceFragment   {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        int vibrationFrequency=0;
        int vibrationLevel=0;

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);
        Log.v("BB", "Settings Fragment");


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        //obtain setting values from settings activity

        //BOOLEAN vibe on or off
        //BOOLEAN bluetooth on or off
        String vibeStrength = sharedPref.getString(SettingsActivity.KEY_PREF_STRENGTH, "");
        String vibeFreq = sharedPref.getString(SettingsActivity.KEY_PREF_FREQ, "");

        Log.v("Strength", vibeStrength);
        Log.v("Frequency", vibeFreq);

    }


}
