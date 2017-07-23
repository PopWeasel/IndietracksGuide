package com.roadsidepoppies.indietracks.guide2017.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.roadsidepoppies.indietracks.guide2017.IndietracksApplication;
import com.roadsidepoppies.indietracks.guide2017.R;
import com.roadsidepoppies.indietracks.guide2017.data.Festival;
import com.roadsidepoppies.indietracks.guide2017.settings.SettingsActivity;
import com.roadsidepoppies.indietracks.guide2017.data.Event;
import com.roadsidepoppies.indietracks.guide2017.sql.IndietracksDataHelper;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by maq on 26/07/2016.
 */
public class EventAlarmManager {
    public static final String TAG = "EventAlarmManager";
    public static final String DEFAULT_ADVANCE = "5";
    public static final String ALARMKEY_PREFIX = "EVENT_ALARM";

    public static void addAlarm(Context context, Event event,
                                SharedPreferences prefs) {
        Log.d(TAG, "Adding Alarm " + event.getKey());

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(event.getKey(), true);
        editor.commit();

        String advanceString = prefs.getString(SettingsActivity.ALARMADVANCE, DEFAULT_ADVANCE);
        Log.d(TAG, "Predelay = " + advanceString);
        int advance = Integer.parseInt(advanceString);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
        cal.add(Calendar.MINUTE, advance);
        if (cal.before(event.start)) {
            Intent intent = new Intent(context, EventAlarmReceiver.class);
            intent.setAction("com.roadsidepoppies.indietracks.guide2017.EVENTALARM");
            intent.setData(Uri.parse("timer:" + event.getKey().hashCode()));
            intent.putExtra(IndietracksApplication.ALARMEVENT_KEY,
                    event.getKey());
            PendingIntent pending = PendingIntent.getBroadcast(context,
                    event.artist.sortName.hashCode(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            cal.set(event.start.get(Calendar.YEAR),
                    event.start.get(Calendar.MONTH),
                    event.start.get(Calendar.DATE),
                    event.start.get(Calendar.HOUR_OF_DAY),
                    event.start.get(Calendar.MINUTE));
            cal.add(Calendar.MINUTE, -advance);
            Log.d(TAG, "Setting alarm for " + event.getKey()
                    + " to go off at " + String.format("%tF %tR", cal, cal));
            Log.d(TAG, "The time now is " + System.currentTimeMillis()
                    + " the alarm is set for " + cal.getTimeInMillis());
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pending);
            // MAQ - debugging alarm
            //alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() - 10000L), pending);
            //Log.d(TAG, "The time now is " + System.currentTimeMillis() + " the alarm is set for " + (System.currentTimeMillis() - 10000L));

        }
    }

    public static void removeAlarm(Context context, Event event,
                                   SharedPreferences prefs) {
        Log.d(TAG, "Removing Alarm " + event.getKey());

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(event.getKey());
        editor.commit();

        String advanceString = prefs.getString(SettingsActivity.ALARMADVANCE, DEFAULT_ADVANCE);
        int advance = Integer.parseInt(advanceString);
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
        cal.add(Calendar.MINUTE, advance);
        if (cal.before(event.start)) {
            Intent intent = new Intent(context, EventAlarmReceiver.class);
            intent.setAction("com.roadsidepoppies.indietracks.guide2017.EVENTALARM");
            intent.setData(Uri.parse("timer:" + event.getKey().hashCode()));
            intent.putExtra(IndietracksApplication.ALARMEVENT_KEY, event.getKey());
            PendingIntent pending = PendingIntent.getBroadcast(context, event.getKey().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            cal.set(event.start.get(Calendar.YEAR),
                    event.start.get(Calendar.MONTH),
                    event.start.get(Calendar.DATE),
                    event.start.get(Calendar.HOUR_OF_DAY),
                    event.start.get(Calendar.MINUTE));
            cal.add(Calendar.MINUTE, -advance);
            AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pending);
        }
    }

    public static void registerAlarms(Context context, SharedPreferences prefs) {
        Log.d(TAG, "Re-registering alarms");
        IndietracksApplication application = (IndietracksApplication) context.getApplicationContext();

        int dataVersion = prefs.getInt(IndietracksApplication.DATAVERSION, 0);
        IndietracksDataHelper dataHelper = new IndietracksDataHelper(context, dataVersion);
        try {
            Festival festival = dataHelper.getFestivalData();
            application.setFestival(festival);
            Map alarms = prefs.getAll();
            for (Iterator<String> it = alarms.keySet().iterator(); it.hasNext();) {
                String eventKey = it.next();
                Log.d(TAG, "Event Key: " + eventKey);
                if(eventKey.startsWith(ALARMKEY_PREFIX)) {
                    if (prefs.getBoolean(eventKey, false)) {
                        Log.d(TAG, " application contains " + eventKey + " => "
                                + festival.eventKeyMap.containsKey(eventKey));
                        if (festival != null
                                && festival.eventKeyMap.containsKey(eventKey)) {
                            Event event = festival.eventKeyMap.get(eventKey);
                            addAlarm(context, event, prefs);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Error loading data for alarm");
            e.printStackTrace();
        }


    }

    public static boolean eventHasAlarm(Event event, SharedPreferences prefs) {
        boolean hasAlarm = prefs.getBoolean(event.getKey(), false);
        return hasAlarm;
    }

    public static Drawable getIcon(Event event, SharedPreferences prefs, Context context) {
        Drawable icon;
        if (prefs.getBoolean(event.getKey(), false))
            icon = context.getResources().getDrawable(R.mipmap.ic_notifications_black_24dp);
        else
            icon = context.getResources().getDrawable(R.mipmap.ic_notifications_off_black_24dp);
        return icon;
    }
}
