package cs5150athletetracking.com.athletetracking.JSONFormats;


import android.util.Log;

import org.json.JSONException;

import java.util.Date;

public class LocationJSON extends AbstractJSONFormat {

    private final String TAG = "LocationJSON";

    public LocationJSON(String username, double latitude,
                        double longitude){

        String timestamp = format.format(new Date());

        try {
            this.put(SERIALIZE_KEY.USERNAME.toString(), username);
            this.put(SERIALIZE_KEY.TIMESTAMP.toString(),timestamp);
            this.put(SERIALIZE_KEY.LATITUDE.toString(), latitude);
            this.put(SERIALIZE_KEY.LONGITUDE.toString(),longitude);
        } catch (JSONException e){
            Log.e(TAG, "Problem in constructor", e);
        }
    }

    public String getUsername() throws JSONException{
        return this.getString(SERIALIZE_KEY.USERNAME.toString());
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
}
