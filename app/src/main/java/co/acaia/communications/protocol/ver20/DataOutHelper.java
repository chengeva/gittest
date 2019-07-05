package co.acaia.communications.protocol.ver20;

import co.acaia.communications.CommLogger;
import co.acaia.communications.events.SendDataEvent;
import de.greenrobot.event.EventBus;
import javolution.io.Struct;

/**
 * Created by hanjord on 15/3/12.
 */

public class DataOutHelper {
    public static final String TAG="DataOutHelper";
    public static byte [] heartBeat(){
        output_struct out = new output_struct();
        ls_action_struct action=new ls_action_struct();
        action.ls_action[0].set((short)2);
        action.ls_action[1].set((short) ScaleProtocol.ESCALE_SYSTEM_MSG.e_systemmsg_alive.ordinal());
        sr_len_struct srLenStruct = new sr_len_struct();
        srLenStruct.sr_len.set((short) 2);
        int ln_count = pack_data(out.ls_out, (short) ScaleProtocol.ECMD.e_cmd_system_sa.ordinal(), action.ls_action, srLenStruct.sr_len);
     /*   out.ls_out[0].set((short)239);
        out.ls_out[1].set((short)221);
        out.ls_out[3].set((short)2);
        out.ls_out[5].set((short)2);
        out.ls_out[7].set((short)53);
        out.ls_out[8].set((short)151);
        out.ls_out[9].set((short)1);
        out.ls_out[13].set((short)168);
        out.ls_out[14].set((short)214);
        out.ls_out[15].set((short)220);
        out.ls_out[16].set((short)111);
        out.ls_out[17].set((short)1);*/
        byte[] outt=u1_array_to_byte_array_withlen(out.ls_out,ln_count);
        for(int i=0;i!=outt.length;i++){
            //CommLogger.logv(TAG,"alive ["+String.valueOf(i)+"]="+String.valueOf(outt[i]));
        }

        return outt;
    }


    /**
     *
     2015-03-27 00:51:09.422 CoffeeScale[522:133051] me alive[0]=239
     2015-03-27 00:51:09.422 CoffeeScale[522:133051] me alive[1]=221
     2015-03-27 00:51:09.423 CoffeeScale[522:133051] me alive[2]=0
     2015-03-27 00:51:09.423 CoffeeScale[522:133051] me alive[3]=2
     2015-03-27 00:51:09.424 CoffeeScale[522:133051] me alive[4]=0
     2015-03-27 00:51:09.424 CoffeeScale[522:133051] me alive[5]=2
     2015-03-27 00:51:09.424 CoffeeScale[522:133051] me alive[6]=0
     2015-03-27 00:51:09.425 CoffeeScale[522:133051] me alive[7]=53
     2015-03-27 00:51:09.425 CoffeeScale[522:133051] me alive[8]=151
     2015-03-27 00:51:09.426 CoffeeScale[522:133051] me alive[9]=1
     2015-03-27 00:51:09.426 CoffeeScale[522:133051] me alive[10]=0
     2015-03-27 00:51:09.427 CoffeeScale[522:133051] me alive[11]=0
     2015-03-27 00:51:09.427 CoffeeScale[522:133051] me alive[12]=0
     2015-03-27 00:51:09.428 CoffeeScale[522:133051] me alive[13]=168
     2015-03-27 00:51:09.428 CoffeeScale[522:133051] me alive[14]=214
     2015-03-27 00:51:09.428 CoffeeScale[522:133051] me alive[15]=220
     2015-03-27 00:51:09.429 CoffeeScale[522:133051] me alive[16]=111
     2015-03-27 00:51:09.429 CoffeeScale[522:133051] me alive[17]=1
     2015-03-27 00:51:09.430 CoffeeScale[522:133051] me alive[18]=0
     2015-03-27 00:51:09.430 CoffeeScale[522:133051] me alive[19]=0
     2015-03-27 00:51:09.431 CoffeeScale[522:133051] me alive[20]=0
     */
    public static byte[] setting_chg(short n_item,short n_value){
        output_struct out = new output_struct();
        ScaleProtocol.cmd_setting cmdsetting=new ScaleProtocol.cmd_setting((short)0,n_item,n_value);
        sr_len_struct srLenStruct = new sr_len_struct();
        srLenStruct.sr_len.set((short) 3);
        int ln_count = pack_data(out.ls_out, (short) ScaleProtocol.ECMD.e_cmd_setting_chg_s.ordinal(), cmdsetting.getByteArray(), srLenStruct.sr_len);
       // debug_byte("send setting",u1_array_to_byte_array(out.ls_out));
        return u1_array_to_byte_array(out.ls_out);
    }

