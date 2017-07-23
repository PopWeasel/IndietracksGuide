package com.roadsidepoppies.indietracks.guide2017.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.roadsidepoppies.indietracks.guide2017.IndietracksApplication;
import com.roadsidepoppies.indietracks.guide2017.IndietracksMainActivity;
import com.roadsidepoppies.indietracks.guide2017.R;
import com.roadsidepoppies.indietracks.guide2017.settings.SettingsActivity;

/**
 * Created by maq on 26/07/2016.
 */
public class EventAlarmReceiver extends BroadcastReceiver {
    public static final String TAG = "EventAlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        SharedPreferences prefs = context.getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, Context.MODE_PRIVATE);
        String advanceString = prefs.getString(SettingsActivity.ALARMADVANCE, EventAlarmManager.DEFAULT_ADVANCE);
        int advance = Integer.parseInt(advanceString);

        Toast.makeText(context, "Alarm worked!", Toast.LENGTH_LONG);
        Bundle bundle = intent.getExtras();
        String eventkey = bundle.getString(IndietracksApplication.ALARMEVENT_KEY);
        String[] tokens = eventkey.split("\\|");
        Log.d(TAG, "Got alarm for " + tokens[1]);

        Intent notificationIntent = new Intent("com.roadsidepoppies.indietracks.guide2017.INDIETRACKS");
        notificationIntent.putExtra(IndietracksMainActivity.EVENTKEY, eventkey);
        PendingIntent pending = PendingIntent.getActivity(context,
                (int) System.currentTimeMillis(), notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        long now = System.currentTimeMillis();
        String message = String.format("%s at %s in %s minutes", tokens[1], tokens[2], advance);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.mipmap.ic_notifications_active_black_36dp);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentText(message);
        notificationBuilder.setContentTitle("Indietracks " + tokens[1]);
        notificationBuilder.setTicker(message);
        notificationBuilder.setContentIntent(pending);

        Log.d(TAG, "Sending notification with text " + message);
        notificationManager.notify(eventkey.hashCode(), notificationBuilder.getNotification());
    }

}
