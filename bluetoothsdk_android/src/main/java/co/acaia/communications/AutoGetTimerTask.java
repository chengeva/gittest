package co.acaia.communications;

import org.greenrobot.eventbus.EventBus;

import java.util.TimerTask;

import co.acaia.communications.scalecommand.ScaleCommandEvent;
import co.acaia.communications.scalecommand.ScaleCommandType;

/**
 * Created by mrjedi on 2015/4/1.
 */
public class AutoGetTimerTask extends TimerTask{
    // auto get setting value from scale
//    private  static final String TAG="AutoGetTimerTask";
    private static final int send_interval=120;
      @Override
    public void run() {
          EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_TIMER.ordinal()));
          try {
              Thread.sleep( send_interval);
          }catch (Exception e){
          }
          EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_BATT.ordinal()));
    }
}
