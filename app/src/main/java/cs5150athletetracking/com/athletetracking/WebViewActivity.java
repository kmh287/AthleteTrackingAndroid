package cs5150athletetracking.com.athletetracking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private final String REGISTRATION_URL = "http://www.google.com";
    private final String SPECTATOR_URL = "http://www.reddit.com";
    private final int REGISTER = 0;
    private final int SPECTATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int reqType = SPECTATE; // Fallback behavior
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            reqType = extras.getInt("reqType", SPECTATE);
        }
        String url;
        if (reqType == REGISTER){
            url = REGISTRATION_URL;
        } else {
            url = SPECTATOR_URL;
        }

        setContentView(R.layout.activity_web_view);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }
}
