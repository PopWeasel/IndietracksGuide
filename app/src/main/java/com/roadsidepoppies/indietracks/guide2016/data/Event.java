package com.roadsidepoppies.indietracks.guide2016.data;

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
