package co.acaia.communications.scalecommand;

/**
 * Created by hanjord on 15/3/30.
 */
public class ScaleCommandType {
    public static enum command_id{
        SEND_TARE,
        SEND_CHANGE_UNIT,
        SEND_TIMER_COMMAND,
        SEND_SOUND_ONOFF,
        SEND_SET_AUTOOFF,
        SEND_SET_DISABLE_KEY,
        SEND_SET_CAPACITY,
        GET_UNIT,
        GET_SOUND_ONOFF,
        GET_AUTO_OFF_TIME,
        GET_DISABLE_KEY_TIME,
        GET_MAX_WEIGHT,
        GET_BATT,
        GET_TIMER,
        GET_CONNECTION_STATE,
        GET_CAPACITY
    }

    public static enum set_sound_on_off{
        ON,OFF
    }

    public static enum set_capacity{
        MAX_1000G,MAX_2000G
    }

    public static enum set_unit{
        GRAM,OZ
    }

    public static enum set_timer{
        START,PAUSE,STOP
    }
}
