package co.acaia.communications.protocol.ver20;

import java.util.Arrays;

import co.acaia.communications.CommLogger;
import co.acaia.communications.ScaleDataParser;
import co.acaia.communications.events.ScaleDataEvent;
import co.acaia.communications.scale.AcaiaScale;
import de.greenrobot.event.EventBus;
import javolution.io.Struct;

import static co.acaia.communications.protocol.ver20.ScaleProtocol.*;
import static co.acaia.communications.protocol.ver20.ScaleProtocol.APPEVENT_ACK_LEN;
import static co.acaia.communications.protocol.ver20.ScaleProtocol.APPEVENT_TIMER_LEN;
import static co.acaia.communications.protocol.ver20.ScaleProtocol.APPEVENT_WEIGHT_LEN;
import static co.acaia.communications.protocol.ver20.ScaleProtocol.ECMD.e_cmd_event_sa;
import static co.acaia.communications.protocol.ver20.ScaleProtocol.ECMD.e_cmd_info_a;
import static co.acaia.communications.protocol.ver20.ScaleProtocol.ECMD.e_cmd_status_a;
import static co.acaia.communications.protocol.ver20.ScaleProtocol.ECMD.e_cmd_str_sa;

/**
 * Created by hanjord on 15/3/10.
 */



public class DataPacketHelper {

