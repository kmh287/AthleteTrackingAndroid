package cs5150athletetracking.com.athletetracking.JSONFormats;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoginJSON extends JSONObject {

    private static final String TAG = "LoginJSON";
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.US);

    public LoginJSON(String username, String password) throws JSONException{
        String timestamp = format.format(new Date());
        this.put(SerializeKey.USERNAME.toString(), username);
        this.put(SerializeKey.PASSWORD.toString(), password);
        this.put(SerializeKey.TIMESTAMP.toString(), password);
    }





}
