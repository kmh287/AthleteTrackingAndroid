package cs5150athletetracking.com.athletetracking;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.atomic.AtomicReference;

import cs5150athletetracking.com.athletetracking.LocationRecorder.LocationRecorder;

public class TrackingActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_tracking);

        final Button statusBar = (Button) findViewById(R.id.status_button);

        final UIStatusCallback callback = new TrackingStatusCallback(statusBar);

        final Button trackingButton = (Button) findViewById(R.id.trackingButton);
        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locRecorder == null || locRecorder.hasError()) {
                    locRecorder = new LocationRecorder(username.get(), TrackingActivity.this, callback);
                    locRecorder.start();
                    trackingButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle){
        // TODO
    }

    private class TrackingStatusCallback extends UIStatusCallback {
        private final Button statusBar;

        public TrackingStatusCallback(Button statusBar) {
            this.statusBar = statusBar;
        }

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
