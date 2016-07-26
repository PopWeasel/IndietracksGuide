package com.roadsidepoppies.indietracks.guide2016;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;

import com.roadsidepoppies.indietracks.guide2016.R;
import com.roadsidepoppies.indietracks.guide2016.data.Event;
import com.roadsidepoppies.indietracks.guide2016.data.Festival;
import com.roadsidepoppies.indietracks.guide2016.data.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    private static final String TAG = "ScheduleFragment";

    private static final String SELECTED_TAB_TAG = "selected_tab";
    private static final String SELECTED_LOCATION = "selected_location";

    SimpleDateFormat dateFormat=new SimpleDateFormat("EEEE", Locale.UK);

    OnArtistSelected callBack;

    String openTab;
    String openLocation;
    TabHost host;
    Festival data;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        IndietracksMainActivity parentActivity = (IndietracksMainActivity) getActivity();
        IndietracksApplication application = (IndietracksApplication) parentActivity.getApplication();
        data = application.getFestival(parentActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");

        View scheduleView = inflater.inflate(R.layout.schedule, container, false);
        host = (TabHost) scheduleView.findViewById(R.id.tabHost);
        host.setup();
        for (final Calendar day : data.days) {
            String dayName = dateFormat.format(day.getTime());
            TabHost.TabSpec spec = host.newTabSpec(dayName);
            spec.setIndicator(dayName);
            spec.setContent(new TabHost.TabContentFactory() {
                @Override
                public View createTabContent(String tag) {
                    View view = null;
                    view = createDayView(day);
                    /*
                    if (cachedTabViews.containsKey(dayOfWeekNumber)) {
                        Log.d(TAG, "Re-using existing tab view for day = "
                                + dayOfWeekNumber);
                        view = cachedTabViews.get(dayOfWeekNumber);
                    }else {
                          view = createDayView(day);
                        cachedTabViews.put(dayOfWeekNumber, view);
                    }
                    */
                    return view;
                }
            });
            host.addTab(spec);
        }
        resetState();
        return scheduleView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SELECTED_TAB_TAG))
                openTab = savedInstanceState.getString(SELECTED_TAB_TAG);
            if (savedInstanceState.containsKey(SELECTED_LOCATION))
                openLocation = savedInstanceState.getString(SELECTED_LOCATION);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState called saving tab state");
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        if (host != null) {
            outState.putString(SELECTED_TAB_TAG, host.getCurrentTabTag());
            Spinner locationSpinner = (Spinner) host.getCurrentView().findViewById(R.id.locationlist);
            outState.putString(SELECTED_LOCATION, (String) locationSpinner.getSelectedItem());
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onPause() {
        if (host != null) {
            openTab = host.getCurrentTabTag();
            Spinner locationSpinner = (Spinner) host.getCurrentView().findViewById(R.id.locationlist);
            openLocation =  (String) locationSpinner.getSelectedItem();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (host != null && openLocation != null && openTab != null)
            resetState();
    }

    void resetState() {
        if (openTab != null && openLocation != null) {
            host.setCurrentTabByTag(openTab);
            Spinner locationSpinner = (Spinner) host.getCurrentView().findViewById(R.id.locationlist);
            ArrayAdapter<String> locationAdaptor = (ArrayAdapter<String>) locationSpinner.getAdapter();
            int locationIndex = locationAdaptor.getPosition(openLocation);
            locationSpinner.setSelection(locationIndex);
        } else {
            Calendar today = GregorianCalendar.getInstance();
            today.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
            String todayName = dateFormat.format(today.getTime());
            Log.d(TAG, "Today is " + todayName);
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            if (data.days.contains(today)) {
                host.setCurrentTabByTag(todayName);
            }
        }
    }

    View createDayView(final Calendar day){
        Log.d(TAG, "Creating Day View for " + day.get(Calendar.DAY_OF_WEEK));
        SortedSet<Location> locations = new TreeSet<>();
        final Map<String, Location> locationNameMap = new HashMap<>();
        for (Location location : data.schedule.get(day).keySet()) {
            locations.add(location);
            locationNameMap.put(location.name, location);
        }
        List<String> sortedLocations = new ArrayList<String>();
        for (Location location : locations) {
            sortedLocations.add(location.name);
        }
        final View dayTab = getActivity().getLayoutInflater().inflate(R.layout.schedule_tab, null, false);

        Spinner locationsSpinner = (Spinner) dayTab.findViewById(R.id.locationlist);
        ArrayAdapter<String> locationAdaptor = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sortedLocations);
        locationAdaptor.setDropDownViewResource(android.R.layout.select_dialog_item);
        locationsSpinner.setAdapter(locationAdaptor);
        locationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String location = (String) parent.getItemAtPosition(position);

                ListView eventList = (ListView) dayTab.findViewById(R.id.eventlist);
                List<Event> events = new ArrayList<Event>(data.schedule.get(day).get(locationNameMap.get(location)));
                eventList.removeAllViewsInLayout();
                eventList.setAdapter(new EventListAdaptor(getActivity(),R.layout.event_row, events, false));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return dayTab;
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
