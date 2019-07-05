package co.acaia.communications.scalecommand;

/**
 * Created by hanjord on 15/3/31.
 */
public class ScaleConnectionCommandEvent {
    public int command=-1;
    public String addr="";
    public ScaleConnectionCommandEvent(int type){
        command=type;
    }
    public ScaleConnectionCommandEvent(int type,String address){
        addr=address;
        command=type;
    }
}
