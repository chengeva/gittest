package co.acaia.firmwaretool.firmwarelunar;

import android.content.Context;

import co.acaia.communications.CommLogger;
import co.acaia.communications.protocol.ver20.ByteDataHelper;
import co.acaia.communications.scaleService.ScaleCommunicationService;
import j2me.nio.ByteOrder;
import javolution.io.Struct;

/**
 * Created by hanjord on 15/4/14.
 */
public class Isp {
    public static final String TAG="Isp";

    //typedef unsigned char u1;
//typedef unsigned short u2;
//typedef unsigned int u4;
    public static final int ISP_INFO_SIZE = 7;
    public static final int TRANSOK_SIZE = 3;
    public static final int PAGE_INFO_SIZE = 4;
    public static final int PAGEHEADER_SIZE = 4;

    public static final short gn_len[] = {2, 1, 255, 2};
    public static final short gs_header[] = {0xEF, 0xDD};
    public static final short gn_cmd_len[] = {
            255,            // e_cmd_system_sa
            255,            // e_ispcmd_info_a
            PAGE_INFO_SIZE,    // e_ispcmd_start_s : sent total page info
            PAGE_INFO_SIZE,    // e_ispcmd_erase_page_a : sent current erase page info
            PAGE_INFO_SIZE,    // e_ispcmd_pageask_a : sent ask page info
            PAGEHEADER_SIZE,// e_ispcmd_pageheader_s
            255,            // e_ispcmd_pagedata_s
            1,              // e_ispcmd_cancelisp_s
            TRANSOK_SIZE    // e_ispcmd_transok_a
    };

    public static enum EISPCMD {
        e_cmd_system_sa,
        e_ispcmd_info_a,        // scale sent info, scale can be restart in sent state state
        e_ispcmd_start_s,
        e_ispcmd_erase_page_a,
        e_ispcmd_pageask_a,
        e_ispcmd_pageheader_s,
        e_ispcmd_pagedata_s,
        e_ispcmd_cancelisp_s,
        e_ispcmd_transok_a,
        e_ispcmd_size,

    }


    public static enum EPARSER_PROCESS {
        e_prs_checkheader,
        e_prs_cmdid,
        e_prs_cmddata,
        e_prs_checksum,
        e_prs_disconnected,
        e_prs_size
    }

