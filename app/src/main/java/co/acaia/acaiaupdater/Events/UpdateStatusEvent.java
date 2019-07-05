package co.acaia.acaiaupdater.Events;

/**
 * Created by hanjord on 2015/10/29.
 */
public class UpdateStatusEvent {
    public static final int  ISPIntialState = 0;
    public static final int  ISPRemovingState = 1;
    public static final int ISPUpdatingState=2;
    public static final int  ISPCompletedState=3;
    public int status=-1;
    public UpdateStatusEvent(int status){
        this.status=status;
    }
}
