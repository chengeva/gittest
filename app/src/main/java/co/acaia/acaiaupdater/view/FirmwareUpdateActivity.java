package co.acaia.acaiaupdater.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseObject;

import co.acaia.acaiaupdater.AcaiaUpdater;
import co.acaia.acaiaupdater.Events.UpdateEraseProgress;
import co.acaia.acaiaupdater.Events.UpdateProgress;
import co.acaia.acaiaupdater.Events.UpdateStatusEvent;
import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;
import de.greenrobot.event.EventBus;

public class FirmwareUpdateActivity extends ActionBarActivity {
    private static int STATE_UPDATE_INIT=0;
    private static int STATE_UPDATE_CHECKING_FW=0;
    private static int STATE_UPDATE_UPDATING=0;
    private static int STATE_UPDATE_COMPLETE=0;

    private int current_updating_state;

    private TextView tv_progress;
    private TextView Update_status;
    private Button btn_updating;
    private ImageView image_device;
    private AcaiaDevice currentSelectedDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_fw);
        current_updating_state=STATE_UPDATE_INIT;
        EventBus.getDefault().register(this);
        tv_progress=(TextView)findViewById(R.id.Updating_progress);
        Update_status=(TextView) findViewById(R.id.Update_status);
        image_device=findViewById(R.id.image_device_update);

        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));

        if(currentSelectedDevice.modelName.equals(AcaiaDevice.modelPearlS)){
            image_device.setImageResource(R.drawable.img_pearls_default);
        }
        if(currentSelectedDevice.modelName.equals(AcaiaDevice.modelLunar)){
            image_device.setImageResource(R.drawable.img_lunar_default);
        }
        if(currentSelectedDevice.modelName.equals(AcaiaDevice.modelOrion)){
            image_device.setImageResource(R.drawable.img_orion_default);
        }
        if(currentSelectedDevice.modelName.equals(AcaiaDevice.modelCinco)){
            image_device.setImageResource(R.drawable.img_cinco_done);
        }

        if(AcaiaUpdater.currentAcaiaDevice.modelName.equals(AcaiaDevice.modelPearlS)){
            Update_status.setText("Please confirm firmware update on Pearl S");
        }

        btn_updating=(Button) findViewById(R.id.btn_updating);
        btn_updating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current_updating_state==STATE_UPDATE_COMPLETE){
                    Intent intent = new Intent(getApplicationContext(), MainDeviceActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                    startActivity(intent);
                }
            }
        });
    }

    public void onEvent(UpdateStatusEvent event){
        if(event.status==UpdateStatusEvent.ISPCompletedState){
            // completed
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_updating.setText(getResources().getString(R.string.Firmware_Update_Done_when_updating));
                    upload_firmware_done_log();
                    current_updating_state=STATE_UPDATE_COMPLETE;
                }
            });
        }
    }

    public void onEvent(final UpdateProgress event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Update_status.setText("Updating Firmware...");
                tv_progress.setText(String.valueOf((int)event.process)+"%");
            }
        });
    }

    public void onEvent(final UpdateEraseProgress event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Update_status.setText("Erasing Previous Firmware...");
                tv_progress.setText(String.valueOf((int)event.process)+"%");
            }
        });
    }

    private void upload_firmware_done_log()
    {

        try {
            //String firmwareLabal=tv_firmware_version.getText().toString();
            String appVersion = getVersionName(getApplicationContext());
            String systemVersion="Android"+ Build.VERSION.RELEASE.replaceAll("\\s+","");
            String deviceName=  getDeviceName().replaceAll("\\s+","");

            ParseObject firmwareLogObject=new ParseObject("update_log");
            firmwareLogObject.put("updatedFirmwareVersion", AcaiaUpdater.currentFirmware.title);
            firmwareLogObject.put("if_success", true);
            firmwareLogObject.put("platform", "Android");
            firmwareLogObject.put("platform_version", systemVersion);
            firmwareLogObject.put("device_model", deviceName);
            firmwareLogObject.put("app_version", appVersion);
            firmwareLogObject.put("scale_model", "lunar");
            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            firmwareLogObject.put("deviceID", android_id);
            firmwareLogObject.put("currentConnectedScaleFirmwareVersion", AcaiaUpdater.currentConnectedDeviceVersion);


            // Save the log eventually,
            firmwareLogObject.saveEventually();
        }catch (Exception e){

        }

    }

    /**
     * get App versionName
     * @param context
     * @return
     */
    public String getVersionName(Context context){
        PackageManager packageManager=context.getPackageManager();
        PackageInfo packageInfo;
        String versionName="";
        try {
            packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
            versionName=packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


}
