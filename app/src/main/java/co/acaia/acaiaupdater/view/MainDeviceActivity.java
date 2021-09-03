package co.acaia.acaiaupdater.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.PowerManager;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.Parse;

import java.util.ArrayList;

import co.acaia.acaiaupdater.AcaiaUpdater;
import co.acaia.acaiaupdater.FirmwareUpdateFragment;
import co.acaia.acaiaupdater.MainActivity;
import co.acaia.androidupdater.R;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;
import co.acaia.acaiaupdater.entity.acaiaDevice.Lunar;
import co.acaia.acaiaupdater.filehelper.OnDataRetrieved;
import co.acaia.acaiaupdater.filehelper.ParseFileRetriever;
import co.acaia.acaiaupdater.view.deviceList.CustomAdaptor;
import co.acaia.acaiaupdater.view.deviceList.DeviceModel;
import co.acaia.communications.scaleService.AcaiaScaleService;
import co.acaia.communications.scalecommand.ScaleCommandEvent;
import co.acaia.communications.scalecommand.ScaleCommandType;
import de.greenrobot.event.EventBus;

public class MainDeviceActivity extends AppCompatActivity {

    private ListView listview_devicelist;
    ArrayList<DeviceModel> dataModels;
    private static CustomAdaptor adapter;
    private ProgressDialog dialog;
    private AcaiaDevice currentSelectedDevice;

    // Old settings
    private BluetoothAdapter mBluetoothAdapter;
    protected BluetoothManager bluetoothManager;

    // Add wake lock to make app screen alive always
    protected PowerManager.WakeLock mWakeLock;
    //public static Scale scale = null;
    //private BluetoothDevice btDevice = null;
    public static ArrayList<BluetoothDevice> mLeDeviceList = null;
    //public static boolean isConnected=false;
    private static final int REQUEST_ENABLE_BT = 1;

    // New service
    private AcaiaScaleService acaiaScaleCommunicationService = null;
    private Handler handler;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_device);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(MainActivity.new_app_id)
                .clientKey(MainActivity.new_client_key)
                .server(MainActivity.new_endpoint)
                .build()
        );
        AcaiaUpdater.currentConnectedDeviceVersion="";
        setActionBar();
        init_view();
        initSettings();
        init_new_bt();
        currentSelectedDevice=null;
        // This is required for later BT version
        this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            Intent intent=new Intent(getApplicationContext(),InfoActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void init_new_bt() {
        // Init new acaia service protocol
        if (acaiaScaleCommunicationService == null) {
            acaiaScaleCommunicationService = new AcaiaScaleService();
            acaiaScaleCommunicationService.initialize(this);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_CONNECTION_STATE.ordinal()));
                }
            }, 3000);
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    private void initSettings()
    {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Acaia Updater");
        this.mWakeLock.acquire();
        handler = new Handler();
        mLeDeviceList = new ArrayList<>();

        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, getResources().getString(R.string.ble_not_supported), Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            // MainActivity.orangeDebug("mBluetoothAdapter.isEnabled=false, enableBtIntent");
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    private void init_view()
    {
        listview_devicelist=(ListView)findViewById(R.id.listview_devicelist);
        dataModels=new ArrayList<>();
        DeviceModel deviceModel=new DeviceModel("Pearl (2021)");
        dataModels.add(deviceModel);

        deviceModel=new DeviceModel("Pearl S");
        dataModels.add(deviceModel);
        deviceModel=new DeviceModel("Lunar");
        dataModels.add(deviceModel);
        deviceModel=new DeviceModel("Orion");
        dataModels.add(deviceModel);
        deviceModel=new DeviceModel("Cinco");
        dataModels.add(deviceModel);


        adapter= new CustomAdaptor(dataModels,getApplicationContext());
        listview_devicelist.setAdapter(adapter);
        listview_devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceModel dataModel=dataModels.get(i);
                //// Log.v("MainDevice",dataModel.modelName);

                ParseFileRetriever parseFileRetriever = new ParseFileRetriever();

                AcaiaDevice acaiaDevice = AcaiaDeviceFactory.acaiaDeviceFromModelName(dataModel.modelName);
                currentSelectedDevice = acaiaDevice;
                parseFileRetriever.retrieveFirmwareFilesByModel(getApplicationContext(), acaiaDevice, new OnDataRetrieved() {
                    @Override
                    public void doneRetrieved(boolean success, String message) {
                        //// Log.v("MainDevice", String.valueOf(success) + " " + message);
                        if (dialog != null) {
                            dialog.cancel();
                            if (currentSelectedDevice != null) {
                                nextActivity(currentSelectedDevice.modelName);
                            }
                        }
                    }
                });
                dialog = ProgressDialog.show(MainDeviceActivity.this, "",
                        "Downloading Firmware...", true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        currentSelectedDevice = null;
                        // Cancle firmware download...
                    }
                });

            }
        });

    }

    private void nextActivity(String modelName){
        Intent intent = new Intent(getApplicationContext(), FirmwareSelectActivity.class);
        intent.putExtra("modelName",modelName);
        AcaiaUpdater.currentFirmware=null;
        AcaiaUpdater.ispHelper=null;
        startActivity(intent);
    }

    private void setActionBar() {
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
