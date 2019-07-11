package co.acaia.acaiaupdater.entity;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
                    Log.v(testName,object.getObjectId());
                }
            }
        });
    }
}
