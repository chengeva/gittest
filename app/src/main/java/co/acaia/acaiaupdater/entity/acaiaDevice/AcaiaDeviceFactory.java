package co.acaia.acaiaupdater.entity.acaiaDevice;

import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelCinco;
import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelLunar;
import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelLunar2021;
import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelOrion;
import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelPearl2021;
import static co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice.modelPearlS;

public class AcaiaDeviceFactory {
    public static AcaiaDevice acaiaDeviceFromModelName(String modelName){
        if(modelName.equals(modelPearl2021)){
            return new Pearl2021(modelName);
        }
        if(modelName.equals(modelLunar2021)){
            return new Lunar2021(modelName);
        }

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

        return null;
    }
}
