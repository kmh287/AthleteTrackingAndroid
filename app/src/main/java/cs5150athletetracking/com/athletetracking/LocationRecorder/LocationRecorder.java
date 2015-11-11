package cs5150athletetracking.com.athletetracking.LocationRecorder;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import cs5150athletetracking.com.athletetracking.Callbacks.ResultCallable;
import cs5150athletetracking.com.athletetracking.Http.AsyncUploader;
import cs5150athletetracking.com.athletetracking.JSONFormats.LocationJSON;
import cs5150athletetracking.com.athletetracking.Callbacks.UIStatusCallback;
import cs5150athletetracking.com.athletetracking.JSONFormats.LocationUploadJSON;
import cs5150athletetracking.com.athletetracking.Util.ThreadUtil;

public class LocationRecorder {

    private static final String TAG = "LocationRecorder";

    public enum LocationRecorderError {
        NONE, PERMISSIONS, NO_PROVIDER, INTERRUPTED; /** maybe more later **/

        public String getErrorString(){
            switch(this){
                case PERMISSIONS:
                    return "Insufficient permissions.";
                case NO_PROVIDER:
                    return "Unable to determine location. \n Press to restart.";
                case INTERRUPTED:
                    return "Too many other apps open. \n Press to restart";
                default:
                    return "No error";
            }
        }
    }

    private final String username;
    private final AtomicReference<LocationRecorderError> error;
    private final ThreadPoolExecutor executor;
    private final LocationTracker locationTracker;
    // The callback for changing the status of the activity.
    // Do not use the callback off of the UI thread! Use the methods in this class
    // such as setStatusGreen(...) to ensure callback runs on the main thread.
    private final UIStatusCallback callback;

    public LocationRecorder(String username, Activity activity, UIStatusCallback callback) {
        this.username = username;
        this.callback = callback;
        this.error = new AtomicReference<>(LocationRecorderError.NONE);
        this.executor = getSingleThreadedThreadPoolExecutor();
        this.locationTracker = getLocationTracker(activity);
    }

    private LocationTracker getLocationTracker(Activity activity){
        LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGPSEnabled && isNetworkEnabled) {
            // Even if we have only one, these can change mid-run.
            // So we should be prepared for having both all the time.
            return new DualProviderLocationTracker(activity);
        } else if (isGPSEnabled) {
            return new ProviderLocationTracker(activity, ProviderLocationTracker.ProviderType.GPS);
        } else if (isNetworkEnabled){
            return new ProviderLocationTracker(activity, ProviderLocationTracker.ProviderType.NETWORK);
        } else {
            fail(LocationRecorderError.NO_PROVIDER);
            return null;
        }
    }

    // Create a new executor service for our location recording thread
    private ThreadPoolExecutor getSingleThreadedThreadPoolExecutor() {
        return new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(), Executors.defaultThreadFactory());
    }

    public void start() {
        if (!hasError()) {
            try {
                locationTracker.start();
            } catch (SecurityException e) {
                fail(LocationRecorderError.PERMISSIONS, e);
            }
            executor.execute(new LocationRecorderRunnable(username, locationTracker, callback));
            callback.green("Beginning tracking...");
        }
    }

    public void fail(LocationRecorderError error){
        fail(error, null);
    }

    public void fail(LocationRecorderError error, Exception e){
        this.error.set(error);
        callback.red(error.getErrorString());
        if (e != null) {
            Log.e(TAG, "Exception in Location Recorder", e);
        } else {
            Log.e(TAG, "Error in Location Recorder");
        }
    }

    public boolean hasError(){
        return error.get() != LocationRecorderError.NONE;
    }

    protected class LocationRecorderRunnable implements Runnable {

        //The amount of time the recorder should wait to get the location after unsuccessfully getting
        // the location
        private static final int NULL_LOC_SLEEP_PERIOD = 1000 * 2; // One minute //TODO change back
        // The amount of time the recorder should wait to get the location after successfully getting
        // the location
        private static final int REGULAR_SLEEP_PERIOD = 1000 * 5; // Five seconds
        // The number of location JSONs we intend to upload at once
        private static final int LOC_DATA_BATCH_SIZE = 5; //TODO 100
        // How many null locations will we accept before we indicate there's a problem
        private static final int NULL_LOC_TOLERANCE = 2;

        private final AtomicInteger nullLocCounter = new AtomicInteger(0);
        private final UIStatusCallback callback;
        private final List<JSONObject> locData;
        private final String username;
        private final LocationTracker locationTracker;

        public LocationRecorderRunnable(String username,
                                        LocationTracker locationTracker,
                                        UIStatusCallback callback){
            this.username = username;
            this.locationTracker = locationTracker;
            this.callback = callback;
            this.locData = new ArrayList<>();
        }

        @Override
        public void run() {
            if (locData.size() >= LOC_DATA_BATCH_SIZE) {
                asyncUploadBatch();
                locData.clear();
            }
            if (locationTracker.hasLocation()){
                Location loc = locationTracker.getLocation();
                LocationJSON json = getLocationJSON(loc);
                locData.add(json);
                Log.i(TAG + "_thread", json.toString());
                nullLocCounter.set(0);
//                callback.green("Transmitting");
                scheduleNextIteration(REGULAR_SLEEP_PERIOD);
            } else {
                if(nullLocCounter.incrementAndGet() >= NULL_LOC_TOLERANCE){
                    callback.yellow("Location Unavailable");
                }
                scheduleNextIteration(NULL_LOC_SLEEP_PERIOD);
            }
        }

        private void asyncUploadBatch(){
            try {
                LocationUploadJSON uploadJSON;
                synchronized (locData) {
                    uploadJSON = new LocationUploadJSON(username, locData);
                }

                final AsyncUploader asyncUploader = new AsyncUploader();
                asyncUploader.setCallBack(new ResultCallable() {
                    @Override
                    public void success() {
                        callback.green("Transmitting");
                    }

                    @Override
                    public void failure() {
                        callback.yellow("Low signal");
                    }
                });
                asyncUploader.execute(uploadJSON);
            } catch (JSONException e){
                Log.e(TAG, "Failure building Upload JSON", e);
                callback.yellow("Upload Error");
            }
        }

        private void scheduleNextIteration(long delay) {
            if (!Thread.currentThread().isInterrupted()) {
                ThreadUtil.sleep(delay);
                executor.execute(this);
            } else {
                fail(LocationRecorderError.INTERRUPTED);
            }
        }

        @NonNull
        private LocationJSON getLocationJSON(Location loc) {
            return new LocationJSON(username, loc.getLatitude(),
                                    loc.getLongitude(), loc.getAltitude());
        }

    }

}
