package co.acaia.acaiaupdater.entity;

import com.parse.ParseObject;

import co.acaia.acaiaupdater.util.RealmUtil;
import io.realm.Realm;

public class FirmwareEntityHelper {
    public static void saveFirmwareEntity(FirmwareFileEntity firmwareFileEntity)
    {

    }

    public static FirmwareFileEntity firmwareFileEntityFromParseObject(ParseObject parseObject)
    {
        Realm realm= RealmUtil.getRealm();
        FirmwareFileEntity firmwareFileEntity=realm.createObject(FirmwareFileEntity.class);
        firmwareFileEntity.model=parseObject.getString("model");
        firmwareFileEntity.title=parseObject.getString("title");
        firmwareFileEntity.detail=parseObject.getString("detail");
        firmwareFileEntity.shortCap=parseObject.getString("caption");
        firmwareFileEntity.addVer=parseObject.getInt("addVersion");
        firmwareFileEntity.subVer=parseObject.getInt("minorVersion");
        firmwareFileEntity.mainVer=parseObject.getInt("majorVersion");
        firmwareFileEntity.customOrdering=parseObject.getInt("customOrdering");
        firmwareFileEntity.fileName=parseObject.getParseFile("firmwareFile").getUrl();

        return firmwareFileEntity;
    }
}
