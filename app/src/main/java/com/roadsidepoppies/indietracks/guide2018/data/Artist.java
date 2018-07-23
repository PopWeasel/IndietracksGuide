package com.roadsidepoppies.indietracks.guide2018.data;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maq on 12/07/2016.
 */
public class Artist implements Comparable<Artist>{
    public long identifier;
    public String name;
    public String sortName;
    public String image;
    public String description;
    public URL link;
    public URL musicLink;
    public URL interviewLink;
    public List<Event> events = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        return this.sortName.equals(((Artist) o).sortName);
    }

    @Override
    public int compareTo(Artist artist) {
        return sortName.compareTo(artist.sortName);
    }
}
