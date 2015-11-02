package cs5150athletetracking.com.athletetracking.JSONFormats;


import org.json.JSONException;

import java.util.Date;

public class LoginJSON extends AbstractJSONFormat {

    private static final String TAG = "LoginJSON";

    public LoginJSON(String username, String password) throws JSONException{
        String timestamp = format.format(new Date());

        this.put(SERIALIZE_KEY.TYPE.toString(), REQUEST_TYPE.LOGIN.ordinal());
        this.put(SERIALIZE_KEY.USERNAME.toString(), username);
        this.put(SERIALIZE_KEY.PASSWORD.toString(), password);
        this.put(SERIALIZE_KEY.TIMESTAMP.toString(), password);
    }

}
