package com.roadsidepoppies.indietracks.guide2016.sql;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by maq on 12/07/2016.
 */
public class DataLoaderTask extends  AsyncTask<String, Integer, String>{
    private Context context;

    public DataLoaderTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... filenames) {
        StringBuffer contents = new StringBuffer();
        for(String filename : filenames) {
            try {
                InputStream jsonFile = context.getAssets().open(filename);
                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonFile));
                String line =  reader.readLine();
                while(line != null) {
                    contents.append(line);
                    line = reader.readLine();
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return contents.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
