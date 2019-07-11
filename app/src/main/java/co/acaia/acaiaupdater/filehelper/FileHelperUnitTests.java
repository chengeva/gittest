package co.acaia.acaiaupdater.filehelper;

import android.content.Context;
import android.util.Log;

import co.acaia.acaiaupdater.entity.FirmwareFileEntity;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;
import co.acaia.acaiaupdater.entity.acaiaDevice.Lunar;

public class FileHelperUnitTests {
    public static void testRetrieveFirmwareFile(Context context)
    {
        final String TAGG="FileHelperUnitTests";
        ParseFileRetriever parseFileRetriever=new ParseFileRetriever();
        Lunar lunar= (Lunar) AcaiaDeviceFactory.acaiaDeviceFromModelName(AcaiaDevice.modelLunar);
        parseFileRetriever.retrieveFirmwareFilesByModel(context,lunar, new OnDataRetrieved() {
            @Override
            public void doneRetrieved(boolean success, String message) {
                Log.v(TAGG,String.valueOf(success)+" "+message);
            }
        });
    }
}
