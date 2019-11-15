package co.acaia.communications.scale;

import static co.acaia.communications.protocol.ver20.DataOutHelper.setting_chg;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import co.acaia.communications.CommLogger;
import co.acaia.communications.protocol.ver20.DataOutHelper;
import co.acaia.communications.protocol.ver20.DataPacketParser;
import co.acaia.communications.protocol.ver20.ScaleProtocol;
import co.acaia.communications.protocol.ver20.SettingEntity;
import co.acaia.communications.protocol.ver20.SettingFactory;
import co.acaia.communications.scaleService.ScaleCommunicationService;
import co.acaia.communications.scaleService.gatt.Log;
import co.acaia.communications.scaleevent.ProtocolModeEvent;
import de.greenrobot.event.EventBus;


/**
 * Created by hanjord on 15/3/20.
 */
public class AcaiaScale2 extends  AcaiaScale  {
    public static final String TAG="AcaiaScale2";
    private Context context=null;
    private Handler handler;
    private Timer heartBeatTimer;
    private ScaleProtocol.app_prsdata mo_prsdata;
    private  boolean isCinco;
    private boolean get_status;

    // warning: need a factory method to do this

    public AcaiaScale2(Context ctx,ScaleCommunicationService mScaleCommunicationService_,Handler h, boolean isCinco){
        init_scale_command();
        context=ctx;
        mScaleCommunicationService=mScaleCommunicationService_;
        handler=h;
        mo_prsdata=new ScaleProtocol.app_prsdata();
        DataPacketParser.init_app_prs_data(mo_prsdata);
        //startHeartBeat();
        EventBus.getDefault().post(new ProtocolModeEvent());
        this.isCinco=isCinco;
        get_status=true;
    }


    @Override
    public int getProtocolVersion(){
        return AcaiaScale.protocol_version_20;
    }

    private void init_scale_command(){
        scaleCommand=new AcaiaScaleCommand() {
            @Override
            public void parseDataPacket(byte[] data) {
                DataPacketParser.ParseData(mo_prsdata,data,context,false,isCinco,null);
            }

            @Override
            public void parseDataPacketCinco(byte[] data, AcaiaScale acaiaScale) {
                DataPacketParser.ParseData(mo_prsdata,data,context,false,isCinco,acaiaScale);
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
                return false;
            }

            @Override
            public boolean getAutoOffTime() {
                return false;
            }

            @Override
            public boolean getBattery() {

                return false;
            }

            @Override
            public boolean getStatus() {
                return getScaleStatus();
            }

            @Override
            public boolean getBeep() {
                CommLogger.logv(TAG,"send get Beep");
                getScaleStatus();
                return false;
            }

            @Override
            public int getConnectionState() {
                return 0;
            }

            @Override
            public boolean getKeyDisabledElapsedTime() {
                return false;
            }

            @Override
            public boolean getTimer() {
                return false;
            }

            @Override
            public boolean getWeight() {
                return false;
            }

            @Override
            public boolean pauseTimer() {
                return mScaleCommunicationService.sendCmdwithResponse(DataOutHelper.timer_action((short) ScaleProtocol.ESCALE_TIMER_ACTION.e_timer_pause.ordinal()));
            }

            @Override
            public boolean setAutoOffTime(int time) {
                SettingEntity settingEntity=null;
                /*switch (time){
                    case ScaleCommunicationService.AUTOOFF_TIME_5_MIN:
                        settingEntity=SettingFactory.getSetting(SettingFactory.set_sleep.item, SettingFactory.set_sleep.length.len_5_min);
                        break;
                    case ScaleCommunicationService.AUTOOFF_TIME_10_MIN:
                        settingEntity= SettingFactory.getSetting(SettingFactory.set_sleep.item, SettingFactory.set_sleep.length.len_10_min);
                        break;
                    case ScaleCommunicationService.AUTOOFF_TIME_30_MIN:
                        settingEntity=SettingFactory.getSetting(SettingFactory.set_sleep.item, SettingFactory.set_sleep.length.len_30_min);
                        break;
                    case
                        break;

                }*/
                settingEntity=SettingFactory.getSetting(SettingFactory.set_sleep.item,(short)time);
                if(settingEntity!=null){
                    CommLogger.logv(TAG,"send sleep="+String.valueOf(settingEntity.getValue()));
                    return mScaleCommunicationService.sendCmdwithResponse(setting_chg(settingEntity.getItem(), settingEntity.getValue()));

                }else {
                    return false;
                }
            }

            @Override
            public boolean setBeep(boolean on) {
                SettingEntity settingEntity;
                if(on){
                    settingEntity=   SettingFactory.getSetting(SettingFactory.set_beep.item, SettingFactory.set_beep.beep_on_off.on);
                }else{
                    settingEntity=   SettingFactory.getSetting(SettingFactory.set_beep.item, SettingFactory.set_beep.beep_on_off.off);
                }
                return mScaleCommunicationService.sendCmdwithResponse(setting_chg(settingEntity.getItem(), settingEntity.getValue()));


            }

            @Override
            public boolean setKeyDisabledWithTime(int second) {

                SettingEntity settingEntity=null;
                if(second<=255) {
                    settingEntity = SettingFactory.getSetting(SettingFactory.set_key_disable.item, (short) second);
                }else{
                    settingEntity = SettingFactory.getSetting(SettingFactory.set_key_disable.item, (short) 255);
                }
                if(settingEntity!=null){
                    CommLogger.logv(TAG,"send disable="+String.valueOf(settingEntity.getValue()));
                    return mScaleCommunicationService.sendCmdwithResponse(setting_chg(settingEntity.getItem(), settingEntity.getValue()));

                }else {
                    return false;
                }



            }

            @Override
            public boolean setLight(boolean on) {
                return false;
            }

            @Override
            public boolean setTare() {
                return mScaleCommunicationService.sendCmdwithResponse( DataOutHelper.app_command((short) ScaleProtocol.ECMD.e_cmd_tare_s.ordinal()));            }

            @Override
            public boolean setUnit(short unit) {
                SettingEntity settingEntity;
                if(unit==0){
                    // gram
                    settingEntity= SettingFactory.getSetting(SettingFactory.set_unit.item, SettingFactory.set_unit.unit.g);
                }else{
                    // oz
                    settingEntity= SettingFactory.getSetting(SettingFactory.set_unit.item, SettingFactory.set_unit.unit.oz);
                }

                return mScaleCommunicationService.sendCmdwithResponse(setting_chg(settingEntity.getItem(), settingEntity.getValue()));
            }

            @Override
            public boolean startScan() {
                return mScaleCommunicationService.startScan();
            }

            @Override
            public boolean startTimer() {
                return mScaleCommunicationService.sendCmdwithResponse(DataOutHelper.timer_action((short)ScaleProtocol.ESCALE_TIMER_ACTION.e_timer_start.ordinal()));

            }

            @Override
            public boolean stopTimer() {
                return mScaleCommunicationService.sendCmdwithResponse(DataOutHelper.timer_action((short)ScaleProtocol.ESCALE_TIMER_ACTION.e_timer_stop.ordinal()));
            }

            @Override
            public boolean getCapacity() {
                return false;
            }

            @Override
            public boolean setCapacity(int capacity) {
                return false;
            }


        };
    }
    @Override
    public void release(){
        stopHeartHeat();
    }

