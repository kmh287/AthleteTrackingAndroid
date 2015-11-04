package cs5150athletetracking.com.athletetracking.JSONFormats;


import org.json.JSONException;

import cs5150athletetracking.com.athletetracking.AthleteTrackerTestCase;

public class LoginJSONTest extends AthleteTrackerTestCase{

    public void testConstructor() throws JSONException{
        // Bad login JSONs, should not necessarily throw Exceptions
        LoginJSON badLoginJSON1 = new LoginJSON(null, null);
        LoginJSON badLoginJSON2 = new LoginJSON("username", null);
        LoginJSON badLoginJSON3 = new LoginJSON(null, "hunter2");

        LoginJSON loginJSON = new LoginJSON("foo", "hunter2");
        assertEquals("foo", loginJSON.get(AbstractJSONFormat.SERIALIZE_KEY.USERNAME.toString()));
        assertEquals("hunter2", loginJSON.get(AbstractJSONFormat.SERIALIZE_KEY.PASSWORD.toString()));
    }

}
