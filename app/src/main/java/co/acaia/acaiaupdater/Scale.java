package co.acaia.acaiaupdater;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


public class Scale {
	private final static String TAG = Scale.class.getSimpleName();
	private Context mCtx = null;
	private ScaleService mScaleService = null;
	

	public boolean initialize (Context context) {
		// MainActivity.orangeDebug("Scale initialize");
		if (mCtx != null) {
			//Log.d(TAG, "Please call release() of Scale class first");
			//return false;
			
			Log.d(TAG, "context is not null, releasing...");
			release();
			
		}
		if (context == null) {
			MainActivity.orangeDebug("Scale initialize, but context=null");
		}
		mCtx = context;
		Intent scalServiceIntent = new Intent(mCtx, ScaleService.class);
		boolean r=mCtx.bindService(scalServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		// MainActivity.orangeDebug("Scale initialize, bind service="+r);
		return true;
	}
	
	public boolean release() {
		Log.d(TAG, "Release...");
		mCtx.unbindService(mServiceConnection);
		mCtx = null;
		return true;
	}
	
	// Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
    			// MainActivity.orangeDebug("Scale onServiceConnected");
    			mScaleService = ((ScaleService.LocalBinder) service).getService();
            if (!mScaleService.initialize()) {
            		Log.i(TAG, "ScaleService initialize failed!");
            		return;
            }else{
            	
            }

            // Automatically connects to the device upon successful start-up initialization.
            //mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        		MainActivity.orangeDebug("Scale onServiceDisconnected");
        		mScaleService = null;
        }
    };
	


	
	public boolean disconnect() {
		if (mScaleService == null) return false;
		mScaleService.disconnect();
		return true;
	}
	
	public boolean getAutoOffTime() {
		return (mScaleService != null)?mScaleService.getAutoOffTime():false;
	}
	
	public boolean getBattery() {
		return (mScaleService != null)?mScaleService.getBattery():false;
	}
	public boolean getBeep() {
		return (mScaleService != null)?mScaleService.getBeep():false;
	}
	
	public int getConnectionState() {
		return (mScaleService != null)?mScaleService.getConnectionState():ScaleService.CONNECTION_STATE_DISCONNECTED;
	}
	
	public boolean getKeyDisabledElapsedTime() {
		return (mScaleService != null)?mScaleService.getKeyDisabledElapsedTime():false;
	}
	
	public void getState() {
		if (mScaleService != null)
			mScaleService.getState();
	}
	
	public boolean getTimer() {
		return (mScaleService != null)?mScaleService.getTimer():false;
	}
	
	public boolean getWeight() {
		return (mScaleService != null)?mScaleService.getWeight():false;
	}
	
	public boolean pauseTimer() {
		return (mScaleService != null)?mScaleService.pauseTimer():false;
	}
	
	public boolean setAutoOffTime(int time) {
		return (mScaleService != null)?mScaleService.setAutoOffTime(time):false;
	}
	
	public boolean setBeep(boolean on) {
		return (mScaleService != null)?mScaleService.setBeep(on):false;
	}
	
	public boolean setKeyDisabledWithTime(int second) {
		return (mScaleService != null)?mScaleService.setKeyDisabledWithTime(second):false;
	}
	
	public boolean setLight(boolean on) {
		return (mScaleService != null)?mScaleService.setLight(on):false;
	}
	
	public boolean setTare() {
		return (mScaleService != null)?mScaleService.setTare():false;
	}
	
	public boolean setUnit(int unit) {
		return (mScaleService != null)?mScaleService.setUnit(unit):false;
	}
	
	public boolean startScan() {
		if (mScaleService == null){
			MainActivity.orangeDebug("startScan but scale service null");
			return false;
		}else{
			// MainActivity.orangeDebug("startScan procced");
		}
		mScaleService.startScan();
		return true;
	}
	
	public boolean startTimer() {
		return (mScaleService != null)?mScaleService.startTimer():false;
	}
	
	public boolean stopScan() {
		if (mScaleService == null) return false;
		mScaleService.stopScan();
		return true;
	}
	
	public boolean stopTimer() {
		return (mScaleService != null)?mScaleService.stopTimer():false;
	}
	
	
	
	
	
}
