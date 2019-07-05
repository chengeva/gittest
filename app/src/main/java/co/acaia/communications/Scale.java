package co.acaia.communications;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;


import co.acaia.communications.protocol.ver20.DataOutHelper;
import co.acaia.communications.protocol.ver20.ScaleProtocol;
import co.acaia.communications.protocol.ver20.SettingEntity;
import co.acaia.communications.protocol.ver20.SettingFactory;
import co.acaia.communications.scaleService.ScaleCommunicationService;

public class Scale {
	private final static String TAG = Scale.class.getSimpleName();
	private Context mCtx = null;
	private ScaleCommunicationService mScaleCommunicationService = null;

	public boolean initialize(Context context) {
		if (mCtx != null) {
			// Log.d(TAG, "Please call release() of Scale class first");
			// return false;

			Log.d(TAG, "context is not null, releasing...");
			release();

		}
		mCtx = context;
		Intent scalServiceIntent = new Intent(mCtx, ScaleCommunicationService.class);
		mCtx.bindService(scalServiceIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
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
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			CommLogger.logv(TAG, "onServiceConnected");
			mScaleCommunicationService = ((ScaleCommunicationService.LocalBinder) service)
					.getService();
			if (!mScaleCommunicationService.initialize()) {
				CommLogger.logv(TAG, "ScaleCommunicationServiceinitialize failed!");
				return;
			}

			// Automatically connects to the device upon successful start-up
			// initialization.
			// mBluetoothLeService.connect(mDeviceAddress);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mScaleCommunicationService = null;
		}
	};

	public boolean connect(final String addr) {
		return mScaleCommunicationService.connect(addr);
	}

    public boolean connectdebug() {
       return false;
    }

	public boolean disconnect() {
		return false;
	}

	public boolean getAutoOffTime() {
		return false;
	}

	public boolean getBattery() {
        mScaleCommunicationService.getBattery();
		return false;
	}

	public boolean getBeep() {
		return false;
	}

	public int getConnectionState() {
		return 0;
	}

	public boolean getKeyDisabledElapsedTime() {
		return false;
	}

	public void getState() {
        DataOutHelper.app_command((short) ScaleProtocol.ECMD.e_cmd_status_s.ordinal());
	}

	public boolean getTimer() {
		return false;
	}

	public boolean getWeight() {
		return false;
	}

	public boolean pauseTimer() {

        DataOutHelper.timer_action((short) ScaleProtocol.ESCALE_TIMER_ACTION.e_timer_pause.ordinal());
        return false;
	}

	public boolean setAutoOffTime(int time) {
		return false;
	}

	public boolean setBeep(boolean on) {
		return false;
	}

	public boolean setKeyDisabledWithTime(int second) {
		return false;
	}

	public boolean setLight(boolean on) {
		return false;
	}

	public boolean setTare() {
        DataOutHelper.app_command((short) ScaleProtocol.ECMD.e_cmd_tare_s.ordinal());
		return false;
	}

	public boolean setUnit(short unit) {
        SettingEntity settingEntity= SettingFactory.getSetting(SettingFactory.set_unit.item, unit);
        DataOutHelper.setting_chg(settingEntity.getItem(),settingEntity.getValue());
		return false;
	}

	public boolean startScan() {
		return mScaleCommunicationService.startScan();

	}

	public boolean startTimer() {
        DataOutHelper.timer_action((short) ScaleProtocol.ESCALE_TIMER_ACTION.e_timer_start.ordinal());
        return false;
	}

	public boolean stopScan() {
        mScaleCommunicationService.stopScan();return true;
	}

	public boolean stopTimer() {

        DataOutHelper.timer_action((short) ScaleProtocol.ESCALE_TIMER_ACTION.e_timer_stop.ordinal());
        return false;
	}

}
