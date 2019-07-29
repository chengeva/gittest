package co.acaia.acaiaupdater;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import co.acaia.acaiaupdater.entity.FirmwareFileEntity;
import co.acaia.ble.events.ScaleConnectedEvent;
import co.acaia.ble.events.ScaleDisconnectedEvent;
import co.acaia.communications.events.ScaleFirmwareVersionEvent;
import co.acaia.communications.events.WeightEvent;
import co.acaia.communications.scaleevent.ProtocolModeEvent;
import co.acaia.communications.scaleevent.ScaleSettingUpdateEvent;
import co.acaia.acaiaupdater.Events.ChangeISPModeEvent;
import co.acaia.acaiaupdater.Events.ConnectionEvent;
import co.acaia.acaiaupdater.Events.DownloadFirmwareFailedEvent;
import co.acaia.acaiaupdater.Events.DownloadedFirmwareEvent;
import co.acaia.acaiaupdater.Events.StartFirmwareUpdateEvent;
import co.acaia.acaiaupdater.Events.UpdateEraseProgress;
import co.acaia.acaiaupdater.Events.UpdateErrorEvent;
import co.acaia.acaiaupdater.Events.UpdateProgress;
import co.acaia.acaiaupdater.Events.UpdateStatusEvent;
import co.acaia.acaiaupdater.ui.SelectFirmwareActivity;
import co.acaia.acaiaupdater.ui.SelectVersionEvent;
import co.acaia.acaiaupdater.util.ListActivity;
import co.acaia.acaiaupdater.util.Utils;
import de.greenrobot.event.EventBus;

public class FirmwareUpdateFragment extends Fragment {
    EventBus bus = EventBus.getDefault();
    private String TAG = FirmwareUpdateFragment.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1;
    Handler lock_handler = new Handler();
    NumberFormat formatter = new DecimalFormat("#0.0");

    /**
     * UI
     */
    private RelativeLayout selectIntruction;
    private RelativeLayout selectScale;
    private RelativeLayout rl_select_firmware;

    //    private RelativeLayout selectFirmware;
//    private TextView tvFirmwareVersion;
    private TextView txtScaleName;
    private TextView txtScaleWeight;
    //    private RelativeLayout switchMode;
    private TextView tvSwitchMode;
//    private RelativeLayout startFirmware;

    private TextView tvAppVersionNumber;
    private ProgressBar updateProgressBar;
    private TextView tv_firmware_version;
    private TextView tv_progress_type;
    private TextView tv_precent;
    private TextView tv_select_firmware;

    // private TextView tvStartUpgrade;
    private Button btn_start_upgrade;
    // hanjord: add change update mode
    private Button btn_change_isp;
    private boolean isWeightVisible = false;

    // current firmware entity
    FirmwareFileEntity currentFirmwareFileEntity=null;

    // connection state
    private boolean isConnected=false;

    public static final String app_id="q0rZrvt0WXb8TvlfE4c61ODQqeOl3HTEEp7Q3qgB";
    public static final String client_key="LKsVXdYOI22U4lHV5B0IJGD0qICXzA9awAELx0sO";

    // scale version

