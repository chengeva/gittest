package co.acaia.communications.scaleevent;

/**
 * Created by hanjord on 15/3/31.
 */
public class NewScaleConnectionStateEvent {

    public NewScaleConnectionStateEvent(String addr){
        scale_mac_id=addr;
    }
    public String scale_mac_id="";
}
