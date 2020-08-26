package co.acaia.acaiaupdater.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;


import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

import co.acaia.acaiaupdater.AcaiaUpdater;
import co.acaia.acaiaupdater.Events.DeviceOKEvent;
import co.acaia.acaiaupdater.Events.DisconnectDeviceEvent;
import co.acaia.acaiaupdater.Events.StartFirmwareUpdateEvent;
import co.acaia.acaiaupdater.Events.UpdateISPEvent;
import co.acaia.androidupdater.R;
import co.acaia.acaiaupdater.ScaleService;
import co.acaia.acaiaupdater.entity.FirmwareEntityHelper;
import co.acaia.acaiaupdater.entity.FirmwareFileEntity;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;
import co.acaia.communications.events.WeightEvent;
import co.acaia.communications.scaleService.DistanceConnectEvent;
import co.acaia.communications.scaleService.ScaleCommunicationService;
import co.acaia.communications.scaleService.gatt.Log;
import co.acaia.communications.scaleevent.ScaleSettingUpdateEvent;
import co.acaia.communications.scaleevent.ScaleSettingUpdateEventType;
import co.acaia.communications.scaleevent.UpdatedStatusEvent;
import de.greenrobot.event.EventBus;

public class ConnectScaleActivity extends AppCompatActivity {
    private Button connectButton;
    private TextView tv_Updating_progress;
    private TextView tv_Update_status;
    private  TextView tv_current_firmware;
    private  TextView tv_disconnect;
    private ImageView image_device;

    private AcaiaDevice currentSelectedDevice;

    private static final int STATE_DISCONNECTED=0;
    private static final int STATE_OBTAININGINFO=1;
    private static final int STATE_CONNECTING=2;
    private static final int STATE_CONNECTED=3;
    //private static final int STATE_CONFIRMED_DEVICE=4;
    private static final int STATE_WRONG_DEVICE=5;

