package co.acaia.communications.protocol;

import android.content.Context;

import co.acaia.communications.CommLogger;
import co.acaia.communications.protocol.ver20.DataPacketHelper;
import co.acaia.communications.scale.AcaiaScale;
import co.acaia.communications.scale.AcaiaScaleFactory;
import co.acaia.communications.scaleService.ScaleCommunicationService;

/**
 * Created by hanjord on 15/3/20.
 */
public class ProtocolHelper {
    public static final String TAG="ProtocolHelper";

    /**
     * Use init data to get new or old acaia scale
     * @param data
     * @return
     */
    public static AcaiaScale getAcaiaScaleFromByte(byte[] data,Context context,ScaleCommunicationService mScaleCommunicationService, android.os.Handler h){
        AcaiaScale acaiaScale=null;
        if(DataPacketHelper.check_protocol_type(data)==1){
            CommLogger.logv(TAG, "Protocol 20");
            acaiaScale= AcaiaScaleFactory.createAcaiaScale(AcaiaScaleFactory.version_20, context, mScaleCommunicationService, h);
        }else{
            CommLogger.logv(TAG, "Protocol Old");
           acaiaScale= AcaiaScaleFactory.createAcaiaScale(AcaiaScaleFactory.version_1, context, mScaleCommunicationService, h);
        }

        return acaiaScale;
    }
}