    private int connectedScaleFirmwareVersion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);
        init_views(view);
        return view;
    }

    public void onEvent(final ScaleFirmwareVersionEvent scaleFirmwareVersionEvent) {
        try {

            //Log.v(TAG, "connect firmware version=" + String.valueOf(scaleFirmwareVersionEvent.firmwarever));
            connectedScaleFirmwareVersion=scaleFirmwareVersionEvent.firmwarever;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onEvent(UpdateStatusEvent event){
        if(event.status==UpdateStatusEvent.ISPCompletedState){
            // completed
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btn_start_upgrade.setText(getResources().getString(R.string.Firmware_Update_Done_when_updating));
                    upload_firmware_done_log();
                    txtScaleName.setText("");
                }
            });
        }
    }


    @Override
    public void onResume() {
        try {
            bus.register(this);
        } catch (Exception e) {
            // MainActivity.orangeDebug("Error on ONRESUME, MainActivity, bus register, e="+e);
        }

        super.onResume();
    }

    public void onEvent(DownloadFirmwareFailedEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_select_firmware.setText(getActivity().getResources().getString(R.string._downloading_firmware_failed));
                tv_firmware_version.setText("Retry");
            }
        });

    }

    public void onEvent(ConnectionEvent connectionEvent){
        isConnected=connectionEvent.isConnected;
    }

    public void onEvent(final WeightEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isWeightVisible) {
                    txtScaleWeight.setVisibility(View.VISIBLE);
                }
                txtScaleWeight.setText(event.weightDisp);
            }
        });
    }


    public void onEvent(final UpdateEraseProgress event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_start_upgrade.setText(getResources().getString(R.string.Firmware_updating));
                tv_precent.setText(formatter.format(event.process) + "%");
                tv_progress_type.setText(getResources().getString(R.string.Erasing_previous_Firmware));
                updateProgressBar.setProgress((int) event.process);
            }
        });
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


    private void setAppVersion() {

        try {
            tvAppVersionNumber.setText(getResources().getString(R.string._version)
                    + " " + String.valueOf(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName)
                    + " " );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ;
    }


    public void onEvent(UpdateErrorEvent event){
        if(event.error_code==UpdateErrorEvent.error_disconnected){
            upload_firmware_fail_log("Disconnected while updating");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //txtScaleWeight.setVisibility(View.VISIBLE);
                    tv_progress_type.setText(getResources().getString(R.string.Disconnected_while_updating));
                }
            });
        }else if(event.error_code==UpdateErrorEvent.error_bluetooth){
            upload_firmware_fail_log(event.error_code_str);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //txtScaleWeight.setVisibility(View.VISIBLE);
                    tv_progress_type.setText(getResources().getString(R.string.error_while_updating));
                }
            });
        }

    }

    private void upload_firmware_fail_log(String error)
    {

        try {
            Parse.initialize(getActivity(), app_id, client_key);
        }catch (Exception e){

        }

        try {
            String firmwareLabal=tv_firmware_version.getText().toString();
            String appVersion = tvAppVersionNumber.getText().toString();
            String systemVersion="Android"+ Build.VERSION.RELEASE.replaceAll("\\s+","");
            String deviceName=  getDeviceName().replaceAll("\\s+","");

            ParseObject firmwareLogObject=new ParseObject("update_log");
            firmwareLogObject.put("updatedFirmwareVersion", firmwareLabal);
            firmwareLogObject.put("if_success", false);
            firmwareLogObject.put("platform", "Android");
            firmwareLogObject.put("platform_version", systemVersion);
            firmwareLogObject.put("device_model", deviceName);
            firmwareLogObject.put("app_version", appVersion);
            firmwareLogObject.put("scale_model", "lunar");
            firmwareLogObject.put("note", error);
          

            String android_id = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            firmwareLogObject.put("deviceID", android_id);
            firmwareLogObject.put("currentConnectedScaleFirmwareVersion",connectedScaleFirmwareVersion);


            // Save the log eventually,
            firmwareLogObject.saveEventually();
        }catch (Exception e){

        }

    }



    private void upload_firmware_done_log()
    {
        try {
            Parse.initialize(getActivity(), app_id, client_key);
        }catch (Exception e){

        }

        try {
            String firmwareLabal=tv_firmware_version.getText().toString();
            String appVersion = tvAppVersionNumber.getText().toString();
            String systemVersion="Android"+Build.VERSION.RELEASE.replaceAll("\\s+","");
            String deviceName=  getDeviceName().replaceAll("\\s+","");

            ParseObject firmwareLogObject=new ParseObject("update_log");
            firmwareLogObject.put("updatedFirmwareVersion", firmwareLabal);
            firmwareLogObject.put("if_success", true);
            firmwareLogObject.put("platform", "Android");
            firmwareLogObject.put("platform_version", systemVersion);
            firmwareLogObject.put("device_model", deviceName);
            firmwareLogObject.put("app_version", appVersion);
            firmwareLogObject.put("scale_model", "lunar");
            String android_id = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            firmwareLogObject.put("deviceID", android_id);
            firmwareLogObject.put("currentConnectedScaleFirmwareVersion",connectedScaleFirmwareVersion);


            // Save the log eventually,
            firmwareLogObject.saveEventually();
        }catch (Exception e){

        }

    }

    public void onEvent(final UpdateProgress event) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_start_upgrade.setText(getResources().getString(R.string.Firmware_updating));
                tv_precent.setText(formatter.format(event.process) + "%");
                tv_progress_type.setText(getResources().getString(R.string.Updating_Firmware));
                updateProgressBar.setProgress((int) event.process);
            }
        });
    }

    public void onEvent(DownloadedFirmwareEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tv_firmware_version != null) {
                    setupVersion();
                }
            }
        });
    }

    private void setupVersion() {
        /*FirmwareFileEntity firmwareFileEntity = FirmwareFileEntityHelper.getLatestFirmware();
        if (firmwareFileEntity != null) {
            currentFirmwareFileEntity=firmwareFileEntity;
            // update textview
            //tv_firmware_version.setText("Lunar-"+firmwareFileEntity.majorversion+"."+firmwareFileEntity.minorversion);
            EventBus.getDefault().post(new SelectVersionEvent(firmwareFileEntity.getId()));
            tv_firmware_version.setText(firmwareFileEntity.title);
            tv_select_firmware.setText(getActivity().getResources().getString(R.string._select_firmware));
        } else {
            // Downlaoding
            tv_select_firmware.setText(getActivity().getResources().getString(R.string._downloading_firmware));
            tv_firmware_version.setText("");
        }*/
    }

    private void init_views(View v) {

        btn_change_isp=(Button)v.findViewById(R.id.btn_change_isp);
        btn_change_isp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isConnected) {
                    EventBus.getDefault().post(new ChangeISPModeEvent());
                }
            }
        });
        tv_precent = (TextView) v.findViewById(R.id.tv_precent);

        tv_firmware_version = (TextView) v.findViewById(R.id.tv_firmware_version);
        // init firmware version

        tv_progress_type = (TextView) v.findViewById(R.id.tv_progress);

        updateProgressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        updateProgressBar.setMax(100);

        selectIntruction = (RelativeLayout) v.findViewById(R.id.rl_select_guide);
        selectScale = (RelativeLayout) v.findViewById(R.id.rl_select_scale);

        btn_start_upgrade = (Button) v.findViewById(R.id.btn_start_upgrade);

        txtScaleName = (TextView) v.findViewById(R.id.p4_scale_name_textview);
        txtScaleWeight = (TextView) v.findViewById(R.id.scale_weight_textview);

        tvAppVersionNumber = (TextView) v.findViewById(R.id.tv_app_versionnumber);
        tv_select_firmware = (TextView) v.findViewById(R.id.tv_select_firmware);

        selectIntruction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra(ListActivity.TYPE, ListActivity.GUIDE_STEP);
                startActivity(intent);
            }
        });
        setupVersion();

        btn_start_upgrade.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // LoadText(R.raw.cal);

                if(isConnected) {
                    if (btn_start_upgrade.isPressed()) {

                                if (tv_select_firmware.getText().toString().equals(getActivity().getResources().getString(R.string._downloading_firmware))) {
                                    Toast.makeText(getActivity(), "Downloading firmware", Toast.LENGTH_LONG).show();
                        } else {
                            MainActivity.orangeDebug("startFirmware clicked.");

                            // TODO: Firmware entitiy
                            //EventBus.getDefault().post(new SelectVersionEvent(currentFirmwareFileEntity.getId()));
                            //EventBus.getDefault().post(new StartFirmwareUpdateEvent());

                        }

                    }
                    else if (btn_start_upgrade.getText().equals(getResources().getString(R.string.Firmware_Update_Done_when_updating))) {
                        btn_start_upgrade.setText(getResources().getString(R.string._start_firmware_update));
                    } else{
                        hint_updating();
                    }
                }else{
                    toaster(getResources().getString(R.string.connect_scale_first));
                }

            }
        });

        selectScale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final BluetoothManager bluetoothManager = (BluetoothManager) getActivity()
                        .getSystemService(Context.BLUETOOTH_SERVICE);
                BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
                if (!mBluetoothAdapter.isEnabled()) {

                    MainActivity.orangeDebug("start scan but mBluetoothAdapter not enabled ");

                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    // start scan


                    Intent settingScanScale = new Intent(getActivity()
                            .getApplicationContext(),
                            SettingScanScaleListActivity.class);
                    startActivity(settingScanScale);
                }
            }
        });

