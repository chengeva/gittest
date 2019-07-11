package co.acaia.firmwaretool.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by Jean on 2015/4/22.
 */
public class Utils {

    public static String getVersionNumber(Context ctx){
        if(ctx != null){
            try {
                return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                logerror("Utils", e.getMessage());
            }
        }
        return "";

    }

    public static String getResStr(Context ctx, int resId){
        if(ctx != null)
            return ctx.getResources().getString(resId);
        else
            return " ";
    }

    public static void logerror(String tag, String message){
        Log.e(tag, message);
    }
}
