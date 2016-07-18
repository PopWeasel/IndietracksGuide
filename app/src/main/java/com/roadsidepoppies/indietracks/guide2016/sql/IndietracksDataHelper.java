package com.roadsidepoppies.indietracks.guide2016.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.roadsidepoppies.indietracks.guide2016.data.Artist;
import com.roadsidepoppies.indietracks.guide2016.data.Event;
import com.roadsidepoppies.indietracks.guide2016.data.Location;

import java.util.ArrayList;

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
        db.execSQL(LocationDAO.CREATE_TABLE);
        db.execSQL(EventDAO.CREATE_TABLE);
        db.execSQL(ArtistDAO.CREATE_TABLE);
        db.execSQL(ArtistEventDAO.CREATE_TABLE);
        db.execSQL(AlarmDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(LocationDAO.DROP_TABLE);
        db.execSQL(EventDAO.DROP_TABLE);
        db.execSQL(ArtistDAO.DROP_TABLE);
        db.execSQL(ArtistEventDAO.DROP_TABLE);
        db.execSQL(AlarmDAO.DROP_TABLE);
        onCreate(db);
    }

    public void addLocation(Location location) {
        SQLiteDatabase db = getWritableDatabase();
        LocationDAO.addLocation(db, location);
    }

    public ArrayList<Location> getLocations() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Location> locations = LocationDAO.getLocations(db);
        return  locations;
    }

    public void addArtist(Artist artist) throws IndietracksDataException{
        SQLiteDatabase db = getWritableDatabase();
        long artistID = ArtistDAO.addArtist(db, artist);
        for (Event event : artist.events) {
            Log.d(TAG, "Adding event for " + artist.name);
            long locationID = LocationDAO.getLocationIDByName(db, event.location.name);
            long eventID = EventDAO.addEvent(db, event, locationID);
            ArtistEventDAO.addArtistEvent(db, artistID, eventID);
        }

    }
}

class LocationDAO implements BaseColumns {
    private final static String TAG = "LocatinDAO";
    public final static String TABLE_NAME = "Locations";
    public final static String CREATE_TABLE =
                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
                            " _id INTEGER PRIMARY KEY, " +
                            " name TEXT NOT NULL UNIQUE, " +
                            " sort_order INTEGER UNIQUE " +
                            ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static void addLocation(SQLiteDatabase db, Location location) {
        db.beginTransaction();
        Log.d(TAG, "Addling location " + location.name);
        try {
            ContentValues values = new ContentValues();
            values.put("name", location.name);
            values.put("sort_order", location.sortOrder);
            long status = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
            Log.d(TAG, "Inserted row " + status);
        } catch (Exception e) {
            Log.e(TAG, "Error creating location: " + e.getMessage());
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    public static int getLocationIDByName(SQLiteDatabase db, String name) throws IndietracksDataException{
        String[] columns = {BaseColumns._ID};
        String[] queryArgs = {name};
        Cursor cursor = db.query(TABLE_NAME, columns, "name = ?", queryArgs, null, null, null);
        try {
            if (cursor.getCount() != 1) {
                throw new IndietracksDataException("Failed to find location row matching '" + name + "'");
            } else {
                cursor.moveToFirst();
                return cursor.getInt(cursor.getColumnIndexOrThrow(columns[0]));
            }
        } finally {
            cursor.close();
        }
    }

    public static Location getLocationByID(SQLiteDatabase db, int id) throws IndietracksDataException {
        Location location;
        String[] columns = {"name", "sort_order"};
        String[] queryArgs = {Integer.toString(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, BaseColumns._ID +" = ?", queryArgs, null, null, null);
        try {
            if (cursor.getCount() != 1) {
                throw new IndietracksDataException("Failed to find location row matching '" + id + "'");
            } else {
                cursor.moveToFirst();
                location = new Location();
                location.name = cursor.getString(cursor.getColumnIndexOrThrow(columns[0]));
                location.sortOrder = cursor.getInt(cursor.getColumnIndexOrThrow(columns[1]));
            }
        } finally {
            cursor.close();
        }
        return  location;
    }

    public static ArrayList<Location> getLocations(SQLiteDatabase db) {
        Log.d(TAG, "Fetching locations");
        ArrayList<Location> locations = new ArrayList<>();
        String[] columns = {"name", "sort_order"};
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, "sort_order");
        Log.d(TAG, cursor.getCount() + " rows in DB");
        if (cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                do {
                    Location location = new Location();
                    location.name = cursor.getString(cursor.getColumnIndexOrThrow(columns[0]));
                    location.sortOrder = cursor.getInt(cursor.getColumnIndexOrThrow(columns[1]));
                    locations.add(location);
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());


            } finally {
                cursor.close();
            }
        }
        Log.d(TAG, "Returning " + locations.size()  +  " locations");
        return locations;
    }
}

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
        long artistID = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("start", event.start.getTimeInMillis());
            values.put("end", event.end.getTimeInMillis());
            values.put("day", event.day.getTimeInMillis());
            values.put("duration", event.duration);
            values.put("location", locationID);


