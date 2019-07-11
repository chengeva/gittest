package co.acaia.communications.protocol.ver20;
import android.util.Log;

import co.acaia.communications.CommLogger;
import j2me.nio.ByteBuffer;
import j2me.nio.ByteOrder;
import javolution.io.Struct;

public class ScaleProtocol {
    public static final int APPEVENT_WEIGHT_LEN = 6;
    public static final int APPEVENT_TIMER_LEN = 3;
    public static final int APPEVENT_ACK_LEN = 2;
    public static final int STATUS_LEN = 16;

    public static enum EDATA_RESULT {
        e_result_success,
        e_result_tobecontinues,
        e_result_error_char,
        e_result_error_unsupport,
        e_result_size
    }


    public static enum ECMD {
        e_cmd_system_sa,
        e_cmd_str_sa,
        e_cmd_battery_s,
        e_cmd_weight_s,
        e_cmd_tare_s,
        e_cmd_custom_sa,
        e_cmd_status_s,
        e_cmd_info_a,
        e_cmd_status_a,
        e_cmd_isp_s,
        e_cmd_setting_chg_s,
        e_cmd_identify_s,
        e_cmd_event_sa,
        e_cmd_timer_s,
        e_cmd_file_s,

        e_cmd_setpwd_s,
        e_cmd_pcs_weight_s,
        e_cmd_pretare_s,

        new_cmd_sync_brewguide_s,
        new_cmd_sync_hello_s,
        new_cmd_sync_brewguide_cmd,
        new_cmd_sync_brewguide_data_str,
        new_cmd_sync_brewguide_data_info,
        new_cmd_sync_brewguide_data_type,

        e_cmd_size
    }



    public static enum EEVENT {
        e_scalevent_weight, // parameter: n * 1/10 second
        e_scalevent_battery, // parameter: n * 10 seconds
        e_scalevent_timer, // parameter: n * 1/10 second
        e_scalevent_key, // tare, unit, mode
        e_scalevent_setting, e_appevent_weight, e_appevent_battery, e_appevent_timer, e_appevent_key, e_appevent_setting, e_appevent_cd, e_appevent_ack, // ack
        // id,
        // result
        e_event_size
    }

    ;

    public static enum ESCALE_KEY_EVENT {
        e_keyevent_tare, // ¶©≠´
        e_keyevent_isp_mode, //
        e_keyevent_weight_mode, // §¡¥´®Ï≠´∂qº“¶°
        e_keyevent_timeweight_mode, // §¡¥´®ÏÆ…∂°≠´∂qº“¶°
        e_keyevent_time_mode, // §¡¥´®ÏÆ…∂°º“¶°
        e_keyevent_kg, // 5 ¬‡¥´≥Ê¶Ï®Ï g
        e_keyevent_lb, e_keyevent_oz, // 7 ¬‡¥´≥Ê¶Ï®Ï oz
        e_keyevent_timestart, // 8 ±“∞ ≠pÆ…
        e_keyevent_timestop, // 9 ∞±§Ó®√≤M∞£≠pÆ…
        e_keyevent_timepause, // 10 ∞±§Ó≠pÆ…
        e_keyevent_cdstart, e_keyevent_cdstop, e_keyevent_cdpause, e_keyevent_size
    }

    ;

    public static enum ESCALE_TIMER_STATE {
        e_timer_state_stop, e_timer_state_start, e_timer_state_pause, e_timer_state_weightstart, e_timer_state_size
    }

    ;

    public static enum ESCALE_TIMER_ACTION {
        e_timer_start, e_timer_stop, e_timer_pause, e_timer_weightstart, e_timer_act_size
    }

    ;

    public static enum EAPP_PROCESS {
        e_prs_checkheader,
        e_prs_cmdid,
        e_prs_cmddata,
        e_prs_checksum,
        e_prs_size
    }

    ;

    // weight event to app
    public static enum EWEIGHT_STBLE {
        e_wt_stable, e_wt_unstable
    }

    ;

