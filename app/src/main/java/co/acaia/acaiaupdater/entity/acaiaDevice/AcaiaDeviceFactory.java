package co.acaia.acaiaupdater.entity.acaiaDevice;

import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelLunar;

public class AcaiaDeviceFactory {
    public static AcaiaDevice acaiaDeviceFromModelName(String modelName){
        if(modelName.equals(modelLunar)){
            return new Lunar(modelName);
        }
        return null;
    }
}
