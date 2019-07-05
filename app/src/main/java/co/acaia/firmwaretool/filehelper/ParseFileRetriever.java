package co.acaia.firmwaretool.filehelper;

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

import co.acaia.ProjectSettings;
import co.acaia.firmwaretool.Events.DownloadFirmwareFailedEvent;
import co.acaia.firmwaretool.Events.DownloadedFirmwareEvent;
import co.acaia.firmwaretool.MainActivity;
import co.acaia.firmwaretool.entity.FirmwareFileEntity;
import co.acaia.firmwaretool.entity.FirmwareFileEntityHelper;
import co.acaia.firmwaretool.rawfile.RawFileHelper;
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

    /**
     * Download files from parse
     * Todo: validate downlaoded files
     */
    private void downloadFirmwareFile(final Context context){
        new Thread(){
            public void run(){
                try {

                    Parse.initialize(new Parse.Configuration.Builder(context)
                            .applicationId(MainActivity.new_app_id)
                            .clientKey(MainActivity.new_client_key)
                            .server(MainActivity.new_endpoint)
                            .build()
                    );
                    Thread.sleep(500);
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("firmware");
                    query.whereEqualTo(ProjectSettings.filter_tag, true);
                    query.addDescendingOrder("releaseDate");
                    // hanjord



                    // 获取 Locale 的方式有二

                    Locale locale = Locale.getDefault();
// 获取当前系统语言
                    String lang = locale.getLanguage() + "-" + locale.getCountry();
                    Log.v("Locale","current lan="+lang);
                    if(lang.contains("zh-CN")){
                        query.whereEqualTo("model","LunaCN");
                        Log.v("Locale","Query for Lunar CN");
                        // current lan=zh-TW
                    }else{
                        query.whereEqualTo("model","Lunar");
                    }
                    List<FirmwareFileEntity> firmwareFileEntities=FirmwareFileEntity.listAll(FirmwareFileEntity.class);
                    for(int i=0;i!=firmwareFileEntities.size();i++){
                        firmwareFileEntities.get(i).delete();
                    }

                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> firmwareFileList, ParseException e) {
                            if (e == null) {
                                Log.v(TAG,"got n files "+String.valueOf(firmwareFileList.size()));
                                for(int i=0;i!=firmwareFileList.size();i++){
                                    ParseObject firmwareParseObject=firmwareFileList.get(i);
                                    ParseFile binFile=firmwareParseObject.getParseFile("binFile");
                                    try {
                                        if(!FirmwareFileEntityHelper.if_downloaded_firmware_object(firmwareParseObject.getObjectId())){
                                            byte[] data= binFile.getData();

                                            // save to local...
                                            FirmwareFileEntity firmwareFileEntity=FirmwareFileEntityHelper.getFirmwareEntityFromParseObject(firmwareParseObject);
                                            RawFileHelper rawFileHelper=new RawFileHelper(context);
                                            firmwareFileEntity.filename= rawFileHelper.saveByteToFile(data,firmwareFileEntity.filename);
                                            Log.v(TAG,"saved file..."+firmwareFileEntity.filename);
                                            firmwareFileEntity.save();
                                            EventBus.getDefault().post(new DownloadedFirmwareEvent());
                                            Log.v(TAG,"Saved "+firmwareFileEntity.title);
                                        }else{
                                            Log.v(TAG,"Already Saved...");
                                        }
                                    } catch (ParseException e1) {
                                        EventBus.getDefault().post(new DownloadFirmwareFailedEvent());
                                        e1.printStackTrace();
                                    }
                                }
                            } else {
                                Log.v(TAG,"error"+e.getMessage());
                                EventBus.getDefault().post(new DownloadFirmwareFailedEvent());
                            }
                        }
                    });
                }catch (Exception e){
                    EventBus.getDefault().post(new DownloadFirmwareFailedEvent());
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
