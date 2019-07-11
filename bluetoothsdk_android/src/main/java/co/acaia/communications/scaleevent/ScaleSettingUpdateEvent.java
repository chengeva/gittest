package co.acaia.communications.scaleevent;

/**
 * Created by mrjedi on 2015/3/31.
 */
public class ScaleSettingUpdateEvent {
    private int event_type=-1;
    private float val;
    public ScaleSettingUpdateEvent(int type){
        event_type=type;
    }
    public ScaleSettingUpdateEvent(int type,float val_){
        event_type=type;
        val=val_;
    }
    public int get_type(){
        return event_type;
    }
    public float get_val(){
        return val;
    }
}
