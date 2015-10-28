package cs5150athletetracking.com.athletetracking.JSONFormats;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationJSON extends JSONObject {

    private final String TAG = "LocationJSON";
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.US);

    public LocationJSON(String username, double latitude,
                        double longitude, double altitude){

        String timestamp = format.format(new Date());

        try {
            this.put(SerializeKey.USERNAME.toString(),     username);
            this.put(SerializeKey.TIMESTAMP.toString(),timestamp);
            this.put(SerializeKey.LATITUDE.toString(), latitude);
            this.put(SerializeKey.LONGITUDE.toString(),longitude);
            this.put(SerializeKey.ALTITUDE.toString(), altitude);
        } catch (JSONException e){
            Log.e(TAG, "Problem in constructor", e);
        }
    }

    public String getUsername() throws JSONException{
        return this.getString(SerializeKey.USERNAME.toString());
    }

    public String getTimestamp() throws JSONException{
        return this.getString(SerializeKey.TIMESTAMP.toString());
    }

    public double getLatitude() throws JSONException{
        return this.getDouble(SerializeKey.LATITUDE.toString());
    }

    public double getLongitude() throws JSONException{
        return this.getDouble(SerializeKey.LONGITUDE.toString());
    }

    public double getAltitude() throws JSONException{
        return this.getDouble(SerializeKey.ALTITUDE.toString());
    }


}