    public static enum EWEIGHT_POSITIVE {
        e_wt_positive, e_wt_negative
    }

    ;

    public static enum EWEIGHT_TYPE {
        e_wttype_net, e_wttype_pw, e_wttype_tare
    }

    ;

    public static enum ESCALE_SYSTEM_MSG {
        e_systemmsg_alive, e_systemmsg_ack, e_systemmsg_btmsg, e_systemmsg_size
    }

    ;

    public static enum ECSRMSG {
        e_csrmsg_bt_advstart, e_csrmsg_bt_advstop, e_csrmsg_bt_error, e_csrmsg_bt_ok, e_csrmsg_bt_bleup, e_csrmsg_bt_bledown, e_csrmsg_size
    }

    ;

    // string type
    public static enum ESTRING_TYPE {
        e_strtype_error, e_strtype_warnning, e_strtype_ok, e_strtype_debug, e_strtype_size
    }

    ;

    public static enum ESETTING_ITEM
    {
        e_setting_unit,
        e_setting_sleep,
        e_setting_keydisable,
        e_setting_resol,
        e_setting_capability,
        e_setting_sound,

        // Brewguide Pearl S
        e_setting_weighingmode,
        e_setting_dual_displaymode,
        e_setting_hello_msg_mode,
        e_setting_protafilter_mode,
        e_setting_espresso_mode,
        e_setting_pourover_mode,
        e_setting_flowrate_mode,
        e_setting_item_size,
        new_setting_item_size

    };


    // result item 5 bits type 3 bits value
    // result
    public static enum ERESULT_TYPE {
        e_resulttype_cmd, e_resulttype_timer, e_resulttype_unit, e_resulttype_sleep, e_resulttype_keydisable, e_resulttype_resol, e_resulttype_capability, e_resulttype_size
    }

    ;

    enum ERESULT_CMD
    {
        e_result_weightcmd_success ,
        e_result_batterycmd_success,
        e_result_setpasswd_success,
        e_result_setpasswd_fail,
        e_result_tare_done,				// execute, maybe success maybe fail.
        e_result_isp_success,
        e_result_isp_fail,
        e_result_alive_success
    };

    ;

    public static enum ERESULT_TIMER {
        e_result_timercmd_start, e_result_timercmd_pause, e_result_timercmd_stop, e_result_timercmd_weightstart,
    }

    ;

    public static class scale_info extends Struct {

        public final Signed8 n_info_length=new Signed8();
        public final Signed8 n_info_version=new Signed8();
        public final Signed8 n_ISP_version=new Signed8();
        public final Signed8 n_firm_main_ver=new Signed8();
        public final Signed8 n_firm_sub_ver=new Signed8();
        public final Signed8 n_firm_add_ver=new Signed8();
        public final Signed8 b_passwd_set=new Signed8();


        public int getVersion(){
            return 100*n_firm_main_ver.get()+n_firm_sub_ver.get();

        }

        public int getMainVersion(){
            return n_firm_main_ver.get();
        }
        public int getSubVersion(){
            return 100*n_firm_sub_ver.get()+n_firm_add_ver.get();
        }
        public int getInfoVersion(){
            return n_info_version.get();
        }
        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;

        }
        public scale_info(byte [] b){
            byte[] bb = ByteBuffer.wrap(b).order(j2me.nio.ByteOrder.LITTLE_ENDIAN).array();
            this.setByteBuffer(ByteBuffer.wrap(b).order(j2me.nio.ByteOrder.LITTLE_ENDIAN), 0);
           // printDebug();;
        }

