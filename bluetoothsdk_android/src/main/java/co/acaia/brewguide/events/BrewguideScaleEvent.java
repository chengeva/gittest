package co.acaia.brewguide.events;

public class BrewguideScaleEvent {
    public short command_id;
    public short command_val;
    public BrewguideScaleEvent(short cmd_id,short cmd_val){
        this.command_id=cmd_id;
        this.command_val=cmd_val;
    }
}