            artistID = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
            Log.d(TAG, "Inserted row " + artistID);
        } catch (Exception e) {
            Log.e(TAG, "Error creating artist: " + e.getMessage());
            throw e;
        } finally {
            db.endTransaction();
        }
        return artistID;
    }
}

class ArtistDAO implements BaseColumns {
    public static final String TAG = "ArtistDAO";
    public final static String TABLE_NAME = "Artists";
    public final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
            " _id INTEGER PRIMARY KEY, " +
            "name TEXT UNIQUE NOT NULL, " +
            "sort_name TEXT NOT NULL, " +
            "image TEXT, " +
            "description TEXT, " +
            "link TEXT, " +
            "music_link TEXT, " +
            "interview_ink TEXT, " +
            "like INTEGER, " +
            "dislike INTEGER " +
            ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static long addArtist(SQLiteDatabase db, Artist artist) {
        db.beginTransaction();
        Log.d(TAG, "Addling artist " + artist.name);
        long artistID = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("name", artist.name);
            values.put("sort_name", artist.sortName);
            values.put("image", artist.image);
            values.put("description", artist.description);
            if (artist.link != null) {
                values.put("link", artist.link.toString());
            }
            if (artist.musicLink != null) {
                values.put("music_link", artist.musicLink.toString());
            }
            if (artist.interviewLink != null) {
                values.put("interview_ink", artist.interviewLink.toString());
            }

            artistID = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
            Log.d(TAG, "Inserted row " + artistID);
        } catch (Exception e) {
            Log.e(TAG, "Error creating artist: " + e.getMessage());
            throw e;
        } finally {
            db.endTransaction();
        }
        return artistID;
    }

}

class ArtistEventDAO implements BaseColumns {
    public final static String TAG = "ArtistEventDAO";
    public final static String TABLE_NAME = "ArtistEvents";
    public final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
                    " _id INTEGER PRIMARY KEY, " +
                    "artist INTEGER NOT NULL, " +
                    "event INTEGER NOT NULL, " +
                    "FOREIGN KEY(artist) REFERENCES artists(_ID), " +
                    "FOREIGN KEY(event) REFERENCES events(_ID) " +
                    ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static long addArtistEvent(SQLiteDatabase db, long artistID, long eventID) {
        db.beginTransaction();
        Log.d(TAG, "Addling artist-event " + artistID + " event " + eventID);
        long rowID = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("artist", artistID);
            values.put("event", eventID);

            rowID = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.setTransactionSuccessful();
            Log.d(TAG, "Inserted row " + artistID);
        } catch (Exception e) {
            Log.e(TAG, "Error creating artist-event: " + e.getMessage());
            throw e;
        } finally {
            db.endTransaction();
        }
        return rowID;
    }
}


class AlarmDAO implements BaseColumns {
    public final static String TABLE_NAME = "Alarms";
    public final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
                    " _id INTEGER PRIMARY KEY, " +
                    "event INTEGER NOT NULL UNIQUE, " +
                    "FOREIGN KEY(event) REFERENCES events(_ID) " +
                    ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

class IndietracksDataException extends Exception {
    public IndietracksDataException(String msg) {
        super(msg);
    }
}