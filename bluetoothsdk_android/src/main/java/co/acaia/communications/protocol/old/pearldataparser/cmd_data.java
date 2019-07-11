package co.acaia.communications.protocol.old.pearldataparser;
import android.util.Log;

import j2me.nio.ByteBuffer;
import j2me.nio.ByteOrder;
import javolution.io.Struct;

/**
 * Created by kenatsushikan on 9/14/15.
 */
public class cmd_data {
    enum ECMD {
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

    public static class EWEIGHT_TYPE {
        int e_weight_none = 0x00;
        int e_weight_net = 0x01;
        int e_weight_gross = 0x02;
        int e_weight_tare = 0x03;
        int e_weight_goss_tare = 0x04;
        int e_weight_size = 0x05;
    }

    enum EUNIT {
        e_unit_off,
        e_unit_kg,
        e_unit_g,
        e_unit_lb,
        e_unit_ct,
        e_unit_oz,
        e_unit_t,
        e_unit_ozt,
        e_unit_gin,
        e_unit_pcs,
        e_unit_pct,
        e_unit_size
    }

    enum EWEIGHT_STBLE {
        e_wt_stable,
        e_wt_unstable
    }

    enum EWEIGHT_POSITIVE {
        e_wt_positive,
        e_wt_negative
    }

    public static class scale_info extends Struct {
        private Signed8 n_info_version=new Signed8();
        private Signed8 n_ISP_version=new Signed8();
        private Signed8 n_firm_mversion=new Signed8();
        private Signed8 n_firm_sversion=new Signed8();
        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public scale_info(byte[]b){
            this.setByteBuffer(ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN), 0);
        }
        public int get_info_version(){
            return n_info_version.get();
        }
        public int get_n_firm_mversion(){
            return n_firm_mversion.get();
        }
        public int get_n_firm_sversion(){
            return n_firm_sversion.get();
        }
    }

    public static final int INFO_LEN = 4;

    public static class bat_rep extends Struct {

        private Unsigned8 n_battery = new Unsigned8();

        public bat_rep(byte[] b) {
            this.setByteBuffer(ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN), 0);
        }

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public float getBatteryLevel() {
            return n_battery.get();
        }

        public void debug() {
            Log.v("bat_rep", "battery level:" + String.valueOf(getBatteryLevel()));
        }
    }

    public static final int BATTERY_R_LEN = 1;

    public static class wt_req extends Struct {
        private Unsigned8 n_period = new Unsigned8();
        private Unsigned8 n_time = new Unsigned8();
        private Unsigned8 n_type = new Unsigned8();
        private Unsigned8[] buffer = new Unsigned8[]{new Unsigned8(),
                new Unsigned8(),
                new Unsigned8()};

        public void setData(int _n_period, int _n_time, int _n_type) {
            this.n_period.set((short) _n_period);
            this.n_time.set((short) _n_time);
            this.n_type.set((short) _n_type);
        }

        public Unsigned8[] getByteArray() {

            buffer[0].set(n_period.get());
            buffer[1].set(n_time.get());
            buffer[2].set(n_type.get());

            return buffer;
        }

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }
    }

    public static final int WEIGHT_LEN = 3;

    public static class snd_req
            extends Struct {
        private Unsigned8 n_period = new Unsigned8();
        private Unsigned8 n_time = new Unsigned8();

        private Unsigned8[] buffer = new Unsigned8[]{new Unsigned8(),
                new Unsigned8(),
        };

        public void setData(int _n_period, int _n_time) {
            this.n_period.set((short) _n_period);
            this.n_time.set((short) _n_time);
        }

        public Unsigned8[] getByteArray() {
            buffer[0].set(n_period.get());
            buffer[1].set(n_time.get());
            return buffer;
        }

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }
    }
    public static final int SOUND_LEN = 3;

    public static class aud_req
            extends Struct {
        private Unsigned8 n_on = new Unsigned8();

        private Unsigned8[] buffer = new Unsigned8[]{new Unsigned8()
        };

        public void setData(int n_on) {
          this.n_on.set((short)n_on);
        }

        public Unsigned8[] getByteArray() {
            buffer[0].set(n_on.get());
            return buffer;
        }

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }
    }
    public static final int AUDIO_LEN = 1;


    public static class tare_req extends Struct {
        private Unsigned8 n_data = new Unsigned8();
        private Unsigned8 n_dp = new Unsigned8();
        private Unsigned8 n_unit = new Unsigned8();

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }
    }

    public static final int TARE_LEN = 6;

    public static class cmd_setting extends  Struct{
        private Unsigned8 n_set_id = new Unsigned8();
        private Unsigned8 n_set_value = new Unsigned8();
        private Unsigned8[] buffer = new Unsigned8[]{new Unsigned8(),
                new Unsigned8(),
        };
        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public void setData(int id,int val){
            n_set_id.set((short)id);
            n_set_value.set((short)val);
        }

        public Unsigned8[] getByteArray() {
            buffer[0].set(n_set_id.get());
            buffer[1].set(n_set_value.get());
            return buffer;
        }
    }

    public static class wt_rep extends Struct {
        private Unsigned32 n_data = new Unsigned32();
        private Unsigned8 n_dp = new Unsigned8();
        private Unsigned8 n_unit = new Unsigned8();
        private Unsigned8 b_stable = new Unsigned8(1);
        private Unsigned8 b_positive = new Unsigned8(1);
        private Unsigned8 n_type = new Unsigned8(6);


        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public wt_rep(byte[] b) {

            this.setByteBuffer(ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN), 0);
        }

        public void debug() {
            Log.v("wt_rep", "n_data=" + String.valueOf(n_data));
            Log.v("wt_rep", "n_dp=" + String.valueOf(n_dp));
            Log.v("wt_rep", "n_unit=" + String.valueOf(n_unit));
            Log.v("wt_rep", "b_stable=" + String.valueOf(b_stable));
            Log.v("wt_rep", "b_positive=" + String.valueOf(b_positive));
            Log.v("wt_rep", "n_type=" + String.valueOf(n_type));
        }

        public int getUnit(){
            return n_unit.get();
        }

        public float getWeight() {
            float weight = 0;
            weight = data_to_float(n_data.get(), n_dp.get());
            return weight;
        }
    }

    public static final int WEIGHT_R_LEN = 7;

    public static final int WEIGHT_R2_LEN = 12;
    public static final int LIGHT_LEN = 2;

    private static float data_to_float(long n_data, int n_dp) {
        float f_rv = n_data;
        byte n_dp_buf = (byte) n_dp;
        int n_loop = 0;
        if (n_dp_buf >= 0) {
            for (n_loop = 0; n_loop < n_dp_buf; n_loop++)
                f_rv /= 10;
        } else {
            n_dp_buf = (byte) (-1 * n_dp_buf);
            for (n_loop = 0; n_loop < n_dp_buf; n_loop++)
                f_rv *= 10;
        }
        return f_rv;
    }




}
