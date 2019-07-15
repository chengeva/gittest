package co.acaia.acaiaupdater.entity.acaiaDevice;

import java.util.ArrayList;

// Abstract class
public class AcaiaDevice {
    public String modelName;

    // Constants
    public static final String modelLunar="Lunar";
    public static final String modelOrion="Orion";
    public static final String modelPearlS="Pearl S";
    public static final String modelCinco="Cinco";

    public AcaiaDevice (String modelName) {
        this.modelName = modelName;
    }

    public static ArrayList<Integer> getValidISPFromModelName(String modelName){
        ArrayList<Integer> isps=new ArrayList<>();
        if(modelName.equals(modelPearlS)){
            isps.add(90);
        }
        return isps;
    }
}
