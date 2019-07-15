package co.acaia.acaiaupdater.entity;

public class AcaiaFirmware {
    public String model;
    public String fileName;
    public String title;
    public String detail;
    public String shortCap;
    public int mainVer;
    public int subVer;
    public int addVer;
    public int customOrdering;

    // deep copy
    public AcaiaFirmware(FirmwareFileEntity firmwareFileEntity){
        this.model=firmwareFileEntity.model;
        this.fileName=firmwareFileEntity.fileName;
        this.title=firmwareFileEntity.title;
        this.detail=firmwareFileEntity.detail;
        this.shortCap=firmwareFileEntity.shortCap;
        this.mainVer=firmwareFileEntity.mainVer;
        this.subVer=firmwareFileEntity.subVer;
        this.addVer=firmwareFileEntity.subVer;
        this.customOrdering=firmwareFileEntity.customOrdering;
    }
}
