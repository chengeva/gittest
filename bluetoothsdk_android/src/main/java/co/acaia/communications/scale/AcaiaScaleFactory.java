package co.acaia.communications.scale;

import android.content.Context;
import android.os.Handler;

import co.acaia.communications.protocol.FellowEKG;
import co.acaia.communications.protocol.old.pearldataparser.PearlDataHelper;
import co.acaia.communications.scaleService.ScaleCommunicationService;

/**
 * Created by hanjord on 15/3/20.
 */
public class AcaiaScaleFactory {
    //public static final int version_1=0;
    // hanjord: no more version 1 scales supported.
   // public static final int version_18=1;
    public static final int version_20=2;
    public static final int version_sette=99;
    public static AcaiaScale createAcaiaScale(int version,Context context,ScaleCommunicationService mScaleCommunicationService,Handler h,PearlDataHelper pearlDataHelper, boolean isCinco){    AcaiaScale acaiaScale=null;

         if(version==version_20){
            acaiaScale=new AcaiaScale2(context,mScaleCommunicationService,h,isCinco);
        }else if(version==version_sette){
            acaiaScale=new Kettle(context,mScaleCommunicationService,h);
        }
        return acaiaScale;
    }
}
