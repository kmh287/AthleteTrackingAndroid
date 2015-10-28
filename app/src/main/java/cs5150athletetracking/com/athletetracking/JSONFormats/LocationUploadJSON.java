package cs5150athletetracking.com.athletetracking.JSONFormats;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationUploadJSON extends JSONObject {

    private static final String TAG = "LocationUploadJSON";
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.US);

    public LocationUploadJSON(String username,
                              List<JSONObject> locations) throws JSONException{
        JSONArray jsonArray = new JSONArray();
        for (JSONObject location : locations){
            jsonArray.put(location);
        }
        this.put(SerializeKey.USERNAME.toString(), username);
        this.put(SerializeKey.TIMESTAMP.toString(), format.format(new Date()));
        this.put(SerializeKey.DATA.toString(), jsonArray);
    }

}
