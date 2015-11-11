package cs5150athletetracking.com.athletetracking.Http;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import cs5150athletetracking.com.athletetracking.Callbacks.ResultCallable;

/**
 * This class is an asynchronous wrapper around {@link Uploader}
 * JSONObjects passed in will be uploaded *separately* but still
 * asynchronously
 */
public class AsyncUploader extends AsyncTask<JSONObject, String, Boolean> {

    private static final String TAG = "AsyncUploader";
    //An optional callback to be called on success or failure of upload
    private ResultCallable callBack;
    private final Uploader uploader;

    public AsyncUploader(){
        this.uploader = new Uploader();
    }


    public void setCallBack(ResultCallable callBack) {
        this.callBack = callBack;
    }

    public ResultCallable getCallBack() {
        return callBack;
    }

    @Override
    protected Boolean doInBackground(JSONObject... params) {

        // Only upload the first param passed in
        // No need for multiple since they cna be bundled as JSON
        // arrays.
        uploader.upload(params[0]);
        JSONObject response = uploader.getResponseJSON();
        boolean result = (response != null) && response.optBoolean("success", false);
        if(result) {
            if (getCallBack() != null)
                getCallBack().success();
        } else {
            Log.e(TAG, "Failed to confirm communication with server.");
            if (getCallBack() != null)
                getCallBack().failure();
        }
        return result;
    }
}
