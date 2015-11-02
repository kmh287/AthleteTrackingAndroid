package cs5150athletetracking.com.athletetracking;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import cs5150athletetracking.com.athletetracking.Callbacks.ResultCallable;
import cs5150athletetracking.com.athletetracking.Callbacks.UIStatusCallback;
import cs5150athletetracking.com.athletetracking.Http.AsyncUploader;
import cs5150athletetracking.com.athletetracking.JSONFormats.RaceSelectionJSON;
import cs5150athletetracking.com.athletetracking.LocationRecorder.LocationRecorder;
import cs5150athletetracking.com.athletetracking.Util.PreferenceUtil;

public class TrackingActivity extends AppCompatActivity {

    private static final String TAG = "TrackingActivity";

    private static final String LOCATION_TRACKER_STATUS_PREF = "locationTrackerStatus";
    private static final String USERNAME_PREF = "username";
    private static final String RACE_PREF = "race";

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
    private final AtomicReference<String> race = new AtomicReference<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String restoredUsername = getUsername();
        if (restoredUsername.equals("")) {
            // Force user to login again.. This should never happen
            finish();
            Log.e(TAG, "Username not passed through properly.");
        }
        this.username.set(restoredUsername);
        final ArrayList<String> races = getRaces();

        setContentView(R.layout.activity_tracking);

        // Setup the status bar and tracking button
        final Button statusBar = (Button) findViewById(R.id.status_button);
        final UIStatusCallback callback = new TrackingStatusCallback(statusBar);
        final Button trackingButton = (Button) findViewById(R.id.trackingButton);
        trackingButton.setOnClickListener(new View.OnClickListener() {
            // When the user presses the button to begin recording,
            // lazily create the location recorder.
            @Override
            public void onClick(View v) {
                createLocationRecorder(username.get(), callback, trackingButton);
            }
        });
        trackingButton.setVisibility(View.INVISIBLE);
        trackingButton.setClickable(false);     //Not clickable until race selected

