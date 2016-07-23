package com.roadsidepoppies.indietracks.guide2016.sql;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by maq on 21/07/2016.
 */
class AlarmDAO implements BaseColumns {
    public static final String TAG = "AlarmDAO";
    public final static String TABLE_NAME = "Alarms";
    public final static String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME  + " ( " +
                    " _id INTEGER PRIMARY KEY, " +
                    "event INTEGER NOT NULL UNIQUE, " +
                    "FOREIGN KEY(event) REFERENCES events(_ID) " +
                    ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static long addAlarm(SQLiteDatabase db, long eventID) {
        db.beginTransaction();
        Log.d(TAG, "Addling alarm for event " + eventID);
        long rowID = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("event", eventID);

            rowID = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_ABORT);
            db.setTransactionSuccessful();
            Log.d(TAG, "Inserted row " + rowID);
        } catch (Exception e) {
            Log.e(TAG, "Error creating artist-event: " + e.getMessage());
            throw e;
        } finally {
            db.endTransaction();
        }
        return rowID;
    }
}
