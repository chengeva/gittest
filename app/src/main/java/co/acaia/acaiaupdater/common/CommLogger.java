package co.acaia.acaiaupdater.common;

import android.util.Log;

/**
 * Created by hanjord on 15/8/9.
 */
public class CommLogger {
    public static void logv(String tag, String what) {
        //Log.v(tag, what);
    }
    public static void loge(String tag, String msg) {
        Log.e(tag, msg);
    }
}
