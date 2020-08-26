package co.acaia.acaiaupdater.entity.acaiaDevice;

import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelAstra;
import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelCinco;
import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelLunar;
import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelOrion;
import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelPearlS;

public class AcaiaDeviceFactory {
    public static AcaiaDevice acaiaDeviceFromModelName(String modelName){
        if(modelName.equals(modelLunar)){
            return new Lunar(modelName);
        }
        if(modelName.equals(modelPearlS)){
            return new PearlS(modelName);
        }
        if(modelName.equals(modelCinco)){
            return new Cinco(modelName);
        }
        if(modelName.equals(modelOrion)){
            return new Orion(modelName);
        }
        if(modelName.equals(modelAstra)){
            return new Astra(modelName);
        }
        return null;
    }
}
