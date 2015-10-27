package cs5150athletetracking.com.athletetracking;


import cs5150athletetracking.com.athletetracking.Util.ThreadUtil;

/**
 * This class is used as an interface between the TrackingActivity and the LocationRecorder object
 * The methods in here MUST be run on the main/UI thread as they interface with UI code.
 *
 * To create a callback, extend this class and implement redCallback, yellowCallback, and greenCallback.
 * These methods will not be directly accessible, and must be run through red, yellow, and green
 * respectively in order to run on the UI thread.
 */
public abstract class UIStatusCallback {

    public final void green(final String message){
        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                greenCallback(message);
            }
        });
    }

    public final void yellow(final String message){
        ThreadUtil.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                yellowCallback(message);
            }
        });
    }

    public final void red(final String message){
        ThreadUtil.runOnMainThread(new Runnable(){
            @Override
            public void run() {
                redCallback(message);
            }
        });
    }

    protected abstract void greenCallback(String message);

    protected abstract void yellowCallback(String message);

    protected abstract void redCallback(String message);

}
