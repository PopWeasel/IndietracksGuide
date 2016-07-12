package com.roadsidepoppies.indietracks.guide2016.data;

import android.support.v7.util.SortedList;

import java.net.URL;

/**
 * Created by maq on 12/07/2016.
 */
public class Artist {
    public String name;
    public String sortName;
    public String image;
    public String description;
    public URL link;
    public URL musicLink;
    public URL interviewLink;
    public SortedList<Event> events;
}
