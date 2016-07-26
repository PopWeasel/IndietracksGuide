package com.roadsidepoppies.indietracks.guide2016;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roadsidepoppies.indietracks.guide2016.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    private static final String TAG = "ScheduleFragment";

    private static final String SELECTED_TAB_TAG = "selected_tab";
    private static final String SELECTED_LOCATION = "selected_location";

    SimpleDateFormat dateFormat=new SimpleDateFormat("EEEE", Locale.UK);

    OnArtistSelected callBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.schedule, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
