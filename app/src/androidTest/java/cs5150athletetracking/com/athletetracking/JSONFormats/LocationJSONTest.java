package cs5150athletetracking.com.athletetracking.JSONFormats;

import org.json.JSONException;

import cs5150athletetracking.com.athletetracking.AthleteTrackerTestCase;

/**
 * Created by Kevin on 11/4/15.
 */
public class LocationJSONTest extends AthleteTrackerTestCase{

    public void testConstructorDoesntThrowException() {
        LocationJSON bad1 = new LocationJSON(null, -1.2, -1.2);
    }

    public void testProperInfoSet() throws JSONException {
        String username = "Foobarbazrah";
        double latitude = 47.47;
        double longitude = 94.94;
        LocationJSON locationJSON = new LocationJSON(username, latitude, longitude);
        assertEquals(username, locationJSON.getUsername());
        assertEquals(latitude, locationJSON.getLatitude());
        assertEquals(longitude, locationJSON.getLongitude());

        assertEquals(username, locationJSON.get(AbstractJSONFormat.SERIALIZE_KEY.USERNAME.toString()));
        assertEquals(latitude, locationJSON.get(AbstractJSONFormat.SERIALIZE_KEY.LATITUDE.toString()));
        assertEquals(longitude, locationJSON.get(AbstractJSONFormat.SERIALIZE_KEY.LONGITUDE.toString()));
    }

}