        // Set up spinner to allow user to select race
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                                         R.layout.spinner_layout,
                                         races);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, false);
        spinner.setOnItemSelectedListener(new RaceSelectionClickListener(races, trackingButton, spinner));

    }

    private ArrayList<String> getRaces(){
        ArrayList<String> races = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            races = extras.getStringArrayList("races");
        }
        if (races == null){
            finish();
            Log.e(TAG, "Races not passed through properly.");
        }
        return races;
    }

    @NonNull
    private String getUsername() {
        // Get the username from the data passed through from
        // login.
        String user = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            user = extras.getString("username", "");
        }
        return user;
    }

    /**Create a new location recorder if it hasn't been created yet or it has an error.
     *
     * @param username The user's username (needed for transmitting data)
     * @param callback A callback to call when status changes between red, yellow, and green
     * @param trackingButton The button that, when pressed, started location recording
     */
    private void createLocationRecorder(String username, UIStatusCallback callback, Button trackingButton) {
        if (locRecorder == null || locRecorder.hasError()) {
            locRecorder = new LocationRecorder(username, TrackingActivity.this, callback);
            locRecorder.start();
            trackingButton.setVisibility(View.INVISIBLE);
        }
    }

    // Save actiivity state on pause
    @Override
    protected void onPause(){
        super.onPause();
        PreferenceUtil.writeToPrefs(getPrefs(), "locationTrackerStatus", status.get().ordinal());
        PreferenceUtil.writeToPrefs(getPrefs(), "username", username.get());
        PreferenceUtil.writeToPrefs(getPrefs(), "race", race.get());
    }

    /**
     *  Restore activity state on resume. Need to restore the username
     *  and the current status.
     */
    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences prefs = getPrefs();
        int statusInt = PreferenceUtil.readPrefs(prefs, LOCATION_TRACKER_STATUS_PREF, -1);
        String restoredUsername = PreferenceUtil.readPrefs(prefs, USERNAME_PREF, "");
        String restoredRaceName = PreferenceUtil.readPrefs(prefs, RACE_PREF, "");
        PreferenceUtil.clearPref(prefs, LOCATION_TRACKER_STATUS_PREF);
        PreferenceUtil.clearPref(prefs, USERNAME_PREF);
        PreferenceUtil.clearPref(prefs, RACE_PREF);

        if (statusInt == -1){
            // IF we somehow can't restore the state, we should
            // go back to the red state and wait for the user
            // to restart recording
            statusInt = Status.RED.ordinal();
        }

        if ("".equals(restoredUsername)){
            // Try to get from the extras bundle if it's not in preferences
            restoredUsername = getUsername();
            // If the username is STILL the empty string, we can't recover.
            if ("".equals(restoredUsername)) {
                finish(); // User needs to login again. Ideally this never happens
            }
        }

        Status restoredStatus =  Status.values()[statusInt];
        status.set(restoredStatus);
        username.set(restoredUsername);
        race.set(restoredRaceName);

        final Button statusBar = (Button) findViewById(R.id.status_button);
        final Button trackingButton = (Button) findViewById(R.id.trackingButton);
        final UIStatusCallback callback = new TrackingStatusCallback(statusBar);
        final TextView currentRaceTextView = (TextView) findViewById(R.id.currentRace);

        if (restoredStatus.equals(Status.YELLOW) || restoredStatus.equals(Status.GREEN)){
            // In yellow and green states, user is already recording
            // so we should re-create the location recorder.
            currentRaceTextView.setText(getString(R.string.currentRace, restoredRaceName));
            createLocationRecorder(restoredUsername, callback, trackingButton);
        }
    }

    @Override
    public void onBackPressed(){
        //Sorry, you can't leave this activity. Muhahahahaha
    }

    private SharedPreferences getPrefs(){
        return getSharedPreferences("RNRPrefs", MODE_PRIVATE);
    }


    // Listener for when the user selects a race from the dropdown
    private class RaceSelectionClickListener implements AdapterView.OnItemSelectedListener {
        private final ArrayList<String> races;
        private final Button trackingButton;
        private final Spinner spinner;

        public RaceSelectionClickListener(ArrayList<String> races, Button trackingButton, Spinner spinner) {
            this.races = races;
            this.trackingButton = trackingButton;
            this.spinner = spinner;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            AsyncUploader asyncUploader = new AsyncUploader();
            ResultCallable callBack = RaceSelectionResultCallback(races.get(position));
            callBack.success();
            //TODO reove above call and uncomment below code

//            asyncUploader.setCallBack(callBack);
//
//            // Build RaceSelectionJSON
//            RaceSelectionJSON json;
//            try {
//                String race = races.get(position);
//                json = new RaceSelectionJSON(username.get(), race);
//            } catch (JSONException e){
//                callBack.failure();
//                return;
//            }
//            asyncUploader.execute(json);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}

        @NonNull
        private ResultCallable RaceSelectionResultCallback(final String raceName) {
            return new ResultCallable() {
                @Override
                public void success() {
                    race.set(raceName);
                    trackingButton.setClickable(true);
                    //Remove the spinner and hide it from view
                    hideSpinner();
                    //Display the current race name
                    showText();
                    // Display the button to start tracking
                    showTrackingButton();
                }

                private void showTrackingButton(){
                    trackingButton.setVisibility(View.VISIBLE);
                    trackingButton.setClickable(true);
                }

                private void showText() {
                    TextView currentRaceTextView = (TextView) findViewById(R.id.currentRace);
                    currentRaceTextView.setText(getString(R.string.currentRace, raceName));
                }

                private void hideSpinner() {
                    spinner.setClickable(false);
                    spinner.setVisibility(View.INVISIBLE);
                }

                @Override
                public void failure() {
                    // We do nothing here. Let the user keep trying.
                }
            };
        }
    }

/**
     * The callback used by the location recorder. There is quite
     * a bit of ugliness here to ensure backwards compatability to
     * older target phones.
     */
    private class TrackingStatusCallback extends UIStatusCallback {
        private final Button statusBar;

        public TrackingStatusCallback(Button statusBar) {
            this.statusBar = statusBar;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void greenCallback(String message) {
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
        public void yellowCallback(String message) {
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
        public void redCallback(String message) {
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
