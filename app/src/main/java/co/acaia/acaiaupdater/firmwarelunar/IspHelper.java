package co.acaia.acaiaupdater.firmwarelunar;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;

import co.acaia.acaiaupdater.entity.FirmwareFileEntity;
import co.acaia.communications.CommLogger;
import co.acaia.communications.protocol.ver20.DataOutHelper;
import co.acaia.communications.scaleService.ScaleCommunicationService;

/**
 * Created by hanjord on 15/4/21.
 */
public class IspHelper {
    public static final String TAG = "IspHelper";
    private ScaleCommunicationService mScaleCommunicationService;
    private Context context = null;
    private Handler handler;
    private CISP_handler cisphandler;
    private int targetISPVersion;
    private String currentModelName;
    public IspHelper(Context ctx, ScaleCommunicationService mScaleCommunicationService_, Handler h, FirmwareFileEntity firmwareFileEntity) {
        context = ctx;
        mScaleCommunicationService = mScaleCommunicationService_;
        handler = h;
        currentModelName=firmwareFileEntity.model;
        File firmwareFile = new File(context.getFileStreamPath(firmwareFileEntity.fileName).getAbsolutePath());
        Log.v("file name=","file name="+firmwareFile.getAbsolutePath());
        cisphandler = new CISP_handler(firmwareFile);
        FileHandler.open_file(context, firmwareFile, cisphandler);

    }

    public void parseDataPacket(byte[] data) {
        for (int i = 0; i != data.length; i++) {
            Isp.parse_input(cisphandler, data[i], context, mScaleCommunicationService,currentModelName);
        }
    }

    public void startIsp() {
        CommLogger.logv(TAG, "start isp");
        cisphandler.mb_started = true;
    }

    public void change_isp_mode(){
        CommLogger.logv(TAG,"change ISP mode");
        DataOutHelper.start_isp();
        ;
    }

    public void release() {
        cisphandler=null;
    }
}
