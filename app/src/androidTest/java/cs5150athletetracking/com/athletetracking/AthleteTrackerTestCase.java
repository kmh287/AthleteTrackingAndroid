package cs5150athletetracking.com.athletetracking;


import android.app.Application;
import android.test.ApplicationTestCase;
import static org.mockito.Mockito.*;

public class AthleteTrackerTestCase extends ApplicationTestCase<Application> {

    // All of our test cases should extend this class.
    // Add any methods / values to this class that you think will be useful in
    // more than one test.

    public AthleteTrackerTestCase(){
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
