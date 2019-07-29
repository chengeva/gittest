package co.acaia.acaiaupdater.entity;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import co.acaia.acaiaupdater.util.RealmUtil;
import io.realm.Realm;
import io.realm.RealmResults;

public class FirmwareUnitTests {

    /**
     *  Test firmware database object
     */
    public static void testFirmwareEntity(){
        final String testName="testFirmwareEntity";
        ParseQuery<ParseObject> query = ParseQuery.getQuery("AcaiaPlusFirmware");
        query.getInBackground("MQoN1xUkXO", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    Realm realm= RealmUtil.getRealm();
                    realm.beginTransaction();
                    //Log.v(testName,object.getObjectId());
                    //Log.v(testName,object.getString("model"));

                    // Delete all database
                    RealmResults<FirmwareFileEntity> results = realm.where(FirmwareFileEntity.class).findAll();
                    results.deleteAllFromRealm();
                    realm.commitTransaction();

                    FirmwareFileEntity firmwareFileEntity=FirmwareEntityHelper.firmwareFileEntityFromParseObject(object);
                    // test fields
                    //Log.v(testName,"Test title:" +firmwareFileEntity.title);
                    //Log.v(testName,"Test detail" +firmwareFileEntity.detail);
                    //Log.v(testName,"Test caption" +firmwareFileEntity.shortCap);
                    //Log.v(testName,"Test model" +firmwareFileEntity.model);
                    //Log.v(testName,"Test fileName" +firmwareFileEntity.fileName);
                    //Log.v(testName,"Test addVersion" +firmwareFileEntity.addVer);
                    //Log.v(testName,"Test majorVersion" +firmwareFileEntity.mainVer);
                    //Log.v(testName,"Test customOrdering" +firmwareFileEntity.customOrdering);




                    // Test retrieve object from database
                    realm.beginTransaction();
                    FirmwareFileEntity firmwareFileEntity1 = realm.where(FirmwareFileEntity.class).findFirst();
                    //Log.v(testName,"Test found title:" +firmwareFileEntity1.title);
                    //Log.v(testName,"Test found detail:" +firmwareFileEntity1.detail);
                    //Log.v(testName,"Test found caption:" +firmwareFileEntity1.shortCap);
                    //Log.v(testName,"Test found model:" +firmwareFileEntity1.model);
                    //Log.v(testName,"Test found fileName:" +firmwareFileEntity1.fileName);
                    //Log.v(testName,"Test found addVersion:" +firmwareFileEntity1.addVer);
                    //Log.v(testName,"Test found majorVersion:" +firmwareFileEntity1.mainVer);
                    //Log.v(testName,"Test customOrdering:" +firmwareFileEntity1.customOrdering);




                    realm.commitTransaction();

                }
            }
        });
    }
}
