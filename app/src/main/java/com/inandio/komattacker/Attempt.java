package com.inandio.komattacker;

/**
 * Created by parodi on 07/10/2015.
 */
public class Attempt {
    int     athleteID;
    boolean isSuccessful;
    String  segmentName;
    long    segmentID;

    public Attempt(int athleteID, boolean isSuccessful, String segmentName, long segmentID) {
        this.athleteID = athleteID;
        this.isSuccessful = isSuccessful;
        this.segmentName = segmentName;
        this.segmentID = segmentID;
    }
}
