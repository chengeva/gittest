package co.acaia.acaiaupdater.firmwarelunar;

import java.io.File;

import co.acaia.communications.CommLogger;
import javolution.io.Struct;

/**
 * Created by hanjord on 15/4/14.
 */
public class CISP_handler extends Struct {
    // NSFileHandle    *mo_file;
    // NSThread        *mo_thread;
    // NSData          *ms_filedata;
    File mo_file;
    long mn_filelen;
    int mn_total_page;
    byte[] ms_filedata=new byte[256];
    int mn_ticker;
    int mn_appstep;
    int mn_app_cmdid;
    int mn_app_index;
    int mn_app_len;

    public Struct.Unsigned8[] mn_app_buffer = new Struct.Unsigned8[]{
            new Struct.Unsigned8(), new Struct.Unsigned8(), new Struct.Unsigned8(),
            new Struct.Unsigned8(), new Struct.Unsigned8(), new Struct.Unsigned8(),
            new Struct.Unsigned8(), new Struct.Unsigned8(), new Struct.Unsigned8(),
            new Struct.Unsigned8(), new Struct.Unsigned8(), new Struct.Unsigned8(),
            new Struct.Unsigned8(), new Struct.Unsigned8(), new Struct.Unsigned8(),
    };
    public Unsigned16 mn_app_checksum = new Unsigned16();
    public Unsigned16 mn_app_datasum = new Unsigned16();

    short mn_disconnect_counter;
    public Boolean mb_started;

    public CISP_handler(File file) {
        CommLogger.logv("CispHandler","init handler!");
        mo_file=file;
        init();
    }

    private void init() {
        mn_ticker = 0;
        mn_appstep = Isp.EPARSER_PROCESS.e_prs_checkheader.ordinal();
        mn_app_cmdid = 0;
        mn_app_index = 0;
        mn_app_len = 2;
        mn_app_checksum .set(0);
        mn_app_datasum .set(0);
        mn_total_page = 0;
        mn_disconnect_counter = 0;
        mb_started = false;
    }

    public void reset() {
        mn_ticker = 0;
        mn_appstep = Isp.EPARSER_PROCESS.e_prs_checkheader.ordinal();
        mn_app_cmdid = 0;
        mn_app_index = 0;
        mn_app_len = 2;
        mn_app_checksum .set(0);
        mn_app_datasum .set(0);
        mn_total_page = 0;
        mn_disconnect_counter = 0;
        mb_started = false;
    }
}
