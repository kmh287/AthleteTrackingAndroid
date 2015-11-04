package cs5150athletetracking.com.athletetracking.JSONFormats;


import org.json.JSONException;

import cs5150athletetracking.com.athletetracking.AthleteTrackerTestCase;

public class RaceSelectionJSONTest extends AthleteTrackerTestCase {

    public void testConstructor() throws JSONException{
        RaceSelectionJSON badRaceSelectionJSON1 = new RaceSelectionJSON(null, null);
        RaceSelectionJSON badRaceSelectionJSON2 = new RaceSelectionJSON("foo", null);
        RaceSelectionJSON badRaceSelectionJSON3 = new RaceSelectionJSON(null, "fake 5k");

        RaceSelectionJSON raceSelectionJSON = new RaceSelectionJSON("b0b", "philly half");
        assertEquals("b0b", raceSelectionJSON.get(AbstractJSONFormat.SERIALIZE_KEY.USERNAME.toString()));
        assertEquals("philly half", raceSelectionJSON.get(AbstractJSONFormat.SERIALIZE_KEY.RACE.toString()));
    }

}
