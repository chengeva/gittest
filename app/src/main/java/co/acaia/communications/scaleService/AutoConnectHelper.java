package co.acaia.communications.scaleService;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import co.acaia.communications.CommLogger;
import co.acaia.communications.protocol.ScaleGattAttributes;
import co.acaia.communications.scalecommand.ScaleConnectionCommandEvent;
import co.acaia.communications.scalecommand.ScaleConnectionCommandEventType;
import co.acaia.communications.scaleevent.ScaleConnectionEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by mrjedi on 2015/4/1.
 */
public class AutoConnectHelper {
    public static final int mode_auto = 0;
    public static final int mode_closest_scale = 1;
    public static final String TAG = "AutoConnectHelper";
    BluetoothAdapter mBluetoothAdapter;
    private int mConnectionMode = -1;
    // auto connect scan for scale timeout
    private static final int auto_connect_scan_timeout = 4000;
    private static final int observe_interval = 200;
    private long start_time = 0;
    private boolean if_found_scale = false;

    public AutoConnectHelper(BluetoothAdapter mBluetoothAdapter_) {
        mBluetoothAdapter = mBluetoothAdapter_;

    }

    public void startAutoConnect(int mode) {
        mConnectionMode = mode;
        start_time = System.nanoTime();
        if (mBluetoothAdapter != null) {
            CommLogger.logv(TAG, "startScan error, cannot initialize");
            CommLogger.logv(TAG, "Scan Mode: " +
                    Integer.toString(mBluetoothAdapter.getScanMode()) + ", " +
                    Boolean.toString(mBluetoothAdapter.isDiscovering()));
            UUID[] uuids = {UUID.fromString(ScaleGattAttributes.CSR_JB_UART_RX_PRIMARY_SERVICE_UUID)};
            if (!mBluetoothAdapter.startLeScan(uuids, autoconnect_LeScanCallback)) {
                Log.d(TAG, "startLeScan Error");
            }
            new Thread() {
                public void run() {
                    for (int i = 0; i != 10; i++) {
                        if (if_found_scale) {
                            break;
                        }
                        if ((System.nanoTime() - start_time) / 1000000.0 > auto_connect_scan_timeout) {
                            // scan failed
                            EventBus.getDefault().post(new ScaleConnectionEvent(false));
                            mBluetoothAdapter.stopLeScan(autoconnect_LeScanCallback);
                            break;
                        }
                        try {
                            Thread.sleep(observe_interval);
                        } catch (Exception e) {

                        }
                    }
                }
            }.start();
        }
    }

    private BluetoothAdapter.LeScanCallback autoconnect_LeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    //   if (device.getAddress().equals("00:1C:97:12:64:69")) {
                    // auto connect
                    // can be inproved by connecting with nearest distance
                    if (mConnectionMode == mode_auto) {
                        CommLogger.logv(TAG, "auto connect" + device.getAddress());
                        mBluetoothAdapter.stopLeScan(autoconnect_LeScanCallback);
                        if_found_scale = true;
                        EventBus.getDefault().post(new ScaleConnectionCommandEvent(ScaleConnectionCommandEventType.connection_command.CONNECT.ordinal(), device.getAddress()));
                    } else if (mConnectionMode == mode_closest_scale) {
                        // implement connect to neatest scale
                    }
                }
                //}
            };
}