        public void printDebug(){
            CommLogger.logv("scale_info", "n_info_length=" + String.valueOf(n_info_length.get()));
            CommLogger.logv("scale_info", "n_info_version=" + String.valueOf(n_info_version.get()));
            CommLogger.logv("scale_info", "n_ISP_version=" + String.valueOf(n_ISP_version.get()));
            CommLogger.logv("scale_info", "n_firm_main_ver=" + String.valueOf(n_firm_main_ver.get()));
            CommLogger.logv("scale_info", "n_firm_sub_ver=" + String.valueOf(n_firm_sub_ver.get()));
            CommLogger.logv("scale_info", "n_firm_add_ver=" + String.valueOf(n_firm_add_ver.get()));
        }
        public static int getSize(){
            return 7;
        }
    }

    public static class cmd_setting extends Struct {
        private Unsigned8 n_ack = new Unsigned8();
        private Unsigned8 n_set_id = new Unsigned8();
        private Unsigned8 n_set_value = new Unsigned8();
        private Unsigned8[] buffer = new Unsigned8[]{new Unsigned8(), new Unsigned8(), new Unsigned8()};

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public cmd_setting(short ack, short id, short value) {
            n_ack.set(ack);
            n_set_id.set(id);
            n_set_value.set(value);
        }

        public Struct.Unsigned8[] getByteArray() {

            buffer[0].set(n_ack.get());
            buffer[1].set(n_set_id.get());
            buffer[2].set(n_set_value.get());

            return buffer;
        }
    }

    public static class tm_event extends Struct {
        private Unsigned8 n_minutes = new Unsigned8();
        private Unsigned8 n_seconds = new Unsigned8();
        private Unsigned8 n_dseconds = new Unsigned8();

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public static int getSize() {
            return APPEVENT_TIMER_LEN;
        }

        public tm_event(byte[] b) {
            this.setByteBuffer(ByteBuffer.wrap(b).order(j2me.nio.ByteOrder.LITTLE_ENDIAN), 0);
            test();
        }

        public int getMin() {
            return n_minutes.get();
        }

        public int getSec() {
            return n_seconds.get();
        }

        public int getDse() {
            return n_dseconds.get();
        }

        private void test() {
            CommLogger.logv("tm_event", "n_minutes=" + String.valueOf(n_minutes.get()));
            CommLogger.logv("tm_event", "n_seconds=" + String.valueOf(n_seconds.get()));
            CommLogger.logv("tm_event", "n_dseconds=" + String.valueOf(n_dseconds.get()));
        }
    }

    private static int getBit(byte[] data, int pos) {
        int posByte = pos / 8;
        int posBit = pos % 8;
        byte valByte = data[posByte];
        int valInt = valByte >> (8 - (posBit + 1)) & 0x0001;
        return valInt;
    }

    public static class scale_status extends Struct {
        //  unsigned byte day;
        // public final Unsigned8 day   = new Unsigned8();
        // 0
        public final Unsigned8 n_status_length = new Unsigned8();
        public final Unsigned8 n_battery = new Unsigned8(7);
        public final Unsigned8 b_timer_start = new Unsigned8(1);
        public final Unsigned8 n_unit = new Unsigned8(7);
        public final Unsigned8 b_cd_start = new Unsigned8(1);
        public final Unsigned8 n_scale_mode = new Unsigned8(7);
        public final Unsigned8 b_tare = new Unsigned8(1);
        public final Unsigned8 n_setting_sleep = new Unsigned8();
        public final Unsigned8 n_setting_keydisable = new Unsigned8();
        public final Unsigned8 n_setting_sound = new Unsigned8();
        public final Unsigned8 n_setting_resol = new Unsigned8();
        public final Unsigned8 n_setting_capability = new Unsigned8();

        // Pearl S states
        public final Unsigned8 n_setting_weighingmode = new Unsigned8();
        public final Unsigned8 n_setting_dual_displaymode = new Unsigned8();
        public final Unsigned8 n_setting_brewguidemode = new Unsigned8();
        public final Unsigned8 n_setting_protafilter_mode = new Unsigned8();
        public final Unsigned8 n_setting_espresso_mode = new Unsigned8();
        public final Unsigned8 n_setting_pourover_mode = new Unsigned8();
        public final Unsigned8 n_setting_flowrate_mode = new Unsigned8();
            /*
            u1 n_setting_weighingmode;
            u1 n_setting_dual_displaymode;
            u1 n_setting_brewguidemode;
            u1 n_setting_protafilter_mode;
            u1 n_setting_espresso_mode;
            u1 n_setting_pourover_mode;
            u1 n_setting_flowrate_mode;
            */

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public static int getSize() {
            return STATUS_LEN;
        }

        public scale_status(byte[] b) {
            byte[] bb = ByteBuffer.wrap(b).order(j2me.nio.ByteOrder.LITTLE_ENDIAN).array();
            String deb = "";
            for (int i = 0; i != bb.length * 8; i++) {
                deb += String.valueOf(getBit(bb, i));
                if ((i + 1) % 8 == 0) {
                    deb += " ";
                }
            }
            Log.v("status_raw", deb);
            this.setByteBuffer(ByteBuffer.wrap(b).order(j2me.nio.ByteOrder.LITTLE_ENDIAN), 0);
            test();
        }

        private void test() {
            CommLogger.logv("scale_status", "n_status_length=" + String.valueOf(n_status_length.get()));
            CommLogger.logv("scale_status", "n_battery=" + String.valueOf(n_battery.get()));
            CommLogger.logv("scale_status", "b_timer_start=" + String.valueOf(b_timer_start.get()));
            CommLogger.logv("scale_status", "n_unit=" + String.valueOf(n_unit.get()));
            CommLogger.logv("scale_status", "b_cd_start=" + String.valueOf(b_cd_start.get()));
            Log.v("scale_status", "n_scale_mode=" + String.valueOf(n_scale_mode.get()));
            CommLogger.logv("scale_status", "b_tare=" + String.valueOf(b_tare.get()));
            CommLogger.logv("scale_status", "n_setting_sleep=" + String.valueOf(n_setting_sleep.get()));
            CommLogger.logv("scale_status", "n_setting_keydisable=" + String.valueOf(n_setting_keydisable.get()));
            CommLogger.logv("scale_status", "n_setting_sound=" + String.valueOf(n_setting_sound.get()));
            CommLogger.logv("scale_status", "n_setting_resol=" + String.valueOf(n_setting_resol.get()));
            CommLogger.logv("scale_status", "n_setting_capability=" + String.valueOf(n_setting_capability.get()));

        }

    }

    public static class ack_event extends Struct {

        private Unsigned8  n_ack_id = new Unsigned8();
        private Unsigned8 n_result_type = new Unsigned8(5);
        private Unsigned8  n_result_value= new Unsigned8(3);


        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public static int getSize() {
            return APPEVENT_ACK_LEN;
        }

        public ack_event(byte[] b) {
            this.setByteBuffer(ByteBuffer.wrap(b).order(j2me.nio.ByteOrder.LITTLE_ENDIAN), 0);
            print_debug();

        }

        private void print_debug() {
            CommLogger.logv("ack_event", "n_ack_id=" + String.valueOf(n_ack_id.get()));
            CommLogger.logv("ack_event", "n_result_type=" + String.valueOf(n_result_type.get()));
            CommLogger.logv("ack_event", "n_result_value=" + String.valueOf(n_result_value.get()));

            if(n_result_type.get()==0){
                if(n_result_value.get()== ERESULT_CMD. e_result_alive_success.ordinal()){
                    CommLogger.logv("ack_event","alive success!");
                }
            }

        }
    }

    public static class wt_event extends Struct {
        private Unsigned32 n_data = new Unsigned32();
        private Unsigned8 n_dp = new Unsigned8();
        private Unsigned8 b_stable = new Unsigned8(1); // 0 : stable 1 : unstable
        private Unsigned8 b_positive = new Unsigned8(1);// 0 : positive 1 : negative
        private Unsigned8 n_type = new Unsigned8(6);

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public static int getSize() {
            return APPEVENT_WEIGHT_LEN;
        }

        public wt_event(byte[] b) {
            this.setByteBuffer(ByteBuffer.wrap(b).order(j2me.nio.ByteOrder.LITTLE_ENDIAN), 0);
            print_debug();
            ;
        }

        public int getDP(){
            return (int)n_dp.get();
        }
        /**
         * get weight of weight event, including positive and negative
         *
         * @return
         */
        public long getWeight() {
            long weight =n_data.get();
            if (b_positive.get() == 1) {
                weight *= -1;
            }
            return weight;
        }

        public int getUnit(){
            return
                    (int)n_dp.get();
        }
        private void print_debug() {
            CommLogger.logv("wt_event", "n_data=" + String.valueOf(n_data.get()));
            CommLogger.logv("wt_event", "n_dp=" + String.valueOf(n_dp.get()));
            CommLogger.logv("wt_event", "b_stable=" + String.valueOf(b_stable.get()));
            CommLogger.logv("wt_event", "b_positive=" + String.valueOf(b_positive.get()));
            CommLogger.logv("wt_event", "n_type=" + String.valueOf(n_type.get()));
        }
    }

    public static class app_prsdata extends Struct {
        public byte mn_id;
        public Unsigned8 mn_appstep = new Unsigned8();
        public Unsigned8 mn_app_index = new Unsigned8();
        public Unsigned8 mn_app_len = new Unsigned8();
        public Unsigned8 mn_app_cmdid = new Unsigned8();
        public Unsigned8[] mn_app_buffer = new Unsigned8[]{new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8()};
        public Unsigned8 mn_app_ack = new Unsigned8();
        public Unsigned16 mn_app_checksum = new Unsigned16();
        public Unsigned16 mn_app_datasum = new Unsigned16();
        public Unsigned8 has_init = new Unsigned8();
        public Unsigned8 curr_unit = new Unsigned8();
    }

    public static class convert_struct extends Struct {

        public Unsigned8 temp_8 = new Unsigned8();
        public Unsigned16 temp_16 = new Unsigned16();
        public Unsigned8[] temp_8_array = new Unsigned8[20];
    }

    public static final String TAG="DataPacketParser";
    public static final short gn_len[] = {2, 1, 255, 2, 0};
    public static final short gn_event_len[] = {
            1,						//e_scalevent_weight
            1,						//e_scalevent_battery
            1,						//e_scalevent_timer
            0,						//e_scalevent_key
            0,						//e_scalevent_setting
            APPEVENT_WEIGHT_LEN,	//e_appevent_weight
            1,						//e_appevent_battery, battery value
            APPEVENT_TIMER_LEN,						//e_appevent_timer
            1,						//e_appevent_key, length 1: unit, weight mode, tare
            2,						//e_appevent_setting: 1 item, 1 value
            APPEVENT_TIMER_LEN,		//e_appevent_cd
            APPEVENT_ACK_LEN,		// e_appevent_ack
    };
    public static final short gs_header[]={0xEF, 0xDD};
    public static final short gn_cmd_len[] = {
            255,                        //new_cmd_system_sa
            255,                        // new_cmd_str_sa
            1,                        // new_cmd_battery_s, {ack}
            2,                    // new_cmd_weight_s, {ack,type}
            1,                        // new_cmd_tare_s, {ack}
            255,                    // new_cmd_custom_sa
            1,                        // new_cmd_info_s, {ack}
            255,                    // new_cmd_info_a
            16,                 //    new_cmd_status_a
            15,                        // new_cmd_isp_s, {pwd}
            3,                        // new_cmd_setting_chg_s, {ack, item, value}
            15,                        // new_cmd_identify_s, [id]
            255,                    // new_cmd_event_s, {event regist value}
            2,                        // new_cmd_timer_s{ack,action}
            255,                        // new_cmd_file_s, {len, packet number, value]
            15,                        // new_cmd_setpwd_s,
            255,                    // new_cmd_pcs_weight_s
            255,                    // new_cmd_pretare_s
            1, // new_cmd_sync_brewguide_s,
            1, // new_cmd_sync_message_s,
            3, // new_cmd_sync_brewguide_cmd,
            255, // new_cmd_sync_brewguide_data,
    };
}

