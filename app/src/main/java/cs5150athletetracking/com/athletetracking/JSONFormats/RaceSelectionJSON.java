package cs5150athletetracking.com.athletetracking.JSONFormats;


import org.json.JSONException;

import java.util.Date;

public class RaceSelectionJSON extends AbstractJSONFormat {

    private static final String TAG = "RaceSelectionJSON";

    public RaceSelectionJSON(String username, String race) throws JSONException{
        String timestamp = format.format(new Date());

        this.put(SERIALIZE_KEY.TYPE.toString(), REQUEST_TYPE.RACE_SELECTION.ordinal());
        this.put(SERIALIZE_KEY.USERNAME.toString(), username);
        this.put(SERIALIZE_KEY.RACE.toString(), race);
    }

}
