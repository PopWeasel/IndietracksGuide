package com.roadsidepoppies.indietracks.guide2016.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.roadsidepoppies.indietracks.guide2016.data.Artist;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maq on 21/07/2016.
 */
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
            "interview_link TEXT, " +
            "like INTEGER, " +
            "dislike INTEGER " +
            ");";
    public final static String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static long addArtist(SQLiteDatabase db, Artist artist) {
        db.beginTransaction();
        Log.d(TAG, "Addling artist " + artist.name);
        long artistId = -1;
        try {
            artistId = getArtistIdByName(db, artist.name);
            if (artistId == -1) {
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
                    values.put("interview_link", artist.interviewLink.toString());
                }

                artistId = db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_ABORT);
                db.setTransactionSuccessful();
                Log.d(TAG, "Inserted row " + artistId);
            } else {
                Log.d(TAG, "Found artist in DB " + artistId);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error creating artist: " + e.getMessage());
            throw e;
        } finally {
            db.endTransaction();
        }
        return artistId;
    }

     public static Long getArtistIdByName(SQLiteDatabase db, String name) {
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

    public static List<Artist> getArtists(SQLiteDatabase db) throws MalformedURLException {
        Log.d(TAG, "Fetching artists");
        List<Artist> artists = new ArrayList<>();
        String[] columns = {"_id", "name", "sort_name", "image", "description", "link", "music_link", "interview_link", "like", "dislike"};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, "sort_name");
        Log.d(TAG, cursor.getCount() + " rows in DB");
        if (cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                do {
                    Artist artist = new Artist();
                    artist.identifier = cursor.getLong(cursor.getColumnIndexOrThrow(columns[0]));
                    artist.name = cursor.getString(cursor.getColumnIndexOrThrow(columns[1]));
                    artist.sortName = cursor.getString(cursor.getColumnIndexOrThrow(columns[2]));
                    artist.image = cursor.getString(cursor.getColumnIndexOrThrow(columns[3]));
                    artist.description = cursor.getString(cursor.getColumnIndexOrThrow(columns[4]));
                    String link = cursor.getString(cursor.getColumnIndexOrThrow(columns[5]));
                    if (link != null) {
                        artist.link = new URL(link);
                    }
                    String musicLink = cursor.getString(cursor.getColumnIndexOrThrow(columns[6]));
                    if (musicLink != null) {
                        artist.musicLink = new URL(musicLink);
                    }
                    String interviewLink = cursor.getString(cursor.getColumnIndexOrThrow(columns[7]));
                    if (interviewLink != null) {
                        artist.interviewLink = new URL(link);
                    }
                    artists.add(artist);
                    cursor.moveToNext();
                } while (!cursor.isAfterLast());
            } finally {
                cursor.close();
            }
        }
        Log.d(TAG, "Returning " + artists.size()  +  " artists");
        return artists;
    }
}
