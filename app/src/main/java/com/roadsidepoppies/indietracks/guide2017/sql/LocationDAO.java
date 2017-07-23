package com.roadsidepoppies.indietracks.guide2017.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.roadsidepoppies.indietracks.guide2017.data.Location;

import java.util.ArrayList;

/**
 * Created by maq on 21/07/2016.
 */
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

    public static void addLocation(SQLiteDatabase db, Location location){
        db.beginTransaction();
        Log.d(TAG, "Addling location " + location.name);
        try {
            Long locationId = getLocationIDByName(db, location.name);
            if (locationId == -1) {
                ContentValues values = new ContentValues();
                values.put("name", location.name);
                values.put("sort_order", location.sortOrder);
                long status = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_ABORT);
                db.setTransactionSuccessful();
                Log.d(TAG, "Inserted row " + status);
            } else {
                Log.d(TAG, "Location found in db " + locationId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating location: " + e.getMessage());
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    public static Long getLocationIDByName(SQLiteDatabase db, String name){
        Long rowId = -1l;
        String[] columns = {BaseColumns._ID};
        String[] queryArgs = {name};
        Cursor cursor = db.query(TABLE_NAME, columns, "name = ?", queryArgs, null, null, null);
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

    public static Location getLocationByID(SQLiteDatabase db, int id) throws IndietracksDataException {
        Location location;
        String[] queryArgs = {Integer.toString(id)};
        Cursor cursor = db.query(TABLE_NAME, null, BaseColumns._ID +" = ?", queryArgs, null, null, null);
        try {
            if (cursor.getCount() != 1) {
                throw new IndietracksDataException("Failed to find location row matching '" + id + "'");
            } else {
                cursor.moveToFirst();
                location = new Location();
                location.identifier = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
                location.name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                location.sortOrder = cursor.getInt(cursor.getColumnIndexOrThrow("sort_order"));
            }
        } finally {
            cursor.close();
        }
        return  location;
    }

    public static ArrayList<Location> getLocations(SQLiteDatabase db) {
        Log.d(TAG, "Fetching locations");
        ArrayList<Location> locations = new ArrayList<>();
        String[] columns = {"_id", "name", "sort_order"};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, "sort_order");
        Log.d(TAG, cursor.getCount() + " rows in DB");
        if (cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                do {
                    Location location = new Location();
                    location.identifier = cursor.getLong(cursor.getColumnIndexOrThrow(columns[0]));
                    location.name = cursor.getString(cursor.getColumnIndexOrThrow(columns[1]));
                    location.sortOrder = cursor.getInt(cursor.getColumnIndexOrThrow(columns[2]));
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
