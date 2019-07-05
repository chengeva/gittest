package co.acaia.communications.scaleService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import co.acaia.communications.CommLogger;

/**
 * Created by hanjord on 15/3/20.
 */
public class AcaiaScaleService {
    public static final String TAG="AcaiaScaleService";
    private Context mCtx;
    protected ScaleCommunicationService mScaleCommunicationService = null;

    public AcaiaScaleService(){
    }

    public ScaleCommunicationService getScaleCommunicationService(){
        return mScaleCommunicationService;
    }

    public boolean initialize(Context context) {
        if (mCtx != null) {
            Log.d(TAG, "context is not null, releasing...");
            release();
        }
        CommLogger.logv(TAG,"init service");
        mCtx = context;
        Intent scalServiceIntent = new Intent(mCtx, ScaleCommunicationService.class);
        mCtx.bindService(scalServiceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
        return true;
    }
    public boolean release() {
        Log.d(TAG, "Release...");
        mScaleCommunicationService.disconnect();
        mCtx.unbindService(mServiceConnection);
        mCtx = null;
        return false;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            CommLogger.logv(TAG, "onServiceConnected");
            mScaleCommunicationService = ((ScaleCommunicationService.LocalBinder) service)
                    .getService();
            if (!mScaleCommunicationService.initialize()) {
                CommLogger.logv(TAG, "ScaleCommunicationServiceinitialize failed!");
                return;
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mScaleCommunicationService = null;
        }
    };
}
