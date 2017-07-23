package com.roadsidepoppies.indietracks.guide2017.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.roadsidepoppies.indietracks.guide2017.IndietracksApplication;

/**
 * Created by maq on 26/07/2016.
 */
public class DeviceRebootReceiver extends BroadcastReceiver {
    public static final String TAG = "AlarmResetReciever";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive - re-registering alarms");
        SharedPreferences prefs = context.getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, Context.MODE_PRIVATE);
        EventAlarmManager.registerAlarms(context, prefs);
    }

}
