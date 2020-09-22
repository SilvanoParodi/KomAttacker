package com.inandio.komattacker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by parodi on 09/09/2015.
 */

public final class Settings {
    public static Boolean compareWithAthletesSameGender = true;
    public static Boolean showSimulationButtons = true;
    public static Boolean useVocalAssistant = true;

    public static void readSettings(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        Settings.compareWithAthletesSameGender = sharedPrefs.getBoolean("prefGenderComparison", true);
        Settings.showSimulationButtons = sharedPrefs.getBoolean("prefDeveloperMode", true);
        Settings.useVocalAssistant =  sharedPrefs.getBoolean("prefVocalAssistant", false);
    }
}