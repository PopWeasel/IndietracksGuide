package com.roadsidepoppies.indietracks.guide2018;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.roadsidepoppies.indietracks.guide2018.data.Event;
import com.roadsidepoppies.indietracks.guide2018.data.Festival;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ListIterator;
import java.util.TimeZone;

public class TimelineFragment extends Fragment {


    private final static String TAG="TimelineFragment";
    private static final String EVENT_POSITION_IN_LIST = "EventPositionInList";

    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    IndietracksApplication application;
    IndietracksMainActivity parentActivity;
    Festival data;

    OnArtistSelected callBack;
    List<Event> pendingEvents = new ArrayList<>();
    EventListAdaptor adapter;

    int currentListPosition = -1;
    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        parentActivity = (IndietracksMainActivity) getActivity();
        application = (IndietracksApplication) parentActivity.getApplication();
        data = application.getFestival(parentActivity);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        if (listView != null) {
            currentListPosition = listView.getFirstVisiblePosition();
            Log.d(TAG, "Current position in list is " + currentListPosition);
            if (currentListPosition > 0) {
                outState.putInt( EVENT_POSITION_IN_LIST, currentListPosition);
            }
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        getPendingEvents();
        FrameLayout timelineView = (FrameLayout) inflater.inflate(R.layout.timeline, container, false);
        listView = new ListView(parentActivity);
        adapter = new EventListAdaptor(parentActivity, R.layout.timeline_row, pendingEvents, true);
        listView.setAdapter(adapter);
        timelineView.addView(listView);
        if (savedInstanceState != null && savedInstanceState.containsKey(EVENT_POSITION_IN_LIST)) {
            currentListPosition = savedInstanceState.getInt(EVENT_POSITION_IN_LIST);
            if (currentListPosition < pendingEvents.size())
                listView.setSelection(currentListPosition);
        }
        return timelineView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        getPendingEvents();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    void getPendingEvents() {
        Calendar now = GregorianCalendar.getInstance();
        now.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
        pendingEvents.clear();
        // not the most efficient method - it might be worth considering looping
        // through days
        // and added whole days at a time and then looping through individual
        // events on current day

        for (ListIterator<Event> it = data.events
                .listIterator(data.events.size()); it.hasPrevious();) {
            Event event = it.previous();
            if (now.before(event.end)) {
                pendingEvents.add(event);
            } else {
                Log.d(TAG,
                        "Skipping all events before "+ timeFormat.format(event.end.getTime()));
                break;
            }
        }
        Collections.sort(pendingEvents);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callBack = (OnArtistSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArtistSelected");
        }
    }

}
