package com.roadsidepoppies.indietracks.guide2016.sql;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;

import com.roadsidepoppies.indietracks.guide2016.IndietracksApplication;
import com.roadsidepoppies.indietracks.guide2016.data.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by maq on 12/07/2016.
 */
public class DataLoaderAsyncTask extends  AsyncTask<Boolean, String, Boolean>{
    private final static String TAG = "DataLoaderAsyncTask";
    public static final String JSON_FILE ="indietracks.json";
    private final static String INTERNET_JSON_PATH = "http://www.roadsidepoppies.com/indietracks/indietracks.json";

    private Activity activity;
    private Context context;
    private ProgressDialog dialog;

    public DataLoaderAsyncTask(Activity activity) {
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
                if(true) {
                //if (newDataVersion > currentDataVersion) {
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
        publishProgress("Reading data...");

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

    void storeNewData(JSONObject jsonData, int dataVersion) throws JSONException {
        IndietracksDataHelper helper = new IndietracksDataHelper(activity.getApplicationContext(), dataVersion);
        JSONArray locationList = jsonData.getJSONArray("locations");
        for (int i = 0; i < locationList.length(); i++) {
            int order = locationList.length() - i;
            String locationName = locationList.getString(i);
            Location location = new Location();
            location.name = locationName;
            location.sortOrder = order;
            helper.addLocation(location);
        }
        ArrayList<Location> locations = helper.getLocations();
        locations.size();

    }

}
