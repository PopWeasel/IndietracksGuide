package com.roadsidepoppies.indietracks.guide2016.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by maq on 12/07/2016.
 */
public class Festival {
    public List<Artist> artists = new ArrayList<>();
    public List<Event> events = new ArrayList<>();
    public SortedSet<Calendar> days = new TreeSet<>();
    public List<Location> locations = new ArrayList<>();

    public SortedMap<String, Artist> artistNameMap;
    public SortedMap<Calendar, List<Event>> eventDayMap;
    public SortedMap<Calendar, SortedMap<Location, List<Event>>> schedule;
}
