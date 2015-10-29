package cs5150athletetracking.com.athletetracking.JSONFormats;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

public class LocationUploadJSON extends AbstractJSONFormat {

    private static final String TAG = "LocationUploadJSON";

    public LocationUploadJSON(String username, List<JSONObject> locations) throws JSONException{

        JSONArray jsonArray = new JSONArray();

        for (JSONObject location : locations){
            jsonArray.put(location);
        }

        this.put(SERIALIZE_KEY.TYPE.toString(), REQUEST_TYPE.LOCATION_UPLOAD.ordinal());
        this.put(SERIALIZE_KEY.USERNAME.toString(), username);
        this.put(SERIALIZE_KEY.TIMESTAMP.toString(), format.format(new Date()));
        this.put(SERIALIZE_KEY.DATA.toString(), jsonArray);
    }

}
