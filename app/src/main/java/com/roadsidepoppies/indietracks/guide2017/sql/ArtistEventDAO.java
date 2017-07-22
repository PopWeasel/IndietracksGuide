package com.roadsidepoppies.indietracks.guide2016.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.roadsidepoppies.indietracks.guide2016.data.Artist;
import com.roadsidepoppies.indietracks.guide2016.data.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by maq on 21/07/2016.
 */
class ArtistEventDAO implements BaseColumns {
    public final static String TAG = "ArtistEventDAO";
    public final static String TABLE_NAME = "ArtistEvents";
    public final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
                    " _id INTEGER PRIMARY KEY, " +
                    "artist INTEGER NOT NULL, " +
                    "event INTEGER NOT NULL, " +
                    "artist_event TEXT NOT NULL UNIQUE, " +
                    "FOREIGN KEY(artist) REFERENCES artists(_ID), " +
                    "FOREIGN KEY(event) REFERENCES events(_ID) " +
                    ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static long addArtistEvent(SQLiteDatabase db, long artistID, long eventID) {
        db.beginTransaction();
        Log.d(TAG, "Addling artist-event " + artistID + " event " + eventID);
        long rowID = -1;
        try {
            rowID = getArtistEventIdByArtistEvent(db, artistID, eventID);
            if (rowID == -1) {
                String artistEventCode = Long.toString(artistID) + "-" + Long.toString(eventID);
                ContentValues values = new ContentValues();
                values.put("artist", artistID);
                values.put("event", eventID);
                values.put("artist_event", artistEventCode);

                rowID = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_ABORT);
                db.setTransactionSuccessful();
                Log.d(TAG, "Inserted row " + rowID);
            } else {
                Log.d(TAG, "Found artist-event in db " + rowID);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating artist-event: " + e.getMessage());
            throw e;
        } finally {
            db.endTransaction();
        }
        return rowID;
    }

    public static Long getArtistEventIdByArtistEvent(SQLiteDatabase db, Long artistId, Long eventId) {
        Long rowId = -1l;
        String[] columns = {BaseColumns._ID};
        String[] queryArgs = {Long.toString(artistId), Long.toString(eventId)};
        Cursor cursor = db.query(TABLE_NAME, columns, "artist = ? AND event = ?", queryArgs, null, null, null);
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

    public static void populateArtistEvents(SQLiteDatabase db,
                                            Map<Long, Artist> artistIdMap,
                                            Map<Long, Event> eventIdMap,
                                            Map<String, Event> eventKeyMap) {
        Log.d(TAG, "Fetching artist-events");
        List<Artist> artists = new ArrayList<>();
        String[] columns = {"artist", "event"};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, "artist");
        Log.d(TAG, cursor.getCount() + " rows in DB");
        if (cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                do {
                    Long artistId = cursor.getLong(cursor.getColumnIndexOrThrow(columns[0]));
                    Long eventId = cursor.getLong(cursor.getColumnIndexOrThrow(columns[1]));

                    Artist artist = artistIdMap.get(artistId);
                    Event event = eventIdMap.get(eventId);
                    event.artist = artist;
                    eventKeyMap.put(event.getKey(), event);

                    if (artist.events == null) {
                        artist.events = new ArrayList<Event>();
                    }
                    artist.events.add(event);
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            } finally {
                cursor.close();
            }
        }
    }
}
