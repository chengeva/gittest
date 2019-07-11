package co.acaia.communications.events;

/**
 * Created by hanjord on 15/3/13.
 */
public class SendDataEvent {

    public byte[] out_data;
    public SendDataEvent(byte[] s_data){
        out_data=s_data.clone();
        s_data=null;
    }
}
