package cs5150athletetracking.com.athletetracking.JSONFormats;

//Enums are expensive on Android.
public enum SerializeKey {
    /* Common */
    USERNAME, TIMESTAMP,
    /* Login JSON */
    PASSWORD,
    /*Location JSON */
    LATITUDE, LONGITUDE, ALTITUDE,
    /* Location Upload */
    DATA;

    @Override
    public String toString(){
        switch(this){
            case USERNAME:
                return "user";
            case TIMESTAMP:
                return "t";
            case LATITUDE:
                return "lat";
            case LONGITUDE:
                return "long";
            case ALTITUDE:
                return "alt";
            case DATA:
                return "data";
            case PASSWORD:
                return "pass";
            default:
                return "";
        }
    }
}
