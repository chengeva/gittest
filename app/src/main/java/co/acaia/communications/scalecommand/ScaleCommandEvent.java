package co.acaia.communications.scalecommand;

/**
 * Created by hanjord on 15/3/30.
 */

/**
 * Event bus event for any process to give command to acaia scale service.
 */
public class ScaleCommandEvent {
    private int command_type=-1;
    private int command_value=0;
    public ScaleCommandEvent(int command_type_,int commend_value_){
        command_type=command_type_;
        command_value=commend_value_;
    }
    public
    ScaleCommandEvent(int command_type_){
        command_type=command_type_;
    }
    public int getCommandType(){
        return command_type;
    }

    public int getCommandVal(){
        return command_value;
    }
}
