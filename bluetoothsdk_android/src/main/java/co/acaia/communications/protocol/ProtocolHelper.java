package co.acaia.communications.protocol;

import android.content.Context;

import co.acaia.communications.CommLogger;
import co.acaia.communications.protocol.old.pearldataparser.PearlDataHelper;
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
    public static AcaiaScale getAcaiaScaleFromByte(PearlDataHelper pearlDataHelper,byte[] data,Context context,ScaleCommunicationService mScaleCommunicationService, android.os.Handler h){
        AcaiaScale acaiaScale= AcaiaScaleFactory.createAcaiaScale(AcaiaScaleFactory.version_20, context, mScaleCommunicationService, h,pearlDataHelper,false);

        return acaiaScale;
    }
}
