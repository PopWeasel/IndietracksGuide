package com.roadsidepoppies.indietracks.guide2017.data;

import com.roadsidepoppies.indietracks.guide2017.alarm.EventAlarmManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by maq on 12/07/2016.
 */
public class Event implements Comparable<Event>{
    public long identifier;
    public Calendar start;
    public Calendar end;
    public Calendar day;
    public int duration;
    public Location location;
    public Artist artist;
    public String key;

    public static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public String getKey() {
        if (key == null) {
            key = String.format("%s|%s|%s|%s", EventAlarmManager.ALARMKEY_PREFIX, artist.name, location, dayFormat.format(start.getTime()));
        }
        return key;
    }

    public boolean equals(Object o) {
        Boolean equals = false;
        Event event = (Event) o;
        if (start.getTimeInMillis() == event.start.getTimeInMillis()
                && end.getTimeInMillis() == event.end.getTimeInMillis()
                && location.equals(event.location)){
            equals = true;
        }
        return equals;
    }

    @Override
    public int compareTo(Event event) {
        return start.compareTo(event.start);
    }
}
