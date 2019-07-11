package co.acaia.communications;

import org.greenrobot.eventbus.EventBus;

import co.acaia.communications.events.ScaleDataEvent;
import co.acaia.communications.protocol.ver20.ScaleProtocol;
/**
 * Created by hanjord on 15/3/17.
 */
public class ScaleDataParser {
    /**
     * Handle Battery Life Val Update
     * @param battery_power_val
     */
    public static void parse_battery_event(short battery_power_val){
        EventBus.getDefault().post(new ScaleDataEvent(ScaleDataEvent.data_battery,battery_power_val));
    }

    public static void parse_key_event(short key_val){
        CommLogger.logv("keyevent","key="+String.valueOf(key_val));
        EventBus.getDefault().post(new ScaleDataEvent(ScaleDataEvent.data_key,key_val));
    }



    /**
     * Handle timer value update
     * @param tmevent
     */
    public static void parse_timer_event(ScaleProtocol.tm_event tmevent){
        EventBus.getDefault().post(new ScaleDataEvent(ScaleDataEvent.data_timer,tmevent.getMin(),tmevent.getSec(),tmevent.getDse()));
    }
}
