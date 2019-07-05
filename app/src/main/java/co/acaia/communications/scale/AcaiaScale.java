package co.acaia.communications.scale;

import co.acaia.communications.scaleService.ScaleCommunicationService;

/**
 * Created by hanjord on 15/3/20.
 */

public abstract class AcaiaScale {
    public static final int protocol_version_old=0;
    public static final int protocol_version_18=1;
    public static final int protocol_version_20=2;

//    private final static String TAG = AcaiaScale.class.getSimpleName();
//    private Context mCtx = null;
    protected ScaleCommunicationService mScaleCommunicationService = null;
    protected AcaiaScaleCommand scaleCommand;


    public AcaiaScaleCommand getScaleCommand(){
        return scaleCommand;
    }
    public void release(){

    }
    public int getProtocolVersion(){
        return -1;
    }

}
