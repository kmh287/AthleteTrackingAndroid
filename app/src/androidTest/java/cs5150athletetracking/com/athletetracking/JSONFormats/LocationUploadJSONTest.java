package cs5150athletetracking.com.athletetracking.JSONFormats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cs5150athletetracking.com.athletetracking.AthleteTrackerTestCase;

public class LocationUploadJSONTest extends AthleteTrackerTestCase {

    private static final int NUMBER_OF_DATAPOINTS = 100;
    private List<JSONObject> locs;
    private LocationUploadJSON goodJSON;

    @Override
    public void setUp() throws JSONException{
        locs = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < NUMBER_OF_DATAPOINTS; ++i){
            LocationJSON loc = new LocationJSON("user" + i, rand.nextDouble(), rand.nextDouble());
            locs.add(loc);
        }

        goodJSON = new LocationUploadJSON("foo", locs);
    }

    public void testConstructor() throws JSONException {
        LocationUploadJSON locationUploadJSON = new LocationUploadJSON(null, null);
        LocationUploadJSON locationUploadJSON2 = new LocationUploadJSON("foo", null);
        LocationUploadJSON locationUploadJSON3 = new LocationUploadJSON(null, locs);
        // NO exception at this point, yay!
    }

    public void testFinalJSON() throws JSONException{
        JSONArray data = goodJSON.getJSONArray(AbstractJSONFormat.SERIALIZE_KEY.DATA.toString());
        assertNotNull(data);
        assertEquals(NUMBER_OF_DATAPOINTS, data.length());

        for (int i = 0; i < NUMBER_OF_DATAPOINTS; ++i){
            JSONObject dataPoint = (JSONObject) data.get(i);
            JSONObject listDataPoint = locs.get(i);
            assertLocationJSONsEqual(dataPoint, listDataPoint);
        }

        assertEquals(AbstractJSONFormat.REQUEST_TYPE.LOCATION_UPLOAD.ordinal(),
                     goodJSON.get(AbstractJSONFormat.SERIALIZE_KEY.TYPE.toString()));
    }

    private void assertLocationJSONsEqual(JSONObject o1, JSONObject o2) throws JSONException{
        assertEquals(o1.get(AbstractJSONFormat.SERIALIZE_KEY.USERNAME.toString()),
                     o2.get(AbstractJSONFormat.SERIALIZE_KEY.USERNAME.toString()));

        assertEquals(o1.get(AbstractJSONFormat.SERIALIZE_KEY.LATITUDE.toString()),
                     o2.get(AbstractJSONFormat.SERIALIZE_KEY.LATITUDE.toString()));

        assertEquals(o1.get(AbstractJSONFormat.SERIALIZE_KEY.LONGITUDE.toString()),
                     o2.get(AbstractJSONFormat.SERIALIZE_KEY.LONGITUDE.toString()));

        assertEquals(o1.get(AbstractJSONFormat.SERIALIZE_KEY.TIMESTAMP.toString()),
                     o2.get(AbstractJSONFormat.SERIALIZE_KEY.TIMESTAMP.toString()));

    }

}
