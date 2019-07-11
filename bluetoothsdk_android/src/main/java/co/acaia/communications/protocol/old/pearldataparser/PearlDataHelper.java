package co.acaia.communications.protocol.old.pearldataparser;
/**
 * Created by hanjord on 15/9/15.
 */
public class PearlDataHelper {
    public cmd_command _cmd_command;
    public en_command _en_command;
    public float previousWeight = 0;
    public boolean minus5mode = false;

    public PearlDataHelper(){
        _cmd_command=new cmd_command();
        _en_command=new en_command();

    }
}
