package co.acaia.communications.protocol.old.pearldataparser;

import android.util.Log;

import javolution.io.Struct;

/**
 * Created by kenatsushikan on 9/14/15.
 */
public class en_command {
    public static final String TAG="en_command";

    public int IDENTIFY_0 = 0xDF;
    public int IDENTIFY_1 = 0x78;
    public int gn_ack = 0;

    private byte[] s_cmdbuf;
    private int n_read_index = 0;
    private int n_counter = 0;

    private enum E_DECODE_STATE {
        e_decode_state_idle,
        e_decode_state_idpass0,
        e_decode_state_idpass1,
        e_decode_state_tlen
    }

    E_DECODE_STATE n_decode_state;

    public en_command() {
        s_cmdbuf = new byte[32];
        n_decode_state = E_DECODE_STATE.e_decode_state_idle;
    }

    public int sr_encrypt(int n_cmd, int n_id, byte[] s_in, int n_len, Struct.Unsigned8[] s_out) {

        Struct.Unsigned8[] s_output = s_out;
        if (gn_ack == 0xff)
            gn_ack = 1;
        else
            gn_ack++;

        gn_ack = 1;
        int outout_ptr = 0;

        // 1 identifiers
        s_output[outout_ptr++].set((short) IDENTIFY_0);
        s_output[outout_ptr++].set((short) IDENTIFY_1);

        // 2 total len?str len type 1byte, ack 1byte, id 1byte, len 1byte, sum 1byte
        s_output[outout_ptr++].set((short) (n_len + 5));        // 1 type 1 ack 1 id 1 len 1 sum

        // 3 type
        s_output[outout_ptr++].set((short) n_cmd);

        // 4 ack
        s_output[outout_ptr++].set((short) gn_ack);

        // 5 Id
        s_output[outout_ptr++].set((short) n_id);
        ;

        // 6 len, ????????????header????
        s_output[outout_ptr++].set((short) n_len);


        // 7 data
        if (n_len != 0)
            str_encrypt(s_in, s_output, n_len, gn_ack, outout_ptr);

        // 8 sum

        s_output[outout_ptr + n_len].set((short) get_sum(s_out, n_len + 4, 3));
        s_output[outout_ptr + n_len + 1].set((short) 0);

        for (int i = 0; i != s_output.length; i++) {
            Log.i("en_command", " [" + String.valueOf(i) + "] " + String.valueOf(s_output[i].get()));
        }

        return n_len + 8;

    }


    private int get_sum(Struct.Unsigned8[] s_in, int n_len, int start) {
        int n_sum = 0;
        for (int n_loop = start; n_loop < n_len + start; n_loop++)
            n_sum += s_in[n_loop].get();
        return n_sum & 0xff;
    }

    private int get_sum(byte[] s_in, int n_len, int start) {
        int n_sum = 0;
        for (int n_loop = start; n_loop < n_len + start; n_loop++)
            n_sum += ByteDataHelper.getUnsignedByte(s_in[n_loop]);
        return n_sum & 0xff;
    }

