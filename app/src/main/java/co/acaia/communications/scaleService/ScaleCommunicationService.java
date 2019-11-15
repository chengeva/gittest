package co.acaia.communications.scaleService;


import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import co.acaia.acaiaupdater.AcaiaUpdater;
import co.acaia.acaiaupdater.Events.DisconnectDeviceEvent;
import co.acaia.acaiaupdater.entity.AcaiaFirmware;
import co.acaia.ble.events.ScaleConnectedEvent;
import co.acaia.communications.CommLogger;
import co.acaia.communications.events.SendDataEvent;
import co.acaia.communications.protocol.ScaleGattAttributes;
import co.acaia.communications.protocol.ver20.DataOutHelper;
import co.acaia.communications.protocol.ver20.DataPacketParser;
import co.acaia.communications.reliableQueue.ReliableSenderQueue;
import co.acaia.communications.scale.AcaiaScale;
import co.acaia.communications.scale.AcaiaScale2;
import co.acaia.communications.scale.AcaiaScaleFactory;
import co.acaia.communications.scaleService.aosp.AospGattAdapter;
import co.acaia.communications.scaleService.gatt.Gatt;
import co.acaia.communications.scaleService.gatt.GattAdapter;
import co.acaia.communications.scaleService.gatt.GattCharacteristic;
import co.acaia.communications.scaleService.gatt.GattDescriptor;
import co.acaia.communications.scaleService.gatt.GattService;
import co.acaia.communications.scalecommand.ScaleCommandEvent;
import co.acaia.communications.scalecommand.ScaleCommandType;
import co.acaia.communications.scalecommand.ScaleConnectionCommandEvent;
import co.acaia.communications.scalecommand.ScaleConnectionCommandEventType;
import co.acaia.communications.scaleevent.NewScaleConnectionStateEvent;
import co.acaia.communications.scaleevent.ScaleConnectionEvent;

import com.acaia.scale.communications.AcaiaCommunicationPacketHelper;

import co.acaia.acaiaupdater.Events.ChangeISPModeEvent;
import co.acaia.acaiaupdater.Events.ConnectionEvent;
import co.acaia.acaiaupdater.Events.StartFirmwareUpdateEvent;
import co.acaia.acaiaupdater.Events.UpdateErrorEvent;
import co.acaia.acaiaupdater.Events.UpdateStatusEvent;
import co.acaia.acaiaupdater.firmwarelunar.IspHelper;
import co.acaia.acaiaupdater.ui.SelectVersionEvent;
import de.greenrobot.event.EventBus;

import static co.acaia.acaiaupdater.firmwarelunar.IspHelper.ISP_CHECK_APP;
import static co.acaia.acaiaupdater.firmwarelunar.IspHelper.ISP_CHECK_INIT;
import static co.acaia.acaiaupdater.firmwarelunar.IspHelper.ISP_CHECK_ISP;
import static co.acaia.acaiaupdater.firmwarelunar.IspHelper.ISP_SET_ISP;

public class ScaleCommunicationService extends Service {
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;
    private ScaleCommunicationService self;

    // new structure

    //private AcaiaScale acaiaScale = null;
    //private IspHelper ispHelper = null;
    private AcaiaScale acaiaScale = null;

    // Reliable
    private ReliableSenderQueue reliableSenderQueue;

    // debug
    Handler handler;

    // Connection State
    public final static int UNIT_GRAM = 0;
    public final static int UNIT_OUNCE = 1;
    private int mConnectionState = CONNECTION_STATE_DISCONNECTED;

    public final static String ACTION_CONNECTION_STATE_CONNECTED =
            "co.acaia.scale.service.ACTION_CONNECTION_STATE_CONNECTED";
    public final static String ACTION_CONNECTION_STATE_DISCONNECTED =
            "co.acaia.scale.service.ACTION_CONNECTION_STATE_DISCONNECTED";
    public final static String ACTION_CONNECTION_STATE_DISCONNECTING =
            "co.acaia.scale.service.ACTION_CONNECTION_STATE_DISCONNECTING";
    public final static String ACTION_CONNECTION_STATE_CONNECTING =
            "co.acaia.scale.service.ACTION_CONNECTION_STATE_CONNECTING";

    public final static String ACTION_SERVICES_DISCOVERED =
            "co.acaia.scale.service.ACTION_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "co.acaia.scale.service.ACTION_DATA_AVAAILABLE";
    public final static String ACTION_DEVICE_FOUND = "co.acaia.scale.service.ACTION_DEVICE_FOUND";


