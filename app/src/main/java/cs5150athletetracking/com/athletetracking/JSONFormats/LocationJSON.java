package cs5150athletetracking.com.athletetracking.JSONFormats;


import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationJSON extends JSONObject {

    private final String TAG = "LocationJSON";
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.US);

    private enum SERIALIZE_KEY{
        USER, TIMESTAMP, LATITUDE, LONGITUDE, ALTITUDE;

        @Override
        public String toString(){
            switch(this){
                case USER:
                    return "user";
                case TIMESTAMP:
                    return "t";
                case LATITUDE:
                    return "lat";
                case LONGITUDE:
                    return "long";
                case ALTITUDE:
                    return "alt";
                default:
                    return "";
            }
        }
    }

    public LocationJSON(String username, double latitude,
                        double longitude, double altitude){

        String timestamp = format.format(new Date());

        try {
            this.put(SERIALIZE_KEY.USER.toString(),     username);
            this.put(SERIALIZE_KEY.TIMESTAMP.toString(),timestamp);
            this.put(SERIALIZE_KEY.LATITUDE.toString(), latitude);
            this.put(SERIALIZE_KEY.LONGITUDE.toString(),longitude);
            this.put(SERIALIZE_KEY.ALTITUDE.toString(), altitude);
        } catch (JSONException e){
            Log.e(TAG, "Problem in constructor", e);
        }
    }

    public String getUsername() throws JSONException{
        return this.getString(SERIALIZE_KEY.USER.toString());
    }

    public String getTimestamp() throws JSONException{
        return this.getString(SERIALIZE_KEY.TIMESTAMP.toString());
    }

    public double getLatitude() throws JSONException{
        return this.getDouble(SERIALIZE_KEY.LATITUDE.toString());
    }

    public double getLongitude() throws JSONException{
        return this.getDouble(SERIALIZE_KEY.LONGITUDE.toString());
    }

    public double getAltitude() throws JSONException{
        return this.getDouble(SERIALIZE_KEY.ALTITUDE.toString());
    }


}
