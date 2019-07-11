package co.acaia.communications.protocol.ver20;

import j2me.nio.ByteOrder;
import javolution.io.Struct;

public class FellowProtocol extends ScaleProtocol {

    //20180519, mikewu
    public static enum ECMD_AF
    {
        e_cmd_power_af,
        e_cmd_target_temp_af,
        e_cmd_ack,
        e_data_factory_fa,

    }

    public static enum EDATA_RESULT {
        e_result_success,
        e_result_tobecontinues,
        e_result_error_char,
        e_result_error_discard,
        e_result_error_unsupport,
        e_result_size
    }


    public static enum ECMD_FA
    {
        e_data_status_fa,
        e_data_hold_status_fa,
        e_data_target_temp_fa,
        e_data_current_temp_fa,
        e_data_hold_timer_fa,
        e_data_base_timer_fa,
        e_data_reach_goal_fa,
        e_data_safe_mode_fa,
        e_data_base_kettle_fa,
        e_data_ack_fa,
        e_cmd_factory_data,
    }

    public static class cmd_power extends Struct {
        private Unsigned8 n_ack = new Unsigned8();
        private Unsigned8[] buffer = new Unsigned8[]{new Unsigned8()};

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public cmd_power(short on) {
            n_ack.set(on);
        }

        public Struct.Unsigned8[] getByteArray() {

            buffer[0].set(n_ack.get());

            return buffer;
        }
    }

    public static class cmd_set_target_temp extends Struct {
        private Unsigned8 n_temp = new Unsigned8();
        private Unsigned8 n_unit = new Unsigned8();
        private Unsigned8[] buffer = new Unsigned8[]{
                new Unsigned8(),
                new Unsigned8()
        };

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public cmd_set_target_temp(short temp, short unit) {
            n_temp.set(temp);
            n_unit.set(unit);
        }

        public Struct.Unsigned8[] getByteArray() {
            buffer[0].set(n_temp.get());
            buffer[1].set(n_unit.get());
            return buffer;
        }
    }

    public static class cmd_unit extends Struct {
        private Unsigned8 n_unit = new Unsigned8();
        private Unsigned8[] buffer = new Unsigned8[]{
                new Unsigned8()
        };

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public cmd_unit(short unit) {
            n_unit.set(unit);
        }

        public Struct.Unsigned8[] getByteArray() {

            buffer[0].set(n_unit.get());

            return buffer;
        }
    }

    public static class data_ack extends Struct {
        private Unsigned8 n_ack = new Unsigned8();
        private Unsigned8[] buffer = new Unsigned8[]{
                new Unsigned8()
        };

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public data_ack(short ack) {
            n_ack.set(ack);
        }

        public Struct.Unsigned8[] getByteArray() {
            buffer[0].set(n_ack.get());
            return buffer;
        }
    }




}
