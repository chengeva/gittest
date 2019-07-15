package co.acaia.communications.protocol.ver20;

import static co.acaia.communications.protocol.ver20.ScaleProtocol.gn_cmd_len;
import static co.acaia.communications.protocol.ver20.ScaleProtocol.gn_len;
import static co.acaia.communications.protocol.ver20.ScaleProtocol.gs_header;

import co.acaia.communications.events.ScaleFirmwareVersionEvent;
import co.acaia.communications.scaleService.gatt.Log;
import javolution.io.Struct;
import android.content.Context;
import android.content.Intent;
import co.acaia.communications.CommLogger;
import co.acaia.communications.ScaleDataParser;
import co.acaia.communications.protocol.ver20.ScaleProtocol.EAPP_PROCESS;
import co.acaia.communications.protocol.ver20.ScaleProtocol.EDATA_RESULT;
import co.acaia.communications.protocol.ver20.ScaleProtocol.ack_event;
import co.acaia.communications.protocol.ver20.ScaleProtocol.app_prsdata;
import co.acaia.communications.reliableQueue.SendSuccessEvent;
import co.acaia.communications.scaleService.ScaleCommunicationService;
import co.acaia.communications.scaleevent.UpdateTimerStartPauseEvent;
import co.acaia.communications.scaleevent.UpdateTimerValueEvent;
import de.greenrobot.event.EventBus;


/**
 * Created by hanjord on 15/3/24.
 */
public class DataPacketParser {
    public static final String TAG="DataPacketParser";

    // weight filter
    // warning: will have problem on multiple scale connection
    public static int incommingPackt = 0;
    private static float previousWeight = 0;
    private static boolean minus5mode = false;

    // default event freq helper
    private  static int sent_default=0;


    private static void broadcastUpdate(final String action,Context context) {
        final Intent intent = new Intent(action);
        context.sendBroadcast(intent);
    }
    public static void ParseData(app_prsdata o_data,byte[] sdata,Context context, boolean isSette, boolean isCinco){
//        CommLogger.logv(TAG, "Testing parse packet...");
//        CommLogger.logv(TAG, "Testing parse packet length=" + sdata.length);


        for(int i=0;i!=sdata.length;i++){
            // CommLogger.logv(TAG,String.valueOf((int)sdata[i]));

            int parse_result= app_uartin(o_data,sdata[i],sdata,context, isSette,isCinco);
            if(parse_result==0){
                break;
            }else if(parse_result== EDATA_RESULT.e_result_error_char.ordinal()){
                break;
            }
            /*if(parse_result== EDATA_RESULT.e_result_error_char.ordinal()){
                CommLogger.logv(TAG,"Parse Error!");
                break;
            }else{
                CommLogger.logv(TAG,"Parse current="+String.valueOf(parse_result));
            }*/
        }
        //CommLogger.logv(TAG, "Testing parse packet end...");
    }

