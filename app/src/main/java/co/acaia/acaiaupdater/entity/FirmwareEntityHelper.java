package co.acaia.acaiaupdater.entity;

import android.content.Context;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import co.acaia.acaiaupdater.filehelper.OnFileRetrieved;
import co.acaia.acaiaupdater.rawfile.RawFileHelper;
import co.acaia.acaiaupdater.util.RealmUtil;
import io.realm.Realm;
import io.realm.RealmResults;

public class FirmwareEntityHelper {

    public static void initFirmwareHelper()
    {
        Realm realm= RealmUtil.getRealm();
        realm.beginTransaction();
        // Delete all database
        RealmResults<FirmwareFileEntity> results = realm.where(FirmwareFileEntity.class).findAll();
        results.deleteAllFromRealm();
        realm.commitTransaction();
    }
    public static void processFirmwareFromParseObject(Context context, ParseObject parseObject, final OnFileRetrieved onFileRetrieved){
        Realm realm= RealmUtil.getRealm();
        realm.beginTransaction();
        final RawFileHelper rawFileHelper=new RawFileHelper(context);
        final FirmwareFileEntity firmwareFileEntity=firmwareFileEntityFromParseObject(parseObject);
        realm.commitTransaction();
        ParseFile firmwareFile=parseObject.getParseFile("firmwareFile");
        firmwareFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if(e==null){
                    onFileRetrieved.doneRetrieved(true,"Retrieve file success");
                    firmwareFileEntity.fileName= rawFileHelper.saveByteToFile(data,firmwareFileEntity.fileName);
                }else{
                    onFileRetrieved.doneRetrieved(false,"Retrieve file fail");
                }
            }
        });
    }

    public static void saveFirmwareEntity(FirmwareFileEntity firmwareFileEntity)
    {
        // Need to process file
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
