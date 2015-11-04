package cs5150athletetracking.com.athletetracking.Callbacks;


import android.os.Looper;

import cs5150athletetracking.com.athletetracking.AthleteTrackerTestCase;

public class UIStatusCallbackTest extends AthleteTrackerTestCase {

    public void testRunsOnMainThread(){
        UIStatusCallback callback = new UIStatusCallback() {
            @Override
            protected void greenCallback(String message) {
                assertOnMainThread();
            }

            @Override
            protected void yellowCallback(String message) {
                assertOnMainThread();
            }

            @Override
            protected void redCallback(String message) {
                assertOnMainThread();
            }
        };
        callback.red("fus");
        callback.yellow("ro");
        callback.green("da");
    }

    private void assertOnMainThread(){
        Looper looper = Looper.myLooper();
        Looper mainLooper = Looper.getMainLooper();
        assertSame(looper, mainLooper);
    }

}
