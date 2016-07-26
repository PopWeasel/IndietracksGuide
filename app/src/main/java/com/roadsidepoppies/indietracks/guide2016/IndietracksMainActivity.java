package com.roadsidepoppies.indietracks.guide2016;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
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
        actionBar.setDisplayHomeAsUpEnabled(true);
        /*
        if (findViewById(R.id.artistContent) != null) {
            isTwoFragmentLayout = true;
        }
        */
        artistListFragment = (ArtistListFragment) getSupportFragmentManager().findFragmentByTag(ARTISTLIST_FRAGMENT);
        if (artistListFragment == null) {
            artistListFragment = new ArtistListFragment();
        }
        artistFragment = (ArtistFragment) getSupportFragmentManager().findFragmentByTag(ARTIST_FRAGMENT);

        if (savedInstanceState == null)  {
            //TODO change this
            displayArtistList();
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
    public void onArtistSelected(String name) {
        Log.d(TAG, "onArtistSelected: " + name);
        displayArtist(name);
    }

    public void displayArtistList() {
        currentFragmentTag = ARTISTLIST_FRAGMENT;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.indietacks_content, artistListFragment, currentFragmentTag);
        if (getSupportFragmentManager().findFragmentById(R.id.indietacks_content) != null)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public void displayArtist(String artistSortName) {
        currentArtistName = artistSortName;
        Bundle message = new Bundle();
        message.putString(ArtistFragment.ARTIST_NAME, artistSortName);
        artistFragment = new ArtistFragment();
        artistFragment.setArguments(message);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isTwoFragmentLayout) {
            //TODO replace this once two-fragment view defined
            /*
            transaction.replace(R.id.artistContent, artistFragment, ARTIST_FRAGMENT);
            if (getSupportFragmentManager().findFragmentById(R.id.artistContent) != null)
                transaction.addToBackStack(null);
            */
        } else {
            currentFragmentTag = ARTIST_FRAGMENT;
            transaction.replace(R.id.indietacks_content, artistFragment, ARTIST_FRAGMENT);
            if (getSupportFragmentManager().findFragmentById(R.id.indietacks_content) != null) {
                transaction.addToBackStack(null);
            }
        }
        transaction.commit();
    }
}
