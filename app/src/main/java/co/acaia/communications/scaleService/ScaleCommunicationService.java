package co.acaia.communications.scaleService;


import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import co.acaia.ble.events.ScaleConnectedEvent;
import co.acaia.communications.CommLogger;
import co.acaia.communications.events.SendDataEvent;
import co.acaia.communications.protocol.ProtocolHelper;
import co.acaia.communications.protocol.ScaleGattAttributes;
import co.acaia.communications.protocol.ver20.DataOutHelper;
import co.acaia.communications.reliableQueue.ReliableSenderQueue;
import co.acaia.communications.scale.AcaiaScale;
import co.acaia.communications.scale.AcaiaScale2;
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

public class ScaleCommunicationService extends Service {
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;
    private ScaleCommunicationService self;

    // new structure

    //private AcaiaScale acaiaScale = null;
    private IspHelper ispHelper = null;
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
        CommLogger.logv(TAG, "click start isp");

        // TODO: start firmware update event
        /*if (FirmwareFileEntityHelper.getLatestFirmware() != null)
            if (ispHelper != null) {
                CommLogger.logv(TAG, "start isp");
                ispHelper.startIsp();
                ;
                setIsISP(true);
            } else {
                Log.v(TAG, "ISP helper null!");
                //TODO: ISP helper
                //ispHelper = new IspHelper(getApplicationContext(), self, handler, firmwareFileEntity);
                //ispHelper.startIsp();

                setIsISP(true);
            }*/
    }

    public void onEvent(UpdateStatusEvent event) {
        if (event.status == UpdateStatusEvent.ISPCompletedState) {
            // completed
            releaseISP();
            ;
            // hanjord: release isp
        }
    }

    public void onEvent(final DistanceConnectEvent distanceConnectEvent) {
        if (mConnectionState == CONNECTION_STATE_CONNECTED) {
            disconnect();
        }
        mMode = MODE.DISTANCE;
        this.distanceConnectHelper = new DistanceConnectHelper();
        startScan();
    }

    private void releaseISP() {
        try {
            ispHelper.release();
            ispHelper = null;
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
                        if (ispHelper != null) {
                            EventBus.getDefault().post(new UpdateErrorEvent(UpdateErrorEvent.error_disconnected));
                            ispHelper.release();
                            ispHelper = null;
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
                    }, 800);

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
                        if (acaiaScale == null) {

                            acaiaScale = ProtocolHelper.getAcaiaScaleFromByte(characteristic.getValue(), getApplicationContext(), self, handler);
                        } else {
                            //Log.v(TAG, "acaia scale not null");
                            // parse packet
                            acaiaScale.getScaleCommand().parseDataPacket(characteristic.getValue());
                            //  update connection
                            incomming_msg_counter++;
                            if (incomming_msg_counter > 15) {
                                EventBus.getDefault().post(new ScaleConnectionEvent(true, mCurrentConnectedDeviceAddr, acaiaScale.getProtocolVersion(), mBluetoothDevice));
                                incomming_msg_counter = 0;
                            }
                        }
                    }
                    try {
                        if (ispHelper == null) {
                            // TODO ISP helper
                           // ispHelper = new IspHelper(getApplicationContext(), self, handler, firmwareFileEntity);
                            //ispHelper.parseDataPacket(characteristic.getValue());
                        } else {
                            // parse packet
                            ispHelper.parseDataPacket(characteristic.getValue());
                            //  update connection
                            incomming_msg_counter++;
                            if (incomming_msg_counter > 15) {
                                //EventBus.getDefault().post(new ScaleConnectionEvent(true, mCurrentConnectedDeviceAddr, acaiaScale.getProtocolVersion(), mBluetoothDevice));
                                incomming_msg_counter = 0;
                            }
                        }
                    } catch (Exception e) {

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
        if (isConnected()) {
            release();
            new Thread() {
                public void run() {
                    try {
                        Thread.sleep(1500);
                        connect(targetBtAddress);
                    } catch (Exception e) {
                        e.printStackTrace();
                        ;
                    }
                }
            }.start();
        } else {

            if (mBluetoothAdapter == null || targetBtAddress == null) {
                Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
                return false;
            }

            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(targetBtAddress);
            if (mBluetoothDevice == null) {
                Log.w(TAG, "Device not found.  Unable to connect.");
                return false;
            }

            // We want to directly connect to the device, so we are setting the autoConnect
            // parameter to false.
            mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mGattCallback);
            Log.d(TAG, "Trying to create a new connection.");
            mBluetoothDeviceAddress = targetBtAddress;
            mConnectionState = CONNECTION_STATE_CONNECTING;
        }
        return true;
    }

    @SuppressLint("LongLogTag")
    public void release() {
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
            if (ispHelper != null) {
                ispHelper.release();
                ispHelper = null;
            }
            //mBluetoothGatt.close();
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }

    @SuppressLint("LongLogTag")
    public synchronized void disconnect() {
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
            if (ispHelper != null) {
                ispHelper.release();
                ispHelper = null;
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

    @SuppressLint("LongLogTag")
    public boolean startScan() {

        if (mBluetoothAdapter == null) {
            if (!initialize()) {
                CommLogger.logv(TAG, "startScan error, cannot initialize");
                return false;
            }
        }

        stopScan();
        CommLogger.logv(TAG, "Scan Mode: " +
                Integer.toString(mBluetoothAdapter.getScanMode()) + ", " +
                Boolean.toString(mBluetoothAdapter.isDiscovering()));
        UUID[] uuids = {UUID.fromString(ScaleGattAttributes.CSR_JB_UART_RX_PRIMARY_SERVICE_UUID)};
        if (!mBluetoothAdapter.startLeScan(uuids, mLeScanCallback)) {
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
                        Log.v(TAG, "scanned device name: " + device.getName() + ", address: " + device.getAddress());
                        if (distanceConnectHelper.onNewScannedDevice(device, (double) rssi) == true) {

                            stopScan();
                            if (mConnectionState != CONNECTION_STATE_CONNECTED) {
                                if (distanceConnectHelper.getTargetBluetoothDevice() != null) {
                                    Log.v("Distance connect", "Connect to:" + distanceConnectHelper.getTargetBluetoothDevice().getAddress());
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

    private boolean sendCmd(byte[] Command) {

        // todo: more robust error handling.

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

    }

    @SuppressLint("LongLogTag")
    public void sendCmdFromQueue(final byte[] Command) {
        try {
            if (mBluetoothGatt
                    .getService(
                            UUID.fromString(ScaleGattAttributes.CSR_JB_UART_TX_PRIMARY_SERVICE_UUID)) != null) {
                try {
                    BluetoothGattCharacteristic TX_SEC = mBluetoothGatt
                            .getService(
                                    UUID.fromString(ScaleGattAttributes.CSR_JB_UART_TX_PRIMARY_SERVICE_UUID))
                            .getCharacteristic(
                                    UUID.fromString(ScaleGattAttributes.CSR_JB_UART_TX_SECOND_UUID));
                    if (TX_SEC == null) {
                        Log.w(TAG, "Found no Characteristic");
                        // TODO: handle connection failure
                    } else {

                        TX_SEC.setValue(Command);
                        TX_SEC.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                        CommLogger.logv4(TAG, "from queuesend command!" + String.valueOf(Command.length));
                        mBluetoothGatt.writeCharacteristic(TX_SEC);
                        CommLogger.logv(TAG, "time apart=" + String.valueOf((System.nanoTime() - last_received) / 1000000.0));
                        if ((System.nanoTime() - last_received) / 1000000.0 > 4000) {
                            // disconnect();
                            if (isConnected()) {
                                CommLogger.logv(TAG, "manual disconnect!");
                                send_failed_count++;
                                if (send_failed_count > send_failed_threshold) {
                                    //disconnect();
                                    ;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    send_failed_count++;
                    if (send_failed_count > send_failed_threshold) {
                        //disconnect();
                        ;
                    }
                }


            } else {
                // no connection
                send_failed_count++;
                if (send_failed_count > send_failed_threshold) {
                    // disconnect();
                    ;
                }
            }
        } catch (Exception e) {

        }

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


}
