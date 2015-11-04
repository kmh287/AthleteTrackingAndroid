package cs5150athletetracking.com.athletetracking.Util;


import android.content.Context;
import android.content.SharedPreferences;

import cs5150athletetracking.com.athletetracking.AthleteTrackerTestCase;

public class PreferenceUtilTest extends AthleteTrackerTestCase {

    public void testWriteAndRead(){
        SharedPreferences prefs = getPrefs();
        PreferenceUtil.clearPref(prefs, "testkey");
        int badLookup = PreferenceUtil.readPrefs(prefs, "testKey", -1);
        assertEquals(-1, badLookup);
        PreferenceUtil.writeToPrefs(prefs, "testkey", 47);
        int goodLookup = PreferenceUtil.readPrefs(prefs, "testkey", -1);
        assertEquals(47, goodLookup);
    }

    private SharedPreferences getPrefs(){
        return getContext().getSharedPreferences("test", Context.MODE_PRIVATE);
    }

}
