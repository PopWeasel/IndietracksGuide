package com.roadsidepoppies.indietracks.guide2016.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

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
}

class LocationDAO implements BaseColumns {
    private final static String TAG = "LocatinDAO";
    public final static String TABLE_NAME = "Locations";
    public final static String CREATE_TABLE =
                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
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
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        } catch (Exception e) {
            Log.e(TAG, "Error creating location: " + e.getMessage());
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    public static int getLocationIDByName(SQLiteDatabase db, String name) throws IndietracksDataException{
        String[] columns = {"_ID"};
        String[] queryArgs = {name};
        Cursor cursor = db.query(TABLE_NAME, columns, "WHERE name = ?", queryArgs, null, null, null);
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
        Cursor cursor = db.query(TABLE_NAME, columns, "WHERE _ID = ?", queryArgs, null, null, null);
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
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        try {

        } finally {
            cursor.close();
        }
        Log.d(TAG, "Returning " + locations.size()  +  " locations");
        return locations;
    }
}

class EventDAO implements BaseColumns {
    public final static String TABLE_NAME = "Events";
    public final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
                    "start INTEGER, " +
                    "end INTEGER, " +
                    "duration INTEGER, " +
                    "day INTEGER, " +
                    "location INTEGER, " +
                    "FOREIGN KEY(location) REFERENCES locations(_ID) " +
                    ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

class ArtistDAO implements BaseColumns {
    public final static String TABLE_NAME = "Artists";
    public final static String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
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
}

class ArtistEventDAO implements BaseColumns {
    public final static String TABLE_NAME = "ArtistEvents";
    public final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
                    "artist INTEGER NOT NULL, " +
                    "event INTEGER NOT NULL, " +
                    "FOREIGN KEY(artist) REFERENCES artists(_ID), " +
                    "FOREIGN KEY(event) REFERENCES events(_ID) " +
                    ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}


class AlarmDAO implements BaseColumns {
    public final static String TABLE_NAME = "Alarms";
    public final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
                    "event INTEGER NOT NULL, " +
                    "FOREIGN KEY(event) REFERENCES events(_ID) " +
                    ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}

class IndietracksDataException extends Exception {
    public IndietracksDataException(String msg) {
        super(msg);
    }
}