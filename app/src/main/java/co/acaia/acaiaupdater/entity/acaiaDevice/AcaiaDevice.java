package co.acaia.acaiaupdater.entity.acaiaDevice;

import java.util.ArrayList;

// Abstract class
public class AcaiaDevice {
    public String modelName;

    // Constants
    public static final String modelPearl2021="Pearl (2021)";
    public static final String modelLunar="Lunar";
    public static final String modelOrion="Orion";
    public static final String modelPearlS="Pearl S";
    public static final String modelCinco="Cinco";
    public static final String modelPyxis="Cinco";

    public AcaiaDevice (String modelName) {
        this.modelName = modelName;
    }

    public static ArrayList<Integer> getValidISPFromModelName(String modelName){
        ArrayList<Integer> isps=new ArrayList<>();
        if(modelName.equals(modelPearl2021)){
            isps.add(15);
        }

        if(modelName.equals(modelPearlS)){
            isps.add(90);
        }
        if(modelName.equals(modelLunar)){
            isps.add(20);
            isps.add(11);
            isps.add(21);
        }
        if(modelName.equals(modelOrion)){
            isps.add(70);
            isps.add(71);
        }

        if(modelName.equals(modelCinco) || modelName.equals(modelPyxis)){
            isps.add(22);
        }

        return isps;
    }
}