    private int check_cmdlen(int n_cmd, int n_len) {
        if (n_cmd == allcmd_data.ECMD.e_cmd_custom.ordinal() || n_cmd == allcmd_data.ECMD.e_cmd_file.ordinal() || n_cmd == allcmd_data.ECMD.e_cmd_str.ordinal()) {
            return 0;
        } else if (n_cmd == cmd_data.ECMD.e_cmd_info_sent.ordinal()) {
            return (n_len == cmd_data.INFO_LEN) ? 0 : 1;
        } else if (n_cmd == cmd_data.ECMD.e_cmd_info_sent.e_cmd_isp.ordinal() || n_cmd == cmd_data.ECMD.e_cmd_info_get.ordinal() || n_cmd == cmd_data.ECMD.e_cmd_battery.ordinal()) {
            return (n_len == 0) ? 0 : 1;
        } else if (n_cmd == cmd_data.ECMD.e_cmd_battery_r.ordinal()) {
            return (n_len == cmd_data.BATTERY_R_LEN) ? 0 : 1;
        } else if (n_cmd == cmd_data.ECMD.e_cmd_weight.ordinal()) {
            return (n_len == cmd_data.WEIGHT_LEN) ? 0 : 1;
        } else if (n_cmd == cmd_data.ECMD.e_cmd_weight_r.ordinal()) {
            return (n_len == cmd_data.WEIGHT_R_LEN) ? 0 : 1;
        } else if (n_cmd == cmd_data.ECMD.e_cmd_weight_r2.ordinal()) {
            return (n_len == cmd_data.WEIGHT_R2_LEN) ? 0 : 1;
        } else if (n_cmd == cmd_data.ECMD.e_cmd_tare.ordinal()) {
            return (n_len == cmd_data.TARE_LEN) ? 0 : 1;
        } else if (n_cmd == cmd_data.ECMD.e_cmd_sound.ordinal()) {
            return (n_len == cmd_data.SOUND_LEN) ? 0 : 1;
        } else if (n_cmd == cmd_data.ECMD.e_cmd_sound_on.ordinal()) {
            return (n_len == cmd_data.AUDIO_LEN) ? 0 : 1;
        } else if (n_cmd == cmd_data.ECMD.e_cmd_light_on.ordinal()) {
            return (n_len == cmd_data.LIGHT_LEN) ? 0 : 1;
        }
        return 1;
    }

    private int str_decrypt(byte[] s_in, Struct.Unsigned8[] s_out, int n_len, int n_off_index, int start) {
        int n_loop, n_index = 0;

        for (n_loop =0; n_loop < n_len; n_loop++) {
            n_index = en_code.s_table2[ByteDataHelper.getUnsignedByte(s_in[n_loop+start])];
            if (n_index < n_off_index)
                n_index = n_index + en_code.TABLE_SIZE;
            n_index -= n_off_index;
           // Log.v(TAG,"n_loop="+String.valueOf(n_loop)+" n_index="+String.valueOf(n_index));
            s_out[n_loop].set((short) n_index);
        }
        return n_index;
    }

    private int str_encrypt(byte[] s_in, Struct.Unsigned8[] s_out, int n_len, int n_off_index, int start) {
        int n_loop, n_index;
        Log.v(TAG,"str_encrypt"+" byte sin size="+String.valueOf(s_in.length)+"n_len="+String.valueOf(n_len));
        for (n_loop = 0; n_loop < n_len ; n_loop++) {
            n_index = (s_in[n_loop] + n_off_index) & 0xff;
           // Log.i("str_encrypt", "s_in[" + String.valueOf(n_loop) + "]" + String.valueOf((short) en_code.s_table[n_index]));
            s_out[n_loop+start].set((short) en_code.s_table[n_index]);
           // Log.i("str_encrypt", "s_in[" + String.valueOf(n_loop) + "]" + String.valueOf((short) en_code.s_table[n_index]));
        }
        return 1;
    }


