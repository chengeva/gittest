package co.acaia.communications.scaleevent;


/**
 * Created by mrjedi on 2015/3/31.
 */
public class ScaleSettingUpdateEventType {
    public enum event_type{	
        EVENT_KEY_DISABLED_ELAPSED_TIME,
        EVENT_BEEP,
        EVENT_AUTO_OFF_TIME,
        EVENT_BATTERY,
        EVENT_UNIT,
        EVENT_CAPACITY
    }

    public static enum event_sound_on_off{
        OFF,ON
    }

    public static enum event_unit{
        GRAM,OZ
    }


}
