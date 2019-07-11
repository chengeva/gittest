package co.acaia.communications.protocol.old.pearldataparser;
import android.util.Log;

import j2me.nio.ByteBuffer;

/**
 * Created by kenatsushikan on 9/14/15.
 */
public class subcmd_data {

    public static class cmd_setting extends  acaiaDataStruct{
        public Unsigned8 n_set_id = new Unsigned8();
        public Unsigned8 n_set_value = new Unsigned8();
        public cmd_setting(byte[] b){
            this.setByteBuffer(ByteBuffer.wrap(b).order(j2me.nio.ByteOrder.LITTLE_ENDIAN), 0);
        }

        public int get_id(){
            return n_set_id.get();
        }

        public int get_value(){
            return n_set_value.get();
        }

        @Override
        public void debug(){
            Log.v("cmd_setting","n_set_id="+String.valueOf(n_set_id.get()));
            Log.v("cmd_setting","n_set_value="+String.valueOf(n_set_value.get()));
        }
        public static int getLength(){
            return 2;
        }
    }

    enum ESUBCMD
    {
        e_subcmd_key,
        e_subcmd_unit,
        e_subcmd_set_setting,
        e_subcmd_read_setting,
        e_subcmd_setting_response,
        e_subcmd_start_timer,
        e_subcmd_pause_timer,
        e_subcmd_stop_timer,
        e_subcmd_get_timer,
        e_subcmd_timer_response,
        e_subcmd_ptimer_response,
        e_subcmd_get_ad,
        e_subcmd_ad_response,
        e_subcmd_start_cd,
        e_subcmd_pause_cd,
        e_subcmd_stop_cd,
        e_subcmd_get_cd,
        e_subcmd_cd_response,
        e_subcmd_pcd_response,
    }
    enum ESETTING_ID
    {
        e_setting_sleep ,
        e_setting_keydisable,
        e_setting_sound,
        e_setting_resol,
        e_setting_capability,   // korean version 20140826
    }

}