    public static final String TAG="DataPacketHelper";
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
                 255,						//e_cmd_system_sa
                255,						// e_cmd_str_sa
                1,						// e_cmd_battery_s, {ack}
                2,					// e_cmd_weight_s, {ack,type}
                1,						// e_cmd_tare_s, {ack}
                255,					// e_cmd_custom_sa
                1,						// e_cmd_info_s, {ack}
                255,					// e_cmd_info_a
                255, 				//	e_cmd_status_a
                15,						// e_cmd_isp_s, {pwd}
                3,						// e_cmd_setting_chg_s, {ack, item, value}
                15,						// e_cmd_identify_s, [id]
                255,					// e_cmd_event_s, {event regist value}
                2,						// e_cmd_timer_s{ack,action}
                255,						// e_cmd_file_s, {len, packet number, value]
                15,						// e_cmd_setpwd_s,
                255,					// e_cmd_pcs_weight_s
                255,					// e_cmd_pretare_s
    };
    //code u1 gs_header[] = {0xEF, 0xDD};

    public static int getUnsignedByte(byte in){
        // warning: need to be tested...
        return in& 0xFF;
    }
    public static int left_shift_8(int in){
        return in<<8;
    }

    public static int getUnsignedShort(short in){
        return in& 0xffff;
    }

    public static int calc_sum(Struct.Unsigned8[] s_in, Struct.Unsigned8 n_len)
    {
        // warning: need to be tested
        short ln_loop = 0;
        short ln_sum1 = 0, ln_sum2 = 0;
        int ln_sum = 0;
        int lb_odd = 1;

        for (ln_loop = 0; ln_loop < n_len.get(); ln_loop++) {
            //System.out.println("debug"+s_in.struct().getByteBuffer().toString());
            if (lb_odd == 1){
                ln_sum1 += s_in[ln_loop].get();
            }
            else{
                ln_sum2 += s_in[ln_loop].get();
            }
            if (lb_odd == 1)
                lb_odd = 0;
            else
                lb_odd = 1;
        }
        ln_sum = (ln_sum1 & 0xff) << 8 | (ln_sum2 & 0xff);
        return getUnsignedShort((short)ln_sum);
    }

    public static  Struct.Unsigned8 u_short_to_u_char(Struct.Unsigned16 in){
        ScaleProtocol.convert_struct convertstruct=new ScaleProtocol.convert_struct();
        convertstruct.temp_16.set(in.get());
        convertstruct.temp_8.set((short)convertstruct.temp_16.get());
        return convertstruct.temp_8;
    }


    public static void init_app_prs_data( ScaleProtocol.app_prsdata o_data){

     /*   u1 gn_appstep = e_prs_checkheader;
        u1 gn_app_index = 0;
        u1 gn_app_len = 2;
        u1 gn_app_cmdid = 0;
        u1 gn_app_buffer[20];
        u1 gn_app_ack = 0;
        u2 gn_app_checksum = 0;
        u2 gn_app_datasum =0;*/
        o_data.mn_id = -1;
        o_data.mn_appstep .set( (short)EAPP_PROCESS.e_prs_checkheader.ordinal());
        o_data.mn_app_index .set((short)0);
        o_data.mn_app_len .set((short) gn_len[EAPP_PROCESS.e_prs_checkheader.ordinal()]);
        o_data.mn_app_cmdid .set((short)0);
        o_data.mn_app_checksum .set((short)0);
        o_data.mn_app_datasum .set((short)0);

    }
    public static int check_protocol_type(byte sdata[],AcaiaScale acaiaScale){
        app_prsdata mo_prsdata=new app_prsdata();
        // warning: need a factory method to do this
        init_app_prs_data(mo_prsdata);
        for(int i=0;i!=sdata.length;i++){
            int parse_result= app_uartin(mo_prsdata,sdata[i],sdata,acaiaScale);
            CommLogger.logv(TAG, "Parse result" + String.valueOf(parse_result));
            if(parse_result==EDATA_RESULT.e_result_error_char.ordinal()){
                CommLogger.logv(TAG, "Parse Error!");
                return -1;
            }else{
                return 1;
            }
        }
        return 1;
    }
    public static void test_parse_packet( byte sdata[],AcaiaScale acaiaScale){
        CommLogger.logv(TAG, "Testing parse packet...");
        CommLogger.logv(TAG, "Testing parse packet length=" + sdata.length);
        app_prsdata mo_prsdata=new app_prsdata();

        // warning: need a factory method to do this
        init_app_prs_data(mo_prsdata);

        for(int i=0;i!=sdata.length;i++){
           // CommLogger.logv(TAG,String.valueOf((int)sdata[i]));

           int parse_result= app_uartin(mo_prsdata,sdata[i],sdata,acaiaScale);
            if(parse_result==EDATA_RESULT.e_result_error_char.ordinal()){
                CommLogger.logv(TAG, "Parse Error!");
                break;
            }
        }
        CommLogger.logv(TAG, "Testing parse packet end...");
    }

    public static void app_event(app_prsdata o_data, byte n_cbid, int n_event, Struct.Unsigned8[] s_param, byte[] orig_data, AcaiaScale acaiaScale){

       // u1 ln_receiverid = -1;
        int ln_loop = 0;
        // warning: need to handle n_cbid
        // n_cbid
        CommLogger.logv(TAG, "n_event" + String.valueOf(n_event));
        if(n_event==e_cmd_info_a.ordinal()){
            CommLogger.logv(TAG, "n_event=e_cmd_info_a");
            DataOutHelper.sent_appid(o_data, "012345678901234".getBytes());
        }else if(n_event== e_cmd_status_a.ordinal()){
            CommLogger.logv(TAG, "n_event=e_cmd_status_a");

            // weird: s_param != orig_data
            scale_status scaleStatus=new scale_status(getByteArrayFromU1(s_param,0,scale_status.getSize()));
            CommLogger.logv(TAG,"curr unit="+String.valueOf(scaleStatus.n_unit.get()));
            acaiaScale.n_unit=scaleStatus.n_unit.get();
            DataOutHelper. default_event();
        }else if(n_event== e_cmd_event_sa.ordinal()){
            CommLogger.logv(TAG, "n_event=e_cmd_event_sa");
            parse_eventmsg(s_param,orig_data);
            // Parse the scale's event

        }else if(n_event== e_cmd_str_sa.ordinal()){
            CommLogger.logv(TAG, "n_event=e_cmd_str_sa");
        }

    }

    public static byte[] getTargetBytes(byte[] s_in,int start,int end){
        byte[] s_out=new byte[end];
        int get_byte_pt=0;
        for(int i=start;i!=start+end;i++){
            CommLogger.logv(TAG, "ori wt_event_ls_process[" + String.valueOf(get_byte_pt) + "]=" + String.valueOf(s_in[i]));

            s_out[get_byte_pt]=s_in[i];
            get_byte_pt++;
        }
        return s_out;
    }

    public static byte[] getByteArrayFromU1(Struct.Unsigned8[] s_param,int start,int end){
        //0 4
        // 0 1 2 3
        int len=end;
        int get_byte_pt=0;
        byte[] getByteArray=new byte[len];
        for(int i=start;i!=start+end;i++){
            CommLogger.logv(TAG, "wt_event_ls_process[" + String.valueOf(get_byte_pt) + "]=" + String.valueOf((byte) s_param[i].get()));
            getByteArray[get_byte_pt]=(byte)(s_param[i].get());
            get_byte_pt++;
        }

        CommLogger.logv(TAG, Arrays.toString(getByteArray));
        return getByteArray;
    }
    public static void parse_eventmsg(Struct.Unsigned8[] s_param,byte [] orig_data){
        int ln_length=s_param.length;

        short ls_pt=1;
        short ls_process;
        while(ln_length>0){
            if(ls_pt==s_param.length)
                break;
            ls_process=s_param[ls_pt].get();
            if(ls_process==EEVENT.e_appevent_weight.ordinal()){
                CommLogger.logv(TAG, "EEVENT process weight event, s_param len=" + s_param.length);
                wt_event wtevent=new wt_event(getByteArrayFromU1(s_param, ls_pt + 1, wt_event.getSize()));
                ls_pt+= ScaleProtocol.APPEVENT_WEIGHT_LEN;
                ln_length -= APPEVENT_WEIGHT_LEN;
                ScaleDataEvent scaleDataEvent=new ScaleDataEvent(ScaleDataEvent.data_weight,wtevent.getWeight());
                EventBus.getDefault().post(scaleDataEvent);
            }else if(ls_process==EEVENT.e_appevent_battery.ordinal()){
                CommLogger.logv(TAG, "EEVENT e_appevent_battery event");
                ScaleDataParser.parse_battery_event(s_param[ls_pt + 1].get());
                ln_length --;
                ls_pt++;
            }else if(ls_process==EEVENT.e_appevent_timer.ordinal()){
                CommLogger.logv(TAG, "EEVENT e_appevent_timer event");
                tm_event tmevent=new tm_event(getByteArrayFromU1(s_param, ls_pt + 1, tm_event.getSize()));
                ScaleDataParser.parse_timer_event(tmevent);
                ls_pt+=ScaleProtocol.APPEVENT_TIMER_LEN;
                ln_length -= APPEVENT_TIMER_LEN;
            }else if(ls_process==EEVENT.e_appevent_key.ordinal()){
                CommLogger.logv(TAG, "EEVENT e_appevent_key event");
                ls_pt++;
                ln_length --;
            }else if(ls_process==EEVENT.e_appevent_setting.ordinal()){
               // CommLogger.logv(TAG,"EEVENT e_appevent_setting event");
              //  ls_pt++;
            }else if(ls_process==EEVENT.e_appevent_ack.ordinal()){
                CommLogger.logv(TAG, "EEVENT e_appevent_ack event");
                ls_pt+=APPEVENT_ACK_LEN;
                ln_length -= APPEVENT_ACK_LEN;
            }
            ls_pt++;
            ln_length--;
        }
    }

    private static void reverseBitsInByteArray(byte[] data){
        for(int i=0;i!=data.length;i++){
            data[i]=reverseBitsByte(data[i]);
        }

    }
    private static byte reverseBitsByte(byte x) {
        int intSize = 8;
        byte y=0;
        for(int position=intSize-1; position>0; position--){
            y+=((x&1)<<position);
            x >>= 1;
        }
        return y;
    }

    private static int getBit(byte[] data, int pos) {
        int posByte = pos/8;
        int posBit = pos%8;
        byte valByte = data[posByte];
        int valInt = valByte>>(8-(posBit+1)) & 0x0001;
        return valInt;
    }

    public static int byteArrayToInt(byte[] b) {
        if (b.length == 4)
            return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8
                    | (b[3] & 0xff);
        else if (b.length == 2)
            return 0x00 << 24 | 0x00 << 16 | (b[0] & 0xff) << 8 | (b[1] & 0xff);

        return 0;
    }

    public static int app_uartin(app_prsdata o_data,byte s_in,byte[] orig_data, AcaiaScale acaiaScale){
        int mn_appstep=o_data.mn_appstep.get();
       // CommLogger.logv(TAG,"----------------");
       // CommLogger.logv(TAG,"mn_appstep="+String.valueOf(mn_appstep));

        if(mn_appstep==EAPP_PROCESS.e_prs_checkheader.ordinal()){
           // CommLogger.logv(TAG,"e_prs_checkheader="+String.valueOf(getUnsignedByte(s_in)));
           // CommLogger.logv(TAG,"gs_header[o_data.mn_app_index.get()]="+String.valueOf(gs_header[o_data.mn_app_index.get()]));
            if (getUnsignedByte(s_in) != gs_header[o_data.mn_app_index.get()])
            {
                o_data.mn_app_index .set((short)0);
                return EDATA_RESULT.e_result_error_char.ordinal();
            }
        }else if(mn_appstep==EAPP_PROCESS.e_prs_cmdid.ordinal()){
            o_data.mn_app_cmdid.set((short)getUnsignedByte(s_in));
        }else if(mn_appstep==EAPP_PROCESS.e_prs_cmddata.ordinal()){
            if (o_data.mn_app_index.get()== 0)	// the first time
            {
                o_data.mn_app_len .set(gn_cmd_len[o_data.mn_app_cmdid.get()]);
                if (o_data.mn_app_len.get() == 255)
                    o_data.mn_app_len.set( s_in);
            }
            // parse command
            o_data.mn_app_buffer[o_data.mn_app_index.get()].set((short)getUnsignedByte(s_in));
            o_data.mn_app_datasum.set((short) ( o_data.mn_app_datasum.get()+(short)1));

        }else if(mn_appstep==EAPP_PROCESS.e_prs_checksum.ordinal()){
            // o_data->mn_app_checksum = (o_data->mn_app_checksum << 8) + (u1)s_in;
            o_data.mn_app_checksum.set((short)(left_shift_8(o_data.mn_app_checksum.get())+getUnsignedByte(s_in)));
        }

        o_data.mn_app_index.set( (short)(o_data.mn_app_index.get()+(short)1));
       // CommLogger.logv(TAG,"o_data.mn_app_index="+String.valueOf(o_data.mn_app_index.get()));
       // CommLogger.logv(TAG,"o_data->mn_app_len="+String.valueOf(o_data.mn_app_len.get()));
        // next step
        if(o_data.mn_app_index.get()==o_data.mn_app_len.get()){
            CommLogger.logv(TAG, "Next step");
            o_data.mn_app_index.set((short)0);
            // last step
            if (o_data.mn_appstep.get() ==  EAPP_PROCESS.e_prs_checksum.ordinal())
            {
                CommLogger.logv(TAG, "Last step");
                o_data.mn_appstep .set((short)EAPP_PROCESS.e_prs_checkheader.ordinal());
                o_data.mn_app_buffer[o_data.mn_app_datasum.get()].set((short)0);
                o_data.mn_app_datasum .set(calc_sum(o_data.mn_app_buffer, u_short_to_u_char( o_data.mn_app_datasum)));

                // warning: what's this for?
                //o_data.mn_app_buffer[o_data.mn_app_len.get()].set((short)0);

                CommLogger.logv(TAG, "o_data.mn_app_checksum=" + String.valueOf(o_data.mn_app_checksum.get()));
                CommLogger.logv(TAG, "o_data.mn_app_datasum=" + String.valueOf(o_data.mn_app_datasum.get()));
                if (o_data.mn_app_checksum.get() == o_data.mn_app_datasum.get()) {
                    CommLogger.logv(TAG, "parse success!");
                    app_event(o_data,o_data.mn_id,o_data.mn_app_cmdid.get(),o_data.mn_app_buffer,orig_data,acaiaScale);
                }
                o_data.mn_app_cmdid .set((short)0);
                o_data.mn_app_checksum .set((short)0);
                o_data.mn_app_datasum.set((short)0);
                return EDATA_RESULT.e_result_success.ordinal();
            }
            else
            {
                o_data.mn_appstep.set((short) (o_data.mn_appstep.get() + (short) 1));

            }
            o_data.mn_app_len .set(gn_len[o_data.mn_appstep.get()]);
        }
       // CommLogger.logv(TAG,"----------------");

        return EDATA_RESULT.e_result_tobecontinues.ordinal();

    }


}
