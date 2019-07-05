package co.acaia.communications.protocol.ver20;

/**
 * Created by hanjord on 15/3/17.
 */
public class SettingFactory {

    public static class set_beep{
        public static final  short item=(short) ScaleProtocol.ESETTING_ITEM.e_setting_sound.ordinal();
        public static class beep_on_off{
            public static final short on=1;
            public static final short off=0;
        }
    }

    public static class set_unit{
        public static final short item=0;
        public static  class unit{
            public static final short g=2;
            public static final short oz=5;
        }
    }
    public static class set_sleep{
        public static final short item= (short)  ScaleProtocol.ESETTING_ITEM.e_setting_sleep.ordinal();
        public static class length{
            public static final short off=0;
            public static final short len_5_min=1;
            public static final short len_10_min=2;
            public static final short len_20_min=3;
            public static final short len_30_min=4;
            public static final short len_60_min=5;
        }
    }
    public static class set_key_disable{
        public static final short item= (short)  ScaleProtocol.ESETTING_ITEM.e_setting_keydisable.ordinal();
        public static class length{
            public static final short off=0;
            public static final short len_10_sec=10;
            public static final short len_20_sec=20;
            public static final short len_30_sec=30;
        }
    }

    public static class set_resolution{
        public static final short item= (short)  ScaleProtocol.ESETTING_ITEM.e_setting_resol.ordinal();
        public static class resolution_type{
            public static final short off=0;

        }
    }

    // TODO hanjord
    public static class set_scale_mode{
       // public static final short item= (short) ScaleProtocol.ESETTING_ITEM.e_setting_capability.ordinal();
        public static class resolution_type{
            public static final short off=0;

        }
    }
    public static SettingEntity getSetting(short item,short val){
        return new SettingEntity(item,val);
    }
}
