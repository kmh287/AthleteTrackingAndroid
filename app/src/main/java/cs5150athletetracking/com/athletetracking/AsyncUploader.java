package cs5150athletetracking.com.athletetracking;

import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONObject;


/**
 * Created by mag94 on 10/19/15.
 */

public class AsyncUploader extends AsyncTask<JSONObject, String, Integer> implements ResultCallable {

    private static final String TAG = "Http Connection";
    private static final String urlStr = "http://ec2-54-165-208-160.compute-1.amazonaws.com/php/test_server.php";
    private static final int HTTP_OK = 200;

    public void success() {
        //
    }

    public void failure() {
        //
    }

    @Override
    protected Integer doInBackground(JSONObject... params) {
        Integer result = 0;

        try {
            int count = params.length;
            for (int i = 0; i < count; i++) {
                JSONObject param = params[i];
                StringBuilder jsonPost = new StringBuilder();
                Iterator keys = param.keys();

                // Format LocationJSON argument for the POST method
                // The format is: "key1=value1&key2=value2"
                while (keys.hasNext()) {
                    String key = (String)keys.next();
                    if (jsonPost.length() != 0)
                        jsonPost.append('&');
                    jsonPost.append(URLEncoder.encode(key, "UTF-8"));
                    jsonPost.append('=');
                    jsonPost.append(URLEncoder.encode(String.valueOf(param.get(key)), "UTF-8"));
                }

                byte[] jsonPostBytes = jsonPost.toString().getBytes("UTF-8");

                // Open up a connection, create appropriate header, and send request
                URL url = new URL(urlStr);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(jsonPostBytes.length));
                urlConnection.setDoOutput(true);
                urlConnection.getOutputStream().write(jsonPostBytes);

                int statusCode = urlConnection.getResponseCode();

                if (statusCode == HTTP_OK) {
                    String message = new String();

                    Reader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                    for ( int c = in.read(); c != -1; c = in.read() )
                        message += (char)c;

                    Log.d(TAG, message);
                    result = 1;
                } else {
                    result = 0;
                    Log.e(TAG, "HTTP Response Code Not OK");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return result;
    }


    @Override
    protected void onPostExecute(Integer result) {
        /* Sent LocationJson. Check for received confirmation */

        if(result == 1){
            this.success();
        } else{
            Log.e(TAG, "Failed to confirm communication with server.");
            this.failure();
        }
    }
}
