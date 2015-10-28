package cs5150athletetracking.com.athletetracking.Http;


import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Uploader {

    private static final String INPUT_LABEL = "params";
    private static final String TAG = "Uploader";
    private static final String URL_STRING = "http://ec2-54-165-208-160.compute-1.amazonaws.com/php/test_server.php";
    private static final int END_OF_STREAM = -1;
    private static final int HTTP_OK = 200;
    private static final int SUCCESS = 1;
    private static final int FAILURE = 0;

    private String response;

    public Integer upload(JSONObject json){
        Integer result = FAILURE;

        try {
            byte[] jsonPostBytes = (URLEncoder.encode(INPUT_LABEL, "UTF-8") + '=' +
                    json.toString()).getBytes("UTF-8");

            // Open up a connection, create appropriate header, and send request
            URL url = new URL(URL_STRING);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(1000*30 /* thirty seconds */);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(jsonPostBytes.length));
//            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(jsonPostBytes);

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == HTTP_OK) {
                StringBuilder message = new StringBuilder();

                Reader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                for ( int c = in.read(); c != END_OF_STREAM; c = in.read() )
                    message.append( (char)c);

                response = message.toString();
                //TODO parse return JSON
                Log.d(TAG, response);
                result = SUCCESS;
            } else {
                result = FAILURE;
                Log.e(TAG, "HTTP Response Code Not OK");
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return result;
    }

    public String getResponse() {
        return response;
    }

}