//        selectFirmware.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                showFirmwareVersionDialog();
//            }
//        });

        String str_version_num = Utils.getResStr(getActivity(), R.string._version) + " " + Utils.getVersionNumber(getActivity());
        tvAppVersionNumber.setText(str_version_num);

        rl_select_firmware = (RelativeLayout) v.findViewById(R.id.rl_select_firmware);
        rl_select_firmware.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_select_firmware.getText().toString().equals(getActivity().getResources().getString(R.string._downloading_firmware))) {
                    Toast.makeText(getActivity(), "Downloading firmware", Toast.LENGTH_LONG).show();
                } else if (tv_select_firmware.getText().toString().equals(getActivity().getResources().getString(R.string._downloading_firmware_failed))) {
                    setupVersion();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), SelectFirmwareActivity.class);
                    if(currentFirmwareFileEntity!=null){
                        // TODO: firmware from intent
                        //intent.putExtra(SelectFirmwareActivity.extra_firmware_ver,currentFirmwareFileEntity.getId());
                    }
                    getActivity().startActivity(intent);
                }
            }
        });

        setAppVersion();
    }

    public void onEvent(final SelectVersionEvent selectVersionEvent) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO: select version
               // tv_firmware_version.setText(FirmwareFileEntity.findById(FirmwareFileEntity.class, selectVersionEvent.id).title);
               // currentFirmwareFileEntity=FirmwareFileEntity.findById(FirmwareFileEntity.class,selectVersionEvent.id);
            }
        });
    }

    private void hint_updating() {

        toaster(getResources().getString(R.string.Firmware_updating));
    }


    private void toaster(String what) {
        Toast.makeText(getActivity(), what, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onEvent(ScaleSettingUpdateEvent scaleSettingUpdateEvent) {

    }

    public void onEvent(ProtocolModeEvent event) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.v(TAG, "protocol mode!");

            }
        });
    }

