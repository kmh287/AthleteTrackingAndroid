package cs5150athletetracking.com.athletetracking.Util;


import android.os.Handler;
import android.os.Looper;

public class ThreadUtil {
    private ThreadUtil (){}

    public static void sleep(long timeMsec){
        try{
            Thread.sleep(timeMsec);
        } catch (InterruptedException e){
            // Let it go!
        }
    }

    public static void runOnMainThread(Runnable r){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        if (r != null){
            mainHandler.post(r);
        }
    }

}