    public static void parse_eventmsg(Struct.Unsigned8[] s_param,byte [] orig_data,Context context){
        final Intent intent = new Intent(ScaleCommunicationService.ACTION_DATA_AVAILABLE);

        int ln_length=s_param.length;

        short ls_pt=1;
        short ls_process;
        while(ln_length>0){
            if(ls_pt==s_param.length)
                break;
            ls_process=s_param[ls_pt].get();
            if(ls_process== co.acaia.communications.protocol.ver20.ScaleProtocol.EEVENT.e_appevent_weight.ordinal()){
                CommLogger.logv(TAG, "EEVENT process weight event, s_param len=" + s_param.length);
                co.acaia.communications.protocol.ver20.ScaleProtocol.wt_event wtevent=new co.acaia.communications.protocol.ver20.ScaleProtocol.wt_event(ByteDataHelper.getByteArrayFromU1(s_param, ls_pt + 1, co.acaia.communications.protocol.ver20.ScaleProtocol.wt_event.getSize()));
                ls_pt+= co.acaia.communications.protocol.ver20.ScaleProtocol.APPEVENT_WEIGHT_LEN;
                ln_length -= co.acaia.communications.protocol.ver20.ScaleProtocol.APPEVENT_WEIGHT_LEN;
               // ScaleDataEvent scaleDataEvent=new ScaleDataEvent(ScaleDataEvent.data_weight,wtevent.getWeight());
                float weightVal =0;
                int unit=wtevent.getUnit();
                // hanjord warning: check weight empty error
                String error=String.valueOf(wtevent.getWeight());
                long val=wtevent.getWeight();
                CommLogger.logv(TAG,"weight error test="+error);
                CommLogger.logv(TAG,"weight unit="+String.valueOf(wtevent.getUnit()));
                CommLogger.logv(TAG,"weight="+String.valueOf(val));
                if(unit==4)
                    weightVal =(float)(val/10000.0);
                else if(unit==2)
                    weightVal =(float)(val/100.0);
                else if(unit==5)
                    weightVal =(float)(val/10000.0);
                else if(unit==1)
                    weightVal =(float)(val/100.0);
                String weightString = "";

                if (unit == 2) {
                    // gram
                    weightString = String.format("%.1f", weightVal);
                    intent.putExtra(ScaleCommunicationService.EXTRA_UNIT, ScaleCommunicationService.UNIT_GRAM);

                } else if(unit==4){
                    weightString = String.format("%.3f", weightVal);
                    intent.putExtra(ScaleCommunicationService.EXTRA_UNIT, ScaleCommunicationService.UNIT_OUNCE);
                }
                else if (unit == 1) {
                    // gram
                    weightString = String.format("%.1f", weightVal);
                    intent.putExtra(ScaleCommunicationService.EXTRA_UNIT, ScaleCommunicationService.UNIT_GRAM);

                } else if(unit==5){
                    weightString = String.format("%.3f", weightVal);
                    intent.putExtra(ScaleCommunicationService.EXTRA_UNIT, ScaleCommunicationService.UNIT_OUNCE);
                }

                    /*
                 * Added weight filter as IOS
				 *
				 * 1. Need 0 two times to show 0 value 2. Need <0 two times to
				 * show <0 value 3. Other illegal cases are implemented in JNI
				 * wrapper:
				 * Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_parsescalepacket
				 */

                boolean illegalDataDetected = false;
                if (weightVal == 9999) {
                    illegalDataDetected = true;
                }

                // hanjord modify 20140813
                if (isNearZero(previousWeight) == false
                        && isNearZero(weightVal) == true) {
                    illegalDataDetected = true;
                }

				/*
                 * // Ignore the first 0. Could be noise. // orange modify
				 * 20140725 // if(previousWeight!=0 && inputWeight==0) if([self
				 * isNearZero:previousWeight]==NO && [self
				 * isNearZero:inputWeight]==YES) { illegalDataDetected=YES; }
				 */
                if (previousWeight != 0 && weightVal == 0) {
                    illegalDataDetected = true;
                }

                previousWeight = weightVal;
                if (weightVal < -5) {
                    if (minus5mode == false) {
                        minus5mode = true;
                        illegalDataDetected = true;
                    }
                } else {
                    minus5mode = false;
                }

                if (previousWeight != 0 && weightVal == 0) {
                    illegalDataDetected = true;
                }

                if (weightVal < -3000 || weightVal > 3000) {
                    illegalDataDetected = true;
                }
                CommLogger.logv(TAG,"previous weight="+String.valueOf(previousWeight));
                if(!illegalDataDetected) {
                    CommLogger.logv6(TAG, "weight=" + weightString);
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA,
                            weightString);
                    intent.putExtra("value",
                            weightVal);
                    intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE,
                            ScaleCommunicationService.DATA_TYPE_WEIGHT);
                    context.sendBroadcast(intent);
                }else{
                    CommLogger.logv(TAG,"Illegal weight");
                }

            }else if(ls_process== co.acaia.communications.protocol.ver20.ScaleProtocol.EEVENT.e_appevent_battery.ordinal()){
                CommLogger.logv(TAG, "EEVENT e_appevent_battery event");
                ScaleDataParser.parse_battery_event(s_param[ls_pt + 1].get());
                float battval=(float)s_param[ls_pt + 1].get();
                intent.putExtra(ScaleCommunicationService.EXTRA_DATA,
                        battval);
                intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE,
                        ScaleCommunicationService.DATA_TYPE_BATTERY);
                context.sendBroadcast(intent);
                ln_length --;
                ls_pt++;

            }else if(ls_process== co.acaia.communications.protocol.ver20.ScaleProtocol.EEVENT.e_appevent_timer.ordinal()){
                CommLogger.logv(TAG, "EEVENT e_appevent_timer event");
                co.acaia.communications.protocol.ver20.ScaleProtocol.tm_event tmevent=new co.acaia.communications.protocol.ver20.ScaleProtocol.tm_event(ByteDataHelper.getByteArrayFromU1(s_param, ls_pt + 1, co.acaia.communications.protocol.ver20.ScaleProtocol.tm_event.getSize()));
                ScaleDataParser.parse_timer_event(tmevent);
                int time=(int)((float)tmevent.getSec()+(float)tmevent.getMin()*60+(float)tmevent.getDse()/10.0+0.2);
                EventBus.getDefault().post(new UpdateTimerValueEvent(time));
                ls_pt+= co.acaia.communications.protocol.ver20.ScaleProtocol.APPEVENT_TIMER_LEN;
                ln_length -= co.acaia.communications.protocol.ver20.ScaleProtocol.APPEVENT_TIMER_LEN;
            }else if(ls_process== co.acaia.communications.protocol.ver20.ScaleProtocol.EEVENT.e_appevent_key.ordinal()){
                CommLogger.logv(TAG, "EEVENT e_appevent_key event");
                ScaleDataParser.parse_key_event(s_param[ls_pt + 1].get());
                ls_pt++;
                ln_length --;
            }else if(ls_process== co.acaia.communications.protocol.ver20.ScaleProtocol.EEVENT.e_appevent_setting.ordinal()){
                // CommLogger.logv(TAG,"EEVENT e_appevent_setting event");

                //  ls_pt++;
            }else if(ls_process== co.acaia.communications.protocol.ver20.ScaleProtocol.EEVENT.e_appevent_ack.ordinal()){
                CommLogger.logv(TAG, "EEVENT e_appevent_ack event");
                ack_event ackEvent=new ack_event(ByteDataHelper.getByteArrayFromU1(s_param, ls_pt + 1, ack_event.getSize()));
                EventBus.getDefault().post(new SendSuccessEvent(0));
                ls_pt+=ScaleProtocol.APPEVENT_ACK_LEN;
                ln_length -=ScaleProtocol.APPEVENT_ACK_LEN;

            }
            ls_pt++;
            ln_length--;
        }
    }

    public static void app_event(app_prsdata o_data,byte n_cbid,int n_event,Struct.Unsigned8[] s_param,byte[] orig_data,Context context, boolean isCinco){

        // u1 ln_receiverid = -1;
        int ln_loop = 0;
        // warning: need to handle n_cbid
        // n_cbid
        CommLogger.logv(TAG, "n_event" + String.valueOf(n_event));
        if(n_event==ScaleProtocol.ECMD.e_cmd_info_a.ordinal()){
            CommLogger.logv(TAG, "n_event=e_cmd_info_a");
            DataOutHelper.sent_appid(o_data, "---------------".getBytes());

            co.acaia.communications.protocol.ver20.ScaleProtocol.scale_info scale_info_=new co.acaia.communications.protocol.ver20.ScaleProtocol.scale_info(ByteDataHelper.getByteArrayFromU1(s_param, 0, co.acaia.communications.protocol.ver20.ScaleProtocol.scale_info.getSize()));
            int ver=scale_info_.getVersion();
            EventBus.getDefault().post(new ScaleFirmwareVersionEvent(ver));

        }else if(n_event== ScaleProtocol.ECMD.e_cmd_status_a.ordinal()){
            CommLogger.logv(TAG, "n_event=e_cmd_status_a");
            String deb="";
            CommLogger.logv("packet_status_raw", deb);
            co.acaia.communications.protocol.ver20.ScaleProtocol.scale_status scaleStatus=new co.acaia.communications.protocol.ver20.ScaleProtocol.scale_status(ByteDataHelper.getByteArrayFromU1(s_param, 0, co.acaia.communications.protocol.ver20.ScaleProtocol.scale_status.getSize()));
            // PROCESS STATUS HERE
            sendIntent(context,ScaleCommunicationService.DATA_TYPE_BEEP,(float)scaleStatus.n_setting_sound.get());
            sendIntent(context,ScaleCommunicationService.DATA_TYPE_KEY_DISABLED_ELAPSED_TIME,(float)scaleStatus.n_setting_keydisable.get());
            sendIntent(context,ScaleCommunicationService.DATA_TYPE_AUTO_OFF_TIME,(float)scaleStatus.n_setting_sleep.get());
            sendIntent(context, ScaleCommunicationService.DATA_TYPE_BATTERY, (float) scaleStatus.n_battery.get());
            
            if(sent_default==0) {
                CommLogger.logv(TAG,"default event!");
                sent_default=1;
                DataOutHelper.default_event();
            }
            sent_default++;

        }else if(n_event== ScaleProtocol.ECMD.e_cmd_event_sa.ordinal()){
            CommLogger.logv(TAG, "n_event=e_cmd_event_sa");
            parse_eventmsg(s_param,orig_data,context);
            // Parse the scale's event

        }else if(n_event== ScaleProtocol.ECMD.e_cmd_str_sa.ordinal()){
            CommLogger.logv(TAG, "n_event=e_cmd_str_sa");
        }

    }

    private static void sendIntent(Context context,final int type,final float val){
        final Intent intent = new Intent(ScaleCommunicationService.ACTION_DATA_AVAILABLE);
        intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE,type );
        intent.putExtra(ScaleCommunicationService.EXTRA_DATA,val );
        context.sendBroadcast(intent);
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

    public static int app_uartin(app_prsdata o_data,byte s_in,byte[] orig_data,Context context, boolean isSette, boolean isCinco){
        int mn_appstep=o_data.mn_appstep.get();
//          CommLogger.logv(TAG,"----------------");
//          CommLogger.logv(TAG,"mn_appstep = "+String.valueOf(mn_appstep)+", val="+String.valueOf(s_in));
        // Log.v(TAG,"mn_appstep = "+String.valueOf(mn_appstep)+", o_data.mn_app_datasum val="+String.valueOf( o_data.mn_app_datasum));

        if(mn_appstep== ScaleProtocol.EAPP_PROCESS.e_prs_checkheader.ordinal()){
            //  CommLogger.logv(TAG,"e_prs_checkheader="+String.valueOf(ByteDataHelper.getUnsignedByte(s_in)));
            //CommLogger.logv(TAG,"gs_header[o_data.mn_app_index.get()]="+String.valueOf(gs_header[o_data.mn_app_index.get()]));
            if(o_data.mn_app_index.get()>=gs_header.length){
                o_data.mn_app_index .set((short)0);
                return ScaleProtocol.EDATA_RESULT.e_result_error_char.ordinal();
            }
            if (ByteDataHelper.getUnsignedByte(s_in) != gs_header[o_data.mn_app_index.get()])
            {
                o_data.mn_app_index .set((short)0);
                return ScaleProtocol.EDATA_RESULT.e_result_error_char.ordinal();
            }
        }else if(mn_appstep==  ScaleProtocol.EAPP_PROCESS.e_prs_cmdid.ordinal()){
            o_data.mn_app_cmdid.set((short) ByteDataHelper.getUnsignedByte(s_in));
        }else if(mn_appstep==  ScaleProtocol.EAPP_PROCESS.e_prs_cmddata.ordinal()){
            if (o_data.mn_app_index.get()== 0)	// the first time
            {
                // Hanjord 20180612
                // Added protection for overflow
                if(o_data.mn_app_cmdid.get() >= gn_cmd_len.length){
                    o_data.mn_app_index .set((short)0);
                    return ScaleProtocol.EDATA_RESULT.e_result_error_char.ordinal();
                }
                o_data.mn_app_len .set(gn_cmd_len[o_data.mn_app_cmdid.get()]);

                /*if (o_data.has_init.get() == 0) {
                    o_data.mn_app_len .set(gn_cmd_len[o_data.mn_app_cmdid.get()]);
                } else {
                    o_data.mn_app_len .set(FellowEKG.gn_cmd_len[o_data.mn_app_cmdid.get()]);
                }*/


                if (o_data.mn_app_len.get() == 255)
                    o_data.mn_app_len.set( s_in);
            }
            // parse command
            if(o_data.mn_app_index.get()< o_data.mn_app_buffer.length) {
                o_data.mn_app_buffer[o_data.mn_app_index.get()].set((short) ByteDataHelper.getUnsignedByte(s_in));
                o_data.mn_app_datasum.set((short) (o_data.mn_app_datasum.get() + (short) 1));
            }else{
                // got error, reset
                init_app_prs_data(o_data);
                return 0;
            }

        }else if(mn_appstep==  ScaleProtocol.EAPP_PROCESS.e_prs_checksum.ordinal()){
            // o_data->mn_app_checksum = (o_data->mn_app_checksum << 8) + (u1)s_in;
            o_data.mn_app_checksum.set((short)(ByteDataHelper.left_shift_8(o_data.mn_app_checksum.get())+ ByteDataHelper.getUnsignedByte(s_in)));
        }

        o_data.mn_app_index.set( (short)(o_data.mn_app_index.get()+(short)1));
        // CommLogger.logv(TAG,"o_data.mn_app_index="+String.valueOf(o_data.mn_app_index.get()));
        //  CommLogger.logv(TAG,"o_data->mn_app_len="+String.valueOf(o_data.mn_app_len.get()));
        // next step
        if(o_data.mn_app_index.get()==o_data.mn_app_len.get()){
            //CommLogger.logv(TAG,"Next step");
            o_data.mn_app_index.set((short)0);
            // last step
            if (o_data.mn_appstep.get() ==  ScaleProtocol.EAPP_PROCESS.e_prs_checksum.ordinal())
            {
                //  CommLogger.logv(TAG,"Last step");
                o_data.mn_appstep .set((short) ScaleProtocol.EAPP_PROCESS.e_prs_checkheader.ordinal());

                o_data.mn_app_datasum .set(ByteDataHelper.calc_sum(o_data.mn_app_buffer, ByteDataHelper.u_short_to_u_char(o_data.mn_app_datasum)));

                // warning: what's this for?
                //o_data.mn_app_buffer[o_data.mn_app_len.get()].set((short)0);

                //CommLogger.logv(TAG,"o_data.mn_app_checksum="+String.valueOf(o_data.mn_app_checksum.get()));
                // CommLogger.logv(TAG,"o_data.mn_app_datasum="+String.valueOf(o_data.mn_app_datasum.get()));
                if (o_data.mn_app_checksum.get() == o_data.mn_app_datasum.get()) {
                    // CommLogger.logv(TAG,"parse success!");
                    app_event(o_data,o_data.mn_id,o_data.mn_app_cmdid.get(),o_data.mn_app_buffer,orig_data,context,isCinco);

                }else{
                    Log.v("DataPacketParser","Check sum error!");
                }
                o_data.mn_app_cmdid .set((short)0);
                o_data.mn_app_checksum .set((short)0);
                o_data.mn_app_datasum.set((short)0);
                return ScaleProtocol.EDATA_RESULT.e_result_success.ordinal();
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


    public static void init_app_prs_data( app_prsdata o_data){
        sent_default=0;
     /*   u1 gn_appstep = e_prs_checkheader;
        u1 gn_app_index = 0;
        u1 gn_app_len = 2;
        u1 gn_app_cmdid = 0;
        u1 gn_app_buffer[20];
        u1 gn_app_ack = 0;
        u2 gn_app_checksum = 0;
        u2 gn_app_datasum =0;*/
        o_data.mn_id = -1;
        o_data.mn_appstep .set( (short) EAPP_PROCESS.e_prs_checkheader.ordinal());
        o_data.mn_app_index .set((short)0);
        o_data.mn_app_len .set((short) gn_len[EAPP_PROCESS.e_prs_checkheader.ordinal()]);
        o_data.mn_app_cmdid .set((short)0);
        o_data.mn_app_checksum .set((short)0);
        o_data.mn_app_datasum .set((short)0);

    }

    private static Boolean isNearZero(float floatValue) {
        if (floatValue <= 1.0 && floatValue >= -1.0)
            return true;
        else
            return false;
    }
}