    public static class isp_info extends Struct {
        // 0
        private Unsigned8 n_ispinfo_length = new Unsigned8();
        private Unsigned8 n_ispinfo_version = new Unsigned8();
        private Unsigned8 n_ISP_version = new Unsigned8();
        private Unsigned8 n_firm_main_ver = new Unsigned8();
        private Unsigned8 n_firm_sub_ver = new Unsigned8();
        // 5
        private Unsigned8 n_firm_add_ver = new Unsigned8();
        private Unsigned8 n_firm_page = new Unsigned8();

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }
    }

    public static class page_info extends Struct {
        public Unsigned8 n_firm_main_ver = new Unsigned8();
        public Unsigned8 n_firm_sub_ver = new Unsigned8();
        public Unsigned8 n_firm_add_ver = new Unsigned8();
        public Unsigned8 n_firm_page = new Unsigned8();
        public Unsigned8[] buffer = new Unsigned8[]{new Unsigned8(),
                new Unsigned8(),
                new Unsigned8(), new Unsigned8()};

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }

        public Struct.Unsigned8[] getByteArray() {
            return buffer;
        }
        public Struct.Unsigned8[] getConvertedByteArray() {
           buffer[0]= n_firm_main_ver;
            buffer[1]=n_firm_sub_ver;
            buffer[2]=n_firm_add_ver;
           buffer[3]= n_firm_page ;
            return buffer;
        }
        public void setDataFromBuf(){
            n_firm_main_ver=buffer[0];
            n_firm_sub_ver=buffer[1];
            n_firm_add_ver=buffer[2];
            n_firm_page =buffer[3];
        }
        public void memcpy(Struct.Unsigned8[] src) {

            n_firm_main_ver.set(src[0].get());
            n_firm_sub_ver.set(src[1].get());
            n_firm_add_ver.set(src[2].get());
            n_firm_page.set(src[3].get());
            CommLogger.logv2("page_info", String.valueOf(n_firm_main_ver.get()));
            CommLogger.logv2("page_info", String.valueOf(n_firm_sub_ver.get()));
            CommLogger.logv2("page_info", String.valueOf(n_firm_add_ver.get()));
            CommLogger.logv2("page_info", String.valueOf(n_firm_page.get()));

        }



    }

    public static class page_header extends Struct {
        public Unsigned8 n_firm_page = new Unsigned8();
        public Unsigned8 n_page_length = new Unsigned8();
        public Unsigned8 n_checksum1 = new Unsigned8();
        public Unsigned8 n_checksum2 = new Unsigned8();
        private Unsigned8[] buffer = new Unsigned8[]{new Unsigned8(),
                new Unsigned8(),
                new Unsigned8(), new Unsigned8()};

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }
        public Struct.Unsigned8[] getByteArray() {

            buffer[0].set(n_firm_page.get());
            buffer[1].set( n_page_length.get());
            buffer[2].set( n_checksum1 .get());
            buffer[3].set(n_checksum2.get());
            return buffer;
        }
    }

    public static class trans_okmsg extends Struct {
        private Unsigned8 n_firm_main_ver = new Unsigned8();
        private Unsigned8 n_firm_sub_ver = new Unsigned8();
        private Unsigned8 n_firm_add_ver = new Unsigned8();

        @Override
        public ByteOrder byteOrder() {
            return ByteOrder.LITTLE_ENDIAN;
        }
    }
    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    public static boolean parse_input( CISP_handler cisp_handler,byte s_in, Context context,final ScaleCommunicationService mScaleCommunicationService) {
        int u_s_in=unsignedToBytes(s_in);

        if (!cisp_handler.mb_started) {
            cisp_handler.mn_app_index = 0;
            CommLogger.logv(TAG, "mb_start false");
            return false;
        }
        CommLogger.logv(TAG, "cisp_handler.mn_appstep " + String.valueOf(cisp_handler.mn_appstep));
        CommLogger.logv(TAG, "byte s_in " + String.valueOf(u_s_in));
        CommLogger.logv(TAG, "cisp_handler.mn_app_index " + String.valueOf(cisp_handler.mn_app_index));
        CommLogger.logv(TAG, "cisp_handler.mn_app_len " + String.valueOf(cisp_handler.mn_app_len));
      //  int mn_appstep = cisp_handler.mn_appstep;
        if ( cisp_handler.mn_appstep == EPARSER_PROCESS.e_prs_checkheader.ordinal()) {
            if (u_s_in != gs_header[cisp_handler.mn_app_index]) {
                //CommLogger.logv(TAG, "gs_header[" + String.valueOf(cisp_handler.mn_app_index) + "] " + String.valueOf(gs_header[cisp_handler.mn_app_index]));
                cisp_handler.mn_app_index = 0;
                //CommLogger.logv(TAG, "------------------------------ ");
                return false;
            }else{
                CommLogger.logv(TAG, "header ok");
            }
        } else if ( cisp_handler.mn_appstep == EPARSER_PROCESS.e_prs_cmdid.ordinal()) {
            cisp_handler.mn_app_cmdid = u_s_in;
            //CommLogger.logv(TAG, "cisp_handler.mn_app_cmdid " + String.valueOf(cisp_handler.mn_app_cmdid));
        } else if ( cisp_handler.mn_appstep == EPARSER_PROCESS.e_prs_cmddata.ordinal()) {
            if (cisp_handler.mn_app_index == 0) { // the first time
                cisp_handler.mn_app_len = gn_cmd_len[cisp_handler.mn_app_cmdid & 0x7f];
                if (cisp_handler.mn_app_len == 255)
                    cisp_handler.mn_app_len = u_s_in;
            }
            cisp_handler.mn_app_buffer[cisp_handler.mn_app_index].set((short) u_s_in);
            cisp_handler.mn_app_datasum.set(cisp_handler.mn_app_datasum.get() + 1);
            //CommLogger.logv(TAG, " cisp_handler.mn_app_datasum =" + String.valueOf(cisp_handler.mn_app_datasum));
        } else if ( cisp_handler.mn_appstep == EPARSER_PROCESS.e_prs_checksum.ordinal()) {
            cisp_handler.mn_app_checksum.set((cisp_handler.mn_app_checksum.get() << 8) + u_s_in);
        }
        cisp_handler.mn_app_index++;
        CommLogger.logv(TAG, "cisp_handler.mn_app_index " + String.valueOf(cisp_handler.mn_app_index));
        CommLogger.logv(TAG, "cisp_handler.mn_app_len " + String.valueOf(cisp_handler.mn_app_len));
        // next step
        if (cisp_handler.mn_app_index == cisp_handler.mn_app_len) {
            cisp_handler.mn_app_index = 0;
            // last step
            if ( cisp_handler.mn_appstep == EPARSER_PROCESS.e_prs_checksum.ordinal())
            {
                //NSLog(@"mn_app_datasum  = %d", mn_app_datasum );
                CommLogger.logv(TAG, "mn_app_datasum =" + String.valueOf(cisp_handler.mn_app_datasum));
                cisp_handler.mn_app_datasum.set(ByteDataHelper.calc_sum(cisp_handler.mn_app_buffer, ByteDataHelper.u_short_to_u_char(cisp_handler.mn_app_datasum)));
                CommLogger.logv(TAG, "mn_app_datasum =" + String.valueOf(cisp_handler.mn_app_datasum));
                CommLogger.logv(TAG, "cisp_handler.mn_app_checksum =" + String.valueOf(cisp_handler.mn_app_checksum));
                if (cisp_handler.mn_app_checksum.get() == cisp_handler.mn_app_datasum.get()) {

                      FileHandler.net_event(cisp_handler, mScaleCommunicationService);

                }

                cisp_handler.mn_appstep= EPARSER_PROCESS.e_prs_checkheader.ordinal();
                cisp_handler.mn_app_cmdid= 0;
                cisp_handler.mn_app_checksum .set(0);
                cisp_handler.mn_app_datasum .set(0);
            }
            else
            {
                cisp_handler.mn_appstep++;

            }
            cisp_handler.mn_app_len = gn_len[ cisp_handler.mn_appstep];
        }
        //CommLogger.logv(TAG, "------------------------------ ");

        return false;
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