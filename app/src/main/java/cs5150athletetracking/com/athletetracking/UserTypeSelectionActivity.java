package cs5150athletetracking.com.athletetracking;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * This is the activity that opens when the app is launched. From
 * this activity, the user can choose to continue as an athlete
 * or spectator.
 */
public class UserTypeSelectionActivity extends AppCompatActivity {

    private static final String SPECTATOR_URL = "http://52.91.63.121/main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startTestfire();
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
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SPECTATOR_URL));
                startActivity(intent);
            }
        });
    }

}
