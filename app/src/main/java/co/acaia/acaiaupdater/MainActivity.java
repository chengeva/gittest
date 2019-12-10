package co.acaia.acaiaupdater;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.parse.Parse;

import java.util.ArrayList;

import co.acaia.acaiaupdater.entity.FirmwareUnitTests;
import co.acaia.acaiaupdater.filehelper.FileHelperUnitTests;
import co.acaia.acaiaupdater.filehelper.ParseFileRetriever;
import co.acaia.ble.events.ScaleConnectedEvent;
import co.acaia.ble.events.ScaleFoundEvent;
import co.acaia.ble.events.ScaleListChangeEvent;
import co.acaia.communications.events.WeightEvent;
import co.acaia.communications.scaleService.AcaiaScaleService;
import co.acaia.communications.scaleService.ScaleCommunicationService;
import co.acaia.communications.scalecommand.ScaleCommandEvent;
import co.acaia.communications.scalecommand.ScaleCommandType;
import co.acaia.communications.scaleevent.ScaleSettingUpdateEvent;
import co.acaia.communications.scaleevent.ScaleSettingUpdateEventType;
import co.acaia.acaiaupdater.filehelper.FirmwareFileFactory;
import co.acaia.acaiaupdater.util.Utils;
import de.greenrobot.event.EventBus;

public class MainActivity extends ActionBarActivity {
    EventBus bus = EventBus.getDefault();
    private final static String TAG = "JordanDebug_" + MainActivity.class.getSimpleName();
    public static final String ORANGE_TAG = "JordanDebug";

    private BluetoothAdapter mBluetoothAdapter;
    protected BluetoothManager bluetoothManager;

    // Add wake lock to make app screen alive always
    protected PowerManager.WakeLock mWakeLock;

    //public static Scale scale = null;
    //private BluetoothDevice btDevice = null;
    public static ArrayList<BluetoothDevice> mLeDeviceList = null;
    //public static boolean isConnected=false;

    private static final int REQUEST_ENABLE_BT = 1;

    private FirmwareUpdateFragment fragment;

    /*
   Integrage new protocol method
    */
    // New service
    private AcaiaScaleService acaiaScaleCommunicationService = null;
    private Handler handler;

    /**
     * Retrieve firmware files
     */
    FirmwareFileFactory firmwareFileFactory;

    /*public static final String new_app_id="UeTaOo1LBsWEbaGAqj6ITY0N4jNjFgzQL5lTjVhU";
    public static final String new_client_key="4Mqo4vvon9yzlcLi7uty9UXLlQW5j4NjUzNIRgaV";
    public static final String new_endpoint="https://pg-app-1s8ari663b0lwp94zxwfth7yc6vgfq.scalabl.cloud/1/";
    */

    public static final String new_app_id="85k1oN8QoRyrSwP2Tl7LJfpgQEdfQfPYpEUDM5N1";
    public static final String new_client_key="3sKqocyrUwcE0BVKUNLrCYFWyto4jPKQCnuKaGKM";
    //public static final String new_endpoint="https://parseapi.back4app.com/";
    public static final String new_endpoint="http://api-updater.acaia.net/";

    //    actionbar;
    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // MainActivity.orangeDebug("MainActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActionBar();

        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
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

        initSettings();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());


        // test NDK file reader
        // FileHandler.tREQUEST_ENABLE_BTestReadRawFile(getApplication());


        // retreieve firmware files
        // todo: add states

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(MainActivity.new_app_id)
                .clientKey(MainActivity.new_client_key)
                .server(MainActivity.new_endpoint)
                .build()
        );

        firmwareFileFactory = new FirmwareFileFactory(getApplicationContext());



        ParseFileRetriever parseFileRetriever=new ParseFileRetriever();
        //parseFileRetriever.test_parse(getApplicationContext());
        //FirmwareUnitTests.testFirmwareEntity();
        FileHelperUnitTests.testRetrieveFirmwareFile(getApplicationContext());

        this.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 123);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        if(requestCode == 123)
        {

        }
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


    private void setActionBar() {
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }

    private void init_new_bt() {
        // Init new acaia service protocol
        if (acaiaScaleCommunicationService == null) {
            acaiaScaleCommunicationService = new AcaiaScaleService();
            // startScaleCommunicationService();
            acaiaScaleCommunicationService.initialize(this);
            //acaiaScaleCommunicationService.getScaleCommunicationService().setActivity(getApplicationContext());

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // EventBus.getDefault().post(new ScaleConnectionCommandEvent(ScaleConnectionCommandEventType.connection_command.AUTO_CONNECT.ordinal()));
                }
            }, 400);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new ScaleCommandEvent(ScaleCommandType.command_id.GET_CONNECTION_STATE.ordinal()));
                }
            }, 3000);
        }
    }

    @Override
    protected void onResume() {
        try {
            bus.register(this);
        } catch (Exception e) {
            // MainActivity.orangeDebug("Error on ONRESUME, MainActivity, bus register, e="+e);
        }

        try {
            if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
                // if(mBluetoothAdapter.isEnabled()){
                init_new_bt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        MainActivity.orangeDebug("MainActivity onStart");
        super.onStart();

    }

    private void initSettings() {
        fragment = new FirmwareUpdateFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_framelayout, fragment)
                .commitAllowingStateLoss();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT
                && resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
            case R.id.menu_main_visit_website:
                String url = Utils.getResStr(this, R.string._url);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            case R.id.menu_main_feedback:
                Intent intent=new Intent();
                intent.setClass(getApplicationContext(), FeedbackInitActivity.class);
                startActivity(intent);
              /*  Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:developer@acaia.co"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                        Utils.getResStr(this, R.string._MailSubject));
                startActivity(Intent.createChooser(emailIntent,
                        Utils.getResStr(this, R.string._SentDialogTitle)));*/

                // feedback
                return true;
           /* case R.id.menu_main_about:
                Intent aboutIntent = new Intent(this, ListActivity.class);
                aboutIntent.putExtra(ListActivity.TYPE, ListActivity.ABOUT);
                startActivity(aboutIntent);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }

    }

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

    public static void orangeDebug(String str) {
        if (str == null) {
            Log.i(ORANGE_TAG, "NULL string!");
        } else {
            Log.i(ORANGE_TAG, str);
        }
    }

    public void onEvent(ScaleConnectedEvent event) {
        try {
            MainActivity.orangeDebug("Connected : " + event);
        } catch (Exception e) {
            Log.e(TAG, "ScaleFoundEvent failed!");
        }
    }

    public void onEvent(ScaleFoundEvent event) {
        try {
            if (MainActivity.mLeDeviceList != null) {
                MainActivity.mLeDeviceList.add(event.device);
                EventBus.getDefault().post(new ScaleListChangeEvent());
            }
        } catch (Exception e) {
            Log.e(TAG, "ScaleFoundEvent failed!");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            acaiaScaleCommunicationService.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            unregisterReceiver(mGattUpdateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Release wake lock
        this.mWakeLock.release();
        super.onDestroy();
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
                            EventBus.getDefault().post(new WeightEvent(result));
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

}
