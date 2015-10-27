package cs5150athletetracking.com.athletetracking;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.atomic.AtomicReference;

import cs5150athletetracking.com.athletetracking.LocationRecorder.LocationRecorder;
import cs5150athletetracking.com.athletetracking.Util.PreferenceUtil;

public class TrackingActivity extends AppCompatActivity {

    public static final String LOCATION_TRACKER_STATUS_PREF = "locationTrackerStatus";
    public static final String USERNAME_PREF = "username";

    private enum Status{
        /**
         * The user hasn't selected a race yet, an error has destroyed the location recorder.
         * The status should ONLY be red at the very beginning before recording starts, or if
         * we've encountered an unrecoverable error.
         */
        RED,
        /**
         * The user has just started recording but hasn't uploaded yet, or the last upload to the
         * server didn't succeed. The status should ONLY be yellow if we are in a recoverable error
         * state. (Upload problems are considered resolved when the next upload succeeds)
         */
        YELLOW,
        /**
         * The user has been recording for enough time to trigger an upload and uploads are going
         * through normally. We may need to tweak this to provide a better user experience. The
         * status should ONLY be green if our most recent connection to the server succeeded.
         */
        GREEN
    }

    private LocationRecorder locRecorder;
    private final AtomicReference<Status> status = new AtomicReference<>(Status.RED);
    private final AtomicReference<String> username = new AtomicReference<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final AtomicReference<String> username = new AtomicReference<>("");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username.set(extras.getString("username", ""));
        }
        if (username.get().equals("")){
            throw new RuntimeException("Username not passed through correctly:" + username);
        }
        this.username.set(username.get());
        setContentView(R.layout.activity_tracking);

        final Button statusBar = (Button) findViewById(R.id.status_button);

        final StatusCallback callback = new TrackingStatusCallback(statusBar);

        final Button trackingButton = (Button) findViewById(R.id.trackingButton);
        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLocationRecorder(username.get(), callback, trackingButton);
            }
        });
    }

    private void createLocationRecorder(String username, StatusCallback callback, Button trackingButton) {
        if (locRecorder == null || locRecorder.hasError()) {
            locRecorder = new LocationRecorder(username, TrackingActivity.this, callback);
            locRecorder.start();
            trackingButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        PreferenceUtil.writeToPrefs(getPrefs(), "locationTrackerStatus", status.get().ordinal());
        PreferenceUtil.writeToPrefs(getPrefs(), "username", username.get());
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences prefs = getPrefs();
        int statusInt = PreferenceUtil.readPrefs(prefs, LOCATION_TRACKER_STATUS_PREF, -1);
        String username = PreferenceUtil.readPrefs(prefs, USERNAME_PREF, "");
        PreferenceUtil.clearPref(prefs, LOCATION_TRACKER_STATUS_PREF);
        PreferenceUtil.clearPref(prefs, USERNAME_PREF);
        if (statusInt == -1){
            return;
        }
        if ("".equals(username)){
            finish(); // User needs to login again. Ideally this never happens
        }
        Status restoredStatus =  Status.values()[statusInt];
        status.set(restoredStatus);

        final Button statusBar = (Button) findViewById(R.id.status_button);
        final Button trackingButton = (Button) findViewById(R.id.trackingButton);
        final StatusCallback callback = new TrackingStatusCallback(statusBar);

        if (!restoredStatus.equals(Status.RED)){
            createLocationRecorder(username, callback, trackingButton);
        }
    }

    @Override
    public void onBackPressed(){
        //Sorry, you can't leave this activity. Muhahahahaha
    }

    private SharedPreferences getPrefs(){
        return getSharedPreferences("RNRPrefs", MODE_PRIVATE);
    }

    private class TrackingStatusCallback implements StatusCallback {
        private final Button statusBar;

        public TrackingStatusCallback(Button statusBar) {
            this.statusBar = statusBar;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void green(String message) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                statusBar.setBackgroundColor(getResources().getColor(R.color.transmitting, null));
            } else{
                statusBar.setBackgroundColor(getResources().getColor(R.color.transmitting));
            }
            statusBar.setText(message);
            status.set(Status.GREEN);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void yellow(String message) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                statusBar.setBackgroundColor(getResources().getColor(R.color.disconnected, null));
            } else {
                statusBar.setBackgroundColor(getResources().getColor(R.color.disconnected));
            }
            statusBar.setText(message);
            status.set(Status.YELLOW);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void red(String message) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                statusBar.setBackgroundColor(getResources().getColor(R.color.cornellRedDark, null));
            } else{
                statusBar.setBackgroundColor(getResources().getColor(R.color.cornellRedDark));
            }
            statusBar.setText(message);
            status.set(Status.RED);
        }
    }
}
