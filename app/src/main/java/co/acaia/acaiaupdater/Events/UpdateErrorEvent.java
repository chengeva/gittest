package co.acaia.acaiaupdater.Events;

/**
 * Created by hanjord on 2015/10/29.
 */
public class UpdateErrorEvent {
    public static final int error_disconnected=0;
    public static final int error_bluetooth=1;
    public int error_code=-1;
    public String error_code_str="";
    public UpdateErrorEvent(int error){
        this.error_code=error;
    }
    public UpdateErrorEvent(int error,String str){
        this.error_code=error;
        this.error_code_str=str;
    }
}
