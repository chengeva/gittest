package co.acaia.communications.protocol.old.pearldataparser;
import j2me.nio.ByteOrder;
import javolution.io.Struct;

/**
 * Created by kenatsushikan on 9/14/15.
 */
public class allcmd_data {
    public enum ECMD {
        e_cmd_none,
        e_cmd_str,
        e_cmd_battery,
        e_cmd_battery_r,
        e_cmd_weight,
        e_cmd_weight_r,
        e_cmd_weight_r2,
        e_cmd_tare,
        e_cmd_sound,
        e_cmd_sound_on,
        e_cmd_light_on,
        e_cmd_file,
        e_cmd_custom,
        // 20140725 -s
        e_cmd_info_get,
        e_cmd_info_sent,
        e_cmd_isp,
        // 20140725 -e
        e_cmd_size
    }

    public enum ESETTING_ID {
        e_setting_sleep ,
        e_setting_keydisable,
        e_setting_sound,
        e_setting_resol,
        e_setting_capability,   // korean version 20140826
    }

    public static class cmd_setting extends Struct {
        private Unsigned8 n_set_id = new Unsigned8();
        private Unsigned8 n_set_value = new Unsigned8();

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }
    }

    public enum EKEY_ID {
        e_key_tare,
        e_key_zero
    }

    public static class EWEIGHT_TYPE {
        public static final int e_weight_none = 0x00;
        public static final int e_weight_net = 0x01;
        public static final int e_weight_gross = 0x02;
        public static final int e_weight_tare = 0x03;
        public static final int e_weight_goss_tare = 0x04;
        public static final int e_weight_size = 0x05;
    }
}
