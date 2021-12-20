package co.acaia.acaiaupdater;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.ListFragment;

import co.acaia.acaiaupdater.common.CommLogger;
import co.acaia.androidupdater.R;
import co.acaia.communications.scalecommand.ScaleConnectionCommandEvent;
import co.acaia.communications.scalecommand.ScaleConnectionCommandEventType;
import de.greenrobot.event.EventBus;


public class SettingScanScaleFragment extends ListFragment {
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private final static String TAG = SettingScanScaleFragment.class
            .getSimpleName();
    private BluetoothDevice mCurrentDevice = null;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothLeScanner bluetoothLeScanner;
    private List<ScanFilter> filters;
    private ScanSettings settings;
    //private long SCAN_PERIOD = 4000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mLeDeviceListAdapter = new LeDeviceListAdapter(inflater);

        /** Setting the list adapter for the ListFragment */

        setListAdapter(mLeDeviceListAdapter);
        init_bt();
        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        filters = new ArrayList<ScanFilter>();
        bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();;
        //mBluetoothAdapter.startLeScan(mLeScanCallback);
        //bluetoothLeScanner.startScan(filters, settings, mScanCallback);

        bluetoothLeScanner.startScan(mScanCallback);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String addr = mLeDeviceListAdapter.getDevice(i).getAddress();
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                EventBus.getDefault().post(new ScaleConnectionCommandEvent(ScaleConnectionCommandEventType.connection_command.CONNECT.ordinal(), addr));
                getActivity().finish();
            }
        });

    }

    public void init_bt() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getActivity()
                    .getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");

        }

    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            CommLogger.logv("callbackType", String.valueOf(callbackType));
            CommLogger.logv("result", result.toString());
            final BluetoothDevice device = result.getDevice();
            if (device != null) {
                try {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    String name = device.getName();
                                    if (name != null) {
                                        // hanjord : warning... new scale names
                                        if (name.startsWith("INFOS") || name.startsWith("PROCHBT")|| name.startsWith("ACAIA")) {
                                            mLeDeviceListAdapter.addDevice(device);
                                            mLeDeviceListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                CommLogger.logv("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };


    // Device scan callback.
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            CommLogger.logv(TAG, "Scanned scale" + device.getAddress());
            // bus.post(new FoundNewScaleEvent(device, device.getAddress(),
            // false, false, ""));
            if (device != null) {
                try {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    String name = device.getName();
                                    if (name != null) {
                                        // hanjord : warning... new scale names
                                        if (name.startsWith("INFOS") || name.startsWith("PROCHBT")|| name.startsWith("ACAIA")) {
                                            mLeDeviceListAdapter.addDevice(device);
                                            mLeDeviceListAdapter.notifyDataSetChanged();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    };


    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter(LayoutInflater inflater) {
            super();
            mLeDevices = new ArrayList<>();
            mInflator = inflater;
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // ImageView image = null;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.adapter_item_device, viewGroup,
                        false);
                viewHolder = new ViewHolder();
                // image = (ImageView) view.findViewById(R.id.scheck);
                // image.setImageResource(0);
                viewHolder.deviceName = (TextView) view
                        .findViewById(R.id.device_name);
                viewHolder.check = (ImageView) view.findViewById(R.id.scheck);
                viewHolder.check.setBackgroundResource(0);
                view.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            final BluetoothDevice device = mLeDevices.get(i);
            if (mCurrentDevice != null) {
                // CommLogger.logv("setting", "setcheck");
                if (device.getAddress().equals(mCurrentDevice.getAddress())) {
                    viewHolder.check
                            .setBackgroundResource(R.drawable.icon_selected_device);

                    // image.setImageResource(R.drawable.iconselect);
                }
            }
            final String deviceName = "acaia Coffee Scale";
            if (deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            }
            return view;
        }

    }

    static class ViewHolder {
        public ImageView check;
        TextView deviceName;
    }

}