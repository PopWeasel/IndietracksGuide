package com.roadsidepoppies.indietracks.guide2016.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.roadsidepoppies.indietracks.guide2016.data.Artist;
import com.roadsidepoppies.indietracks.guide2016.data.Event;
import com.roadsidepoppies.indietracks.guide2016.data.Festival;
import com.roadsidepoppies.indietracks.guide2016.data.Location;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by maq on 12/07/2016.
 */
public class IndietracksDataHelper extends SQLiteOpenHelper {
    private final static String TAG = "IndietracksDataHelper";
    private final static String DATABASE_NAME = "indietracks2016.db";

    public IndietracksDataHelper(Context context, int dataVersion) {
        super(context, DATABASE_NAME, null, dataVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate DB");
        db.execSQL(LocationDAO.CREATE_TABLE);
        db.execSQL(EventDAO.CREATE_TABLE);
        db.execSQL(ArtistDAO.CREATE_TABLE);
        db.execSQL(ArtistEventDAO.CREATE_TABLE);
        db.execSQL(AlarmDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade DB");
        db.execSQL(LocationDAO.DROP_TABLE);
        db.execSQL(EventDAO.DROP_TABLE);
        db.execSQL(ArtistDAO.DROP_TABLE);
        db.execSQL(ArtistEventDAO.DROP_TABLE);
        db.execSQL(AlarmDAO.DROP_TABLE);
        onCreate(db);
    }

    public void addLocation(Location location) {
        Log.d(TAG, "addLocation");
        SQLiteDatabase db = getWritableDatabase();
        LocationDAO.addLocation(db, location);
    }

    public List<Location> getLocations() {
        Log.d(TAG, "getLocations");
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Location> locations = LocationDAO.getLocations(db);
        return  locations;
    }

    public void addArtist(Artist artist) throws IndietracksDataException{
        Log.d(TAG, "addArtist");
        SQLiteDatabase db = getWritableDatabase();
        long artistID = ArtistDAO.addArtist(db, artist);
        for (Event event : artist.events) {
            Log.d(TAG, "Adding event for " + artist.name);
            long locationID = LocationDAO.getLocationIDByName(db, event.location.name);
            long eventID = EventDAO.addEvent(db, event, locationID);
            ArtistEventDAO.addArtistEvent(db, artistID, eventID);
        }
    }

    public Festival getFestivalData() throws MalformedURLException {
        Log.d(TAG, "getFestivalData");
        SQLiteDatabase db = getReadableDatabase();
        Festival festival = new Festival();
        List<Location> locations = getLocations();
        Map<Long, Location> locationIdMap = new HashMap<>();
        for (Location location : locations) {
            locationIdMap.put(location.identifier, location);
        }
        List<Event> events = EventDAO.getEvents(db, locationIdMap);
        Map<Long, Event> eventIdMap = new HashMap<>();
        SortedSet<Calendar> days = new TreeSet<>();
        SortedMap<Calendar, List<Event>> eventDayMap= new TreeMap<>();
        for (Event event : events) {
            eventIdMap.put(event.identifier, event);
            if (!days.contains(event.day)) {
                days.add(event.day);
            }
            if (!eventDayMap.keySet().contains(event.day)) {
                eventDayMap.put(event.day, new ArrayList<Event>());
            }
            eventDayMap.get(event.day).add(event);
        }
        List<Artist> artists = ArtistDAO.getArtists(db);
        Map<Long, Artist> artistIdMap = new HashMap<>();
        for(Artist artist : artists) {
            artistIdMap.put(artist.identifier, artist);
        }

        //add events to artists
        ArtistEventDAO.populateArtistEvents(db, artistIdMap, eventIdMap);

        festival.locations = locations;
        festival.events = events;
        festival.artists = artists;
        festival.days = days;
        festival.eventDayMap = eventDayMap;

        SortedMap<Calendar, SortedMap<Location, List<Event>>> schedule = new TreeMap<>();

        for(Calendar day : days) {
            SortedMap<Location, List<Event>> locationEventList = new TreeMap<>();

            schedule.put(day, locationEventList);
            List<Event> dayEvents = festival.eventDayMap.get(day);

            for (Event event : dayEvents) {
                if(!locationEventList.containsKey(event.location)) {
                    List<Event> locationEvents = new ArrayList<>();
                    locationEventList.put(event.location, locationEvents);
                }
                locationEventList.get(event.location).add(event);

            }


        }


        return festival;
    }
}


