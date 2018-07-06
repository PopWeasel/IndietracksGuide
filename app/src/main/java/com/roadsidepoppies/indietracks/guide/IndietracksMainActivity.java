package com.roadsidepoppies.indietracks.guide;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.roadsidepoppies.indietracks.guide.data.Event;

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
    ScheduleFragment scheduleFragment;
    TimelineFragment timeLineFragment;
    ArtistFragment artistFragment;
    String currentFragmentTag;
    String currentArtistName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        //MAQ - debugging only
        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());

        setContentView(R.layout.activity_indietracks_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.indietracks_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" Indietracks ");
        actionBar.setIcon(R.mipmap.ic_indietracks);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.artist_content) != null) {
            isTwoFragmentLayout = true;
        }

        artistListFragment = (ArtistListFragment) getSupportFragmentManager().findFragmentByTag(ARTISTLIST_FRAGMENT);
        if (artistListFragment == null) {
            artistListFragment = new ArtistListFragment();
        }
        scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag(SCHEDULE_FRAGMENT);
        if (scheduleFragment == null) {
            scheduleFragment = new ScheduleFragment();
        }
        timeLineFragment = (TimelineFragment) getSupportFragmentManager().findFragmentByTag(TIMELINE_FRAGMENT);
        if (timeLineFragment == null) {
            timeLineFragment = new TimelineFragment()
            ;
        }
        artistFragment = (ArtistFragment) getSupportFragmentManager().findFragmentByTag(ARTIST_FRAGMENT);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            if (extras.containsKey(EVENTKEY)) {
                String eventKey = extras.getString(EVENTKEY);
                Event event = ((IndietracksApplication) getApplication()).getFestival(this).eventKeyMap.get(eventKey);
                Log.d(TAG, "Recieved intent that must have come from alarm for " + eventKey);
                displayArtist(event.artist.sortName);
                return;
            }
        }

        if (savedInstanceState == null)  {
            displaySchedule();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.action_artist:
                displayArtistList();
                return true;
            case R.id.action_schedule:
                displaySchedule();
                return true;
            case R.id.action_timeline:
                displayTimeline();
                return true;
            case R.id.action_info:
                displayInfo();
                return true;
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);

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
            transaction.replace(R.id.artist_content, artistFragment, ARTIST_FRAGMENT);
            if (getSupportFragmentManager().findFragmentById(R.id.artist_content) != null)
                transaction.addToBackStack(null);
        } else {
            currentFragmentTag = ARTIST_FRAGMENT;
            transaction.replace(R.id.indietacks_content, artistFragment, ARTIST_FRAGMENT);
            if (getSupportFragmentManager().findFragmentById(R.id.indietacks_content) != null) {
                transaction.addToBackStack(null);
            }
        }
        transaction.commit();
    }

    public void displayTimeline() {
        currentFragmentTag = TIMELINE_FRAGMENT;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.indietacks_content, timeLineFragment, currentFragmentTag);
        if (getSupportFragmentManager().findFragmentById(R.id.indietacks_content) != null)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public void displaySchedule() {
        currentFragmentTag = SCHEDULE_FRAGMENT;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.indietacks_content, scheduleFragment, currentFragmentTag);
        if (getSupportFragmentManager().findFragmentById(R.id.indietacks_content) != null)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    public void displayInfo() {
        intent.setAction("com.roadsidepoppies.indietracks.guide.INFO");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
