package co.acaia.communications.protocol.ver20;

import j2me.nio.ByteOrder;
import javolution.io.Struct;

public class BrewguideProtocol {

    public static enum BREWGUIDE_CMD
    {
        brewguide_cmd_state,
        brewguide_cmd_request_len,
        brewguide_cmd_request_page,
        brewguide_cmd_app_page_len,
    };

    public static  enum brewguide_dat_tpe
    {
        brewguide_data_str_title,
        brewguide_data_str_roaster,
        brewguide_data_info,
        brewguide_data_step,
    };

    public static class new_brewguide_data_string extends Struct {
        private Unsigned8 brewguide_data_type = new Unsigned8();
        private Unsigned8 page_id1 = new Unsigned8();
        private Unsigned8 page_id2 = new Unsigned8();
        private Unsigned8 str_id = new Unsigned8();
        public Unsigned8 num_str = new Unsigned8();
        private Unsigned8 data_len = new Unsigned8();
        public Unsigned8 string0 = new Unsigned8();
        public Unsigned8 string1 = new Unsigned8();
        public Unsigned8 string2 = new Unsigned8();
        public Unsigned8 string3 = new Unsigned8();
        public Unsigned8 string4 = new Unsigned8();
        public Unsigned8 string5 = new Unsigned8();
        public Unsigned8 string6 = new Unsigned8();
        public Unsigned8 string7 = new Unsigned8();


        private Unsigned8[] buffer = new Unsigned8[]{
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(),
        };

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public new_brewguide_data_string()
        {
            brewguide_data_type.set((short) brewguide_dat_tpe.brewguide_data_str_title.ordinal());
        }

        public void set_pageid(short page_id){
            page_id1.set((short)(page_id& 0xFF));
            page_id2.set((short)(page_id >> 8));
        }

        public void set_string_id(short string_id){
            str_id.set(string_id);
        }

        public void set_data_len(short data_len_){
            data_len.set(data_len_);
        }

        public Struct.Unsigned8[] getByteArray() {

            buffer[0].set(brewguide_data_type.get());
            buffer[1].set(page_id1.get());
            buffer[2].set(page_id2.get());
            buffer[3].set(str_id.get());
            buffer[4].set(num_str.get());
            buffer[5].set(data_len.get());
            buffer[6].set(string0.get());
            buffer[7].set(string1.get());
            buffer[8].set(string2.get());
            buffer[9].set(string3.get());
            buffer[10].set(string4.get());
            buffer[11].set(string5.get());
            buffer[12].set(string6.get());
            buffer[13].set(string7.get());

            return buffer;
        }
    }

    public static class new_brewguide_data_step extends Struct {
        private Unsigned8 brewguide_data_type = new Unsigned8();
        private Unsigned8 page_id = new Unsigned8();
        private Unsigned8 step_type = new Unsigned8();
        private Unsigned8 step_subtype = new Unsigned8();
        private Unsigned8 timer_label1 = new Unsigned8();
        private Unsigned8 timer_label2 = new Unsigned8();
        private Unsigned8 target_water1 = new Unsigned8();
        private Unsigned8 target_water2 = new Unsigned8();
        private Unsigned8 auto_pause1 = new Unsigned8();
        private Unsigned8 auto_pause2 = new Unsigned8();
        private Unsigned8 alert_sound1 = new Unsigned8();
        private Unsigned8 alert_sound2 = new Unsigned8();


        private Unsigned8[] buffer = new Unsigned8[]{
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
        };

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public new_brewguide_data_step( short step_page_id,short time, short water, short pause1, short pause2, short sound1, short sound2, short stepType,short subType) {
            brewguide_data_type.set((short)brewguide_dat_tpe.brewguide_data_step.ordinal());
            page_id.set(step_page_id);
            timer_label1.set((short)(time &0XFF));
            timer_label2.set((short)(time >> 8 ));
            target_water1.set((short)(water &0XFF));
            target_water2.set((short)(water >> 8 ));
            step_type.set(stepType);
            step_subtype.set(subType);
            auto_pause1.set(pause1);
            auto_pause2.set(pause2);
            alert_sound1.set(sound1);
            alert_sound2.set(sound2);
        }

        public Struct.Unsigned8[] getByteArray() {

            buffer[0].set(brewguide_data_type.get());
            buffer[1].set(page_id.get());
            buffer[2].set(step_type.get());
            buffer[3].set(step_subtype.get());
            buffer[4].set(timer_label1.get());
            buffer[5].set(timer_label2.get());
            buffer[6].set(target_water1.get());
            buffer[7].set(target_water2.get());
            buffer[8].set(auto_pause1.get());
            buffer[9].set(auto_pause2.get());
            buffer[10].set(alert_sound1.get());
            buffer[11].set(alert_sound2.get());

            return buffer;
        }
    }

    public static class new_brewguide_data_info extends Struct {
        private Unsigned8 brewguide_data_type = new Unsigned8();
        private Unsigned8 page_id = new Unsigned8();
        private Unsigned8 temp = new Unsigned8();
        private Unsigned8 dose1 = new Unsigned8();
        private Unsigned8 dose2 = new Unsigned8();
        private Unsigned8 water1 = new Unsigned8();
        private Unsigned8 water2 = new Unsigned8();
        private Unsigned8 data_type = new Unsigned8();


        private Unsigned8[] buffer = new Unsigned8[]{
            new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
            new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8()
        };

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public new_brewguide_data_info( short dose, short water, short temperature) {
            brewguide_data_type.set((short)brewguide_dat_tpe.brewguide_data_info.ordinal());
            page_id.set((short)0);
            temp.set(temperature);
            dose1.set((short)(dose &0XFF));
            dose2.set((short)(dose >> 8 ));
            water1.set((short)(water &0XFF));
            water2.set((short)(water >> 8 ));
            data_type.set((short)0); // 0 for user mode, 1 for factory mode
        }

        public Struct.Unsigned8[] getByteArray() {

            buffer[0].set(brewguide_data_type.get());
            buffer[1].set(page_id.get());
            buffer[2].set(temp.get());
            buffer[3].set(dose1.get());
            buffer[4].set(dose2.get());
            buffer[5].set(water1.get());
            buffer[6].set(water2.get());
            buffer[7].set(data_type.get());

            return buffer;
        }
    }

    // new_brewguide_data_info
    public static class new_brewguide_setting extends Struct {
        private Unsigned8 n_set_id = new Unsigned8();
        private Unsigned8 n_set_value1 = new Unsigned8();
        private Unsigned8 n_set_value2 = new Unsigned8();

        private Unsigned8[] buffer = new Unsigned8[]{new Unsigned8(), new Unsigned8(), new Unsigned8()};

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public new_brewguide_setting(short command_id, short n_value) {
            n_set_id.set(command_id);
            n_set_value1.set((short)(n_value & 0xFF));
            n_set_value2.set((short)(n_value >>8));
        }

        public Struct.Unsigned8[] getByteArray() {

            buffer[0].set(n_set_id.get());
            buffer[1].set(n_set_value1.get());
            buffer[2].set(n_set_value2.get());

            return buffer;
        }
    }
}
