package com.roadsidepoppies.indietracks.guide2017.data;

/**
 * Created by maq on 12/07/2016.
 */
public class Location implements Comparable<Location>{
    public long identifier;
    public String name;
    public int sortOrder;

    @Override
    public boolean equals(Object o) {
        return this.sortOrder == (((Location) o).sortOrder);
    }

    @Override
    public int compareTo(Location location) {
        int cmp;
        if (this.sortOrder > location.sortOrder) {
            cmp = 1;
        } else if (this.sortOrder < location.sortOrder) {
            cmp = -1;
        } else {
            cmp = 0;
        }
        return cmp;
    }
}
