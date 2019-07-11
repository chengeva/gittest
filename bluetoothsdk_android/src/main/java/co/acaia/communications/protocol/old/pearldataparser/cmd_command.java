package co.acaia.communications.protocol.old.pearldataparser;
import javolution.io.Struct;

/**
 * Created by kenatsushikan on 9/14/15.
 */
public class cmd_command {

    en_command _en_command=new en_command();
    public cmd_command(){

    }

     /*
    // e_cmd_info_get
unsigned char pack_info_req(unsigned char *s_out)
{
    unsigned char n_len;
    n_len = sr_encrypt(e_cmd_info_get, 0, 0, 0, s_out);
    return n_len;
}
     */

    public int pack_info_req(Struct.Unsigned8[] s_out){
        int ln_len = 0;
        _ls_temp ls_temp=new _ls_temp();
        ln_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_info_get.ordinal(), 0, ByteDataHelper.getByteArrayFromU1(ls_temp.ls_temp,0,ls_temp.ls_temp.length), 0, s_out);
        return ln_len;
    }

    public  int pack_custom(Struct.Unsigned8[] s_out, byte n_customid, Struct.Unsigned8[] s_data, int n_len){
        int ln_len = 0, ln_loop = 0;
        _ls_temp ls_temp=new _ls_temp();
        ls_temp.ls_temp[0].set(n_customid);
        for(ln_loop =0; ln_loop < n_len; ln_loop ++)
            ls_temp.ls_temp[1 + ln_loop] .set(s_data[ln_loop].get());
        ln_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_custom.ordinal(), 0, ByteDataHelper.getByteArrayFromU1(ls_temp.ls_temp,0,ls_temp.ls_temp.length), n_len + 1, s_out);
        return ln_len;
    }

    public int pack_setting(Struct.Unsigned8[] s_out,int n_settingid, int n_value){
        int ln_len = 0;
        _ls_temp3 ls_temp=new _ls_temp3();
        ls_temp.ls_temp[0] .set((short)ByteDataHelper.getUnsignedByte((byte) subcmd_data.ESUBCMD. e_subcmd_setting_response.ordinal()));
        ls_temp.ls_temp[1] .set((short)n_settingid);
        ls_temp.ls_temp[2].set((short) n_value);
        ln_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_custom.ordinal(), 0, ByteDataHelper.getByteArrayFromU1(ls_temp.ls_temp,0,ls_temp.ls_temp.length), 3, s_out);

        return ln_len;
    }

    public int pack_battery_req(Struct.Unsigned8[] s_out){
        int ln_len = 0;
        ln_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_battery.ordinal(), 0, new byte[]{0}, 0, s_out);
        return ln_len;
    }

    public int pack_weight_req(Struct.Unsigned8[] s_out,int n_period,int n_time,int n_type) {
        int n_len = 0;
        cmd_data.wt_req lo_req = new cmd_data.wt_req();
        lo_req.setData(n_period, n_time, n_type);
        Struct.Unsigned8[] ls_temp = lo_req.getByteArray();
        n_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_weight.ordinal(), 0, ByteDataHelper.getByteArrayFromU1(ls_temp, 0, ls_temp.length), cmd_data.WEIGHT_LEN, s_out);

        return n_len;
    }

    public int pack_sound_req(Struct.Unsigned8[] s_out,int n_period,int n_time){
        int n_len = 0;

        return n_len;
    }

    public int pack_audio_req(Struct.Unsigned8[] s_out,int n_on){
        int n_len = 0;
        cmd_data.aud_req lo_req=new cmd_data.aud_req();
        lo_req.setData(n_on);
        Struct.Unsigned8[] ls_temp = lo_req.getByteArray();
        n_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_sound_on.ordinal(), 0, ByteDataHelper.getByteArrayFromU1(ls_temp, 0, ls_temp.length), cmd_data.AUDIO_LEN, s_out);
        return n_len;
    }

    public int pack_start_cd(Struct.Unsigned8[] s_out,int n_hr, int n_min, int n_sec){
        int ln_len = 0;

        _ls_temp4 ls_temp =new _ls_temp4();
        ls_temp.ls_temp[0].set((short)subcmd_data.ESUBCMD.e_subcmd_start_cd.ordinal());
        ls_temp.ls_temp[1].set((short) n_hr);
        ls_temp.ls_temp[2] .set((short)n_min);
        ls_temp.ls_temp[3] .set((short) n_sec);
        ln_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_custom.ordinal(), 0, ByteDataHelper.getByteArrayFromU1(ls_temp.ls_temp, 0, ls_temp.ls_temp.length), 4, s_out);

        return ln_len;
    }


    public int pack_start_timer(Struct.Unsigned8[] s_out){
        int ln_len = 0;
        ln_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_custom.ordinal(), 0,new byte[]{(byte) subcmd_data.ESUBCMD.e_subcmd_start_timer.ordinal()}, 1, s_out);

        return ln_len;
    }

    public int pack_pause_timer(Struct.Unsigned8[] s_out){
        int ln_len = 0;
        ln_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_custom.ordinal(), 0,new byte[]{(byte) subcmd_data.ESUBCMD.e_subcmd_pause_timer.ordinal()}, 1, s_out);

        return ln_len;
    }

    public int pack_stop_timer(Struct.Unsigned8[] s_out){
        int ln_len = 0;
        ln_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_custom.ordinal(), 0,new byte[]{(byte) subcmd_data.ESUBCMD.e_subcmd_stop_timer.ordinal()}, 1, s_out);

        return ln_len;
    }

    public int pack_get_timer(Struct.Unsigned8[] s_out, int second){
        int ln_len = 0;
        _ls_temp2 ls_temp =new _ls_temp2();
        ls_temp.ls_temp[0].set((short)subcmd_data.ESUBCMD.e_subcmd_get_timer.ordinal());
        ls_temp.ls_temp[1].set((short)second);
        ln_len = _en_command.sr_encrypt(allcmd_data.ECMD.e_cmd_custom.ordinal(), 0,ByteDataHelper.getByteArrayFromU1(ls_temp.ls_temp, 0, ls_temp.ls_temp.length), 2, s_out);
        return ln_len;
    }



    // LEN=2
    public static class _ls_temp2 extends Struct {
        public  Unsigned8[] ls_temp = new Unsigned8[] { new Unsigned8(),
                new Unsigned8(), new Unsigned8(),
        };
    }
    // LEN=3
    public static class _ls_temp3 extends Struct {
        public  Unsigned8[] ls_temp = new Unsigned8[] { new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
        };
    }

    // LEN=4
    public static class _ls_temp4 extends Struct {
        public  Unsigned8[] ls_temp = new Unsigned8[] { new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
        };
    }

    // LEN=64
    public static class _ls_temp extends Struct {
        public  Unsigned8[] ls_temp = new Unsigned8[] { new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),    new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),    new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),    new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),    new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),    new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),    new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),    new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),    new Unsigned8(), new Unsigned8(), new Unsigned8(), new Unsigned8(),
        };
    }
}
