package co.acaia.acaiaupdater.firmwarelunar;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import co.acaia.acaiaupdater.AcaiaUpdater;
import co.acaia.acaiaupdater.Events.DeviceOKEvent;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.communications.CommLogger;
import co.acaia.communications.protocol.ver20.ByteDataHelper;
import co.acaia.communications.protocol.ver20.DataOutHelper;
import co.acaia.communications.scaleService.ScaleCommunicationService;
import co.acaia.acaiaupdater.Events.UpdateEraseProgress;
import co.acaia.acaiaupdater.Events.UpdateProgress;
import co.acaia.acaiaupdater.Events.UpdateStatusEvent;
import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.rawfile.RawFileHelper;
import de.greenrobot.event.EventBus;

import static co.acaia.acaiaupdater.firmwarelunar.Isp.ISP_INFO_LENGTH;
import static co.acaia.acaiaupdater.firmwarelunar.IspHelper.ISP_CHECK_ISP;

/**
 * Created by hanjord on 15/4/14.
 */
public class FileHandler {
    public static final String TAG = "FileHandler";

    public static Boolean open_file(Context context, File file, CISP_handler cisp_handler) {
        int ln_loop = 0, ln_len = 0, ln_lastpage = 0;
        Boolean b_last = false;

        if (file == null)
            return false;
        cisp_handler.mn_filelen = file.length();
        CommLogger.logv(TAG,"mn_filelen1 ="+String.valueOf(cisp_handler.mn_filelen));
       //NSLog(@"mn_filelen1 = %d", mn_filelen);
        ln_lastpage = (int) (cisp_handler.mn_filelen / 256);
        if ((cisp_handler.mn_filelen & 0xff) == 0 && cisp_handler.mn_filelen != 0)
            ln_lastpage--;
        int count=0;
        try {
            Log.v(TAG,"file path"+file.getAbsolutePath());
            FileInputStream i = new FileInputStream(file);
            RandomAccessFile rfile = new RandomAccessFile(file, "r");
            while (true) {
                //NSLog(@"step = %d", count);
                CommLogger.logv(TAG,"step ="+String.valueOf(count));


                byte[] b = new byte[256];
                // i.getChannel().position(ln_lastpage << 8);

                rfile.seek( ln_lastpage << 8);
                CommLogger.logv4(TAG,"seek"+String.valueOf( ln_lastpage << 8));
               // i.read(b, ln_lastpage << 8, b.length);
                rfile.read(b);
                ln_len=(int)rfile.length()-(ln_lastpage << 8);
               // NSLog(@"ln_len = %d", ln_len);
                if(ln_len>=256)
                    ln_len=256;
                CommLogger.logv4(TAG,"ln_len ="+String.valueOf(ln_len));
                for (ln_loop = ln_len - 1; ln_loop >= 0; ln_loop--) {
                    if (b[ln_loop] != 0xff) {
                        b_last = true;
                        break;
                    }
                }
                if (b_last == true) {
                    cisp_handler.mn_filelen = ln_loop + (ln_lastpage << 8) + 1;
                    cisp_handler.mn_total_page = ln_lastpage + 1;
                    break;
                }
                ln_lastpage--;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
       // NSLog(@"mn_filelen = %d", mn_filelen);
        //NSLog(@"mn_total_page = %d", mn_total_page);
        CommLogger.logv4(TAG,"mn_filelen ="+String.valueOf(cisp_handler.mn_filelen));
        CommLogger.logv4(TAG,"mn_total_page = ="+String.valueOf(cisp_handler.mn_total_page));

        return true;
    }

    public static Boolean test_open_file(Context context, File file) {
        int ln_loop = 0, ln_len = 0, ln_lastpage = 0;
        Boolean b_last = false;

        if (file == null)
            return false;
        long mn_filelen = file.length();
        long mn_total_page;

        CommLogger.logv(TAG,"mn_filelen1 ="+String.valueOf(mn_filelen));
        //NSLog(@"mn_filelen1 = %d", mn_filelen);
        ln_lastpage = (int) (mn_filelen / 256);
        if ((mn_filelen & 0xff) == 0 && mn_filelen != 0)
            ln_lastpage--;
        int count=0;
        try {
            Log.v(TAG,"file path"+file.getAbsolutePath());
            FileInputStream i = new FileInputStream(file);
            RandomAccessFile rfile = new RandomAccessFile(file, "r");
            while (true) {
                //NSLog(@"step = %d", count);
                CommLogger.logv(TAG,"step ="+String.valueOf(count));


                byte[] b = new byte[256];
                // i.getChannel().position(ln_lastpage << 8);

                rfile.seek( ln_lastpage << 8);
                CommLogger.logv4(TAG,"seek"+String.valueOf( ln_lastpage << 8));
                // i.read(b, ln_lastpage << 8, b.length);
                rfile.read(b);
                ln_len=(int)rfile.length()-(ln_lastpage << 8);
                // NSLog(@"ln_len = %d", ln_len);
                if(ln_len>=256)
                    ln_len=256;
                CommLogger.logv4(TAG,"ln_len ="+String.valueOf(ln_len));
                for (ln_loop = ln_len - 1; ln_loop >= 0; ln_loop--) {
                    if (b[ln_loop] != 0xff) {
                        b_last = true;
                        break;
                    }
                }
                if (b_last == true) {
                    mn_filelen = ln_loop + (ln_lastpage << 8) + 1;
                    mn_total_page = ln_lastpage + 1;
                    break;
                }
                ln_lastpage--;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // NSLog(@"mn_filelen = %d", mn_filelen);
        //NSLog(@"mn_total_page = %d", mn_total_page);
        CommLogger.logv4(TAG,"mn_filelen ="+String.valueOf(mn_filelen));
        CommLogger.logv4(TAG,"mn_total_page = ="+String.valueOf(mn_total_page));

        return true;
    }

    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }


    public static void net_event(CISP_handler cisp_handler, ScaleCommunicationService mScaleCommunicationService, String modelName){
        CommLogger.logv2(TAG, "net event!  cisp_handler.mn_app_cmdid=" + String.valueOf(cisp_handler.mn_app_cmdid));
        int ln_len;
        DataOutHelper.sr_len_struct srLenStruct = new DataOutHelper.sr_len_struct();
        srLenStruct.sr_len.set((short) Isp.PAGE_INFO_SIZE);
        DataOutHelper.output_struct output_struct_=new DataOutHelper.output_struct();
        Isp.page_header lo_header=new Isp.page_header();
        Isp.page_info lo_page=new Isp.page_info();

        if(cisp_handler.mn_app_cmdid== Isp.EISPCMD.e_ispcmd_info_a.ordinal()){
            AcaiaUpdater.ispHelper.isISP=ISP_CHECK_ISP;

            // obtain ISP info and check device valid...
            for(int i=0;i!=7;i++){
                Log.v(TAG,"ISP data="+String.valueOf(i)+" "+String.valueOf(cisp_handler.mn_app_buffer[i].get()));
            }
            Isp.isp_info isp_info=new Isp.isp_info(ByteDataHelper.getByteArrayFromU1(cisp_handler.mn_app_buffer,0,ISP_INFO_LENGTH));
            //isp_info.memcpy(cisp_handler.mn_app_buffer);


            ArrayList<Integer> validISPs= AcaiaDevice.getValidISPFromModelName(modelName);
            boolean checkISP=false;
            for (int i=0;i!=validISPs.size();i++){
                if(validISPs.get(i)==isp_info.n_ISP_version.get()){
                    checkISP=true;
                    EventBus.getDefault().post(new DeviceOKEvent());
                }
            }

            Log.v(TAG,"ISP version=="+String.valueOf(isp_info.n_ISP_version)+" "+String.valueOf(checkISP)+" "+String.valueOf(cisp_handler.mb_started));
            if(checkISP==true && cisp_handler.mb_started==true){
                Log.v(TAG,"device check ok!=="+String.valueOf(isp_info.n_ISP_version));
                lo_page.n_firm_main_ver .set((short)1);
                lo_page.n_firm_sub_ver .set((short)1);
                // TODO: check 'T' char int val
                lo_page.n_firm_add_ver .set((short)84);
                lo_page.n_firm_page .set((short)cisp_handler.mn_total_page);

                ln_len = DataOutHelper.pack_data(output_struct_.ls_out,(short)Isp.EISPCMD.e_ispcmd_start_s.ordinal(),lo_page.getConvertedByteArray(),srLenStruct.sr_len);
                byte[] outt=DataOutHelper.u1_array_to_byte_array_withlen(output_struct_.ls_out,ln_len);

                byte[] out=new byte[ln_len];
                for(int i=0;i!=out.length;i++){
                    out[i]=outt[i];
                }
                mScaleCommunicationService.sendCmdFromQueue(out);
            }else{
                // Handle invalid device
            }

        }else if( cisp_handler.mn_app_cmdid== Isp.EISPCMD.e_ispcmd_erase_page_a.ordinal() && cisp_handler.mb_started==true){
            //DataOutHelper.sr_memcpy(lo_page.buffer,(short)0,cisp_handler.mn_app_buffer,(short)4);
           // lo_page.setDataFromBuf();;
            lo_page.memcpy(cisp_handler.mn_app_buffer);
            // TODO: check mem copy
            // TODO: update erace process
           // DataOutHelper.sr_memcpy(lo_page,(short)0,cisp_handler.mn_app_buffer,(short)4);
            //memcpy(&lo_page, mn_app_buffer, 4);

            float process= lo_page.n_firm_page.get() * 100/ (0xF6);
            CommLogger.logv4(TAG,"erase_process="+String.valueOf(process));
            EventBus.getDefault().post(new UpdateEraseProgress(process));
           // [mo_delegate erase_process:0xF6 * 100/ (mn_total_page) ];
        }else if( cisp_handler.mn_app_cmdid== Isp.EISPCMD.e_ispcmd_transok_a.ordinal()){
            // TODO: close trans
            CommLogger.logv(TAG,"e_ispcmd_transok_a");
            EventBus.getDefault().post(new UpdateProgress(100));
            EventBus.getDefault().post(new UpdateStatusEvent(UpdateStatusEvent.ISPCompletedState));
            // hanjord: add transfer complete

        }else if( cisp_handler.mn_app_cmdid== Isp.EISPCMD.e_ispcmd_pageask_a.ordinal() && cisp_handler.mb_started==true){
            //DataOutHelper.sr_memcpy(lo_page.buffer,(short)0,cisp_handler.mn_app_buffer,(short)4);
            //lo_page.setDataFromBuf();;
            lo_page.memcpy(cisp_handler.mn_app_buffer);
            CommLogger.logv4(TAG, "scale ask for page #!" + String.valueOf(lo_page.n_firm_page.get()));
            // page correct

            try {
                pack_fileheader(lo_page.n_firm_page.get(),lo_header,cisp_handler);
                ln_len = DataOutHelper.pack_data(output_struct_.ls_out,(short)Isp.EISPCMD.e_ispcmd_pageheader_s.ordinal(),lo_header.getByteArray(),srLenStruct.sr_len);
                byte[] outt=DataOutHelper.u1_array_to_byte_array_withlen(output_struct_.ls_out,ln_len);
                for(int i=0;i!=outt.length;i++){
                    CommLogger.logv2(TAG, "header out [" + String.valueOf(i) + "]=" + String.valueOf(outt[i]));
                }
                isp_output(outt,ln_len,mScaleCommunicationService);


                handle_trans_file(lo_header.n_firm_page.get(),cisp_handler,mScaleCommunicationService);
                double process=(cisp_handler.mn_total_page - lo_page.n_firm_page.get()) *100 / cisp_handler.mn_total_page;
                CommLogger.logv5(TAG,"process"+String.valueOf(process));
                EventBus.getDefault().post(new UpdateProgress(process));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void handle_trans_file(short n_page,CISP_handler cisp_handler,ScaleCommunicationService scaleCommunicationService) throws IOException {

        int ln_len = 0, ln_counter;
        DataOutHelper.output_struct output_struct_=new DataOutHelper.output_struct();
        int ln_loop = 0;

        ln_counter = 0;
        //RandomAccessFile rfile = new RandomAccessFile(cisp_handler.mo_file, "r");
        byte[] lp_filepos= cisp_handler.ms_filedata;
        for(int k=0;k!=lp_filepos.length;k++) {
            CommLogger.logv(TAG, "lp_filepos [" + String.valueOf(k) + "]=" + String.valueOf(lp_filepos[k]));
        }
        ln_len=lp_filepos.length;

        CommLogger.logv(TAG,"ln_len ="+String.valueOf(ln_len));

        for (ln_loop = ln_len-1 ; ln_loop >= 0; ln_loop --)
        {
            CommLogger.logv(TAG,"handle_trans_file ["+String.valueOf(ln_loop)+"]="+String.valueOf(lp_filepos[ln_loop]));
            output_struct_.ls_out[ln_counter++].set(lp_filepos[ln_loop]);
            if (ln_counter == 20)
            {
                byte[] outt=DataOutHelper.u1_array_to_byte_array_withlen(output_struct_.ls_out,ln_counter);
                for(int k=0;k!= outt.length;k++) {
                    CommLogger.logv(TAG, "handle_trans_file_out1 [" + String.valueOf(k) + "]=" + String.valueOf(outt[k]));
                }
                isp_output(outt,ln_counter,scaleCommunicationService);
                ln_counter = 0;

            }
        }
        byte[] outt=DataOutHelper.u1_array_to_byte_array_withlen(output_struct_.ls_out,ln_counter);

        if (ln_counter != 0){
            for(int k=0;k!= outt.length;k++) {
                CommLogger.logv(TAG, "handle_trans_file_out [" + String.valueOf(k) + "]=" + String.valueOf(outt[k]));
            }
            isp_output(outt,ln_counter,scaleCommunicationService);
        }


    }

    private static void isp_output(byte[] data,int n_len,ScaleCommunicationService scaleCommunicationService){
        CommLogger.logv5(TAG,"isp_output len="+String.valueOf(data.length));
        int ln_len = n_len;
        int ls_sentpos=0;
        while(ln_len>20){
            scaleCommunicationService.sendCmdWithLength(data,20);

            ls_sentpos+=20;
            ln_len-=20;
        }
        byte[] next=new byte[ln_len];
        int ct=0;
        for(int i=ls_sentpos;i!=n_len;i++){
            next[ct]=data[i];
            ct++;
            if(ct>next.length)
                break;
        }
        scaleCommunicationService.sendCmdWithLength(next,next.length);
    }


    public static void pack_fileheader(long n_pageid,Isp.page_header o_header,CISP_handler cisp_handler) throws IOException {
        CommLogger.logv3(TAG, "Pack header start, " + String.valueOf(n_pageid));
        Boolean lb_odd = true;
        int ln_loop = 0,ln_len = 0;
        int ln_checksum1 = 0;
        int ln_checksum2 = 0;

        RandomAccessFile rfile = new RandomAccessFile(cisp_handler.mo_file, "r");
        long  ln_sentpage = n_pageid;
        if (cisp_handler.mn_filelen < (ln_sentpage  << 8))
            ln_sentpage = ((cisp_handler.mn_filelen - 1) >> 8) + 1;
        CommLogger.logv3(TAG,"ln_sentpage= "+String.valueOf(ln_sentpage ));
        CommLogger.logv3(TAG,"seek"+String.valueOf((n_pageid - 1) << 8));
        CommLogger.logv3(TAG,"length - seek"+String.valueOf(cisp_handler.mn_filelen-((n_pageid - 1) << 8)));
        rfile.seek((n_pageid - 1) << 8);
        if((int)cisp_handler.mn_filelen-(int)((n_pageid - 1) << 8)<256){
            ln_len=(int)cisp_handler.mn_filelen-(int)((n_pageid - 1) << 8);
        }else{
            ln_len=256;
        }
        cisp_handler.ms_filedata=new byte[ln_len];

        rfile.read(cisp_handler.ms_filedata);

        byte[] lp_filepos = cisp_handler.ms_filedata;

        CommLogger.logv4(TAG,"ln_len"+String.valueOf(ln_len));
        for (ln_loop = 0; ln_loop <ln_len; ln_loop++)
        {

            if (lb_odd) {
                ln_checksum1 += lp_filepos[ln_loop];
            }
            else {
                ln_checksum2 += lp_filepos[ln_loop];
            }
            lb_odd = !lb_odd;
        }
        o_header.n_firm_page .set((short) n_pageid);
        o_header.n_page_length .set((short) (ln_len-1));
        // TODO: n_page_length error
        o_header.n_checksum1 .set((short) ln_checksum1);
        o_header.n_checksum2 .set((short) ln_checksum2);
        for(int i=0;i!=ln_len;i++){
            CommLogger.logv4(TAG,"lp_filepos["+String.valueOf(i)+"]"+String.valueOf(lp_filepos[i]));
        }
        CommLogger.logv4(TAG," o_header.n_firm_page= "+String.valueOf( o_header.n_firm_page.get()));
        CommLogger.logv4(TAG," o_header.n_page_length= "+String.valueOf( o_header.n_page_length));
        CommLogger.logv4(TAG,"  o_header.n_checksum1 = "+String.valueOf( o_header.n_checksum1 ));
        CommLogger.logv4(TAG,"  o_header.n_checksum2= "+String.valueOf(  o_header.n_checksum2));

    }















}
