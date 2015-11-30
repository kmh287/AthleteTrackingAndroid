package cs5150athletetracking.com.athletetracking;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import io.testfire.Testfire;
import io.testfire.TestfireParamCrashReporting;
import io.testfire.TestfireParamGesture;
import io.testfire.TestfireParamLocationTracking;
import io.testfire.TestfireParamLogIntegration;
import io.testfire.TestfireParamLogLevel;
import io.testfire.TestfireParamSettings;
import io.testfire.TestfireParamVideoQuality;
import io.testfire.TestfireParams;
import io.testfire.TestfireParamsBuilder;

/**
 * This is the activity that opens when the app is launched. From
 * this activity, the user can choose to continue as an athlete
 * or spectator.
 */
public class UserTypeSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTestfire();
        setContentView(R.layout.activity_user_type_selection);
        final Button athleteButton = (Button) findViewById(R.id.athlete_button);
        final Button spectatorButton = (Button) findViewById(R.id.spectator_button);
        athleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When the athlete button is clicked, start a new LoginActivity
            public void onClick(View v) {
                startActivity(new Intent(UserTypeSelectionActivity.this, LoginActivity.class));
            }
        });
        spectatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When the spectator button is clicked, start a new SpectatorActivity
            public void onClick(View v) {
                startActivity(new Intent(UserTypeSelectionActivity.this, SpectatorActivity.class));
            }
        });
    }

    /**
     * This starts our user testing SDK.
     */
    private void startTestfire(){

        Resources res = getResources();

        TestfireParams params = TestfireParamsBuilder.testfireParams()
                                                     .withApiKey("QoZgEyzof1eKC8811iqa")
                                                     .withAppId("gjiGFG7z_H_JoJ-CVzyx")
                                                     .withApplication(this)
                                                     .withCrashReporting(TestfireParamCrashReporting.ENABLED)
                                                     .withGesture(TestfireParamGesture.NOTIFICATION)
                                                     .withVideoQuality(TestfireParamVideoQuality.MEDIUM)
                                                     .withSettingsMode(TestfireParamSettings.ENABLED)
                                                     .withLogIntegration(TestfireParamLogIntegration.LOGCAT)
                                                     .withLogLevel(TestfireParamLogLevel.DEBUG)
                                                     .withLocationTracking(TestfireParamLocationTracking.ENABLED)
                                                     .build();
        Testfire.initialize(params);
    }

}
