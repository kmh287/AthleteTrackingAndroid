package cs5150athletetracking.com.athletetracking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cs5150athletetracking.com.athletetracking.LocationRecorder.LocationRecorder;

public class TrackingActivity extends AppCompatActivity {

    private LocationRecorder locRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        final Button trackingButton = (Button) findViewById(R.id.trackingButton);
        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locRecorder == null || locRecorder.hasError()) {
                    locRecorder = new LocationRecorder("test", TrackingActivity.this);
                    locRecorder.start();
                    trackingButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
