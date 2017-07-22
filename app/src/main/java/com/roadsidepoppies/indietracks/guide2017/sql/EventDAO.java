package com.roadsidepoppies.indietracks.guide2016.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.roadsidepoppies.indietracks.guide2016.IndietracksApplication;
import com.roadsidepoppies.indietracks.guide2016.data.Event;
import com.roadsidepoppies.indietracks.guide2016.data.Location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by maq on 21/07/2016.
 */
class EventDAO implements BaseColumns {
    public static final String TAG = "EventDAO";
    public final static String TABLE_NAME = "Events";
    public final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
                    " _id INTEGER PRIMARY KEY, " +
                    "start INTEGER, " +
                    "end INTEGER, " +
                    "duration INTEGER, " +
                    "day INTEGER, " +
                    "location INTEGER, " +
                    "FOREIGN KEY(location) REFERENCES locations(_ID) " +
                    ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static long addEvent(SQLiteDatabase db, Event event, long locationID) {
        db.beginTransaction();
        Log.d(TAG, "Addling event " + event.start.toString());
        long eventID = -1;
        try {
            eventID = getEventIdByParameters(db, event.start.getTimeInMillis(), event.end.getTimeInMillis(), locationID);
            if (eventID == -1) {
                ContentValues values = new ContentValues();
                values.put("start", event.start.getTimeInMillis());
                values.put("end", event.end.getTimeInMillis());
                values.put("day", event.day.getTimeInMillis());
                values.put("duration", event.duration);
                values.put("location", locationID);


                eventID = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_ABORT);
                db.setTransactionSuccessful();
                Log.d(TAG, "Inserted row " + eventID);
            } else {
                Log.d(TAG, "Found event in db " + event);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating artist: " + e.getMessage());
            throw e;
        } finally {
            db.endTransaction();
        }
        return eventID;
    }

    public static Long getEventIdByParameters(SQLiteDatabase db, Long start, Long end, Long locationId) {
        Long rowId = -1l;
        String[] columns = {BaseColumns._ID};
        String[] queryArgs = {Long.toString(start), Long.toString(end), Long.toString(locationId)};
        Cursor cursor = db.query(TABLE_NAME, columns, "start = ? AND end = ? AND location = ?", queryArgs, null, null, null);
        try {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                rowId =  cursor.getLong(cursor.getColumnIndexOrThrow(columns[0]));
            }
        } finally {
            cursor.close();
        }
        return rowId;
    }

    public static List<Event> getEvents(SQLiteDatabase db, Map<Long, Location> locationMap) {
        Log.d(TAG, "Fetching events");
        List<Event> events = new ArrayList<>();
        String[] columns = {"_id", "start", "end", "day", "duration", "location"};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, "start");
        Log.d(TAG, cursor.getCount() + " rows in DB");
        if (cursor.getCount() > 0) {
            try {
                Map<Long, Calendar> millisecondsDayMap = new HashMap<>();
                cursor.moveToFirst();
                do {
                    Event event = new Event();
                    event.identifier = cursor.getLong(cursor.getColumnIndexOrThrow(columns[0]));
                    Calendar start = new GregorianCalendar(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
                    start.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(columns[1])));
                    event.start = start;

                    Calendar end = new GregorianCalendar(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
                    end.setTimeInMillis(cursor.getLong(cursor.getColumnIndexOrThrow(columns[2])));
                    event.end = end;
                    //re-use same day object across all events
                    Calendar day;
                    Long millisecsDay = cursor.getLong(cursor.getColumnIndexOrThrow(columns[3]));
                    if (millisecondsDayMap.containsKey(millisecsDay)) {
                        day = millisecondsDayMap.get(millisecsDay);
                    } else {
                        day = new GregorianCalendar(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
                        day.setTimeInMillis(millisecsDay);
                        millisecondsDayMap.put(millisecsDay, day);
                    }
                    event.day = day;

                    event.duration = cursor.getInt(cursor.getColumnIndexOrThrow(columns[4]));
                    event.location = locationMap.get(cursor.getLong(cursor.getColumnIndexOrThrow(columns[5])));
                    events.add(event);
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            } finally {
                cursor.close();
            }
        }
        Log.d(TAG, "Returning " + events.size()  +  " events");
        return events;
    }
}
