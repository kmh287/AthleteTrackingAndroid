package cs5150athletetracking.com.athletetracking.LocationRecorder;

import android.location.Location;

// http://gabesechansoftware.com/location-tracking/
public interface LocationTracker {

    public interface LocationUpdateListener{
        public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime);
    }

    public void start();

    public void start(LocationUpdateListener update);

    public void stop();

    public boolean hasLocation();

    public boolean hasPossiblyStaleLocation();

    public Location getLocation();

    public Location getPossiblyStaleLocation();

}
