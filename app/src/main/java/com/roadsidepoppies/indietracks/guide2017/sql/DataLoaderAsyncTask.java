package com.roadsidepoppies.indietracks.guide2016.sql;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;

import com.roadsidepoppies.indietracks.guide2016.IndietracksApplication;
import com.roadsidepoppies.indietracks.guide2016.data.Festival;

/**
 * Created by maq on 18/07/2016.
 */
public class DataLoaderAsyncTask extends AsyncTask<Boolean, String, Festival> {
    private final static String TAG = "DataToSQLAsyncTask";

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
            dialog = ProgressDialog.show(activity, "", "Loading indietracks 2016 data", true);
            dialog.getWindow().setGravity(Gravity.BOTTOM);
        }
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Festival festival) {
        Log.d(TAG, "onPostExecute");
        IndietracksApplication application = (IndietracksApplication) activity.getApplication();
        application.setFestival(festival);
        dialog.dismiss();
        Intent intent = new Intent("com.roadsidepoppies.indietracks.guide2016.INDIETRACKS");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
        super.onPostExecute(festival);

    }

    @Override
    protected void onProgressUpdate(String... values) {
        Log.d(TAG, "onProgressUpdate");
        dialog.setMessage(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected Festival doInBackground(Boolean... booleen) {
        publishProgress("Loading from database...");
        SharedPreferences prefs = context.getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, Context.MODE_PRIVATE);
        int dataVersion = prefs.getInt(IndietracksApplication.DATAVERSION, 0);
        IndietracksDataHelper helper = new IndietracksDataHelper(activity.getApplicationContext(), dataVersion);
        Festival festival = null;
        try {
            festival = helper.getFestivalData();
        } catch (Exception e) {
            e.printStackTrace();;
        }
        return festival;
    }
}