    public static byte[] app_command(short n_cmd) {
        output_struct out = new output_struct();
        sr_len_struct srLenStruct = new sr_len_struct();
        srLenStruct.sr_len.set((short) 1);
        ls_action_struct action = new ls_action_struct();
        int ln_count = pack_data(out.ls_out, n_cmd, action.ls_action, srLenStruct.sr_len);
        return u1_array_to_byte_array(out.ls_out);
    }

    public static byte[] timer_action(short n_action){
        output_struct out=new output_struct();
        ls_action_struct action=new ls_action_struct();
        // warning: need to implement ack key
        action.ls_action[0].set((short)0);
        action.ls_action[1].set((short) n_action);
        sr_len_struct srLenStruct=new sr_len_struct();
        srLenStruct.sr_len.set((short)2);
        int ln_count=pack_data(out.ls_out, (short) ScaleProtocol.ECMD.e_cmd_timer_s.ordinal(),action.ls_action,srLenStruct.sr_len);
        return u1_array_to_byte_array(out.ls_out);
    }


    public static void default_event(){
        CommLogger.logv(TAG, "send default event!");
        output_struct out=new output_struct();
        ls_data_struct ls_data=new ls_data_struct();
        short ln_count=1,ln_len=0;
        ln_count+=pack_event(ls_data.ls_data,ln_count, (short)(ScaleProtocol.EEVENT.e_scalevent_weight.ordinal()),(short)1);
        ln_count+=pack_event(ls_data.ls_data,ln_count, (short)(ScaleProtocol.EEVENT.e_scalevent_battery.ordinal()),(short)2);
        ln_count+=pack_event(ls_data.ls_data,ln_count, (short)(ScaleProtocol.EEVENT.e_scalevent_timer.ordinal()),(short)5);
        ln_count+=pack_event(ls_data.ls_data,ln_count, (short)(ScaleProtocol.EEVENT.e_scalevent_key.ordinal()),(short)0);
        ln_count+=pack_event(ls_data.ls_data,ln_count, (short)(ScaleProtocol.EEVENT.e_scalevent_setting.ordinal()),(short)0);

        ls_data.ls_data[0].set( ln_count);
        sr_len_struct srLenStruct=new sr_len_struct();
        srLenStruct.sr_len.set(ln_count);

        pack_data(out.ls_out,  (short)(ScaleProtocol.ECMD.e_cmd_event_sa.ordinal()), ls_data.ls_data, srLenStruct.sr_len);
        EventBus.getDefault().post(new SendDataEvent(u1_array_to_byte_array(out.ls_out)));


    }

    private static void debug_byte(String tag,byte[] in){
        for(int i=0;i!=in.length;i++){
            CommLogger.logv(TAG, tag + " [" + String.valueOf(i) + "]=" + String.valueOf(in[i]));
        }
    }
    public static Boolean sent_appid(ScaleProtocol.app_prsdata o_data,byte[] s_id){
        output_struct out=new output_struct();
      //  pack_data(ls_out, e_cmd_identify_s, (u1*)s_id, sr_strlen(s_id,15));
        output_struct s_out=new output_struct();
        for(int i=0;i!=s_id.length;i++){
            if(i==s_out.ls_out.length){
                break;
            }else{
                s_out.ls_out[i].set(s_id[i]);
            }
        }
        sr_len_struct sr_len=new sr_len_struct();
        sr_len.sr_len.set((short) sr_strlen(s_id, 15));
        pack_data(out.ls_out, (short) ScaleProtocol.ECMD.e_cmd_identify_s.ordinal(), s_out.ls_out, sr_len.sr_len);
        EventBus.getDefault().post(new SendDataEvent(u1_array_to_byte_array(out.ls_out)));
        return true;
    }

    // hanjord: add start isp

