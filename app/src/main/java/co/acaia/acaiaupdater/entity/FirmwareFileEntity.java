package co.acaia.acaiaupdater.entity;

import io.realm.RealmObject;

public class FirmwareFileEntity extends RealmObject {

    public String model;
    public String fileName;
    public String title;
    public String detail;
    public String shortCap;
    public int mainVer;
    public int subVer;
    public int addVer;
    public int customOrdering;

}
