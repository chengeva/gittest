package co.acaia.communications.scale;

import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import co.acaia.communications.CommLogger;
import co.acaia.communications.protocol.old.AcaiaScaleAttributes;
import co.acaia.communications.scaleService.ScaleCommunicationService;
import co.acaia.communications.scaleevent.ScaleSettingUpdateEvent;
import co.acaia.communications.scaleevent.ScaleSettingUpdateEventType;
import co.acaia.communications.scaleevent.UpdateTimerEvent;

import com.acaia.scale.communications.AcaiaCommunicationPacketHelper;

import de.greenrobot.event.EventBus;


/**
 * Created by hanjord on 15/3/20.
 */
public class AcaiaScaleOld extends AcaiaScale {
    public static final String TAG = "AcaiaScaleOld";
    private Context context = null;
    private Handler handler;
    private Timer getWeightTimer;

    // debug file
    //  private File file;
    private long start = 0;
    FileOutputStream stream;

    // weight filter

    public int incommingPackt = 0;
    private float previousWeight = 0;
    private boolean minus5mode = false;

    public AcaiaScaleOld(Context ctx, ScaleCommunicationService mScaleCommunicationService_, Handler h) {
        init_scale_command();
        context = ctx;
        mScaleCommunicationService = mScaleCommunicationService_;
        handler = h;
        startGetWeightTask();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        //  file = new File(path + "/0_debug_weight"+new Date().toString()+".txt");
        start = System.nanoTime();
    /*    try {
            FileOutputStream stream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

    }

    private void writeLine(String what) {
        String elapse = String.valueOf((long) (System.nanoTime() - start) / 1000000000.0);
        //  try {
        //PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        // out.println(elapse+" "+what);
        // out.close();
        //  } catch (IOException e) {
        //exception handling left as an exercise for the reader
        //  }
    }


    private void init_scale_command() {
        scaleCommand = new AcaiaScaleCommand() {
            @Override
            public void parseDataPacket(byte[] data, AcaiaScale acaiaScale) {
                parsePacket(data);
            }

            @Override
            public void parseDataPacketCinco(byte[] data, AcaiaScale acaiaScale) {

            }


            @Override
            public boolean connect(String addr) {
                return false;
            }

            @Override
            public boolean stopScan() {
                return false;
            }

            @Override
            public boolean connectdebug() {
                return false;
            }

            @Override
            public boolean disconnect() {
                mScaleCommunicationService.disconnect();
                return false;
            }

            @Override
            public boolean getAutoOffTime() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getaskautooffcmd());
                return false;
            }

            @Override
            public boolean getBattery() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper
                        .getBatteryCommand());
                return true;
            }

            @Override
            public boolean getStatus() {
                return false;
            }

            @Override
            public boolean getBeep() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper
                        .getScaleBeepSound());
                return false;
            }

            @Override
            public int getConnectionState() {
                return 0;
            }

            @Override
            public boolean getKeyDisabledElapsedTime() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getaskdisablekeysecondscmd());

                return false;
            }

            @Override
            public boolean getTimer() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getScaleTimer());
                return false;
            }

            @Override
            public boolean getWeight() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper
                        .getSendWeightCommand());
                return true;
            }

            @Override
            public boolean pauseTimer() {
                CommLogger.logv(TAG, "pause");
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.pauseScaleTimer());

                return false;
            }

            @Override
            public boolean setAutoOffTime(int setting) {

                if (setting < 0 || setting > 5) {
                    return false;
                }
                return mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getautooffCmd(setting));

            }

            @Override
            public boolean setBeep(boolean on) {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getBeepONOFFCmd(on));
                return false;
            }

            @Override
            public boolean setKeyDisabledWithTime(int second) {

                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getdisableKeyCmd(second));
                return false;
            }

            @Override
            public boolean setLight(boolean on) {
                return false;
            }

            @Override
            public boolean setTare() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getTareCommand());
                return false;
            }

            @Override
            public boolean setUnit(short unit) {
                if (unit == 0)
                    mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getswitchunitGramcmd());
                else
                    mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getswitchUnitOzCmd());

                return false;
            }

            @Override
            public boolean startScan() {

                return false;
            }

            @Override
            public boolean startTimer() {
                CommLogger.logv(TAG, "start timer");
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.startScaleTimer());
                return false;
            }

            @Override
            public boolean stopTimer() {
                CommLogger.logv(TAG, "stop timer");
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.stopScaleTimer());
                return false;
            }

            @Override
            public boolean getCapacity() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getAskCapacityCmd());
                return false;
            }

            @Override
            public boolean setCapacity(int capacity) {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getSetCapacityCmd(capacity));
                return false;
            }
        };
    }

    @Override
    public int getProtocolVersion(){
        return AcaiaScale.protocol_version_old;
    }
    @Override
    public void release() {
        stopGetWeightTask();
    }

    private void parsePacket(byte[] data) {
        AcaiaCommunicationPacketHelper.parseScalePacket(data);
        final Intent intent = new Intent(ScaleCommunicationService.ACTION_DATA_AVAILABLE);
        int type = AcaiaCommunicationPacketHelper
                .getScalePacketDataType(data);

        switch (type) {
            case AcaiaScaleAttributes.ECMD.e_cmd_weight_r:
                float weightVal = AcaiaCommunicationPacketHelper
                        .parseScalePacket(data);

                // writeLine(String.valueOf(weightVal));

                String weightString = "";
                if ((int) AcaiaCommunicationPacketHelper.getweightUnit(data) == 0) {
                    // gram
                    weightString = String.format("%.1f", weightVal);
                    intent.putExtra(ScaleCommunicationService.EXTRA_UNIT, ScaleCommunicationService.UNIT_GRAM);

                } else {
                    weightString = String.format("%.3f", weightVal);
                    intent.putExtra(ScaleCommunicationService.EXTRA_UNIT, ScaleCommunicationService.UNIT_OUNCE);
                }

                /*
                 * Added weight filter as IOS
				 *
				 * 1. Need 0 two times to show 0 value 2. Need <0 two times to
				 * show <0 value 3. Other illegal cases are implemented in JNI
				 * wrapper:
				 * Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_parsescalepacket
				 */

                boolean illegalDataDetected = false;
                if (weightVal == 9999) {
                    illegalDataDetected = true;
                }

                // hanjord modify 20140813
                if (isNearZero(previousWeight) == false
                        && isNearZero(weightVal) == true) {
                    illegalDataDetected = true;
                }

				/*
                 * // Ignore the first 0. Could be noise. // orange modify
				 * 20140725 // if(previousWeight!=0 && inputWeight==0) if([self
				 * isNearZero:previousWeight]==NO && [self
				 * isNearZero:inputWeight]==YES) { illegalDataDetected=YES; }
				 */
                if (previousWeight != 0 && weightVal == 0) {
                    illegalDataDetected = true;
                }

                previousWeight = weightVal;
                if (weightVal < -5) {
                    if (minus5mode == false) {
                        minus5mode = true;
                        illegalDataDetected = true;
                    }
                } else {
                    minus5mode = false;
                }

                if (previousWeight != 0 && weightVal == 0) {
                    illegalDataDetected = true;
                }

                if (weightVal < -3000 || weightVal > 3000) {
                    illegalDataDetected = true;
                }

                if (!illegalDataDetected) {
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA,
                            weightString);
                    intent.putExtra("value",
                            weightVal);
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE,
                            ScaleCommunicationService.DATA_TYPE_WEIGHT);
                    if (weightVal >= 2050) {
                        CommLogger.logv(TAG, "ERROR weight >= 2050");
                    }
                }
                // CommLogger.logv(TAG,"Weight="+weightString);
                break;

            case AcaiaScaleAttributes.ECMD.e_cmd_battery_r:
                intent.putExtra(ScaleCommunicationService.EXTRA_DATA,
                        (AcaiaCommunicationPacketHelper
                                .parseScalePacket(data)));
                intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE,
                        ScaleCommunicationService.DATA_TYPE_BATTERY);
                break;
            case AcaiaScaleAttributes.ECMD.e_cmd_custom: {
                int sub_type = AcaiaCommunicationPacketHelper.getScalePacketSubDataType(data);
                float sub_type_value = AcaiaCommunicationPacketHelper.getSubdataValue(data);
                CommLogger.logv(TAG, "SUB TYPE: " + Integer.toString(sub_type));
                if (sub_type == 0) {
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_AUTO_OFF_TIME);
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA, sub_type_value);
                } else if (sub_type == 1) {
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_KEY_DISABLED_ELAPSED_TIME);
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA, sub_type_value);
                } else if (sub_type == 2) {
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_BEEP);
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA, sub_type_value);
                } else if (sub_type == 9) {
                    // timer started
                    int time = (int) AcaiaCommunicationPacketHelper.parseScalePacket(data);
                    CommLogger.logv(TAG, "timer 9 =" + String.valueOf(time));
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_TIMER);
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA, time);
                    CommLogger.logv(TAG, "timer 9 sub data=" + String.valueOf(sub_type_value));
                    EventBus.getDefault().post(new UpdateTimerEvent(false, time));
                } else if (sub_type == 10) {
                    // timer paused
                    int time = (int) AcaiaCommunicationPacketHelper.parseScalePacket(data);
                    CommLogger.logv(TAG, "timer 10 =" + String.valueOf(time));
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_TIMER);
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA, time);
                    CommLogger.logv(TAG, "timer 10 sub data=" + String.valueOf(sub_type_value));
                    EventBus.getDefault().post(new UpdateTimerEvent(true, time));
                } else if (sub_type == 4) { // korean version 20140827
                    int capacity = (int) (AcaiaCommunicationPacketHelper
                            .parseScalePacket(data));
                    if (capacity == 1)
                        capacity = 2000;
                    else {
                        capacity = 1000;
                    }
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_CAPACITY);
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA, capacity);
                    EventBus.getDefault().post(new ScaleSettingUpdateEvent(ScaleSettingUpdateEventType.event_type.EVENT_CAPACITY.ordinal(), capacity));
                }
            }
            break;
        }
        context.sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }

    private void startGetWeightTask() {
        if (getWeightTimer != null) {
            getWeightTimer.cancel();
            ;
            getWeightTimer = null;
        }
        getWeightTimer = new Timer();
        getWeightTimer.schedule(new GetWeightTask(this), 1500, 1800);
    }

    private void stopGetWeightTask() {
        getWeightTimer.cancel();
        getWeightTimer = null;
    }

    /*
        Old protocol needs a get data task
     */
    private class GetWeightTask extends TimerTask {

        public static final String TAG = "GetWeightTask";
        AcaiaScaleOld scale;

        public GetWeightTask(AcaiaScaleOld acaiaScaleOld) {
            scale = acaiaScaleOld;
        }

        @Override
        public void run() {
            CommLogger.logv(TAG, "Get weight!");
            if (scale.mScaleCommunicationService != null) {
                scale.getScaleCommand().getWeight();
            } else {
                CommLogger.logv(TAG, "Error: scale service null");
            }
        }
    }

    private Boolean isNearZero(float floatValue) {
        if (floatValue <= 1.0 && floatValue >= -1.0)
            return true;
        else
            return false;
    }

}
