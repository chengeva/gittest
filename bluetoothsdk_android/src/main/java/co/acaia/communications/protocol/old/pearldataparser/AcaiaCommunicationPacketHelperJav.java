package co.acaia.communications.protocol.old.pearldataparser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import co.acaia.communications.CommLogger;
import co.acaia.communications.events.FirmwareEvent;
import co.acaia.communications.protocol.old.AcaiaScaleAttributes;
import co.acaia.communications.scaleService.ScaleCommunicationService;
import co.acaia.communications.scaleevent.ScaleSettingUpdateEvent;
import co.acaia.communications.scaleevent.ScaleSettingUpdateEventType;
import co.acaia.communications.scaleevent.UpdateTimerEvent;
import javolution.io.Struct;

/**
 * Created by kenatsushikan on 9/14/15.
 */
public class AcaiaCommunicationPacketHelperJav {
    public static final String TAG = "AcaiaCommunicationPacketHelperJav";

    public static int getSubdataValue(byte[] data) {
        return 0;
    }

    public static byte[] getaskautooffcmd(PearlDataHelper pearlDataHelper) {
        _ls_buf ls_buf = new _ls_buf();
        cmd_data.cmd_setting lo_setting = new cmd_data.cmd_setting();
        lo_setting.setData(allcmd_data.ESETTING_ID.e_setting_sleep.ordinal(), 1);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_read_setting.ordinal(), lo_setting.getByteArray(), 2);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    public static byte[] get_pack_info_req(PearlDataHelper pearlDataHelper){
        _ls_buf ls_buf = new _ls_buf();
        int ln_len = pearlDataHelper._cmd_command.pack_info_req(ls_buf.ls_buf);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }




    public static float getweightUnit(byte[] buf) {
        return 0;
    }

    public static byte[] getScaleTimer(PearlDataHelper pearlDataHelper) {
        _ls_buf ls_buf = new _ls_buf();
        cmd_data.cmd_setting lo_setting = new cmd_data.cmd_setting();
        lo_setting.setData(allcmd_data.ESETTING_ID.e_setting_keydisable.ordinal(), 1);
        int ln_len = pearlDataHelper._cmd_command.pack_get_timer(ls_buf.ls_buf, 20);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    public static byte[] getaskdisablekeysecondscmd(PearlDataHelper pearlDataHelper) {
        _ls_buf ls_buf = new _ls_buf();
        cmd_data.cmd_setting lo_setting = new cmd_data.cmd_setting();
        lo_setting.setData(allcmd_data.ESETTING_ID.e_setting_keydisable.ordinal(), 1);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_read_setting.ordinal(), lo_setting.getByteArray(), 2);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }


