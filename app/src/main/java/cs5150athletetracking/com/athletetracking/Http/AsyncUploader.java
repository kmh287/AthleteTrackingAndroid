package cs5150athletetracking.com.athletetracking.Http;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import cs5150athletetracking.com.athletetracking.Callbacks.ResultCallable;


/**
 * Created by mag94 on 10/19/15.
 */

public class AsyncUploader extends AsyncTask<JSONObject, String, Integer> {

    private static final String TAG = "AsyncUploader";

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
    protected Integer doInBackground(JSONObject... params) {
        int result = 0;

        int count = params.length;
        for (JSONObject param : params) {
            result += uploader.upload(param);
        }

        return result;
    }


    @Override
    protected void onPostExecute(Integer result) {
        /* Sent LocationJson. Check for received confirmation */

        //TODO fix?
        if(result > 0) {
            if (getCallBack() != null)
                getCallBack().success();
        } else {
            Log.e(TAG, "Failed to confirm communication with server.");
            if (getCallBack() != null)
                getCallBack().failure();
        }
    }
}
