package co.acaia.communications.scaleService;

import android.util.Log;

import co.acaia.communications.protocol.ver20.DataOutHelper;

/**
 * Created by hanjord on 2017/12/13.
 */

public class AcaiaProtocolHelper extends Thread{
    public static final int max_tries=20;
    private boolean scale_init=false;
    public AcaiaProtocolHelper()
    {

    }

    public void scale_has_init()
    {
        this.scale_init=true;
    }

    @Override
    public void run()
    {
        for(int i=0;i!=max_tries;i++){
            //DataOutHelper.force_handshake( "012345678901234".getBytes());
            //Log.v("AcaiaProtocolHelper","Send pass!");
            try {
                Thread.sleep(1000);
            }catch (Exception e){

            }
            if(this.scale_init){
                break;
            }
        }
    }

    public void shutdown_protocol_helper()
    {
        try{
            this.interrupt();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
