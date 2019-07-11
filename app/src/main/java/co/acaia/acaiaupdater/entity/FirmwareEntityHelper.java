package co.acaia.acaiaupdater.entity;

import android.content.Context;
import android.util.Log;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;

import co.acaia.acaiaupdater.filehelper.OnFileRetrieved;
import co.acaia.acaiaupdater.firmwarelunar.CISP_handler;
import co.acaia.acaiaupdater.firmwarelunar.FileHandler;
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
    public static void processFirmwareFromParseObject(final Context context, ParseObject parseObject, final OnFileRetrieved onFileRetrieved){
        final Realm realm= RealmUtil.getRealm();

        final RawFileHelper rawFileHelper=new RawFileHelper(context);
        final FirmwareFileEntity firmwareFileEntity=firmwareFileEntityFromParseObject(parseObject);

        ParseFile firmwareFile=parseObject.getParseFile("firmwareFile");
        firmwareFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                realm.beginTransaction();
                if(e==null){

                    onFileRetrieved.doneRetrieved(true,"Retrieve file success");
                    firmwareFileEntity.fileName= rawFileHelper.saveByteToFile(data,firmwareFileEntity.fileName);
                }else{
                    onFileRetrieved.doneRetrieved(false,"Retrieve file fail");
                }
                realm.commitTransaction();
                File firmwareFile = new File(context.getFileStreamPath(firmwareFileEntity.fileName).getAbsolutePath());
                FileHandler.test_open_file(context, firmwareFile);

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
        realm.beginTransaction();
        FirmwareFileEntity firmwareFileEntity=realm.createObject(FirmwareFileEntity.class);
        firmwareFileEntity.model=parseObject.getString("model");
        firmwareFileEntity.title=parseObject.getString("title");
        firmwareFileEntity.detail=parseObject.getString("detail");
        firmwareFileEntity.shortCap=parseObject.getString("caption");
        firmwareFileEntity.addVer=parseObject.getInt("addVersion");
        firmwareFileEntity.subVer=parseObject.getInt("minorVersion");
        firmwareFileEntity.mainVer=parseObject.getInt("majorVersion");
        firmwareFileEntity.customOrdering=parseObject.getInt("customOrdering");

        firmwareFileEntity.fileName=parseObject.getParseFile("firmwareFile").getUrl().split("/")[parseObject.getParseFile("firmwareFile").getUrl().split("/").length-1];
        realm.commitTransaction();

        return firmwareFileEntity;
    }
}