    @Override
    public void startHeartBeat(){
        if(heartBeatTimer!=null){
            heartBeatTimer.cancel();;
            heartBeatTimer=null;
        }
        //Log.v("heartbeat","start heartbeat timer");
        heartBeatTimer=new Timer();
        heartBeatTimer.schedule(new HeartBeatTask(this),3000,1000);
    }

    private void stopHeartHeat(){
        heartBeatTimer.cancel();
        heartBeatTimer=null;
    }

    private Boolean getScaleStatus(){
        //return mScaleCommunicationService.sendCmdwithResponse(DataOutHelper.app_command((short)ScaleProtocol.ECMD.e_cmd_status_s.ordinal()));
        return mScaleCommunicationService.sendStatus();
    }
    /*
        Old protocol needs a get data task
     */
    private class HeartBeatTask extends TimerTask {

        public static final String TAG="HeartBeat";
        AcaiaScale2 scale;

        public HeartBeatTask(AcaiaScale2 acaiaScale2){
            scale=acaiaScale2;
        }

        @Override
        public void run() {
           CommLogger.logv("test heartbeat","running heartbeat ");
            if(scale.mScaleCommunicationService!=null) {
                //scale.getScaleCommand().getWeight();
                if(scale.mScaleCommunicationService.isConnected() && scale.mScaleCommunicationService.scaleGetStatue){

                    // warning hanjord
                    CommLogger.logv(TAG,"send heartbeat!");
                    ///mScaleCommunicationService.sendHeartBeat();
                    try {
                        Thread.sleep(1000);
                        ////Log.v("test heartbeat","heartbeat ");
                        //mScaleCommunicationService.sendCmdFromQueue(DataOutHelper.heartBeat());
                        // hanjord debug
                        if(get_status) {
                            //Log.v(TAG,"get staus!");
                            getScaleStatus();
                        }
                    }catch(Exception e){
                        e.printStackTrace();;
                    }


                    CommLogger.logv(TAG, "Send heart beat!");
                }
            }else{
                CommLogger.logv(TAG,"Error: scale service null");
            }
        }
    }
}