package cs5150athletetracking.com.athletetracking;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationJSON extends JSONObject {

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

    private final String TAG = "LocationJSON";

    public LocationJSON(String username, String timestamp,
                        double latitude, double longitutde,
                        double altitude){
        try {
            this.put(SERIALIZE_KEY.USER.toString(),     username);
            this.put(SERIALIZE_KEY.TIMESTAMP.toString(),timestamp);
            this.put(SERIALIZE_KEY.LATITUDE.toString(), latitude);
            this.put(SERIALIZE_KEY.LONGITUDE.toString(),longitutde);
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