    private int current_connection_state;
    private FirmwareFileEntity currentFirmwareFileEntity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_to_device);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Connect");

        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));
        init_views();
        current_connection_state=STATE_DISCONNECTED;
        update_view_status();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        EventBus.getDefault().register(this);
        currentFirmwareFileEntity= FirmwareEntityHelper.obtainFirmwareWithModelName(currentSelectedDevice).get(0);
        //Log.v("ConnectScaleActivity","Updating to: "+currentFirmwareFileEntity.detail);


    }

    /*public void onEvent(UpdateISPEvent updateISPEvent){
        ArrayList<Integer> validISPs= AcaiaDevice.getValidISPFromModelName(currentSelectedDevice.modelName);
        boolean checkISP=false;
        for (int i=0;i!=validISPs.size();i++){
            if(validISPs.get(i)==updateISPEvent.isp_version){
                checkISP=true;
                this.current_connection_state=STATE_CONFIRMED_DEVICE;
            }
        }
        if(checkISP==false){
            this.current_connection_state=STATE_WRONG_DEVICE;
        }
    }*/

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
            intent.putExtra("type","Intro");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onEvent(final DeviceOKEvent deviceOKEvent){
        this.current_connection_state=STATE_CONNECTED;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_Update_status.setText("Connected to "+currentSelectedDevice.modelName);
                tv_current_firmware.setText("Device in update mode");
                tv_current_firmware.setVisibility(View.VISIBLE);
                update_view_status();
            }
        });
    }

    public void onEvent(final UpdatedStatusEvent updatedStatusEvent){
        this.current_connection_state=STATE_CONNECTED;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_current_firmware.setText("Current FW "+String.valueOf(updatedStatusEvent.mainVersion)+"."+String.valueOf(updatedStatusEvent.subVersion)+"."+String.valueOf(updatedStatusEvent.addVersion));
                tv_current_firmware.setVisibility(View.VISIBLE);
                AcaiaUpdater.currentConnectedDeviceVersion=tv_current_firmware.getText().toString();
                update_view_status();
            }
        });
    }

    private void update_view_status(){
        switch (current_connection_state){
            case STATE_CONNECTED:
                tv_disconnect.setVisibility(View.VISIBLE);
                tv_Updating_progress.setText("Connected to "+currentSelectedDevice.modelName);
                connectButton.setText("Next");
                break;
            case STATE_CONNECTING:
                tv_Update_status.setText("Connecting to "+currentSelectedDevice.modelName);
                break;
            case STATE_OBTAININGINFO:

                break;
            case STATE_DISCONNECTED:
                tv_disconnect.setVisibility(View.GONE);
                tv_current_firmware.setVisibility(View.GONE);
                connectButton.setText("Connect");

                if(currentSelectedDevice.modelName.equals("Cinco")){
                    tv_Updating_progress.setText("Connect to "+"Pyxis / Cinco");
                }else{
                    tv_Updating_progress.setText("Connect to "+currentSelectedDevice.modelName);
                }

                if(currentSelectedDevice.modelName.equals("Cinco")){

                    tv_Update_status.setText("Please place your phone\n close to "+"Pyxis / Cinco");
                }else{

                    tv_Update_status.setText("Please place your phone\n close to "+currentSelectedDevice.modelName);
                }


                break;
            default:
                break;
        }
    }
    private void init_views()
    {
        image_device=findViewById(R.id.image_device);

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

        connectButton=(Button)findViewById(R.id.btn_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (current_connection_state){
                    case STATE_CONNECTED:
                        StartFirmwareUpdateEvent startFirmwareUpdateEvent=new StartFirmwareUpdateEvent(currentFirmwareFileEntity);
                        EventBus.getDefault().post(startFirmwareUpdateEvent);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AcaiaUpdater.ispHelper.startIsp();
                                nextActivity(currentSelectedDevice.modelName);
                            }
                        }, 500);

                        break;
                    /*case STATE_CONNECTED:
                        // TODO: change firmware
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Checking device...", Toast.LENGTH_LONG);
                        //顯示Toast
                        toast.show();

                        break;*/
                    case STATE_CONNECTING:
                        break;
                    case STATE_OBTAININGINFO:
                        break;
                    case STATE_DISCONNECTED:
                        current_connection_state=STATE_CONNECTING;
                        update_view_status();
                        EventBus.getDefault().post(new DistanceConnectEvent(currentSelectedDevice));
                        break;
                    default:
                        break;
                }
            }
        });

        tv_Updating_progress=(TextView)findViewById(R.id.Updating_progress);
        tv_Update_status=(TextView)findViewById(R.id.Update_status);
        tv_current_firmware=(TextView)findViewById(R.id.tv_current_firmware);
        tv_disconnect=(TextView) findViewById(R.id.tv_disconnect);
        // Dixconnect device when tapped
        tv_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new DisconnectDeviceEvent());
                current_connection_state=STATE_DISCONNECTED;
                update_view_status();
            }
        });
    }

    private void nextActivity(String modelName){
        Intent intent = new Intent(getApplicationContext(), FirmwareUpdateActivity.class);
        intent.putExtra("modelName",modelName);
        startActivity(intent);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // ApplicationUtils.logcat(TAG,"got broadcast event!,action="+String.valueOf(action) );
            if (ScaleCommunicationService.ACTION_DATA_AVAILABLE.equals(action)) {

                try {
                    int resultType = intent.getExtras().getInt(
                            ScaleCommunicationService.EXTRA_DATA_TYPE);


                    float val;
                    // ApplicationUtils.logcat(TAG,"got broadcast event! result type="+String.valueOf(resultType)+" "+"data="+String.valueOf(result) );
                    switch (resultType) {

                        case ScaleCommunicationService.DATA_TYPE_WEIGHT:
                            // //Log.v(TAG,"got weight!");
                            String result = intent.getExtras().getString(
                                    ScaleCommunicationService.EXTRA_DATA);
                            int unit = intent.getExtras().getInt(ScaleCommunicationService.EXTRA_UNIT);
                            EventBus.getDefault().post(new ScaleSettingUpdateEvent(ScaleSettingUpdateEventType.event_type.EVENT_UNIT.ordinal(), unit));
                            if(unit==0){
                                result=result+"g";
                            }else{
                                result=result+"oz";
                            }
                            ////Log.v("ConnectScaleActivity",result);

                            if(current_connection_state==STATE_CONNECTING){
                                current_connection_state=STATE_OBTAININGINFO;
                                update_view_status();
                            }
                            if(current_connection_state==STATE_CONNECTED) {
                                tv_Update_status.setText(result);
                                EventBus.getDefault().post(new WeightEvent(result));
                            }
                            break;

                        case ScaleCommunicationService.DATA_TYPE_KEY_DISABLED_ELAPSED_TIME:
                            val = intent.getExtras().getFloat(
                                    ScaleCommunicationService.EXTRA_DATA);
                            EventBus.getDefault().post(new ScaleSettingUpdateEvent(ScaleSettingUpdateEventType.event_type.EVENT_KEY_DISABLED_ELAPSED_TIME.ordinal(), val));
                            break;
                        case ScaleCommunicationService.DATA_TYPE_BEEP:
                            val = intent.getExtras().getFloat(
                                    ScaleCommunicationService.EXTRA_DATA);
                            EventBus.getDefault().post(new ScaleSettingUpdateEvent(ScaleSettingUpdateEventType.event_type.EVENT_BEEP.ordinal(), val));
                            break;
                        case ScaleCommunicationService.DATA_TYPE_AUTO_OFF_TIME:
                            val = intent.getExtras().getFloat(
                                    ScaleCommunicationService.EXTRA_DATA);
                            EventBus.getDefault().post(new ScaleSettingUpdateEvent(ScaleSettingUpdateEventType.event_type.EVENT_AUTO_OFF_TIME.ordinal(), val));
                            break;
                        case ScaleCommunicationService.DATA_TYPE_BATTERY:
                            val = intent.getExtras().getFloat(
                                    ScaleCommunicationService.EXTRA_DATA);
                            EventBus.getDefault().post(new ScaleSettingUpdateEvent(ScaleSettingUpdateEventType.event_type.EVENT_BATTERY.ordinal(), val));
                            break;
                    }

                } catch (Exception e) {
                    // Log.i(TAG, "Error on getting data!");
                }


            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScaleService.ACTION_CONNECTION_STATE_CONNECTED);
        intentFilter.addAction(ScaleService.ACTION_CONNECTION_STATE_DISCONNECTED);
        intentFilter.addAction(ScaleService.ACTION_CONNECTION_STATE_DISCONNECTING);
        intentFilter.addAction(ScaleService.ACTION_CONNECTION_STATE_CONNECTING);
        intentFilter.addAction(ScaleService.ACTION_SERVICES_DISCOVERED);
        intentFilter.addAction(ScaleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(ScaleService.ACTION_DEVICE_FOUND);
        return intentFilter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mGattUpdateReceiver);
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
