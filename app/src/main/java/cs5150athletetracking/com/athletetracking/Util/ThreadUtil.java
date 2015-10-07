package cs5150athletetracking.com.athletetracking.Util;


public class ThreadUtil {
    private ThreadUtil (){}

    public static void sleep(long timeMsec){
        try{
            Thread.sleep(timeMsec);
        } catch (InterruptedException e){
            // Let it go!
        }
    }
}
