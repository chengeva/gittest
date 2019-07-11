package co.acaia.firmwaretool.common;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ApplicationUtils {
    public static final int TIMER = 0;
    public static final int BREWING_TOOL = 1;
    public static final int FLAVOR = 2;
    public static final int VARIETY = 3;
    public static final int BEAN = 4;

    private final static String TAG = ApplicationUtils.class.getSimpleName();

    /**
     * Show the toast in the short time.
     * @param ctx - The Activity.
     * @param str - The message you want to display.
     */
    public static void toaster(Context ctx,String str){
        Toast.makeText(ctx.getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

    /**
     * Show the toast in the short time
     * @param ctx
     * @param resId - The string resource id.
     */
    public static void toaster(Context ctx, int resId){
        Toast.makeText(ctx.getApplicationContext(), ctx.getResources().getString(resId), Toast.LENGTH_LONG).show();
    }

    /**
     * Get the memory usage of application.
     * @param ctx
     */
    public static void getMemoryUsage(Context ctx){
        ActivityManager activityManager =  (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        activityManager.getMemoryInfo(mi);
        Log.i(TAG, "Memory free : " + mi.availMem);
    }


    /**
     * Get the device information as device code.
     */
    public static void getDeviceInfo(){
        Log.i(TAG, "Device Info " + System.getProperty("android.os.Build.DEVICE"));

    }


    /**
     *  Convert pixel to dip
     * @param pixels - The pixels you wanna convert.
     * @param ctx - The Activity
     * @return dp
     */
    public static int getDipsFromPixel(float pixels, Context ctx) {
        // Get the screen's density scale
        final float scale = ctx.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    /**
     * Get the width of the device.
     * @param act
     * @return The width of Screen.
     */
    public static int getScreenWidth(Activity act){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public static boolean isConnectNetwork(Context ctx){
        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            // notify user you are online
            return true;
        }else{
            // notify user you are not online
            return false;
        }

    }

    public static String getResStr(Context ctx, int resId){
        if(ctx != null)
            return ctx.getResources().getString(resId);
        else
            return " ";
    }

    /**
     * Check the file location.
     * @param path
     * @return
     */
    public static boolean isFileExsist(String path){
        boolean fileExists = new File(path).isFile();
        return fileExists;
    }



    public static String getTimeStamp(){
        return new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
    }


    public static void logError(String tag, String message){
        try{
            Log.e(tag, message);
        }catch(Exception e){
//            e.printStackTrace();
        }

    }


    public static String getVersionNumber(Context ctx){
        if(ctx != null){
            try {
                return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
            } catch (NameNotFoundException e) {
                logError(TAG, e.getMessage());
            }
        }
        return null;

    }

    /**
     * If the user's device is phone, then return true. Otherwise, return false.
     * Just follow the layout's folders. If you want to support different screens, please add the strings in screens.xml.
     * @param ctx
     * @return boolean
     */
    public static boolean isTablet(Context ctx){
        if(ctx != null){
            Configuration config = ctx.getResources().getConfiguration();
            if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==
                    Configuration.SCREENLAYOUT_SIZE_XLARGE){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    /**
     * Stop reporting bug to HockeyApp.
     * @deprecated
     */
    @SuppressWarnings("unused")
    private static void setDefaultUncaughtExceptionHandler() {
        try {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    e.printStackTrace();
                    Log.i(TAG,
                            "Uncaught Exception detected in thread {}"
                                    + t.getName());
                }
            });
        } catch (SecurityException e) {
            // logger.error("Could not set the Default Uncaught Exception Handler",
            // e);
        }
    }

}
