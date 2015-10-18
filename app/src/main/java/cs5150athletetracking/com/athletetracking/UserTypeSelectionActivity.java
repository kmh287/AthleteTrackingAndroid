package cs5150athletetracking.com.athletetracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserTypeSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_selection);
        final Button athleteButton = (Button) findViewById(R.id.athlete_button);
        final Button spectatorButton = (Button) findViewById(R.id.spectator_button);
        athleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserTypeSelectionActivity.this, LoginActivity.class));
            }
        });
        spectatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserTypeSelectionActivity.this, WebViewActivity.class);
                intent.putExtra("reqType", 1 /*spectator */);
                startActivity(intent);
            }
        });
    }
}
