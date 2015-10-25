package cs5150athletetracking.com.athletetracking;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.atomic.AtomicReference;

import cs5150athletetracking.com.athletetracking.LocationRecorder.LocationRecorder;

public class TrackingActivity extends AppCompatActivity {

    private enum STATUS{
        DISCONNECTED, /*red */
        TRANSMITTING /* green */
    }

    private LocationRecorder locRecorder;

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

        final StatusCallback callback = new TrackingStatusCallback(statusBar);

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

    private class TrackingStatusCallback implements StatusCallback {
        private final Button statusBar;

        public TrackingStatusCallback(Button statusBar) {
            this.statusBar = statusBar;
        }

        @Override
        public void green(String message) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                statusBar.setBackgroundColor(getResources().getColor(R.color.transmitting, null));
            } else{
                statusBar.setBackgroundColor(getResources().getColor(R.color.transmitting));
            }
            statusBar.setText(message);
        }

        @Override
        @TargetApi(Build.VERSION_CODES.M)
        public void yellow(String message) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                statusBar.setBackgroundColor(getResources().getColor(R.color.disconnected, null));
            } else {
                statusBar.setBackgroundColor(getResources().getColor(R.color.disconnected));
            }
            statusBar.setText(message);
        }

        @Override
        public void red(String message) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                statusBar.setBackgroundColor(getResources().getColor(R.color.cornellRedDark, null));
            } else{
                statusBar.setBackgroundColor(getResources().getColor(R.color.cornellRedDark));
            }
            statusBar.setText(message);
        }
    }
}
