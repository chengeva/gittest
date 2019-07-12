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

package co.acaia.communications.scaleService.aosp;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import co.acaia.communications.scaleService.gatt.GattCharacteristic;
import co.acaia.communications.scaleService.gatt.GattService;

public class AospGattService implements GattService {

    private BluetoothGattService mSrv;
    private List<GattCharacteristic> mList;

    public AospGattService(BluetoothGattService srv) {
        mSrv = srv;
        mList = new ArrayList<GattCharacteristic>();
    }

    @Override
    public Object getImpl() {
        return mSrv;
    }

    @Override
    public AospGattCharacteristic getCharacteristic(UUID uuid) {
        return new AospGattCharacteristic(mSrv.getCharacteristic(uuid));
    }

    @Override
    public List<GattCharacteristic> getCharacteristics() {
        mList.clear();

        /* Always return current charactesristic, we do not hold any cache. */
        List<BluetoothGattCharacteristic> chrs = mSrv.getCharacteristics();
        for (BluetoothGattCharacteristic chr: chrs) {
            mList.add(new AospGattCharacteristic(chr));
        }

        return mList;
    }

    @Override
    public int getInstanceId() {
        return mSrv.getInstanceId();
    }

    @Override
    public int getType() {
        return mSrv.getType();
    }

    @Override
    public UUID getUuid() {
        return mSrv.getUuid();
    }
}

