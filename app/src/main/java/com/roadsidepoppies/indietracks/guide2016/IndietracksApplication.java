package com.roadsidepoppies.indietracks.guide2016;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.roadsidepoppies.indietracks.guide2016.data.Festival;
import com.roadsidepoppies.indietracks.guide2016.sql.DataLoaderAsyncTask;

/**
 * Created by maq on 12/07/2016.
 */
public class IndietracksApplication extends Application {
    public final static String TAG = "IndietracksApplication";

    public static final String ALARMEVENT_KEY = "eventKey";
    public static final String INDIETRACKS_PREFERENCES = "IndietracksPreferences";
    public static final String TIMEZONE = "Europe/London";
    public static final String DATAVERSION = "DataVersion";

    private Festival festival;

    public Festival getFestival(Activity activity) {
        if (festival == null) {
            Log.d(TAG, "Indietracks application data = null... loading from SQLite");
            new DataLoaderAsyncTask(activity).execute(false);
        }
        return festival;
    }

    public void setFestival(Festival festival) {
        this.festival = festival;
    }


}
