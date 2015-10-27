package cs5150athletetracking.com.athletetracking;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cs5150athletetracking.com.athletetracking.Util.PreferenceUtil;

public class SpectatorActivity extends AppCompatActivity {

    public static final String URL_PREF = "url";
    private final String SPECTATOR_URL = "http://www.reddit.com";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(SPECTATOR_URL);
        this.webView = webView;
    }

    /**
     * Use the back button for the web view unless at the very first page.
     * It's likely the spectators will not want to navigate back to the
     * previous activity from here for the lifetime of the app.
     */
    @Override
    public void onBackPressed(){
        if (webView != null && webView.canGoBack()){
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (webView != null) {
            PreferenceUtil.writeToPrefs(getPrefs(), "url", webView.getUrl());
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        String url = PreferenceUtil.readPrefs(getPrefs(), URL_PREF, SPECTATOR_URL);
        PreferenceUtil.clearPref(getPrefs(), URL_PREF);
        if (webView != null){
            webView.loadUrl(url);
        }
    }

    private SharedPreferences getPrefs(){
        return getSharedPreferences("RNRPrefs", MODE_PRIVATE);
    }

}
