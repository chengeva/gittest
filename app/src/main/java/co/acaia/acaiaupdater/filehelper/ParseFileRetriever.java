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
import co.acaia.acaiaupdater.rawfile.RawFileHelper;
import de.greenrobot.event.EventBus;

public class ParseFileRetriever implements  FileRetriever{


    public static final String TAG="ParseFileRetriever";



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

    public void test_parse(Context context)
    {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("AcaiaPlusFirmware");
            //query.whereEqualTo(ProjectSettings.filter_tag, true);
            query.addDescendingOrder("releaseDate");
            // hanjord
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> firmwareFileList, ParseException e) {
                    if (e == null) {
                        Log.v(TAG,"got n files "+String.valueOf(firmwareFileList.size()));
                        for(int i=0;i!=firmwareFileList.size();i++){

                        }
                    } else {
                        Log.v(TAG,"error"+e.getMessage());
                        EventBus.getDefault().post(new DownloadFirmwareFailedEvent());
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Download files from parse
     * Todo: validate downlaoded files
     */
    private void downloadFirmwareFile(final Context context){
        
    }
}
