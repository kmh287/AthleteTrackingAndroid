package cs5150athletetracking.com.athletetracking.JSONFormats;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginJSON extends JSONObject {

    private final String TAG = "LoginJSON";
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.US);

    private enum SERIALIZE_KEY{
        USERNAME, PASSWORD, TIMESTAMP;

        @Override
        public String toString(){
            switch(this){
                case USERNAME:
                    return "user";
                case PASSWORD:
                    return "pass";
                case TIMESTAMP:
                    return "t";
                default:
                    return "";
            }
        }
    }

    public LoginJSON(String username, String password) throws JSONException{
        String timestamp = format.format(new Date());
        this.put(SERIALIZE_KEY.USERNAME.toString(), username);
        this.put(SERIALIZE_KEY.PASSWORD.toString(), password);
    }





}
