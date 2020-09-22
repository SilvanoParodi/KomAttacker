package com.inandio.komattacker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inandio.komattacker.entities.segment.SegmentEffort;

import java.util.ArrayList;

/**
 * Created by parodi on 22/09/2015.
 */
public class AttemptPersister {
    static final String attemptsKey = "attempts";


    public static Boolean saveAttempt(int athleteId, Boolean successfulAttempt, SegmentEffort oldEffort, Context context)
    {
        int oldIndex = -1;
        Attempt oldAttempt = null;

        if (Common.attempts == null)
        {
            return false;
        }

        for (int i = 0; i < Common.attempts.size(); i++)
        {
            Attempt attempt = Common.attempts.get(i);
            if (attempt.segmentID == oldEffort.getSegment().getId() && attempt.athleteID == athleteId) {
                oldIndex = i;
                oldAttempt = attempt;
                break;
            }
        }

        if (oldIndex != -1) {
            oldAttempt.isSuccessful = successfulAttempt;
            Common.attempts.remove(oldIndex);
            Common.attempts.add(0, oldAttempt);
        }
        else {
            Attempt attempt = new Attempt(athleteId, successfulAttempt, oldEffort.getSegment().getName(), oldEffort.getSegment().getId());
            Common.attempts.add(0, attempt);
        }
        saveToSharedPreferences(context);
        return true;
    }

    private static void saveToSharedPreferences(Context context) {
        Gson gson = new Gson();
        String jsonAttempts = gson.toJson(Common.attempts);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(attemptsKey, jsonAttempts).commit();
    }

    public static ArrayList<Attempt> retrieveFromSharedPreferences(Context context, int athleteID) {

        SharedPreferences prefs;
        ArrayList<Attempt> tempAttemptsPerAthlete = null;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.contains(attemptsKey)) {
            String jsonFavorites = prefs.getString(attemptsKey, null);
            Gson gson = new Gson();
            tempAttemptsPerAthlete = gson.fromJson(jsonFavorites, new TypeToken<ArrayList<Attempt>>(){}.getType());
        } else
            return null;

        //filtro che ritorna solo attempts di atleta
        for (int i = tempAttemptsPerAthlete.size() - 1; i >= 0; i--)
        {
            Attempt attempt = tempAttemptsPerAthlete.get(i);
            if (attempt.athleteID != athleteID) {
                tempAttemptsPerAthlete.remove(i);
            }
        }
        return tempAttemptsPerAthlete;
    }
}
