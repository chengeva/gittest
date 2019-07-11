package co.acaia.brewguide.events;

public class BrewguideCommandEvent {
    private short command_type=-1;
    private short command_value=0;
    public BrewguideCommandEvent(short command_type_,short commend_value_){
        command_type=command_type_;
        command_value=commend_value_;
    }

    public short getCommandType(){
        return command_type;
    }
    public short getCommandVal(){
        return command_value;
    }
}
