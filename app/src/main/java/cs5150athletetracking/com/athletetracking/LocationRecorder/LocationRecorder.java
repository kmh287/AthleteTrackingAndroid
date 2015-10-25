package cs5150athletetracking.com.athletetracking.LocationRecorder;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import cs5150athletetracking.com.athletetracking.JSONFormats.LocationJSON;
import cs5150athletetracking.com.athletetracking.StatusCallback;
import cs5150athletetracking.com.athletetracking.Util.ThreadUtil;

public class LocationRecorder {

    private static final String TAG = "LocationRecorder";
    private static final int NULL_LOC_SLEEP_PERIOD = 1000 * 2; // One minute //TODO change back
    private static final int REGULAR_SLEEP_PERIOD = 1000 * 5; // Five seconds
    public static final int LOC_DATA_BATCH_SIZE = 5; //TODO 100

    public enum LocationRecorderError {
        NONE, PERMISSIONS, NO_PROVIDER; /** maybe more later **/

        public String getErrorString(){
            switch(this){
                case PERMISSIONS:
                    return "Insufficient permissions.";
                case NO_PROVIDER:
                    return "Unable to determine location";
                default:
                    return "No error";
            }
        }
    }


    private final String username;
    private final List<JSONObject> locData;
    private final AtomicReference<LocationRecorderError> error;
    private final ThreadPoolExecutor executor;
    private final Activity activity;
    private final LocationTracker locationTracker;
    private final StatusCallback callback;

    public LocationRecorder(String username, Activity activity, StatusCallback callback) {
        this.username = username;
        this.activity = activity;   //TODO this may cause problems. Let's be careful
        this.callback = callback;
        this.locData = new ArrayList<>();
        this.error = new AtomicReference<>(LocationRecorderError.NONE);
        this.executor = getSingleThreadedThreadPoolExecutor();
        this.locationTracker = getLocationTracker();
    }

    private LocationTracker getLocationTracker(){
        LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isGPSEnabled && isNetworkEnabled){
            return new DualProviderLocationTracker(activity);
        } else if (isGPSEnabled){
            return new ProviderLocationTracker(activity, ProviderLocationTracker.ProviderType.GPS);
        } else if (isNetworkEnabled){
            return new ProviderLocationTracker(activity, ProviderLocationTracker.ProviderType.NETWORK);
        } else {
            fail(LocationRecorderError.NO_PROVIDER);
            return null;
        }
    }

    @NonNull
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
            executor.execute(new LocationRecorderRunnable());
            callback.green("Beginning tracking...");
        }
    }

    public void fail(LocationRecorderError error){
        fail(error, null);
    }

    public void fail(LocationRecorderError error, Exception e){
        this.error.set(error);
        setStatusRed(error.getErrorString());
        if (e != null) {
            Log.e(TAG, "Exception in Location Recorder", e);
        } else {
            Log.e(TAG, "Error in Location Recorder");
        }
    }

    public boolean hasError(){
        return error.get() != LocationRecorderError.NONE;
    }

    // ALL UI code belongs on the main thread, hence this ugliness

    private void setStatusGreen(final String message){
        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                callback.green(message);
            }
        });
    }

    private void setStatusYellow(final String message){
        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                callback.yellow(message);
            }
        });
    }

    private void setStatusRed(final String message){
        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                callback.red(message);
            }
        });
    }

    protected class LocationRecorderRunnable implements Runnable {

        @Override
        public void run() {
            if (locData.size() >= LOC_DATA_BATCH_SIZE) {
                if (uploadBatch()){
                    setStatusGreen("Transmitting");
                } else {
                    setStatusYellow("Connection Interrupted. Retrying");
                }
            }
            if (haveLocationPermission()) {
                if (locationTracker.hasLocation()){
                    Location loc = locationTracker.getLocation();
                    LocationJSON json = getLocationJSON(loc);
                    locData.add(json);
                    Log.i("LOCATIONRECORDER", json.toString());
                    scheduleNextIteration(REGULAR_SLEEP_PERIOD);
                } else {
                    scheduleNextIteration(NULL_LOC_SLEEP_PERIOD);
                }
            } else {
                fail(LocationRecorderError.PERMISSIONS);
            }
        }

        private boolean uploadBatch(){
            return true;
        }

        private void scheduleNextIteration(long delay) {
            if (!Thread.currentThread().isInterrupted()) {
                ThreadUtil.sleep(delay);
                executor.execute(this);
            }
        }

        @NonNull
        private LocationJSON getLocationJSON(Location loc) {
            double latitude = loc.getLatitude();
            double longitude = loc.getLongitude();
            double altitude = loc.getAltitude();
            return new LocationJSON(username, latitude,
                                    longitude, altitude);
        }

        private boolean haveLocationPermission() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                        || activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                int res = activity.checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                return res == PackageManager.PERMISSION_GRANTED;
            }
        }
    }

}
