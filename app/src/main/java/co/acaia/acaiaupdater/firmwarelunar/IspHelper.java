package co.acaia.acaiaupdater.firmwarelunar;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;

import co.acaia.acaiaupdater.entity.AcaiaFirmware;
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
    public CISP_handler cisphandler;
    private int targetISPVersion;
    private String currentModelName;
    public int isISP;
    public static final int ISP_CHECK_INIT=0;
    public static final int ISP_CHECK_APP=1;
    public static final int ISP_CHECK_ISP=2;
    public IspHelper(Context ctx, ScaleCommunicationService mScaleCommunicationService_, Handler h, AcaiaFirmware firmwareFileEntity) {
        context = ctx;
        isISP=0;
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
