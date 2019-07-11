// vim: et sw=4 sts=4 tabstop=4
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package co.acaia.communications.scaleService.gatt;

import android.bluetooth.BluetoothDevice;
import android.content.Context;


import java.util.List;

/**
 * Encapsulate Gatt initial procedures.
 */
public interface GattAdapter {


    public Gatt connectGatt(Context ctx, boolean autoConnect, Gatt.Listener listener, BluetoothDevice dev);

    public boolean startLeScan(LeScanCallback clbk);
    public void stopLeScan(LeScanCallback clbk);

    public List<BluetoothDevice> getConnectedDevices();
    public int getConnectionState(BluetoothDevice device);

    public interface LeScanCallback {
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord);
    }
}

