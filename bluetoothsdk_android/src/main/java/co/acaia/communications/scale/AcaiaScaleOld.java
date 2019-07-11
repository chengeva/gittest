package co.acaia.communications.scale;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;

import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import co.acaia.communications.CommLogger;
import co.acaia.communications.protocol.old.pearldataparser.AcaiaCommunicationPacketHelperJav;
import co.acaia.communications.protocol.old.pearldataparser.PearlDataHelper;
import co.acaia.communications.scaleService.ScaleCommunicationService;

//import com.acaia.scale.communications.AcaiaCommunicationPacketHelper;
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

    PearlDataHelper pearlDataHelper;

    public AcaiaScaleOld(PearlDataHelper pearlDataHelper, Context ctx, ScaleCommunicationService mScaleCommunicationService_, Handler h) {
        init_scale_command();
        this.pearlDataHelper = pearlDataHelper;
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
            public void parseDataPacket(byte[] data) {
                //parsePacket(data);
                final Intent intent = new Intent(ScaleCommunicationService.ACTION_DATA_AVAILABLE);
                AcaiaCommunicationPacketHelperJav.parsePacketJav(data,pearlDataHelper,intent,context);
            }

            @Override
            public boolean getFirmwareInfo() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.get_pack_info_req(pearlDataHelper));
                return false;
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
                //mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getaskautooffcmd());
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getaskautooffcmd(pearlDataHelper));

                return false;
            }

            @Override
            public boolean getBattery() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getBatteryCommand(pearlDataHelper));
                return true;
            }

            @Override
            public boolean getStatus() {
                return false;
            }

            @Override
            public boolean getBeep() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getScaleBeepSound(pearlDataHelper));
                return false;
            }

            @Override
            public int getConnectionState() {
                return 0;
            }

            @Override
            public boolean getKeyDisabledElapsedTime() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getaskdisablekeysecondscmd(pearlDataHelper));
                return false;
            }

            @Override
            public boolean getTimer() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getScaleTimer(pearlDataHelper));
                return false;
            }

            @Override
            public boolean getWeight() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getSendWeightCommand(pearlDataHelper));
                return true;
            }

            @Override
            public boolean pauseTimer() {
                CommLogger.logv(TAG, "pause");
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.pauseScaleTimer(pearlDataHelper));

                return false;
            }

            @Override
            public boolean setAutoOffTime(int setting) {

                if (setting < 0 || setting > 5) {
                    return false;
                }
                //return mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelper.getautooffCmd(setting));
                return mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getautooffCmd(pearlDataHelper, setting));

            }

            @Override
            public boolean setBeep(boolean on) {

                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getBeepONOFFCmd(pearlDataHelper, on));
                return false;
            }

            @Override
            public boolean setKeyDisabledWithTime(int second) {

                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getdisableKeyCmd(pearlDataHelper, second));
                return false;
            }

            @Override
            public boolean setLight(boolean on) {
                return false;
            }

            @Override
            public boolean setTare() {
                // byte[] origout = AcaiaCommunicationPacketHelper.getTareCommand();
                byte[] newOut = AcaiaCommunicationPacketHelperJav.getTareCommand(pearlDataHelper);
                //mScaleCommunicationService.sendCmdwithResponseDebug(newOut);
                mScaleCommunicationService.sendCmdwithResponse(newOut);
                return false;
            }

            @Override
            public boolean setUnit(short unit) {
                if (unit == 0)
                    mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getswitchunitGramcmd(pearlDataHelper));
                else
                    mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getswitchUnitOzCmd(pearlDataHelper));

                return false;
            }

            @Override
            public boolean startScan() {

                return false;
            }

            @Override
            public boolean startTimer() {
                CommLogger.logv(TAG, "start timer");
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.startScaleTimer(pearlDataHelper));
                return false;
            }

            @Override
            public boolean stopTimer() {
                CommLogger.logv(TAG, "stop timer");
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.stopScaleTimer(pearlDataHelper));
                return false;
            }

            @Override
            public boolean getCapacity() {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getAskCapacityCmd(pearlDataHelper));
                return false;
            }

            @Override
            public boolean setCapacity(int capacity) {
                mScaleCommunicationService.sendCmdwithResponse(AcaiaCommunicationPacketHelperJav.getSetCapacityCmd(pearlDataHelper, capacity));
                return false;
            }

            @Override
            public boolean setKettleTargetTemp(int temp) {
                return false;
            }

            @Override
            public boolean setKettleOnOff(boolean on) {
                return false;
            }
        };
    }

    @Override
    public int getProtocolVersion() {
        return AcaiaScale.protocol_version_old;
    }

    @Override
    public void release() {
        stopGetWeightTask();
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