//    private void showFirmwareVersionDialog() {
//        final String tempArry[] = getActivity().getResources().getStringArray(R.array.firmware_version_arry);
//
//        final Dialog d = new Dialog(getActivity());
//        d.setTitle(getActivity().getResources().getString(R.string._select_firmware));
//        d.setContentView(R.layout.layout_numberpicker);
//        Button np_done = (Button) d.findViewById(R.id.layout_numberpicker_done);
//        final NumberPicker np = (NumberPicker) d.findViewById(R.id.layout_numberpicker_np);
//        np.setMaxValue(tempArry.length - 1);
//        np.setMinValue(0);
//        np.setDisplayedValues(tempArry);
//        np.setWrapSelectorWheel(true);
//        np_done.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                tvFirmwareVersion.setText(tempArry[np.getValue()]);
//                d.dismiss();
//            }
//        });
//        d.show();
//
//    }

    public void onEvent(ScaleConnectedEvent event) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtScaleName.setText("acaia scale");
                }
            });

        } catch (Exception e) {
            MainActivity.orangeDebug("ScaleConnectedEvent failed! e=" + e);
        }
    }


    public void onEvent(ScaleDisconnectedEvent event) {
        try {
            MainActivity.orangeDebug("Disconnected ");
            txtScaleName.setText(getResources().getString(R.string._none));
        } catch (Exception e) {
            MainActivity.orangeDebug("ScaleDisconnectedEvent failed! e=" + e);
        }
    }

    public void LoadText(int resourceId) {
        // The InputStream opens the resourceId and sends it to the buffer
        InputStream is = this.getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;

        try {
            // While the BufferedReader readLine is not null
            while ((readLine = br.readLine()) != null) {
                Log.d("TEXT", readLine);
            }

            // Close the InputStream and BufferedReader
            is.close();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
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