    public final static String EXTRA_CONNECTION_STATE = "co.acaia.scale.service.EXTRA_CONNECTION_STATE";
    public final static String EXTRA_DEVICE = "co.acaia.scale.service.EXTRA_DEVICE";
    public final static String EXTRA_RSSI = "co.acaia.scale.service.EXTRA_RSSI";
    public final static String EXTRA_DATA =
            "co.acaia.scale.service.EXTRA_DATA";
    public final static String EXTRA_UNIT =
            "co.acaia.scale.service.UNIT";
    public final static String EXTRA_DATA_TYPE =
            "co.acaia.scale.service.DATA_TYPE";

    // Microchip
    private final static String sPREFIX = "0000";
    private final static String sPOSTFIX = "-0000-1000-8000-00805f9b34fb";
    public static UUID SERVICE_ISSC_PROPRIETARY = UUID.fromString("49535343-FE7D-4AE5-8FA9-9FAFD205E455");
    public static UUID CHR_CONNECTION_PARAMETER = UUID.fromString("49535343-6DAA-4D02-ABF6-19569ACA69FE");
    public static UUID CHR_ISSC_TRANS_TX = UUID.fromString("49535343-1E4D-4BD9-BA61-23C647249616");
    public static UUID CHR_ISSC_TRANS_RX = UUID.fromString("49535343-8841-43F4-A8D4-ECBE34729BB3");
    public final static UUID CHR_ISSC_MP = UUID.fromString("49535343-ACA3-481C-91EC-D85E28A60318");
    public static UUID CHR_ISSC_TRANS_CTRL = UUID.fromString("49535343-4C8A-39B3-2F49-511CFF073B7E");
    public final static UUID CHR_AIR_PATCH = UUID.fromString("49535343-ACA3-481C-91EC-D85E28A60318");
    public final static UUID DES_CLIENT_CHR_CONFIG = uuidFromStr("2902");

    public final static int CONNECTION_STATE_DISCONNECTED = 0;
    public final static int CONNECTION_STATE_DISCONNECTING = 1;
    public final static int CONNECTION_STATE_CONNECTED = 2;
    public final static int CONNECTION_STATE_CONNECTING = 3;

    public final static int AUTOOFF_TIME_5_MIN = 0;
    public final static int AUTOOFF_TIME_10_MIN = 1;
    public final static int AUTOOFF_TIME_15_MIN = 2;
    public final static int AUTOOFF_TIME_30_MIN = 3;


    public final static int DATA_TYPE_WEIGHT = 0;
    public final static int DATA_TYPE_BATTERY = 1;
    public final static int DATA_TYPE_AUTO_OFF_TIME = 2;
    public final static int DATA_TYPE_BEEP = 3;
    public final static int DATA_TYPE_KEY_DISABLED_ELAPSED_TIME = 4;
    public final static int DATA_TYPE_TIMER = 5;
    public final static int DATA_TYPE_CAPACITY = 6;

    public static final String TAG = "ScaleCommunicationService";

    private final LocalBinder mBinder = new LocalBinder();

    // incomming packet debug
    private long last_received = 0;
    private String mCurrentConnectedDeviceAddr = "";
    private long incomming_msg_counter = 0;

    // send failed
    private long send_failed_count = 0;
    private static final int send_failed_threshold = 3;
    //private Activity parentActivity;
    //private Queue<byte[]> sendingQueue;

    private boolean isISPMode = false;

    public enum MODE {
        NORMAL,
        SETTE,
        FELLOW,
        DISTANCE,
    }

    private MODE mMode = MODE.NORMAL;
    // Distance connect helper
    DistanceConnectHelper distanceConnectHelper;

    public boolean scaleGetStatue = true;

    // Microchip
    private Bm71GattListener mBM71Listener;
    private Gatt mBM71Gatt = null;
    private GattAdapter mBM71GattAdapter = null;
    private GattCharacteristic mTransTx;
    private GattCharacteristic mTransRx;
    private GattCharacteristic mAirPatch;
    private GattCharacteristic mTransCtrl;

    public synchronized void setIsISP(boolean isIsp) {
        CommLogger.logv(TAG,"set is isp!");
        isISPMode = isIsp;
    }

    public synchronized boolean isISPMode() {
        return isISPMode;
    }

    // Parse scale setting command
    public void onEvent(ScaleCommandEvent event) {

        if (event.getCommandType() == ScaleCommandType.command_id.GET_CONNECTION_STATE.ordinal()) {
            // Get conmnection state async
            if (isConnected()) {
                EventBus.getDefault().post(new ScaleConnectionEvent(true, mCurrentConnectedDeviceAddr, AcaiaScale2.protocol_version_20, mBluetoothDevice));
            } else {
                EventBus.getDefault().post(new ScaleConnectionEvent(false));
            }
        }

    }

