package com.uta.shoeperstar.vibe.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.uta.shoeperstar.vibe.R;


/**
 * Created by saads_000 on 3/9/2015.
 */
public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        Log.v("BddddddB","HERE");
    }
    public static final String MY_PREFS_NAME = "preference.xml";

    //Vibration Settings
    public static final String KEY_PREF_VIBE = "vibeOnOffPref";
    public static final String KEY_PREF_FREQ = "freqPref";
    public static final String KEY_PREF_STRENGTH = "vibePref";

    //Bluetooth Settings
    public static final String KEY_PREF_BLUETOOTH = "bluetoothPref";


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {

        Log.v("BddddddB","HERE");
        if (key.equals(KEY_PREF_VIBE)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            Log.v("VIBE",key);
        }

        if (key.equals(KEY_PREF_FREQ)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            Log.v("FREQ",key);
        }

        if (key.equals(KEY_PREF_STRENGTH)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            System.out.println(key);
        }

        if (key.equals(KEY_PREF_BLUETOOTH)) {
            Preference connectionPref = findPreference(key);
            // Set summary to be the user-description for the selected value
            connectionPref.setSummary(sharedPreferences.getString(key, ""));
            System.out.println(key);
        }


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        if (restoredText != null) {
            String name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
            int idName = prefs.getInt("idName", 0); //0 is the default value.
        }

    }
}