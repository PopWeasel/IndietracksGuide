package com.roadsidepoppies.indietracks.guide2016;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.roadsidepoppies.indietracks.guide2016.sql.DataLoaderAsyncTask;

/**
 * Created by maq on 12/07/2016.
 */
public class InitialiseDataActivity extends AppCompatActivity {

    private static final String TAG = "InitialiseDataActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new DataLoaderAsyncTask(this).execute(false);
    }
}
