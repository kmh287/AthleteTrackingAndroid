package cs5150athletetracking.com.athletetracking.Util;


import android.content.SharedPreferences;

public class PreferenceUtil {

    private PreferenceUtil(){}

    public static void writeToPrefs(SharedPreferences prefs, String key, String value){
        SharedPreferences.Editor editor =  prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void writeToPrefs(SharedPreferences prefs, String key, int value){
        SharedPreferences.Editor editor =  prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int readPrefs(SharedPreferences prefs, String key, int fallback){
        return prefs.getInt(key, fallback);
    }

    public static String readPrefs(SharedPreferences prefs, String key, String fallback){
        return prefs.getString(key, fallback);
    }

    public static void clearPrefs(SharedPreferences prefs){
        prefs.edit().clear().commit();
    }

}
