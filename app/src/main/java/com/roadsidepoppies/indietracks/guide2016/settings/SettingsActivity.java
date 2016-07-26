package com.roadsidepoppies.indietracks.guide2016.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import com.roadsidepoppies.indietracks.guide2016.IndietracksApplication;
import com.roadsidepoppies.indietracks.guide2016.R;
import com.roadsidepoppies.indietracks.guide2016.alarm.EventAlarmManager;

/**
 * Created by maq on 26/07/2016.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = "SettingsActivity";
    public static final String ALARMADVANCE = "pref_alarmAdvance";
    private static Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                intent.setAction("com.roadsidepoppies.indietracks.guide2016.INDIETRACKS");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences tempPrefs, String s) {
        Log.d(TAG, "Updating alarm times");
        if (s.equals(SettingsActivity.ALARMADVANCE)) {
            SharedPreferences appPrefs = this.getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, Context.MODE_PRIVATE);

            Log.d(TAG, "New Pre-delay value = " + tempPrefs.getString(ALARMADVANCE, "no value") + " current value = " + appPrefs.getString(ALARMADVANCE, "no value"));

            SharedPreferences.Editor editor = appPrefs.edit();
            editor.putString(SettingsActivity.ALARMADVANCE, tempPrefs.getString(ALARMADVANCE, getString(R.string.default_predelay)));
            editor.commit();
            EventAlarmManager alarmManager = new EventAlarmManager();
            alarmManager.registerAlarms(this, appPrefs);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

}