    public static void start_isp(){
        output_struct out=new output_struct();
        output_struct s_out=new output_struct();
        sr_len_struct sr_len=new sr_len_struct();
        for(int i=0;i!=15;i++){
            if(i==s_out.ls_out.length){
                break;
            }else{
                s_out.ls_out[i].set((short) 0);
            }
        }
        sr_len.sr_len.set((short) 15);
        pack_data(out.ls_out, (short) ScaleProtocol.ECMD.e_cmd_isp_s.ordinal(), s_out.ls_out, sr_len.sr_len);

        EventBus.getDefault().post(new SendDataEvent(u1_array_to_byte_array(out.ls_out)));
    }
    public static byte[] u1_array_to_byte_array_withlen(Struct.Unsigned8[] s_in,int len){
        byte[] out=new byte[len];
        for(int i=0;i!=len;i++){
            out[i]=(byte)s_in[i].get();
        }
        return out;
    }

    public static byte[] u1_array_to_byte_array(Struct.Unsigned8[] s_in){
        byte[] out=new byte[s_in.length];
        for(int i=0;i!=out.length;i++){
            out[i]=(byte)s_in[i].get();
        }
        return out;
    }

    static int sr_strlen(byte[] s_src, int n_max_size)
    {
        int ln_loop = 0;
        for(ln_loop = 0; ln_loop < n_max_size; ln_loop++)
        {
            if (s_src[ln_loop] == 0)
                break;
        }
        return ln_loop;
    }

    public static int pack_event(Struct.Unsigned8[] s_out,short start,short n_event_id,short  s_param){
        short  ln_argc = 0; // event id
        Struct.Unsigned8[] lp_out=s_out;
        short pt=start;

        lp_out[pt].set(n_event_id);
        pt++;
        ln_argc= ScaleProtocol.gn_event_len[n_event_id];
        if(ln_argc==1){
            // problem
            // *lp_out++ = (u1)s_param;

            lp_out[pt].set(s_param);
            pt++;
            //  CommLogger.logv();
        }else{

        }
        return 1 + ln_argc;
    }



    public static int pack_data(Struct.Unsigned8[] s_out,short n_cmd_id,Struct.Unsigned8[] s_data,Struct.Unsigned8 n_len){
        Struct.Unsigned8[] lp_out=s_out;
        sum_struct sum=new sum_struct();
        sum.ln_sum.set((short)0);
        short pt=0;
        lp_out[pt].set(ScaleProtocol.gs_header[0]);
        pt++;
        lp_out[pt].set(ScaleProtocol.gs_header[1]);
        pt++;
        lp_out[pt].set(n_cmd_id);
        pt++;

        sr_memcpy(lp_out, pt,s_data, n_len.get());
        sum.ln_sum.set(ByteDataHelper.calc_sum(s_data, n_len));
        lp_out[pt+n_len.get()].set((short)(sum.ln_sum.get()>>8));
        lp_out[pt+n_len.get()+1].set((short)(sum.ln_sum.get()& 0xff));
        for(int i=0;i!=lp_out.length;i++){
            CommLogger.logv(TAG, "lp_out[" + String.valueOf(i) + "]=" + String.valueOf(lp_out[i]));
        }
       /* for(int i=0;i!=s_data.length;i++){
            CommLogger.logv(TAG,"s_data["+String.valueOf(i)+"]="+String.valueOf(s_data[i]));
        }*/
        return 5 + n_len.get();

    }


  public static void sr_memcpy(Struct.Unsigned8[] ls_dest ,short start ,Struct.Unsigned8[] ls_src, short n_len)
    {
           short ln_loop = 0;
        // length must bigger than 0. we do nothing when source is the same as destination.
        if (n_len <= 0)
            return;
        CommLogger.logv(TAG, "start=" + String.valueOf(start));
        for (ln_loop = start; ln_loop < n_len+start; ln_loop++) {
            ls_dest[ln_loop].set(ls_src[ln_loop-start].get());
        }

    }

    public static class sr_len_struct extends Struct {
        public Unsigned8  sr_len=new Unsigned8();
    }
    public static class sum_struct extends Struct {
        public Unsigned16 ln_sum=new Unsigned16();
    }
    public static class ls_action_struct extends Struct{
        public Unsigned8[] ls_action=new Unsigned8[]{
                new Unsigned8(), new Unsigned8(),
        };
    }
    public static class ls_data_struct extends Struct {
        public  Unsigned8[] ls_data = new Unsigned8[] {
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8() };
    }
    public static class output_struct extends Struct {
        public  Unsigned8[] ls_out = new Unsigned8[] { new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(), new Unsigned8(), new Unsigned8(),
                new Unsigned8(),new Unsigned8() };
    }

}
