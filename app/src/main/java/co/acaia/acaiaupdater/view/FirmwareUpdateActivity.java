package co.acaia.acaiaupdater.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.Parse;
import com.parse.ParseObject;

import co.acaia.acaiaupdater.AcaiaUpdater;
import co.acaia.acaiaupdater.Events.DeviceWrongEvent;
import co.acaia.acaiaupdater.Events.UpdateEraseProgress;
import co.acaia.acaiaupdater.Events.UpdateProgress;
import co.acaia.acaiaupdater.Events.UpdateStatusEvent;
import co.acaia.androidupdater.R;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;
import de.greenrobot.event.EventBus;

public class FirmwareUpdateActivity extends AppCompatActivity {
    private static int STATE_UPDATE_INIT=0;
    private static int STATE_UPDATE_CHECKING_FW=0;
    private static int STATE_UPDATE_UPDATING=0;
    private static int STATE_UPDATE_COMPLETE=2;
    private static int STATE_UPDATE_WRONG_DEVICE=1;

    private int current_updating_state;

    private TextView tv_progress;
    private TextView Update_status;
    private Button btn_updating;
    private ImageView image_device;
    private AcaiaDevice currentSelectedDevice;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icon2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_instructions) {
            Intent intent = new Intent(getApplicationContext(), ManualTroubleActivity.class);
            intent.putExtra("modelName",currentSelectedDevice.modelName);
            intent.putExtra("type","Trouble");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Updating firmware", Toast.LENGTH_LONG);
        //??????Toast
        toast.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_fw);
        getSupportActionBar().setTitle("Updating");
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
        if(currentSelectedDevice.modelName.equals(AcaiaDevice.modelPearl2021)){
            image_device.setImageResource(R.drawable.img_pearl2021_update);
        }
        if(currentSelectedDevice.modelName.equals(AcaiaDevice.modelLunar2021)){
            image_device.setImageResource(R.drawable.img_lunar2021_update);
        }




        Update_status.setText("Checking device");

        if(AcaiaUpdater.currentAcaiaDevice.modelName.equals(AcaiaDevice.modelPearlS)){
            Update_status.setText("Please confirm firmware update on Pearl S");
        }

        if(AcaiaUpdater.currentAcaiaDevice.modelName.equals(AcaiaDevice.modelPearl2021)){
            Update_status.setText("Please confirm firmware update on Pearl (2021)");
        }

        if(AcaiaUpdater.currentAcaiaDevice.modelName.equals(AcaiaDevice.modelLunar2021)){
            Update_status.setText("Please confirm firmware update on Lunar (2021)");
        }

        btn_updating=(Button) findViewById(R.id.btn_updating);
        btn_updating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(current_updating_state==STATE_UPDATE_COMPLETE || current_updating_state==STATE_UPDATE_WRONG_DEVICE){

                    Intent intent = new Intent(getApplicationContext(), MainDeviceActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                    startActivity(intent);
                }
            }
        });
    }

    public void onEvent(DeviceWrongEvent deviceWrongEvent){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Update_status.setText("Wronge device. Please restart firmware update.");
                current_updating_state=STATE_UPDATE_WRONG_DEVICE;
            }
        });
    }

    public void onEvent(UpdateStatusEvent event){
        if(event.status==UpdateStatusEvent.ISPCompletedState){
            // completed
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Update_status.setText(getResources().getString(R.string.Firmware_Update_Done_when_updating));
                    btn_updating.setText(getResources().getString(R.string.Firmware_Update_Done???Btn));
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
                Update_status.setText("Updating Firmware");
                tv_progress.setText(String.valueOf((int)event.process)+"%");
            }
        });
    }

    public void onEvent(final UpdateEraseProgress event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Update_status.setText("Erasing Previous Firmware");
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
