package co.acaia.firmwaretool.filehelper;

import android.content.Context;

/**
 * Created by hanjord on 15/5/5.
 */
public class FirmwareFileFactory {
    ParseFileRetriever parseFileRetriever;
    LocalFileRetriever localFileRetriever;
    Context context;
    public FirmwareFileFactory(Context context){
        parseFileRetriever=new ParseFileRetriever();
        localFileRetriever=new LocalFileRetriever();
        this.context=context;
    }
    public void proceedRetrieveFiles(){
        parseFileRetriever.retrieve_firmware_files(context);
        localFileRetriever.retrieve_firmware_files(context);
    }
}
