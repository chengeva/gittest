package co.acaia.acaiaupdater.entity.acaiaDevice;

// Abstract class
public class AcaiaDevice {
    public String modelName;

    // Constants
    public static final String modelLunar="Lunar";
    public static final String modelOrion="Orion";
    public static final String modelPearlS="PearlS";
    public static final String modelCinco="Cinco";

    public AcaiaDevice (String modelName) {
        this.modelName = modelName;
    }
}
