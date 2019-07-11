package co.acaia.acaiaupdater.filehelper;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.List;
import java.util.Locale;

import co.acaia.acaiaupdater.Events.DownloadFirmwareFailedEvent;
import co.acaia.acaiaupdater.Events.DownloadedFirmwareEvent;
import co.acaia.acaiaupdater.MainActivity;
import co.acaia.acaiaupdater.ProjectSettings;
import co.acaia.acaiaupdater.entity.FirmwareEntityHelper;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.rawfile.RawFileHelper;
import de.greenrobot.event.EventBus;

public class ParseFileRetriever implements  FileRetriever{
    int numData=0;

    public static final String TAG="ParseFileRetriever";

    public void retrieveFirmwareFilesByModel(final Context context,AcaiaDevice acaiaDevice, final OnDataRetrieved onDataRetrieved){
        // Call callback if success or fail
        String modelName=acaiaDevice.modelName;
        numData=0;
        // Query Parse
        try {
            // Clear database
            FirmwareEntityHelper.initFirmwareHelper();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("AcaiaPlusFirmware");
            query.whereEqualTo("model", modelName);
            query.addDescendingOrder("releaseDate");
            // hanjord
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(final List<ParseObject> firmwareFileList, ParseException e) {
                    if (e == null) {
                        Log.v(TAG,"got n files "+String.valueOf(firmwareFileList.size()));
                        numData=firmwareFileList.size();

                        for(int i=0;i!=firmwareFileList.size();i++){
                            FirmwareEntityHelper.processFirmwareFromParseObject(context,firmwareFileList.get(i), new OnFileRetrieved() {
                                @Override
                                public void doneRetrieved(boolean success, String message) {
                                    if(success==true){
                                        numData--;
                                        if(numData==0){
                                            Log.v(TAG,"Done retrieve "+String.valueOf(firmwareFileList.size()));
                                        }
                                    }
                                }
                            });
                        }

                    } else {
                        onDataRetrieved.doneRetrieved(false,"Parse error "+e.getLocalizedMessage());
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        // Fail
        //onFileRetrieved.done(false);
    }

    /**
     * Reteieves firmware files from parse
     */
    @Override
    public void retrieve_firmware_files(Context context) {
        downloadFirmwareFile(context);
    }

    @Override
    public boolean validate_file(File firmwareFile) {
        return false;
    }


    /**
     * Download files from parse
     * Todo: validate downlaoded files
     */
    private void downloadFirmwareFile(final Context context){
        
    }
}
