package co.acaia.communications.events;

/**
 * Created by hanjord on 2015/11/9.
 */
public class ScaleFirmwareVersionEvent {
    public int firmwarever=0;
    public ScaleFirmwareVersionEvent(int ver){
        this.firmwarever=ver;
    }
}