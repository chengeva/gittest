package co.acaia.communications.scaleService;

import android.bluetooth.BluetoothDevice;
import android.os.CountDownTimer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;

public class DistanceConnectHelper {

    public static final int RSSI_HIST_SIZE = 25;
    private boolean timeUp;
    private HashMap<String, ArrayList<Double>> distanceMap;
    private HashMap<String, BluetoothDevice> btDevice;
    private BluetoothDevice targetBluetoothDevice;
    private AcaiaDevice acaiaDevice;
    public DistanceConnectHelper(AcaiaDevice acaiaDevice_) {
        this.acaiaDevice=acaiaDevice_;
        distanceMap = new HashMap<>();
        btDevice = new HashMap<>();

        final int secs = 5;
        timeUp = false;
        new CountDownTimer((secs + 1) * 1000, 1000) // Wait 5 secs, tick every 1 sec
        {
            @Override
            public final void onTick(final long millisUntilFinished) {
            }

            @Override
            public final void onFinish() {
                // calculate target Pearl S
                getClosestBluetoothDevice();
                timeUp = true;
            }
        }.start();
    }

    private void getClosestBluetoothDevice() {
        // Very basic sorting, improve this later. Probably will work since only <100 scales scanned at a time.
        Double maxRSSI = -10000.0;
        Iterator it = distanceMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ArrayList<Double> rssiHist = distanceMap.get(pair.getKey());
            double currAvg = calculateAverage(rssiHist);
            if (currAvg > maxRSSI) {
                maxRSSI = currAvg;
                targetBluetoothDevice = btDevice.get(pair.getKey());
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    private double calculateAverage(ArrayList<Double> marks) {
        Double sum = 0.0;
        if (!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    public BluetoothDevice getTargetBluetoothDevice() {
        return targetBluetoothDevice;
    }

    // Return true if ready to distance connect
    public boolean onNewScannedDevice(BluetoothDevice bluetoothDevice, Double rssi) {
        if (timeUp == true) {
            return true;
        }
        if (bluetoothDevice.getName() == null) {
            return false;
        }

        String deviceName = bluetoothDevice.getName();

        if (!deviceName.startsWith("ORION") &&!deviceName.startsWith("acaia") && !deviceName.startsWith("PEARLS") && !deviceName.startsWith("ACAIA") && !deviceName.startsWith("PROCHBT") && !deviceName.startsWith("LINK")) {
            return false;
        }

        String deviceAddr = bluetoothDevice.getAddress();

        if (!distanceMap.containsKey(deviceAddr)) {
            distanceMap.put(deviceAddr, new ArrayList<Double>());
            btDevice.put(deviceAddr, bluetoothDevice);
        }

        ArrayList<Double> deviceRSSIHistory = distanceMap.get(deviceAddr);
        deviceRSSIHistory.add(rssi);

        return false;
    }
}