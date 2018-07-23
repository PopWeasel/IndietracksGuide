package com.roadsidepoppies.indietracks.guide2018.sql;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;

import com.roadsidepoppies.indietracks.guide2018.IndietracksApplication;
import com.roadsidepoppies.indietracks.guide2018.data.Artist;
import com.roadsidepoppies.indietracks.guide2018.data.Event;
import com.roadsidepoppies.indietracks.guide2018.data.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

/**
 * Created by maq on 12/07/2016.
 */
public class DataToSQLAsyncTask extends  AsyncTask<Boolean, String, Boolean>{
    private final static String TAG = "DataToSQLAsyncTask";
    public static final String JSON_FILE ="indietracks.json";
    private final static String INTERNET_JSON_PATH = "http://www.roadsidepoppies.com/indietracks/indietracks.json";

    private Activity activity;
    private Context context;
    private ProgressDialog dialog;
    static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public DataToSQLAsyncTask(Activity activity) {
        this.activity = activity;
        this.context = activity.getBaseContext();
    }

    @Override
    protected void onPreExecute() {
        Log.d(TAG, "onPreExecute");
        if (!isCancelled()) {
            dialog = ProgressDialog.show(activity, "", "Preparing Indietracks 2013 Festival Guide", true);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Log.d(TAG, "onProgressUpdate");
        dialog.setMessage(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean loaded) {
        Log.d(TAG, "onPostExecute");
        dialog.dismiss();
        super.onPostExecute(loaded);
        new DataLoaderAsyncTask(this.activity).execute(false);
    }

    @Override
    protected Boolean doInBackground(Boolean... loadFromInternet) {
        Log.d(TAG, "doInBackground");
        Boolean loaded = false;

        SharedPreferences prefs = context.getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, Context.MODE_PRIVATE);

        JSONObject jsonData = null;
        try{
            if (loadFromInternet[0]) {
                jsonData = fetchFromInternet();
            } else {
                jsonData = readFromFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (jsonData != null && jsonData.has(IndietracksApplication.DATAVERSION)) {
            try {
                int currentDataVersion = prefs.getInt(IndietracksApplication.DATAVERSION, 0);
                int newDataVersion = jsonData.getInt(IndietracksApplication.DATAVERSION);
                //if(true) {
                if (newDataVersion > currentDataVersion) {
                    publishProgress("Updating data...");
                    storeNewData(jsonData, newDataVersion);
                    loaded = true;
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(IndietracksApplication.DATAVERSION, newDataVersion);
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                loaded = false;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                loaded = false;
            } catch (ParseException e) {
                e.printStackTrace();
                loaded = false;
            } catch (IndietracksDataException e) {
                e.printStackTrace();
                loaded = false;
            }
        }
        return loaded;
    }

    JSONObject readFromFile() throws IOException, JSONException {
        JSONObject jsonData;
        publishProgress("Loading from file");
        InputStream jsonFile = context.getAssets().open(JSON_FILE);
        BufferedInputStream stream = new BufferedInputStream(jsonFile);
        publishProgress("Loaded from file");
        jsonData = streamToJson(stream);
        return jsonData;
    }

    JSONObject fetchFromInternet() throws MalformedURLException, IOException, JSONException {
        JSONObject jsonData;
        publishProgress("Fetching from internet");
        URL jsonURL = new URL(INTERNET_JSON_PATH);
        URLConnection connection = jsonURL.openConnection();
        BufferedInputStream stream = new BufferedInputStream(jsonURL.openStream());
        publishProgress("Fetched from internet");
        jsonData = streamToJson(stream);
        return jsonData;
    }

    JSONObject streamToJson(BufferedInputStream stream) throws JSONException {
        StringBuilder jsonBuilder = new StringBuilder();
        Scanner scanner = new Scanner(stream, "UTF-8");
        publishProgress("Reading data file...");

        try {
            while (scanner.hasNextLine()) {
                String jsonLine = scanner.nextLine();
                jsonBuilder.append(jsonLine);
            }
        }
        finally {
            scanner.close();
        }
        JSONTokener tokener = new JSONTokener(jsonBuilder.toString());
        JSONObject jsonData = (JSONObject) tokener.nextValue();
        return jsonData;
    }

    void storeNewData(JSONObject jsonData, int dataVersion) throws JSONException, MalformedURLException, ParseException, IndietracksDataException {
        IndietracksDataHelper helper = new IndietracksDataHelper(activity.getApplicationContext(), dataVersion);
        JSONArray locationList = jsonData.getJSONArray("locations");
        JSONArray artistList = jsonData.getJSONArray("artists");
        Map<String, Location> locationMap =  storeLocations(helper, locationList);
        storeArtistsAndEvents(helper, artistList, locationMap);
    }

    void storeArtistsAndEvents(IndietracksDataHelper helper, JSONArray artistList, Map locationMap) throws JSONException, MalformedURLException, ParseException, IndietracksDataException {
        for (int i=0; i < artistList.length(); i++) {
            JSONObject jsonArtist = artistList.getJSONObject(i);
            Artist artist = new Artist();
            artist.name = jsonArtist.getString("name");
            artist.sortName = jsonArtist.getString("sortName");
            if (jsonArtist.has("image")) {
                artist.image = jsonArtist.getString("image");
            }
            if (jsonArtist.has("url")) {
                artist.link = new URL(jsonArtist.getString("url"));
            }
            if (jsonArtist.has("music_url")) {
                artist.musicLink = new URL(jsonArtist.getString("music_url"));
            }
            if (jsonArtist.has("interview_url")) {
                artist.interviewLink = new URL(jsonArtist.getString("interview_url"));
            }
            if (jsonArtist.has("description")) {
                artist.description = jsonArtist.getString("description");
            }
            if (jsonArtist.has("events")) {
                addEvents(artist, locationMap, jsonArtist.getJSONArray("events"));
            }
            publishProgress("Storing " + artist.name);
            helper.addArtist(artist);
        }
    }

    void addEvents(Artist artist, Map<String, Location> locationMap, JSONArray jsonEvents) throws JSONException, ParseException, IndietracksDataException {
        for(int i=0; i < jsonEvents.length(); i++) {
            JSONObject jsonEvent = jsonEvents.getJSONObject(i);
            Event event = new Event();
            String locationName = jsonEvent.getString("stage");
            event.location = locationMap.get(locationName);
            String dayString = jsonEvent.getString("day");
            String startString = jsonEvent.getString("start");
            String endString = jsonEvent.getString("end");
            Date startDate = timeFormat.parse(startString);
            Date endDate = timeFormat.parse(endString);
            Date dayDate = dayFormat.parse(dayString);

            Calendar start = new GregorianCalendar(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
            start.setTime(startDate);
            event.start = start;

            Calendar end = new GregorianCalendar(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
            end.setTime(endDate);
            event.end = end;

            Calendar day = new GregorianCalendar(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
            day.setTime(dayDate);
            event.day = day;

            long milliDuration =  end.getTimeInMillis() - start.getTimeInMillis();
            event.duration = (int) (milliDuration / 60000l);

            if (start.getTimeInMillis() > end.getTimeInMillis()) {
                throw new IndietracksDataException("Data error in event with " + artist.name);

            }
            if (event.location == null) {
                throw new IndietracksDataException("Data error in location with " + artist.name);
            }
            artist.events.add(event);
        }
    }

    Map<String, Location> storeLocations(IndietracksDataHelper helper, JSONArray jsonLocations) throws JSONException {
        Map<String, Location> locationMap = new HashMap<>();
        for (int i = 0; i < jsonLocations.length(); i++) {
            String locationName = jsonLocations.getString(i);
            Location location = new Location();
            location.name = locationName;
            location.sortOrder = i;
            helper.addLocation(location);
            locationMap.put(location.name, location);
        }
        return locationMap;
    }
}