    public static byte[] getdisableKeyCmd(PearlDataHelper pearlDataHelper, int time) {
        _ls_buf ls_buf = new _ls_buf();
        cmd_data.cmd_setting lo_setting = new cmd_data.cmd_setting();
        lo_setting.setData(allcmd_data.ESETTING_ID.e_setting_keydisable.ordinal(), time);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_set_setting.ordinal(), lo_setting.getByteArray(), 2);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }


    // korean version 20140827
    public static byte[] getSetCapacityCmd(PearlDataHelper pearlDataHelper,int capacity) {
        _ls_buf ls_buf = new _ls_buf();
        cmd_data.cmd_setting lo_setting = new cmd_data.cmd_setting();
        lo_setting.setData(allcmd_data.ESETTING_ID.e_setting_capability.ordinal(), capacity);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_set_setting.ordinal(), lo_setting.getByteArray(), 2);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    public static byte[] getAskCapacityCmd(PearlDataHelper pearlDataHelper) {
        _ls_buf ls_buf = new _ls_buf();
        cmd_data.cmd_setting lo_setting = new cmd_data.cmd_setting();
        lo_setting.setData(allcmd_data.ESETTING_ID.e_setting_capability.ordinal(), 0);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_read_setting.ordinal(), lo_setting.getByteArray(), 2);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);

    }

    public static byte[] getaskdisableKeycmd(PearlDataHelper pearlDataHelper) {
        _ls_buf ls_buf = new _ls_buf();
        cmd_data.cmd_setting lo_setting = new cmd_data.cmd_setting();
        lo_setting.setData(allcmd_data.ESETTING_ID.e_setting_keydisable.ordinal(), 1);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_read_setting.ordinal(), lo_setting.getByteArray(), 2);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    public static byte[] getBatteryCommand(PearlDataHelper pearlDataHelper) {
        _ls_buf ls_buf = new _ls_buf();
        int ln_len = pearlDataHelper._cmd_command.pack_battery_req(ls_buf.ls_buf);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);

    }

    public static byte[] startScaleTimer(PearlDataHelper pearlDataHelper) {

        _ls_buf ls_buf = new _ls_buf();
        int ln_len = pearlDataHelper._cmd_command.pack_start_timer(ls_buf.ls_buf);

        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    public static byte[] pauseScaleTimer(PearlDataHelper pearlDataHelper) {
        _ls_buf ls_buf = new _ls_buf();
        int ln_len = pearlDataHelper._cmd_command.pack_pause_timer(ls_buf.ls_buf);

        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    public static byte[] stopScaleTimer(PearlDataHelper pearlDataHelper) {
        _ls_buf ls_buf = new _ls_buf();
        int ln_len = pearlDataHelper._cmd_command.pack_stop_timer(ls_buf.ls_buf);

        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }


    public static byte[] getScaleBeepSound(PearlDataHelper pearlDataHelper) {
        _ls_buf ls_buf = new _ls_buf();
        cmd_data.cmd_setting lo_setting = new cmd_data.cmd_setting();
        lo_setting.setData(allcmd_data.ESETTING_ID.e_setting_sound.ordinal(), 2);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_read_setting.ordinal(), lo_setting.getByteArray(), 2);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }


    public static byte[] getswitchunitGramcmd(PearlDataHelper pearlDataHelper) {
        int ln_id = cmd_data.EUNIT.e_unit_g.ordinal();
        _ls_buf ls_buf = new _ls_buf();
        _ls_temp_1 ls_temp_1 = new _ls_temp_1();
        ls_temp_1.ls_temp_1[0].set((short) ln_id);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_unit.ordinal(), ls_temp_1.ls_temp_1, 1);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    public static byte[] getswitchUnitOzCmd(PearlDataHelper pearlDataHelper) {
        int ln_id = cmd_data.EUNIT.e_unit_oz.ordinal();
        _ls_buf ls_buf = new _ls_buf();
        _ls_temp_1 ls_temp_1 = new _ls_temp_1();
        ls_temp_1.ls_temp_1[0].set((short) ln_id);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_unit.ordinal(), ls_temp_1.ls_temp_1, 1);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    public static byte[] getTareCommand(PearlDataHelper pearlDataHelper) {

        int ln_id = allcmd_data.EKEY_ID.e_key_tare.ordinal();
        _ls_buf ls_buf = new _ls_buf();
        _ls_temp_1 ls_temp_1 = new _ls_temp_1();
        ls_temp_1.ls_temp_1[0].set((short) ln_id);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_key.ordinal(), ls_temp_1.ls_temp_1, 1);

        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    /**
     *
     */
    public static byte[] getautooffCmd(PearlDataHelper pearlDataHelper,int time) {
        _ls_buf ls_buf = new _ls_buf();
        cmd_data.cmd_setting lo_setting = new cmd_data.cmd_setting();
        lo_setting.setData(allcmd_data.ESETTING_ID.e_setting_sleep.ordinal(), time);
        int ln_len = pearlDataHelper._cmd_command.pack_custom(ls_buf.ls_buf, (byte) subcmd_data.ESUBCMD.e_subcmd_set_setting.ordinal(), lo_setting.getByteArray(), 2);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    /**
     * @param on Turn beep on/off
     * @return The encoded command for sending beep configuration command to the Acaia scale.
     */
    public static byte[] getBeepONOFFCmd(PearlDataHelper pearlDataHelper, boolean on) {
        int if_on = 0;
        if (on) {
            if_on = 1;
        } else {
            if_on = 0;
        }
        _ls_buf ls_buf = new _ls_buf();
        int ln_len = pearlDataHelper._cmd_command.pack_audio_req(ls_buf.ls_buf, if_on);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);
    }

    /**
     * @return The encoded command for getting updated weight from the Acaia scale.
     */
    public static byte[] getSendWeightCommand(PearlDataHelper pearlDataHelper) {
        _ls_buf ls_buf = new _ls_buf();
        int ln_len = pearlDataHelper._cmd_command.pack_weight_req(ls_buf.ls_buf, 1, 100, allcmd_data.EWEIGHT_TYPE.e_weight_net);
        return ByteDataHelper.getByteArrayFromU1(ls_buf.ls_buf, 0, ln_len);

    }

    /**
     * @param buf The data of the packet received from the Acaia scale
     * @return The data type of the packet, e.g. Weight data
     */

    public static int getScalePacketDataType(byte[] buf, PearlDataHelper pearlDataHelper) {
        _ls_decode ls_decode = new _ls_decode();

        int ls_buf_index = 0;
        sr_decrypt_holder sr_decrypt_holder_ = new sr_decrypt_holder();

        int ln_datalen = buf.length;

        while (ln_datalen > 0) {
            en_code.E_ENCRY_RESULT result = pearlDataHelper._en_command.sr_decrypt(buf, ln_datalen, ls_decode.ls_decode, sr_decrypt_holder_, ls_buf_index);
            //Log.v("Packet Helper", "current result " + String.valueOf(result.ordinal()));
            switch (result) {
                case e_encry_tobe_continue:
                    return 0;
                case e_encry_success:
                    return sr_decrypt_holder_.n_cmd;
                default:
                    break;
            }
            ls_buf_index += sr_decrypt_holder_.n_index;
            ln_datalen -= sr_decrypt_holder_.n_index;
        }
        return 0;
    }

    @SuppressLint("LongLogTag")
    public static int parsePacketJav(byte[] buf, PearlDataHelper pearlDataHelper,Intent intent,Context context) {
        _ls_decode ls_decode = new _ls_decode();
        int ls_buf_index = 0;
        sr_decrypt_holder sr_decrypt_holder_ = new sr_decrypt_holder();
        int ln_datalen = buf.length;

        while (ln_datalen > 0) {
            en_code.E_ENCRY_RESULT result = pearlDataHelper._en_command.sr_decrypt(buf, ln_datalen, ls_decode.ls_decode, sr_decrypt_holder_, ls_buf_index);
            //Log.v("Packet Helper", "current result " + String.valueOf(result.ordinal()));
            switch (result) {
                case e_encry_tobe_continue:
                    return 0;
                case e_encry_success:
                    int ln_cmd = sr_decrypt_holder_.n_cmd;
                    // weight data...
                    //Log.v(TAG,"ln_cmd="+String.valueOf(ln_cmd));
                    if (ln_cmd == allcmd_data.ECMD.e_cmd_weight_r.ordinal()) {
                        cmd_data.wt_rep wt_rep_ = new cmd_data.wt_rep(ByteDataHelper.getByteArrayFromU1(ls_decode.ls_decode, 0, cmd_data.WEIGHT_R_LEN));
                        Log.v(TAG, "weight val" + String.valueOf(wt_rep_.getWeight()));
                        float weightVal=wt_rep_.getWeight();
                        String weightString = "";
                        // wt_rep_.debug();;
                        if ((int) wt_rep_.getUnit() == 2) {
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
                        if (isNearZero(pearlDataHelper.previousWeight) == false
                                && isNearZero(weightVal) == true) {
                            illegalDataDetected = true;
                        }

/*
             * // Ignore the first 0. Could be noise. // orange modify
 * 20140725 // if(previousWeight!=0 && inputWeight==0) if([self
 * isNearZero:previousWeight]==NO && [self
 * isNearZero:inputWeight]==YES) { illegalDataDetected=YES; }
 */
                        if (pearlDataHelper.previousWeight != 0 && weightVal == 0) {
                            illegalDataDetected = true;
                        }

                        pearlDataHelper.previousWeight = weightVal;
                        if (weightVal < -5) {
                            if (pearlDataHelper.minus5mode == false) {
                                pearlDataHelper. minus5mode = true;
                                illegalDataDetected = true;
                            }
                        } else {
                            pearlDataHelper.minus5mode = false;
                        }

                        if (pearlDataHelper.previousWeight != 0 && weightVal == 0) {
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

                    } else if (ln_cmd == allcmd_data.ECMD.e_cmd_battery_r.ordinal()) {
                        cmd_data.bat_rep bat_rep = new cmd_data.bat_rep(ByteDataHelper.getByteArrayFromU1(ls_decode.ls_decode, 0, cmd_data.BATTERY_R_LEN));
                        //bat_rep.debug();
                        //  Log.v(TAG,"jav battery=")

                        intent.putExtra(ScaleCommunicationService.EXTRA_DATA,
                                bat_rep.getBatteryLevel());
                        intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE,
                                ScaleCommunicationService.DATA_TYPE_BATTERY);
                    }else if(ln_cmd== AcaiaScaleAttributes.ECMD.e_cmd_info_sent){
                        // TODO: fill in the code
                        cmd_data.scale_info scale_info=new cmd_data.scale_info(ByteDataHelper.getByteArrayFromU1(ls_decode.ls_decode, 0, cmd_data.INFO_LEN));
                        // Log.v(TAG, "version" + String.valueOf(ver));
                        EventBus.getDefault().post(new FirmwareEvent(scale_info.get_n_firm_mversion(),scale_info.get_n_firm_sversion(),scale_info.get_info_version()));
                    } else if (ln_cmd == allcmd_data.ECMD.e_cmd_custom.ordinal()) {
                        // process subcommand...
                        subcmd_data.cmd_setting cmd_setting = new subcmd_data.cmd_setting(ByteDataHelper.getByteArrayFromU1(ls_decode.ls_decode, 1, subcmd_data.cmd_setting.getLength()));
                        int subcmdid = ls_decode.ls_decode[0].get();
                        float sub_type_value = cmd_setting.get_value();

                        if (subcmdid == subcmd_data.ESUBCMD.e_subcmd_setting_response.ordinal()) {
                            int n_set_id = cmd_setting.get_id();
                            if (n_set_id == subcmd_data.ESETTING_ID.e_setting_capability.ordinal()) {
                                // Log.v(TAG,"e_setting_capability="+String.valueOf(cmd_setting.get_value()));
                                int capacity = (int) sub_type_value;
                                if (capacity == 1)
                                    capacity = 2000;
                                else {
                                    capacity = 1000;
                                }
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_CAPACITY);
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA, capacity);
                                EventBus.getDefault().post(new ScaleSettingUpdateEvent(ScaleSettingUpdateEventType.event_type.EVENT_CAPACITY.ordinal(), capacity));
                            }
                            if (n_set_id == subcmd_data.ESETTING_ID.e_setting_keydisable.ordinal()) {
                                // Log.v(TAG,"e_setting_keydisable="+String.valueOf(cmd_setting.get_value()));
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_KEY_DISABLED_ELAPSED_TIME);
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA, sub_type_value);
                            }
                            if (n_set_id == subcmd_data.ESETTING_ID.e_setting_resol.ordinal()) {
                                // Log.v(TAG,"e_setting_resol="+String.valueOf(cmd_setting.get_value()));
                            }
                            if (n_set_id == subcmd_data.ESETTING_ID.e_setting_sleep.ordinal()) {
                                // Log.v(TAG,"e_setting_sleep="+String.valueOf(cmd_setting.get_value()));
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_AUTO_OFF_TIME);
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA, sub_type_value);
                            }
                            if (n_set_id == subcmd_data.ESETTING_ID.e_setting_sound.ordinal()) {
                                // problem ?
                                //  Log.v(TAG,"e_setting_sound="+String.valueOf(cmd_setting.get_value()));
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_BEEP);
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA, sub_type_value);
                            }
                            if(n_set_id == subcmd_data.ESUBCMD.e_subcmd_ptimer_response.ordinal()){
// timer paused
                                int time = (int) sub_type_value;
                                CommLogger.logv(TAG, "timer 10 =" + String.valueOf(time));
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_TIMER);
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA, time);
                                CommLogger.logv(TAG, "timer 10 sub data=" + String.valueOf(sub_type_value));
                                EventBus.getDefault().post(new UpdateTimerEvent(true, time));
                            }

                            if(n_set_id == subcmd_data.ESUBCMD.e_subcmd_timer_response.ordinal()){
                                // timer started
                                int time = (int) sub_type_value;
                                CommLogger.logv(TAG, "timer 9 =" + String.valueOf(time));
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_TIMER);
                                intent.putExtra(ScaleCommunicationService.EXTRA_DATA, time);
                                CommLogger.logv(TAG, "timer 9 sub data=" + String.valueOf(sub_type_value));
                                EventBus.getDefault().post(new UpdateTimerEvent(false, time));
                            }
                        }

                        // Timer
                        int e_cmd_custom_id = ls_decode.ls_decode[0].get();
                        if (e_cmd_custom_id == subcmd_data.ESUBCMD.e_subcmd_ptimer_response.ordinal()) {
                            float second = ByteDataHelper.fourBytesToUInt(ls_decode.ls_decode[1], ls_decode.ls_decode[2], ls_decode.ls_decode[3], ls_decode.ls_decode[4]);
                            int time = (int) second;
                            CommLogger.logv(TAG, "timer 10 =" + String.valueOf(time));
                            intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_TIMER);
                            intent.putExtra(ScaleCommunicationService.EXTRA_DATA, time);
                            CommLogger.logv(TAG, "timer 10 sub data=" + String.valueOf(sub_type_value));
                            EventBus.getDefault().post(new UpdateTimerEvent(true, time));
                        }
                        if (e_cmd_custom_id == subcmd_data.ESUBCMD.e_subcmd_timer_response.ordinal()) {
                            float second = ByteDataHelper.fourBytesToUInt(ls_decode.ls_decode[1], ls_decode.ls_decode[2], ls_decode.ls_decode[3], ls_decode.ls_decode[4]);
                            // Log.v(TAG,"second="+String.valueOf(second));
                            int time = (int) second;
                            CommLogger.logv(TAG, "timer 9 =" + String.valueOf(time));
                            intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE, ScaleCommunicationService.DATA_TYPE_TIMER);
                            intent.putExtra(ScaleCommunicationService.EXTRA_DATA, time);
                            CommLogger.logv(TAG, "timer 9 sub data=" + String.valueOf(sub_type_value));
                            EventBus.getDefault().post(new UpdateTimerEvent(false, time));
                        }

                    }

                default:
                    break;
            }
            ls_buf_index += sr_decrypt_holder_.n_index;
            ln_datalen -= sr_decrypt_holder_.n_index;
        }
        context.sendBroadcast(intent);
        return 0;
    }

    /**
     *
     */
    public static int getScalePacketSubDataType(byte[] buf) {
        return 0;
    }



    /**
     * @param buff The data of the packet received from the Acaia scale
     * @return The decoded data of the packet. e.g. 11.0
     */
    public static float parseScalePacket(byte[] buff) {
        return 0;
    }

    public static class _ls_temp_1 extends Struct {
        public Unsigned8[] ls_temp_1 = new Unsigned8[]{new Unsigned8(),};

    }

    public static class _ls_buf extends Struct {
        public Unsigned8[] ls_buf = new Unsigned8[]{
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
        };

    }

    public static class _ls_decode extends Struct {
        public Unsigned8[] ls_decode = new Unsigned8[]{
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
        };

    }


    private static Boolean isNearZero(float floatValue) {
        if (floatValue <= 1.0 && floatValue >= -1.0)
            return true;
        else
            return false;
    }
}
