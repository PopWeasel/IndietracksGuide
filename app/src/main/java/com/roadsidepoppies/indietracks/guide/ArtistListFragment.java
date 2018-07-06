package com.roadsidepoppies.indietracks.guide;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.roadsidepoppies.indietracks.guide.data.Artist;
import com.roadsidepoppies.indietracks.guide.data.Festival;

import java.util.ArrayList;
import java.util.List;

public class ArtistListFragment extends ListFragment {
    public final static String TAG = "ArtistListFragment";
    private static final String ARTIST_POSITION_IN_LIST = "ArtistPositionInList";

    private OnArtistSelected listener;

    private List<Artist> artistList;

    int currentListPosition = -1;

    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(TAG, "Item " + i + " clicked");
            if (artistList != null) {

                Toast.makeText(getActivity().getApplicationContext(),
                        ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                ((OnArtistSelected) getActivity()).onArtistSelected(artistList.get(i).sortName);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        IndietracksApplication application = (IndietracksApplication) getActivity().getApplication();
        Festival festival = application.getFestival(getActivity());
        artistList = festival.artists;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.artist_row, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<String> namesList = new ArrayList<>(artistList.size());
        for(Artist artist : artistList) {
            namesList.add(artist.name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, namesList);
        ListView listView = getListView();
        listView.setOnItemClickListener(clickListener);
        //listView.setTextFilterEnabled(true);
        listView.setAdapter(adapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(ARTIST_POSITION_IN_LIST)) {
            currentListPosition = savedInstanceState.getInt(ARTIST_POSITION_IN_LIST);
            listView.setSelection(currentListPosition);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState");
        //outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        if (currentListPosition > 0) {
            outState.putInt(ARTIST_POSITION_IN_LIST, currentListPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        ListView listView = getListView();
        currentListPosition = listView.getSelectedItemPosition();
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArtistSelected) {
            listener = (OnArtistSelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnArtistSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
