package cs5150athletetracking.com.athletetracking;

/**
 * Created by Kevin on 10/18/15.
 */
public interface StatusCallback {

    public void green(String message);
    public void yellow(String message);
    public void red(String message);

}
