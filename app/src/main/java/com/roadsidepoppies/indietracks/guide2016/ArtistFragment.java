package com.roadsidepoppies.indietracks.guide2016;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roadsidepoppies.indietracks.guide2016.alarm.EventAlarmManager;
import com.roadsidepoppies.indietracks.guide2016.data.Artist;
import com.roadsidepoppies.indietracks.guide2016.data.Event;
import com.roadsidepoppies.indietracks.guide2016.data.Festival;
import com.roadsidepoppies.indietracks.guide2016.settings.SettingsActivity;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {

    private static final String TAG = "ArtistFragment";

    public static final String ARTIST_NAME = "ArtistNameField";
    static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    static SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.UK);

    Festival data;
    Artist artist;
    View artistView;
    Activity parentActivity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        parentActivity = (IndietracksMainActivity) getActivity();
        IndietracksApplication application = (IndietracksApplication) parentActivity.getApplication();
        data = application.getFestival(getActivity());

        if (savedInstanceState != null && savedInstanceState.containsKey(ARTIST_NAME)) {
            String sortedArtistName = savedInstanceState.getString(ARTIST_NAME);
            artist = data.artistNameMap.get(sortedArtistName);
        }
        setupArtistFields();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        artistView = inflater.inflate(R.layout.artist, container, false);
        return artistView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        if (artist != null) {
            outState.putString(ARTIST_NAME, artist.name);
        }
        super.onSaveInstanceState(outState);
    }

    public void setupArtistFields() {
        Log.d(TAG, "setupArtistFields");
        Bundle message = getArguments();
        if (message != null && message.containsKey(ARTIST_NAME)) {
            String artistName = message.getString(ARTIST_NAME);
            artist = data.artistNameMap.get(artistName);
        }

        String descText = "";
        String titleText = "";
        if (artist != null) {
            titleText = artist.name;
            if (artist.description != null) {
                descText = artist.description;
            } else {
                descText = "";
            }
            if (artist.link != null || artist.musicLink != null || artist.interviewLink != null) {
                descText += "\n\n";
            }
            if (artist.link != null) {
                descText += "• " + artist.link + "\n";
            }
            if (artist.musicLink != null) {
                descText += "• " + artist.musicLink + "\n";
            }
            if (artist.interviewLink != null) {
                descText += "• " + artist.interviewLink + "\n";
            }
            if (artist.link != null || artist.musicLink != null || artist.interviewLink != null) {
                descText += "\n";
            }

            if (artist.image != null) {
                ImageView photoView = (ImageView) artistView.findViewById(R.id.photo);
                int resource = getResources().getIdentifier(artist.image,
                        "drawable", parentActivity.getPackageName());
                photoView.setImageResource(resource);
            }
        }

        TextView title = (TextView) artistView.findViewById(R.id.displayname);

        title.setText(titleText);
        TextView description = (TextView) artistView.findViewById(R.id.description);
        description.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(description, Linkify.WEB_URLS);
        description.setText(descText);

        if (artist != null) {
            LinearLayout eventContainer = (LinearLayout) artistView.findViewById(R.id.event_container);
            for (final Event event : artist.events) {
                if (event != null) {
                    final SharedPreferences prefs = parentActivity.getSharedPreferences(IndietracksApplication.INDIETRACKS_PREFERENCES, Context.MODE_PRIVATE);
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View row = inflater.inflate(R.layout.artist_event_row, eventContainer, false);
                    TextView detailsView = (TextView) row.findViewById(R.id.event_text);
                    String eventStart = String.format("%s - %s", timeFormat.format(event.start.getTime()), timeFormat.format(event.end.getTime()));
                    String eventDay = dayFormat.format(event.start.getTime());
                    detailsView.setText(eventDay + " " + eventStart + " " + event.location.name);
                    eventContainer.addView(row);
                    final ImageView imageView = (ImageView) row.findViewById(R.id.alarm_icon);
                    Drawable icon = EventAlarmManager.getIcon(event, prefs, parentActivity);
                    imageView.setImageDrawable(icon);
                    View detailsContainer = artistView.findViewById(R.id.event_container);
                    EventClickListener clickListener = new EventClickListener(event, prefs, imageView);
                    detailsContainer.setOnClickListener(clickListener);
                    artistView.refreshDrawableState();
                }
            }
        } else {
            TextView textView = (TextView) artistView.findViewById(R.id.eventHeader);
            textView.setText("");
        }
    }


    class EventClickListener implements View.OnClickListener {
        Event event;
        SharedPreferences prefs;
        ImageView imageView;

        EventClickListener(Event event, SharedPreferences prefs, ImageView imageView) {
            this.event = event;
            this.prefs = prefs;
            this.imageView = imageView;
        }

        @Override
        public void onClick(View v) {
            String eventKey = event.getKey();
            String message;
            if (prefs.getBoolean(eventKey, false)) {
                message = String.format("Removing alarm for %1s", artist.name);
                EventAlarmManager.removeAlarm(v.getContext(), event, prefs);
            } else {
                Calendar cal = new GregorianCalendar(
                        event.start.get(Calendar.YEAR),
                        event.start.get(Calendar.MONTH),
                        event.start.get(Calendar.DATE),
                        event.start.get(Calendar.HOUR_OF_DAY),
                        event.start.get(Calendar.MINUTE));
                cal.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
                String advanceString = prefs.getString(SettingsActivity.ALARMADVANCE, EventAlarmManager.DEFAULT_ADVANCE);
                int advance = Integer.parseInt(advanceString);
                cal.add(Calendar.MINUTE, - advance);
                Calendar now = GregorianCalendar.getInstance();
                now.setTimeZone(TimeZone.getTimeZone(IndietracksApplication.TIMEZONE));
                if (now.before(cal)) {
                    message = String.format(
                            "Setting alarm for %1s %s", artist.name, timeFormat.format(cal.getTime()));
                    EventAlarmManager
                            .addAlarm(v.getContext(), event, prefs);
                } else {
                    message = String.format("%1s %s is in the past", artist.name, timeFormat.format(cal.getTime()));
                }
            }
            Drawable icon = EventAlarmManager.getIcon(event, prefs, parentActivity);
            imageView.setImageDrawable(icon);
            Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();

        }
    };

}
