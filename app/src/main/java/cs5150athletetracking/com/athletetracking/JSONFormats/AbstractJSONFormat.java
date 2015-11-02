package cs5150athletetracking.com.athletetracking.JSONFormats;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;


public abstract class AbstractJSONFormat extends JSONObject {

    protected static final SimpleDateFormat format =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.US);

    protected static enum REQUEST_TYPE {
        LOGIN, RACE_SELECTION, LOCATION_UPLOAD
    }

    /**
     * Enums are expensive on android. Use one common one rather than
     * multiple enums in each of the JSON classes.
     */
    protected static enum SERIALIZE_KEY {
        /* Common */
        USERNAME, TIMESTAMP, TYPE,
        /* Race Selection JSON */
        RACE,
        /* Login JSON */
        PASSWORD,
        /*Location JSON */
        LATITUDE, LONGITUDE,
        /* Location Upload */
        DATA;

        @Override
        public String toString(){
            switch(this){
                case USERNAME:
                    return "user";
                case TIMESTAMP:
                    return "t";
                case TYPE:
                    return "type";
                case LATITUDE:
                    return "lat";
                case LONGITUDE:
                    return "long";
                case DATA:
                    return "data";
                case PASSWORD:
                    return "pass";
                case RACE:
                    return "race";
                default:
                    return "";
            }
        }

    }

}
