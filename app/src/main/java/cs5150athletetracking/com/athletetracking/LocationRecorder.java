package cs5150athletetracking.com.athletetracking;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import cs5150athletetracking.com.athletetracking.Util.ThreadUtil;
import io.testfire.Testfire;

public class LocationRecorder {

    public static final int LOC_DATA_BATCH_SIZE = 100;

    public enum LocationRecorderError {
        NONE, PERMISSIONS, /** maybe more later **/
    }

    private static final String TAG = "LocationRecorder";
    private static final int NULL_LOC_SLEEP_PERIOD = 1000 * 60; // One minute
    private static final int REGULAR_SLEEP_PERIOD = 1000 * 5; // Five seconds

    private final String username;
    private final List<JSONObject> locData;
    private final AtomicBoolean paused;
    private final AtomicReference<LocationRecorderError> error;
    private final ThreadPoolExecutor executor;
    private final Activity activity;
    // TODO fill in with format and locale.US!!!!!
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", Locale.US);

    public LocationRecorder(String username, Activity activity) {
        this.username = username;
        this.activity = activity;   //TODO this may cause problems. LEt's be careful
        this.locData = new ArrayList<>();
        this.paused = new AtomicBoolean(true);
        this.error = new AtomicReference<>(LocationRecorderError.NONE);
        this.executor = getSingleThreadedThreadPoolExecutor();
    }

    @NonNull
    private ThreadPoolExecutor getSingleThreadedThreadPoolExecutor() {
        return new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(), Executors.defaultThreadFactory());
    }

    public void start() {
        paused.set(false);
        executor.execute(new LocationRecorderRunnable());
    }

    public void pause() {
        paused.set(true);
    }

    public void fail(LocationRecorderError error){
        fail(error, null);
    }

    public void fail(LocationRecorderError error, Exception e){
        this.error.set(error);
        if (e != null) {
            Log.e(TAG, "Exception in Location Recorder", e);
        } else {
            Log.e(TAG, "Error in Location Recorder");
        }
    }

    public boolean hasError(){
        return error.get() != null;
    }

    protected class LocationRecorderRunnable implements Runnable {

        @Override
        public void run() {
            if (locData.size() >= LOC_DATA_BATCH_SIZE){
                uploadBatch();
            }
            if (haveLocationPermission()) {
                try {
                    Location loc = getLocation();
                    if (loc != null){
                        LocationJSON json = getLocationJSON(loc);
                        locData.add(json);
                        Log.i("LOCATIONRECORDER", json.toString());
                        if (!paused.get()){
                            scheduleNextIteration(REGULAR_SLEEP_PERIOD);
                        }
                    } else {
                        if (!paused.get()) {
                            scheduleNextIteration(NULL_LOC_SLEEP_PERIOD);
                        }
                    }
                } catch (SecurityException e){
                    // Shouldn't be reachable
                    fail(LocationRecorderError.PERMISSIONS, e);
                }
            } else {
                fail(LocationRecorderError.PERMISSIONS);
            }
        }

        @Nullable
        private Location getLocation() throws SecurityException{
            LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Location loc = null;

            if (isGPSEnabled) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) null);
                loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else if (isNetworkEnabled){
                // Lower precision, but better than nothing
                loc = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            return loc;
        }

        @NonNull
        private LocationJSON getLocationJSON(Location loc) {
            double latitude = loc.getLatitude();
            double longitude = loc.getLongitude();
            double altitude = loc.getAltitude();
            String timestamp = format.format(new Date());
            return new LocationJSON(username, timestamp,
                                                 latitude, longitude,
                                                 altitude);
        }

        private void uploadBatch(){
            // Send the batch to the uploader

            // Clear the collection right now, regardless of the result
            locData.clear();
        }

        private void scheduleNextIteration(long delay){
            ThreadUtil.sleep(delay);
            executor.execute(this);
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
