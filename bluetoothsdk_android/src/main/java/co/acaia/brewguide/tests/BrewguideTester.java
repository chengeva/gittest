package co.acaia.brewguide.tests;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.greenrobot.eventbus.EventBus;

import co.acaia.brewguide.BrewguideUploader;
import co.acaia.brewguide.events.BrewguideCommandEvent;
import co.acaia.brewguide.model.Brewguide;
import co.acaia.communications.protocol.ver20.BrewguideProtocol;
import co.acaia.communications.protocol.ver20.ScaleProtocol;
import co.acaia.communications.scaleService.gatt.Log;

public class BrewguideTester {
    public static void testBrewguide()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("BrewguidePearlS");
        query.getInBackground("qw41uUm02D", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject brewguideObj, ParseException e) {
            if(e==null){
                Log.v("BrewguideTester","Got sample brewguide"); // OK
                Brewguide brewguide=new Brewguide(brewguideObj);
            }
            }
        });
    }

    public static void testStartUploadBrewguide()
    {

        final BrewguideUploader brewguideUploader=new BrewguideUploader(BrewguideUploader.UPLOAD_MODE.upload_mode_brewguide);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Brewguide");
        query.getInBackground("Gv8VHXnVMs", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject brewguideObj, ParseException e) {
                if(e==null){
                    Log.v("BrewguideTester","Got sample brewguide"); // OK
                    Brewguide brewguide=new Brewguide(brewguideObj);
                    brewguideUploader.setBrewguideData(brewguide);
                    BrewguideCommandEvent event=new BrewguideCommandEvent((short)ScaleProtocol.ECMD.new_cmd_sync_brewguide_s.ordinal(),(short)5);
                    EventBus.getDefault().post(event);
                }else{
                    Log.v("BrewguidePearlS"," error getting brewguide"+e.getLocalizedMessage());
                }
            }
        });
    }

    public static void testStartUploadHello()
    {

        final BrewguideUploader brewguideUploader=new BrewguideUploader(BrewguideUploader.UPLOAD_MODE.upload_mode_hello);
        brewguideUploader.setHelloData("Hello Acaia :)");
        BrewguideCommandEvent event=new BrewguideCommandEvent((short)ScaleProtocol.ECMD.new_cmd_sync_hello_s.ordinal(),(short)5);
        EventBus.getDefault().post(event);
    }
}
