package co.acaia.communications.scalecommand;

/**
 * Created by hanjord on 2016/2/21.
 */
public class AutoConnectEvent {
    private int scantimeOut=3000;
    public AutoConnectEvent(int scan_time_out){
        this.scantimeOut=scan_time_out;
    }
    public AutoConnectEvent(){

    }
    public int getScanTimeOut(){
        return scantimeOut;
    }
}