    // note: recursive.... need start index
    public en_code.E_ENCRY_RESULT sr_decrypt(byte[] s_in, int n_in, Struct.Unsigned8[] s_out, sr_decrypt_holder sr_decrypt_holder_,int start) {
         int n_len = start;
        int n_ack = 0;
        int n_read = start;


        en_code.E_ENCRY_RESULT n_result = en_code.E_ENCRY_RESULT.e_encry_fail;

        s_out[0].set((short) 0);

        for (n_read = 0; n_read < n_in+start; n_read++) {
            // ????
            switch (n_decode_state) {
                case e_decode_state_idle:
                    // 1 ????
                    Log.v(TAG,"start="+String.valueOf(start)+" s_in[n_read]="+String.valueOf(s_in[n_read]));
                    if (ByteDataHelper.getUnsignedByte(s_in[n_read]) != IDENTIFY_0) {
                        n_result = en_code.E_ENCRY_RESULT.e_encry_tobe_continue;
                        break;
                    }
                    n_result = en_code.E_ENCRY_RESULT.e_encry_tobe_continue;
                    n_decode_state = E_DECODE_STATE.e_decode_state_idpass0;
                    break;
                case e_decode_state_idpass0:
                    if (ByteDataHelper.getUnsignedByte(s_in[n_read]) != IDENTIFY_1) {
                        n_decode_state = E_DECODE_STATE.e_decode_state_idle;
                        n_result = en_code.E_ENCRY_RESULT.e_encry_tobe_continue;
                        break;
                    }
                    n_result = en_code.E_ENCRY_RESULT.e_encry_tobe_continue;
                    n_decode_state = E_DECODE_STATE.e_decode_state_idpass1;
                    break;
                case e_decode_state_idpass1:
                    // 2 ????
                    n_counter = ByteDataHelper.getUnsignedByte(s_in[n_read]);
                    n_decode_state = E_DECODE_STATE.e_decode_state_tlen;
                    n_read_index = 0;
                    n_result = en_code.E_ENCRY_RESULT.e_encry_tobe_continue;
                    break;
                case e_decode_state_tlen:
                    s_cmdbuf[n_read_index] = s_in[n_read];
                    n_read_index += 1;
                    // ??????
                    if (n_counter > 1) {
                        n_counter--;
                        n_result = en_code.E_ENCRY_RESULT.e_encry_tobe_continue;
                        break;
                    }
                    if (ByteDataHelper.getUnsignedByte(s_in[n_read]) != get_sum(s_cmdbuf, n_read_index - 1, 0)) {
                        n_decode_state = E_DECODE_STATE.e_decode_state_idle;
                        n_counter = 0;
                        s_cmdbuf[0] = 0;
                        n_read_index = 0;

                        sr_decrypt_holder_.n_index = (n_read + 1);
                        return en_code.E_ENCRY_RESULT.e_encry_sum_error;
                    }

                    s_cmdbuf[n_read_index] = 0;

                    //
                    if (ByteDataHelper.getUnsignedByte(s_cmdbuf[0]) >= allcmd_data.ECMD.e_cmd_size.ordinal()) {
                        n_result = en_code.E_ENCRY_RESULT.e_encry_protocol_error;
                        break;
                    }

                    sr_decrypt_holder_.n_cmd = ByteDataHelper.getUnsignedByte(s_cmdbuf[0]);
                    n_ack = ByteDataHelper.getUnsignedByte(s_cmdbuf[1]);
                    sr_decrypt_holder_.n_id = ByteDataHelper.getUnsignedByte(s_cmdbuf[2]);
                    n_len = ByteDataHelper.getUnsignedByte(s_cmdbuf[3]);
                    if (n_len != n_read_index - 5 || check_cmdlen(sr_decrypt_holder_.n_cmd, n_len) == 1) {
                        sr_decrypt_holder_.n_index = (n_read + 1);
                        n_decode_state = E_DECODE_STATE.e_decode_state_idle;
                        return en_code.E_ENCRY_RESULT.e_encry_len_error;
                    }
                   // Log.v(TAG,"n_len="+String.valueOf(n_len)+" "+"n_ack="+String.valueOf(n_ack));
                    str_decrypt(s_cmdbuf, s_out, n_len, n_ack, 4);
                    for(int i=0;i!=n_len;i++){
                       // Log.v(TAG,"s_out["+String.valueOf(i)+"]="+String.valueOf(s_out[i]));
                    }
                    s_out[n_len].set((short) 0);
                    sr_decrypt_holder_.n_index = (n_read + 1);
                    n_decode_state = E_DECODE_STATE.e_decode_state_idle;
                    return en_code.E_ENCRY_RESULT.e_encry_success;
            }
        }
        sr_decrypt_holder_.n_index = n_read;
       // Log.v(TAG,"Decode state="+String.valueOf(n_decode_state));
        return n_result;
    }
}
