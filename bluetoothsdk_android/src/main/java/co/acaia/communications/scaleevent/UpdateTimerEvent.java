package co.acaia.communications.scaleevent;

/**
 * Created by hanjord on 15/3/31.
 */
public class UpdateTimerEvent {
    public Boolean if_paused=false;
    public int currSeconds=0;
    public UpdateTimerEvent(boolean if_paused_,int sec){
           if_paused=if_paused_;
        currSeconds=sec;
    }
}
