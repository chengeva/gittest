package co.acaia.communications.scale;

import android.content.Context;
import android.os.Handler;

import org.greenrobot.eventbus.EventBus;

import co.acaia.communications.CommLogger;
import co.acaia.communications.protocol.ver20.DataOutHelper;
import co.acaia.communications.protocol.ver20.DataPacketParser;
import co.acaia.communications.protocol.ver20.ScaleProtocol;
import co.acaia.communications.protocol.ver20.SettingEntity;
import co.acaia.communications.protocol.ver20.SettingFactory;
import co.acaia.communications.scaleService.ScaleCommunicationService;
import co.acaia.communications.scaleevent.ProtocolModeEvent;

import static co.acaia.communications.protocol.ver20.DataOutHelper.setting_chg;

public class Kettle extends AcaiaScale {
    public static final String TAG="Kettle";
    private Handler handler;
    private Context context=null;

    private ScaleProtocol.app_prsdata mo_prsdata;

    // warning: need a factory method to do this

    public Kettle(Context ctx, ScaleCommunicationService mScaleCommunicationService_, Handler h){
        init_scale_command();
        context=ctx;
        mScaleCommunicationService=mScaleCommunicationService_;
        handler=h;
        mo_prsdata=new ScaleProtocol.app_prsdata();
        DataPacketParser.init_app_prs_data(mo_prsdata);
        EventBus.getDefault().post(new ProtocolModeEvent());

    }

    @Override
    public int getProtocolVersion(){
        return AcaiaScale.protocol_version_20;
    }

    private void init_scale_command(){
        scaleCommand=new AcaiaScaleCommand() {
            @Override
            public void parseDataPacket(byte[] data) {
                DataPacketParser.ParseData(mo_prsdata,data,context,true,false);
            }
            @Override
            public boolean getFirmwareInfo() {
//                getScaleStatus();
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
                return true;
            }

            @Override
            public boolean getBeep() {
                CommLogger.logv(TAG,"send get Beep");
                return true;
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
                settingEntity= SettingFactory.getSetting(SettingFactory.set_sleep.item,(short)time);
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

            @Override
            public boolean setKettleTargetTemp(int temp) {
                return mScaleCommunicationService.sendCmdwithResponse(setting_chg((short)1, (short) temp));
            }

            @Override
            public boolean setKettleOnOff(boolean on) {
                return mScaleCommunicationService.sendCmdwithResponse(setting_chg((short)0, (short)((on)?1:0)));
            }
        };
    }
}