    // Parse scale connection command
    public void onEvent(ScaleConnectionCommandEvent event) {
        CommLogger.logv(TAG, "got scale connection event=" + event.addr);
        if (event.command == ScaleConnectionCommandEventType.connection_command.CONNECT.ordinal()) {
            if (event.addr != null) {
                connect(event.addr);
            }
        } else if (event.command == ScaleConnectionCommandEventType.connection_command.DISCONNECT.ordinal()) {
            disconnect();
        } else if ((event.command == ScaleConnectionCommandEventType.connection_command.AUTO_CONNECT.ordinal())) {
            if (!isConnected()) {
                AutoConnectHelper autoConnectHelper = new AutoConnectHelper(mBluetoothAdapter);
                autoConnectHelper.startAutoConnect(AutoConnectHelper.mode_auto);
            }
        }
    }

    public Boolean isConnected() {
        if (mConnectionState == CONNECTION_STATE_CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    public void onEvent(ChangeISPModeEvent event) {
        CommLogger.logv(TAG, "click change isp");
        if(!isISPMode()) {

        }else{
            CommLogger.logv(TAG, "ISP MODE!");
        }
    }

    @SuppressLint("LongLogTag")
    public void onEvent(StartFirmwareUpdateEvent event) {
        setIsISP(true);
        CommLogger.logv(TAG, "click start isp");
        //Log.v(TAG,"got ustart update event");
        if(AcaiaUpdater.ispHelper.isISP==ISP_CHECK_APP) {
            AcaiaUpdater.ispHelper.isISP=ISP_SET_ISP;
            AcaiaUpdater.ispHelper.change_isp_mode();
        }
        //ispHelper.startIsp();

    }

    public void onEvent(UpdateStatusEvent event) {
        if (event.status == UpdateStatusEvent.ISPCompletedState) {
            // completed
            releaseISP();
            ;
            // hanjord: release isp
        }
    }

    @SuppressLint("LongLogTag")
    public void onEvent(final DistanceConnectEvent distanceConnectEvent) {
        //Log.v(TAG,"Got distance connect event");

        if (mConnectionState == CONNECTION_STATE_CONNECTED) {
            disconnect();
            disconnectBm71();
            DataPacketParser.initDataParser();
        }
        mMode = MODE.DISTANCE;

        this.distanceConnectHelper = new DistanceConnectHelper(distanceConnectEvent.currentConnectingDevice);
        startScan();
    }

    private void releaseISP() {
        try {
            AcaiaUpdater.ispHelper.release();
            AcaiaUpdater. ispHelper = null;
        } catch (Exception e) {

        }
    }

    public void onEvent(SelectVersionEvent selectVersionEvent) {
        //firmwareFileEntity = FirmwareFileEntity.findById(FirmwareFileEntity.class, selectVersionEvent.id);
    }


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @SuppressLint("LongLogTag")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            try {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    String connection_str = "";
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        mConnectionState = CONNECTION_STATE_CONNECTING;
                        CommLogger.logv(TAG, "Connected to GATT server.");
                        CommLogger.logv(TAG, "Attempting to start service discovery:"
                        );
                        if (gatt != null) {
                            gatt.discoverServices();
                            mCurrentConnectedDeviceAddr = gatt.getDevice().getAddress();
                            mBluetoothDevice = gatt.getDevice();
                            CommLogger.logv(TAG, "current connected addr=" + mCurrentConnectedDeviceAddr);
                            connection_str = ACTION_CONNECTION_STATE_CONNECTED;
                            incomming_msg_counter = 0;
                        } else {
                            disconnect();
                        }
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        mConnectionState = CONNECTION_STATE_DISCONNECTED;

                        CommLogger.logv(TAG, "Disconnected from GATT server.");
                        //Mike, 2014/05/09, This is an workaround, because if we just do disconnect and then connect, the time of building connection is very long
                        mBluetoothDeviceAddress = null;
                        if (mBluetoothGatt != null) {
                            mBluetoothGatt.close();
                        }
                        mBluetoothGatt = null;
                        // Release current acaia scale
                        if (AcaiaUpdater.ispHelper != null) {
                            EventBus.getDefault().post(new UpdateErrorEvent(UpdateErrorEvent.error_disconnected));
                            AcaiaUpdater.ispHelper.release();
                            AcaiaUpdater.ispHelper = null;
                        }
                        EventBus.getDefault().post(new ConnectionEvent(false));
                        last_received = 0;
                        mCurrentConnectedDeviceAddr = "";
                        EventBus.getDefault().post(new ScaleConnectionEvent(false));
                        connection_str = ACTION_CONNECTION_STATE_DISCONNECTED;
                        CommLogger.logv("ack_event_test", "disconnected: " + Integer.toString(status));
                        incomming_msg_counter = 0;
                    }
                    //The next two conditions are unnecessary
                    else if (newState == BluetoothProfile.STATE_CONNECTING) {
                        mConnectionState = CONNECTION_STATE_CONNECTING;
                        connection_str = ACTION_CONNECTION_STATE_CONNECTING;
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                        mConnectionState = CONNECTION_STATE_DISCONNECTING;
                        connection_str = ACTION_CONNECTION_STATE_DISCONNECTING;
                    } else {
                        Log.d(TAG, "onConnectionStateChange error");
                    }
                    broadcastUpdate(connection_str);

                } else {
                    disconnect();
                    ;
                    Log.d(TAG, "onConnectionStateChange error:" + Integer.toString(status));
                }
            } catch (Exception e) {
                e.printStackTrace();
                EventBus.getDefault().post(new UpdateErrorEvent(UpdateErrorEvent.error_bluetooth, e.getMessage()));

            }

        }

        @SuppressLint("LongLogTag")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            CommLogger.logv(TAG, "onServicesDiscovered received: " + status);
            try {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    EventBus.getDefault().post(new ScaleConnectedEvent());
                    CommLogger.logv(TAG, "Service discover OK");
                    //mBluetoothGatt.beginReliableWrite();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                init_sync();
                            } catch (Exception e) {

                            }
                        }
                    }, 100);

                    EventBus.getDefault().post(new ConnectionEvent(true));

                } else {
                    Log.w(TAG, "onServicesDiscovered received: " + status);
                }
            } catch (Exception e) {
                EventBus.getDefault().post(new UpdateErrorEvent(UpdateErrorEvent.error_bluetooth, e.getMessage()));
                e.printStackTrace();
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            CommLogger.logv(TAG, "onCharacteristicRead received: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            CommLogger.logv(TAG, "onCharacteristicWrite received: " + Integer.toString(status));
            CommLogger.logv("ack_event_test", "onCharacteristicWrite received: " + Integer.toString(status));
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            CommLogger.logv(TAG, "onDescriptorRead received");
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            CommLogger.logv(TAG, "onDescriptorWrite received: " + Integer.toString(status));
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            CommLogger.logv(TAG, "onReadRemoteRssi received");
        }

        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            CommLogger.logv(TAG, "onReliableWriteCompleted received=" + String.valueOf(status));
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            try {
                if (gatt.getDevice().getAddress().equals(mCurrentConnectedDeviceAddr)) {
                    mConnectionState = CONNECTION_STATE_CONNECTED;
                    CommLogger.logv(TAG, "onCharacteristicChanged received");
                    // CommLogger.logv(TAG,"chara len="+String.valueOf(readLen));
                    if (last_received == 0) {
                        EventBus.getDefault().post(new NewScaleConnectionStateEvent(mCurrentConnectedDeviceAddr));
                    }
                    // CommLogger.logv(TAG, "last received ttl=" + String.valueOf((System.nanoTime() - last_received) / 1000000.0));
                    send_failed_count = 0;
                    last_received = System.nanoTime();

                    // need to know if ISP mode...

                    if (!isISPMode()) {
                        //Log.v(TAG,"App Mode!");
                        if (acaiaScale == null) {
                            acaiaScale = AcaiaScaleFactory.createAcaiaScale(AcaiaScaleFactory.version_20, getApplicationContext(), self, handler, null, false);
                            acaiaScale.getScaleCommand().parseDataPacket(characteristic.getValue());
                        } else {
                            ////Log.v(TAG, "acaia scale not null");
                            // parse packet


                            if(AcaiaUpdater.ispHelper==null){
                                AcaiaUpdater.ispHelper=new IspHelper(getApplicationContext(), self, handler, AcaiaUpdater.currentFirmware);
                            }

                            AcaiaUpdater.ispHelper.parseDataPacket(characteristic.getValue());

                            if(AcaiaUpdater.ispHelper.isISP!=ISP_CHECK_ISP) {
                                acaiaScale.getScaleCommand().parseDataPacket(characteristic.getValue());
                            }else if(AcaiaUpdater.ispHelper.isISP==ISP_CHECK_ISP){
                                setIsISP(true);
                            }
                            //  update connection
                        }

                    }else{
                        //Log.v(TAG,"ISP Mode!");
                        if(AcaiaUpdater.ispHelper==null){
                            AcaiaUpdater.ispHelper=new IspHelper(getApplicationContext(), self, handler, AcaiaUpdater.currentFirmware);
                        }
                        AcaiaUpdater.ispHelper.parseDataPacket(characteristic.getValue());
                    }

                }
            } catch (Exception e) {
                EventBus.getDefault().post(new UpdateErrorEvent(UpdateErrorEvent.error_bluetooth, e.getMessage()));
                e.printStackTrace();
            }
            // Only connect when

        }
    };


    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    public void onEvent(SendDataEvent sendDataEvent) {
        CommLogger.logv(TAG, "Got send event");
        byte[] debug = sendDataEvent.out_data;
        for (int i = 0; i != debug.length; i++) {
            CommLogger.logv(TAG, "Sent [" + String.valueOf(i) + "]" + "=" + String.valueOf(debug[i]));
        }
        sendCmd(sendDataEvent.out_data);
        //sendCmdwithResponse(sendDataEvent.out_data);
    }

    @SuppressLint("LongLogTag")
    public boolean connect(final String targetBtAddress) {
        // Stop BLE scan before connecting
        stopScan();
        if (mBluetoothAdapter == null || targetBtAddress == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(targetBtAddress);
        if (mBluetoothDevice == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }

        if (mBluetoothDevice.getName().contains("PYXIS") || mBluetoothDevice.getName().contains("CINCO") || mBluetoothDevice.getName().contains("PEARLS")) {
            //Log.v(TAG, "Trying to create a new connection. Pearls cinco");
            if(mBM71Gatt==null) {
                mBM71Gatt = mBM71GattAdapter.connectGatt(getApplicationContext(), false, mBM71Listener, mBluetoothDevice);
                mBluetoothGatt = null;
            }
        } else {
            if(mBluetoothGatt == null && mConnectionState==CONNECTION_STATE_DISCONNECTED) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mGattCallback);
                //Log.v(TAG, "Trying to create a new connection.");
                mBluetoothDeviceAddress = targetBtAddress;
                mConnectionState = CONNECTION_STATE_CONNECTING;
            }
        }
        return true;
    }

    @SuppressLint("LongLogTag")
    public void release() {
        if (mBluetoothAdapter != null && mBluetoothGatt != null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            try {
                mBluetoothDevice = null;
                mBluetoothDeviceAddress = null;
                mBluetoothGatt.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }

            try {
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }


            try {
                mConnectionState = CONNECTION_STATE_DISCONNECTING;
                mCurrentConnectedDeviceAddr = "";
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                if (AcaiaUpdater.ispHelper != null) {
                    AcaiaUpdater.ispHelper.release();
                    AcaiaUpdater. ispHelper = null;
                }
                //mBluetoothGatt.close();
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }
        }

    }

    @SuppressLint("LongLogTag")
    public synchronized void disconnect() {
        if(mBM71Gatt!=null){
            try {
                mBluetoothDevice = null;
                mBluetoothDeviceAddress = null;
                mBM71Gatt.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }

            try {
                if (mBM71Gatt != null) {
                    mBM71Gatt.close();
                    mBM71Gatt = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }

            try {
                if (AcaiaUpdater.ispHelper != null) {
                    AcaiaUpdater. ispHelper.release();
                    AcaiaUpdater.ispHelper = null;
                }
                //mBluetoothGatt.close();
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }
            try {
                EventBus.getDefault().post(new ScaleConnectionEvent(false));
            } catch (Exception e) {
                CommLogger.logv(TAG, "failed disconnect event post");
                e.printStackTrace();
                ;
            }


        }else {

            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                Log.w(TAG, "BluetoothAdapter not initialized");
                return;
            }

            try {
                mBluetoothDevice = null;
                mBluetoothDeviceAddress = null;
                mBluetoothGatt.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }

            try {
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }
            try {
                mConnectionState = CONNECTION_STATE_DISCONNECTING;
                mCurrentConnectedDeviceAddr = "";
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                if (AcaiaUpdater.ispHelper != null) {
                    AcaiaUpdater. ispHelper.release();
                    AcaiaUpdater.ispHelper = null;
                }
                //mBluetoothGatt.close();
            } catch (Exception e) {
                e.printStackTrace();
                ;
            }
            try {
                EventBus.getDefault().post(new ScaleConnectionEvent(false));
            } catch (Exception e) {
                CommLogger.logv(TAG, "failed disconnect event post");
                e.printStackTrace();
                ;
            }

        }




    }

    @SuppressLint("LongLogTag")
    public synchronized void disconnectBm71() {
        if (mBluetoothAdapter == null || mBM71Gatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        try {
            mBluetoothDevice = null;
            mBluetoothDeviceAddress = null;
            mBM71Gatt.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }

        try {
            if (mBM71Gatt != null) {
                mBM71Gatt.close();
                mBM71Gatt = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }


        try {
            mConnectionState = CONNECTION_STATE_DISCONNECTING;
            mCurrentConnectedDeviceAddr = "";
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("LongLogTag")
    public boolean startScan() {

        if (mBluetoothAdapter == null) {
            if (!initialize()) {
                CommLogger.logv(TAG, "startScan error, cannot initialize");
                return false;
            }
        }

        stopScan();
        if (!mBluetoothAdapter.startLeScan(mLeScanCallback)) {
            Log.d(TAG, "startLeScan Error");
        }
        return true;
    }

    public void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }
    // auto connect

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @SuppressLint("LongLogTag")
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    //  if (device.getAddress().equals("00:1C:97:11:F1:79")) {
                   /* CommLogger.logv(TAG, "onLeScan");
                    CommLogger.logv(TAG, "address" + device.getAddress() + ",RSSI=" + String.valueOf(rssi));
                    // debug
                    //  }
                    final Intent intent = new Intent(ACTION_DEVICE_FOUND);
                    intent.putExtra(EXTRA_DEVICE, device);
                    intent.putExtra(EXTRA_RSSI, rssi);
                    sendBroadcast(intent);*/

                    if (mMode == MODE.DISTANCE) {
                        //Log.v(TAG, "scanned device name: " + device.getName() + ", address: " + device.getAddress());
                        if (distanceConnectHelper.onNewScannedDevice(device, (double) rssi) == true) {

                            stopScan();
                            if (mConnectionState != CONNECTION_STATE_CONNECTED) {
                                if (distanceConnectHelper.getTargetBluetoothDevice() != null) {
                                    //Log.v("Distance connect", "Connect to:" + distanceConnectHelper.getTargetBluetoothDevice().getAddress());
                                    connect(distanceConnectHelper.getTargetBluetoothDevice().getAddress());
                                }
                                // Hanjord todo: Work on failure states
                            }
                        }
                    }
                }
            };

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that
        // BluetoothGatt.close() is called
        // such that resources are cleaned up properly. In this particular
        // example, close() is
        // invoked when the UI is disconnected from the Service.
        // close();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        CommLogger.logv(TAG, "onBind");
        // TODO Auto-generated method stub
        EventBus.getDefault().register(this);
        // return mBinder;
        return mBinder;
    }

    @Override
    public void onCreate() {
        // Handler will get associated with the current thread,
        // which is the main thread.
        handler = new Handler();
        self = this;

        // Init BM71 listener...
        mBM71Listener = new Bm71GattListener();
        mBM71GattAdapter = new AospGattAdapter(getApplicationContext(), mBM71Listener);

        super.onCreate();
    }

    /*public void setActivity(Activity activity) {
        parentActivity = activity;
    }*/

    public class LocalBinder extends Binder {
        public ScaleCommunicationService getService() {
            return ScaleCommunicationService.this;
        }
    }

    @SuppressLint("LongLogTag")
    public boolean initialize() {

        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        if (mBluetoothGatt != null) {
            mConnectionState = CONNECTION_STATE_DISCONNECTED;
            mBluetoothGatt.close();
        }
        CommLogger.logv(TAG, "init service!");
        mBluetoothGatt = null;
        reliableSenderQueue = new ReliableSenderQueue(this);
        //sendingQueue=new LinkedList<>();


        return true;
    }

    @SuppressLint("LongLogTag")
    public void onEvent(DisconnectDeviceEvent event){
        // disconnect device if connected
        //Log.v(TAG,"Disconnect!");
        disconnect();
        mConnectionState=CONNECTION_STATE_DISCONNECTED;
    }

    private boolean sendCmd(byte[] Command) {

        if (mBluetoothGatt == null && mBM71Gatt == null) {
            return false;
        }
        if (mBluetoothGatt != null) {
            //Log.v("sendCmd", "mBluetoothGatt not null");
            if (mBluetoothGatt
                    .getService(
                            UUID.fromString(ScaleGattAttributes.CSR_JB_UART_TX_PRIMARY_SERVICE_UUID)) != null) {


                BluetoothGattCharacteristic TX_SEC = mBluetoothGatt
                        .getService(
                                UUID.fromString(ScaleGattAttributes.CSR_JB_UART_TX_PRIMARY_SERVICE_UUID))
                        .getCharacteristic(
                                UUID.fromString(ScaleGattAttributes.CSR_JB_UART_TX_SECOND_UUID));
                if (TX_SEC != null) {
                    TX_SEC.setValue(Command);
                    return mBluetoothGatt.writeCharacteristic(TX_SEC);
                } else {
                    return false;
                }
            }
        }

        if (mTransRx != null && mBM71Gatt != null) {

            mTransRx.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
            mTransRx.setValue(Command);
            //Log.v("CINCODEBUG", "bm71 send command, len=%d" + String.valueOf(Command.length));
            for (int i = 0; i != Command.length; i++) {
                //Log.v("SENDDATA", String.valueOf(i) + " " + String.valueOf(Command[i]));
            }
            return mBM71Gatt.writeCharacteristic(mTransRx);
            //return false;
        } else {
            if (mBM71Gatt == null) {
                //Log.v("sendCmd", "mBM71Gatt null");
            }

            if (mTransRx == null) {
                //Log.v("sendCmd", "mTransRx null");
            }
            return false;
        }

    }

    public boolean getBattery() {

        return sendCmdwithResponse(AcaiaCommunicationPacketHelper
                .getBatteryCommand());

    }

    private boolean init_sync() {
        BluetoothGattCharacteristic TX_SEC = mBluetoothGatt
                .getService(
                        UUID.fromString(ScaleGattAttributes.CSR_JB_UART_TX_PRIMARY_SERVICE_UUID))
                .getCharacteristic(
                        UUID.fromString(ScaleGattAttributes.CSR_JB_UART_TX_SECOND_UUID));
        setCharacteristicNotification(TX_SEC, true);
        try {
            if (TX_SEC == null) {
                return false;
            } else {
                // mBluetoothGatt.writeCharacteristic(TX_SEC);
                return true;
            }
        } catch (Exception e) {
            if (e instanceof DeadObjectException) {
                // notify user through a dialog or something that they should either restart bluetooth or their phone.
                // another option is to reset the stack programmatically.
            } else {
                // your choice of whether to rethrow it or treat it the same as DeadObjectException.
            }

            return false;
        }


    }


    public void sendCmdWithLength(final byte[] s_in, final int len) {
        byte[] Command = new byte[len];
        CommLogger.logv4(TAG, "len: " + String.valueOf(len));
        for (int i = 0; i != Command.length; i++) {
            Command[i] = s_in[i];
            CommLogger.logv4(TAG, "send with len: out[i" + String.valueOf(i) + "]=" + String.valueOf(s_in[i]));
        }


        reliableSenderQueue.sendHighPriorityJob(Command, len);
        //sendCmd(Command);
    }

    @SuppressLint("LongLogTag")
    public void sendCmdFromQueue(final byte[] Command) {
        sendCmd(Command);
    }


    public Boolean sendCmdwithResponse(byte[] Command) {
        // reliableSenderQueue.sendHighPriorityJob(Command, 1);
        sendCmdFromQueue(Command);
        return true;
    }

    public Boolean sendHeartBeat() {
        if (!isISPMode())
            sendCmdwithResponse(DataOutHelper.heartBeat());
        return true;
    }

    @SuppressLint("LongLogTag")
    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        if (ScaleGattAttributes.CSR_JB_UART_RX_SECOND_UUID

                .equals(String.valueOf(characteristic.getUuid()))) {

            if (characteristic.getDescriptors().size() != 0) {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptors().get(0);

                descriptor
                        .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            } else {
                Log.i(TAG, "Set descriptor failed!");
            }
        }

    }



    class Bm71GattListener extends Gatt.ListenerHelper {

        Bm71GattListener() {
            super("BM71 GATTListener");
        }

        public void onMtuChanged(Gatt gatt, int mtu, int newState) {
            //Log.v("", "onMtuChanged called newState: " + newState);
            //Log.v("", "MTU changed MTU " + mtu);
            mBM71Gatt.discoverServices();
        }

        @Override
        public void onConnectionStateChange(Gatt gatt, int status, int newState) {

            //Log.d("onConnectionStateChange: DATA TRANSFER ");


            if (newState == BluetoothProfile.STATE_CONNECTED) {
                onBM71Connected();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                onBM71Disonnected();
            }
        }

        @Override
        public void onServicesDiscovered(Gatt gatt, int status) {
            onBM71ServiceDiscover();
            //Log.v("CINCODEBUG", "BM71 discovered service");
        }

        @Override
        public void onCharacteristicRead(Gatt gatt, GattCharacteristic charac,
                                         int status) {
            //Log.v("CINCODEBUG", "BM71 onCharacteristicRead");
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onCharacteristicChanged(Gatt gatt, GattCharacteristic chrc) {
            byte[] input_data = chrc.getValue();
            for (int i = 0; i != input_data.length; i++) {
                //Log.v("Input data", "[" + String.valueOf(i) + "] " + String.valueOf(input_data[i]));
            }

            mConnectionState = CONNECTION_STATE_CONNECTED;

            send_failed_count = 0;
            if (last_received != 0) {
                last_received = System.nanoTime();
            }


            if (!isISPMode()) {
                //Log.v(TAG,"App Mode!");
                if (acaiaScale == null) {
                    if(gatt.getDevice().getName().contains("CINCO") || gatt.getDevice().getName().contains("PYXIS")) {
                        acaiaScale = AcaiaScaleFactory.createAcaiaScale(AcaiaScaleFactory.version_20, getApplicationContext(), self, handler, null, true);
                    }else{
                        acaiaScale = AcaiaScaleFactory.createAcaiaScale(AcaiaScaleFactory.version_20, getApplicationContext(), self, handler, null, false);
                    }
                } else {
                    ////Log.v(TAG, "acaia scale not null");
                    // parse packet


                    if(AcaiaUpdater.ispHelper==null){
                        AcaiaUpdater.ispHelper=new IspHelper(getApplicationContext(), self, handler, AcaiaUpdater.currentFirmware);
                    }

                    AcaiaUpdater.ispHelper.parseDataPacket(chrc.getValue());

                    if(AcaiaUpdater.ispHelper.isISP==ISP_CHECK_APP) {
                        acaiaScale.getScaleCommand().parseDataPacketCinco(chrc.getValue(),acaiaScale);
                    }else if(AcaiaUpdater.ispHelper.isISP==ISP_CHECK_ISP){
                        setIsISP(true);
                    }
                    //  update connection
                }

            }else{
                //Log.v(TAG,"ISP Mode!");
                if(AcaiaUpdater.ispHelper==null){
                    AcaiaUpdater.ispHelper=new IspHelper(getApplicationContext(), self, handler, AcaiaUpdater.currentFirmware);
                }
                AcaiaUpdater.ispHelper.parseDataPacket(chrc.getValue());
            }
        }

        @Override
        public void onCharacteristicWrite(Gatt gatt, GattCharacteristic charac,
                                          int status) {


        }

        @Override
        public void onDescriptorWrite(Gatt gatt, GattDescriptor dsc, int status) {
            //Log.v("CINCODEBUG", "onDescriptorWrite service here");
            BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) dsc
                    .getCharacteristic().getImpl();

        }
    }

    private void onBM71Connected() {
        if (mBM71Gatt != null) {
            if (acaiaScale != null) {
                acaiaScale = null;
            }
            //mBM71Gatt.discoverServices();
            if (true == mBM71Gatt.requestMtu(247)) {
                Log.d("MTU", "RequestMTU return TRUE");
            } else {
                Log.d("MTU", "RequestMTU return TRUE");
            }
        }
    }

    private void onBM71Disonnected() {
        if (mBM71Gatt != null) {
            //mBM71Gatt.disconnect();
            mConnectionState = CONNECTION_STATE_DISCONNECTED;
            EventBus.getDefault().post(new ScaleConnectionEvent(false));
        }
    }

    private void onBM71ServiceDiscover() {
        //Log.v("CINCODEBUG", "calling mService.setCharacteristicNotification:Activity Transperent");

        if (mBM71Gatt != null) {
            GattService proprietary = mBM71Gatt.getService(SERVICE_ISSC_PROPRIETARY);
            List<GattService> services = mBM71Gatt.getServices();
            for (int i = 0; i != services.size(); i++) {
                //Log.v("CINCODEBUG", "service uuid=" + String.valueOf(services.get(i).getUuid().toString()));
            }
            if (services.size() == 0) {
                //Log.v("CINCODEBUG", "no servive");
                return;
            }

            mConnectionState = CONNECTION_STATE_CONNECTED;

            mTransTx = proprietary.getCharacteristic(CHR_ISSC_TRANS_TX);
            mTransRx = proprietary.getCharacteristic(CHR_ISSC_TRANS_RX);
            mAirPatch = proprietary.getCharacteristic(CHR_AIR_PATCH);
            mTransCtrl = proprietary.getCharacteristic(CHR_ISSC_TRANS_CTRL);

            //Log.v("CINCODEBUG", "calling mService.setCharacteristicNotification:Activity Transperent");
            boolean set = mBM71Gatt.setCharacteristicNotification(mTransTx, true);
            //Log.v("hanjord", "hanjord mTransTx " + mTransTx.getUuid().toString());
            //Log.v("CINCODEBUG", "set notification:" + set);
            GattDescriptor dsc = mTransTx
                    .getDescriptor(DES_CLIENT_CHR_CONFIG);
            dsc.setValue(dsc
                    .getConstantBytes(GattDescriptor.ENABLE_NOTIFICATION_VALUE));

            mBM71Gatt.writeDescriptor(dsc);
            //android.util.//Log.v("hanjord", "tx descriptor=" + dsc.getUuid().toString());

        } else {
            //Log.v("CINCODEBUG", "mBM71Gatt null");
        }
    }

    public static UUID uuidFromStr(String str) {
        if (!str.matches(".{4}")) {
            return null;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(sPREFIX);
            sb.append(str);
            sb.append(sPOSTFIX);
            return UUID.fromString(sb.toString());
        }
    }

}
