package co.acaia.acaiaupdater;

/**
 * Created by hanjord on 2015/10/29.
 */
public class UpdateLog {

    // fields
    public static final String field_firmwareVersion="firmwareVersion";
    public static final String field_if_success="if_success";
    public static final  String field_platform="platform";
    public static final String field_platform_version="platform_version";
    public static final String field_device_model="device_model";
    public static final String field_note="note";
    public static final String field_app_version="app_version";
    public static final String field_scale_model="scale_model";

    // content
    public String firmwareVersion;
    public boolean if_success;
    public String platform;
    public String platform_version;
    public String device_model;
    public String note;
    public String app_version;
    public String scale_model;

    public UpdateLog(){
        this.platform="Android";
    }
}
