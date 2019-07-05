package co.acaia.firmwaretool.entity;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by hanjord on 15/5/7.
 */
public class FirmwareFileEntity extends SugarRecord<FirmwareFileEntity>{
    public String remoteid;
    public String filename;
    public String title;
    public Date createdat;
    public String detail;
    public int majorversion;
    public int minorversion;
    //
    public String secret_code;
}
