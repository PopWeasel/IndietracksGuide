package com.roadsidepoppies.indietracks.guide2016.data;

import android.support.v7.util.SortedList;

import java.util.Date;
import java.util.SortedMap;

/**
 * Created by maq on 12/07/2016.
 */
public class Festival {
    public SortedMap<String, Artist> artists;
    public SortedMap<Date, SortedList<Event>> events;
    public SortedList<Location> locations;
    public SortedMap<Date, SortedMap<Location, SortedList<Event>>> schedule;
}
