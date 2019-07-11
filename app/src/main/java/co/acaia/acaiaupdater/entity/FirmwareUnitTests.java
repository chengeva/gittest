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
        query.getInBackground("pSeGEAKO9V", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    Realm realm= RealmUtil.getRealm();
                    realm.beginTransaction();
                    Log.v(testName,object.getObjectId());
                    Log.v(testName,object.getString("model"));

                    // Delete all database
                    RealmResults<FirmwareFileEntity> results = realm.where(FirmwareFileEntity.class).findAll();
                    results.deleteAllFromRealm();

                    FirmwareFileEntity firmwareFileEntity=FirmwareEntityHelper.firmwareFileEntityFromParseObject(object);
                    // test fields
                    Log.v(testName,"Test title:" +firmwareFileEntity.title);

                    realm.commitTransaction();
                    // Test retrieve object from database
                    realm.beginTransaction();
                    FirmwareFileEntity firmwareFileEntity1 = realm.where(FirmwareFileEntity.class).findFirst();
                    Log.v(testName,"Test found title:" +firmwareFileEntity1.title);
                    realm.commitTransaction();

                }
            }
        });
    }
}
