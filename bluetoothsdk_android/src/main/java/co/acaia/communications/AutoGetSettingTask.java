package co.acaia.communications;

import org.greenrobot.eventbus.EventBus;

import java.util.TimerTask;

import co.acaia.communications.scalecommand.ScaleCommandEvent;
import co.acaia.communications.scalecommand.ScaleCommandType;

/**
 * Created by mrjedi on 2015/3/31.
 */
public class AutoGetSettingTask extends TimerTask {
    // auto get setting value from scale
    private  static final String TAG="AutoGetSettingTask";
    private static final int send_interval=120;
    @Override
    public void run() {
        CommLogger.logv(TAG,"get settings from scale!!");
         // get Auto disable key
        EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_DISABLE_KEY_TIME.ordinal()));
        try {
            Thread.sleep( send_interval);
        }catch (Exception e){
        }
        EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_SOUND_ONOFF.ordinal()));
        try {
            Thread.sleep( send_interval);
        }catch (Exception e){
        }
        EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_UNIT.ordinal()));
        try {
            Thread.sleep( send_interval);
        }catch (Exception e){
        }
        EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_AUTO_OFF_TIME.ordinal()));
        try {
            Thread.sleep( send_interval);
        }catch (Exception e){
        }
        EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_MAX_WEIGHT.ordinal()));
        try {
            Thread.sleep( send_interval);
        }catch (Exception e){
        }
        EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_BATT.ordinal()));
        try {
            Thread.sleep( send_interval);
        }catch (Exception e){
        }
        EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_CAPACITY.ordinal()));
    }
}
