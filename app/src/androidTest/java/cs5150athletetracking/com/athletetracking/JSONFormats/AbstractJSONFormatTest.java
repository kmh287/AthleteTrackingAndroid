package cs5150athletetracking.com.athletetracking.JSONFormats;


import cs5150athletetracking.com.athletetracking.AthleteTrackerTestCase;

public class AbstractJSONFormatTest extends AthleteTrackerTestCase {

    public void testRequestTypeEnum(){
        assertEquals(0, AbstractJSONFormat.REQUEST_TYPE.LOGIN.ordinal());
        assertEquals(1, AbstractJSONFormat.REQUEST_TYPE.RACE_SELECTION.ordinal());
        assertEquals(2, AbstractJSONFormat.REQUEST_TYPE.LOCATION_UPLOAD.ordinal());
    }

}
