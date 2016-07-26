package com.roadsidepoppies.indietracks.guide2016;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class IndietracksMainActivity extends AppCompatActivity implements OnArtistSelected {

    public static final String TAG = "IndietracksMainActivity";

    static final String ARTISTLIST_FRAGMENT = "artislistFragment";
    static final String SCHEDULE_FRAGMENT = "scheduleFragment";
    static final String TIMELINE_FRAGMENT = "timelineFragment";
    static final String ARTIST_FRAGMENT = "artistFragment";
    public static final String EVENTKEY = "eventAlarmKey";

    private static Intent intent = new Intent();

    boolean isTwoFragmentLayout = false;
    ArtistListFragment artistListFragment;
    //ScheduleFragment scheduleFragment;
    //TimelineFragment timeLineFragment;
    ArtistFragment artistFragment;
    String currentFragmentTag;
    String currentArtistName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        //MAQ - debugging only
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());

        setContentView(R.layout.activity_indietracks_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.indietracks_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Indietracks ");
        actionBar.setIcon(R.mipmap.ic_indietracks);
        /*
        if (findViewById(R.id.artistContent) != null) {
            isTwoFragmentLayout = true;
        }
        */
        if (savedInstanceState == null) {
            artistListFragment = new ArtistListFragment();
            artistFragment = new ArtistFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.indietacks_content, artistListFragment).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.action_artist:
                break;
            case R.id.action_schedule:
                break;
            case R.id.action_timeline:
                break;
            case R.id.action_info:
                break;
        }
        return true;
    }


    @Override
    public void onArtistSelected(String sortName) {

    }
}
