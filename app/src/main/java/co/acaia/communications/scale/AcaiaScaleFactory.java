package co.acaia.communications.scale;

import android.content.Context;
import android.os.Handler;

import co.acaia.communications.scaleService.ScaleCommunicationService;

/**
 * Created by hanjord on 15/3/20.
 */
public class AcaiaScaleFactory {
    public static final int version_1=0;
    public static final int version_18=1;
    public static final int version_20=2;
    public static AcaiaScale createAcaiaScale(int version,Context context,ScaleCommunicationService mScaleCommunicationService,Handler h){
        AcaiaScale acaiaScale=null;
        if(version==version_1){
            // Original Protocol
            acaiaScale=new AcaiaScaleOld(context,mScaleCommunicationService,h) ;
        }else if(version==version_18){
            // Protocol 1.8
            acaiaScale=new AcaiaScale18();
        }else if(version==version_20){
            acaiaScale=new AcaiaScale2(context,mScaleCommunicationService,h);
        }
        return acaiaScale;
    }
}
