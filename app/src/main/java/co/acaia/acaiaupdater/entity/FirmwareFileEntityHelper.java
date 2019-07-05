package co.acaia.acaiaupdater.entity;

import com.parse.ParseObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.acaia.acaiaupdater.filehelper.RemoteParseFirmwareObject;


public class FirmwareFileEntityHelper {
    public static List<FirmwareFileEntity> getFirmwareEntitiesByRemoteID(String id){
        return FirmwareFileEntity.find(FirmwareFileEntity.class,"remoteid=?",id);
    }

    public static FirmwareFileEntity getLatestFirmware(){
        List<FirmwareFileEntity> firmwareFileEntities=FirmwareFileEntity.listAll(FirmwareFileEntity.class);
        Collections.sort(firmwareFileEntities,new Comparator<FirmwareFileEntity>() {
            @Override
            public int compare(FirmwareFileEntity lhs, FirmwareFileEntity rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                if (lhs.title == rhs.title) {
                    return 0;
                }
                if (lhs.title == null) {
                    return -1;
                }
                if (rhs.title == null) {
                    return 1;
                }
                return (-1)*lhs.title.compareTo(rhs.title);

            }
        });
        if(firmwareFileEntities.size()!=0)
            return firmwareFileEntities.get(0);
        else
            return null;
    }

    public static List<FirmwareFileEntity> getDownloadedFirmware(){
        List<FirmwareFileEntity> firmwareFileEntities=FirmwareFileEntity.listAll(FirmwareFileEntity.class);
        Collections.sort(firmwareFileEntities,new Comparator<FirmwareFileEntity>() {
            @Override
            public int compare(FirmwareFileEntity lhs, FirmwareFileEntity rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                if (lhs.title == rhs.title) {
                    return 0;
                }
                if (lhs.title == null) {
                    return -1;
                }
                if (rhs.title == null) {
                    return 1;
                }
                return (-1)*lhs.title.compareTo(rhs.title);

            }
        });
        return firmwareFileEntities;
    }

    public static boolean if_downloaded_firmware_object(String id){
        List<FirmwareFileEntity> firmwareFileEntities=getFirmwareEntitiesByRemoteID(id);
        if(firmwareFileEntities!=null){
            if(firmwareFileEntities.size()>0){
                return true;
            }
        }
        return false;
    }
    public static FirmwareFileEntity getFirmwareEntityFromParseObject(ParseObject firmwareParseObject){
        FirmwareFileEntity firmwareFileEntity=new FirmwareFileEntity();
        firmwareFileEntity.title=firmwareParseObject.getString(RemoteParseFirmwareObject.title);
        firmwareFileEntity.detail=firmwareParseObject.getString(RemoteParseFirmwareObject.detail);
        firmwareFileEntity.secret_code=""; // todo: secret code
        firmwareFileEntity.majorversion=firmwareParseObject.getInt(RemoteParseFirmwareObject.majorVersion);
        firmwareFileEntity.minorversion=firmwareParseObject.getInt(RemoteParseFirmwareObject.minorVersion);
        firmwareFileEntity.filename=firmwareParseObject.getParseFile(RemoteParseFirmwareObject.binFile).getName();
        firmwareFileEntity.createdat=firmwareParseObject.getDate(RemoteParseFirmwareObject.releaseDate);
        firmwareFileEntity.remoteid=firmwareParseObject.getObjectId();
        return firmwareFileEntity;
    }
}
