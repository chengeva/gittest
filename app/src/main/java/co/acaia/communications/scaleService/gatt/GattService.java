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

import java.util.List;
import java.util.UUID;

import co.acaia.communications.scaleService.aosp.AospGattCharacteristic;

/**
 * This is a wrapper.
 *
 * It will be an interface to help us to avoid depending on any specific
 * platform.
 **/
public interface GattService {
    public Object getImpl();

    public AospGattCharacteristic getCharacteristic(UUID uuid);
    public List<GattCharacteristic> getCharacteristics();
    public int getInstanceId();
    public int getType();
    public UUID getUuid();
}

