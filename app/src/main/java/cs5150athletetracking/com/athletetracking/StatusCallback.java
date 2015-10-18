package cs5150athletetracking.com.athletetracking;

/**
 * Created by Kevin on 10/18/15.
 */
public interface StatusCallback {

    public void transmitting(String message);
    public void disconnected (String message);
    public void error(String message);

}
