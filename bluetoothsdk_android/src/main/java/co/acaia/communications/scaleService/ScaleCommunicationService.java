package co.acaia.communications.scaleService;


import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.UUID;

import co.acaia.ble.events.ScaleConnectedEvent;
import co.acaia.brewguide.events.BrewguideCommandEvent;
import co.acaia.brewguide.events.BrewguideInfoEvent;
import co.acaia.brewguide.events.BrewguideStepEvent;
import co.acaia.brewguide.events.BrewguideStringEvent;
import co.acaia.brewguide.events.PearlSModeEvent;
import co.acaia.brewguide.events.RequestPearlSStatueEvent;
import co.acaia.communications.CommLogger;
import co.acaia.communications.events.DistanceConnectEvent;
import co.acaia.communications.events.ManualDisconnectEvent;
import co.acaia.communications.events.ModeEvent;
import co.acaia.communications.events.ScaleDataEvent;
import co.acaia.communications.events.SendDataEvent;
import co.acaia.communications.events.ServiceBindedEvent;
import co.acaia.communications.helpers.DistanceConnectHelper;
import co.acaia.communications.protocol.ProtocolHelper;
import co.acaia.communications.protocol.ScaleGattAttributes;
import co.acaia.communications.protocol.old.pearldataparser.PearlDataHelper;
import co.acaia.communications.protocol.ver20.BrewguideProtocol;
import co.acaia.communications.protocol.ver20.DataOutHelper;
import co.acaia.communications.protocol.ver20.ScaleProtocol;
import co.acaia.communications.reliableQueue.ReliableSenderQueue;
import co.acaia.communications.scale.AcaiaScale;
import co.acaia.communications.scale.AcaiaScaleFactory;
import co.acaia.communications.scaleService.aosp.AospGattAdapter;
import co.acaia.communications.scaleService.gatt.Gatt;
import co.acaia.communications.scaleService.gatt.GattAdapter;
import co.acaia.communications.scaleService.gatt.GattCharacteristic;
import co.acaia.communications.scaleService.gatt.GattDescriptor;
import co.acaia.communications.scaleService.gatt.GattService;
import co.acaia.communications.scalecommand.AutoConnectEvent;
import co.acaia.communications.scalecommand.ScaleCommandEvent;
import co.acaia.communications.scalecommand.ScaleCommandType;
import co.acaia.communications.scalecommand.ScaleConnectionCommandEvent;
import co.acaia.communications.scalecommand.ScaleConnectionCommandEventType;
import co.acaia.communications.scaleevent.NewScaleConnectionStateEvent;
import co.acaia.communications.scaleevent.ScaleConnectionEvent;
import co.acaia.firmwaretool.Events.ConnectionEvent;
import co.acaia.firmwaretool.Events.UpdateErrorEvent;

public class ScaleCommunicationService extends Service {
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mLastConnectedBluetoothDeviceAddress = null;
    private String mBluetoothDeviceAddress;

    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;

    // Microchip
    private Bm71GattListener mBM71Listener;
    private Gatt mBM71Gatt = null;
    private GattAdapter mBM71GattAdapter = null;
    private GattCharacteristic mTransTx;
    private GattCharacteristic mTransRx;
    private GattCharacteristic mAirPatch;
    private GattCharacteristic mTransCtrl;


    private ScaleCommunicationService self;

    // new structure

    private AcaiaScale acaiaScale = null;

    // Acaia protocol helper
    private AcaiaProtocolHelper acaiaProtocolHelper;

    // Reliable
    private ReliableSenderQueue reliableSenderQueue;

    // debug
    Handler handler;

    // Connection State
    public final static int UNIT_GRAM = 0;
    public final static int UNIT_OUNCE = 1;

    public final static int TEMP_UNIT_C = 0;
    public final static int TEMP_UNIT_F = 1;

    private int mConnectionState = CONNECTION_STATE_DISCONNECTED;

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

    public final static String ACTION_TEMP =
            "co.acaia.scale.service.ACTION_TEMP";
    public final static String ACTION_FELLOW_EVENT =
            "co.acaia.scale.service.ACTION_FELLOW_EVENT";

    public final static String ACTION_FELLOW_TIMER =
            "co.acaia.scale.service.ACTION_FELLOW_TIMER";

    public final static String EXTRA_CONNECTION_STATE = "co.acaia.scale.service.EXTRA_CONNECTION_STATE";
    public final static String EXTRA_DEVICE = "co.acaia.scale.service.EXTRA_DEVICE";
    public final static String EXTRA_RSSI = "co.acaia.scale.service.EXTRA_RSSI";
    public final static String EXTRA_DATA =
            "co.acaia.scale.service.EXTRA_DATA";
    public final static String EXTRA_EVENT =
            "co.acaia.scale.service.EXTRA_EVENT";
    public final static String EXTRA_UNIT =
            "co.acaia.scale.service.UNIT";
    public final static String EXTRA_DATA_TYPE =
            "co.acaia.scale.service.DATA_TYPE";

    public final static String EXTRA_TEMP_TYPE =
            "co.acaia.scale.service.EXTRA_TEMP_TYPE";


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
    public final static int DATA_TYPE_TEMP = 7;

    public static final String TAG = "SCService";

    private final LocalBinder mBinder = new LocalBinder();

    // incomming packet debug
    private long last_received = 0;
    private String mCurrentConnectedDeviceAddr = "";
    private long incomming_msg_counter = 0;

    // send failed
    private long send_failed_count = 0;
    private static final int send_failed_threshold = 3;
    private Activity parentActivity;
    //private Queue<byte[]> sendingQueue;

    private boolean isISPMode = false;
    public boolean scaleGetStatue = false;

    // New java acaia data parser
    PearlDataHelper pearlDataHelper;

    // Distance connect helper
    DistanceConnectHelper distanceConnectHelper;

    public synchronized void setIsISP(boolean isIsp) {
        CommLogger.logv(TAG, "set is isp!");
        isISPMode = isIsp;
    }

    public synchronized boolean isISPMode() {
        return isISPMode;
    }


    public enum MODE {
        NORMAL,
        SETTE,
        FELLOW,
        DISTANCE,
    }

    private MODE mMode = MODE.NORMAL;

    public synchronized void setMode(MODE mode) {
        Log.e(TAG, "setMode: " + mode);
        if (mMode != mode) {
            // before change mode
            switch (mMode) {
                case NORMAL:
                    if (isConnected()) {
                        // remember last connected device address
                        mLastConnectedBluetoothDeviceAddress = mBluetoothDeviceAddress;
                        Log.e(TAG, "mLastConnectedBluetoothDeviceAddress: " + mLastConnectedBluetoothDeviceAddress);

                        disconnect();
                    } else {
                        Log.e(TAG, "no current connected device");
                        mLastConnectedBluetoothDeviceAddress = null;
                    }
                    break;
                case SETTE:
                    stopScan();
                    break;
                case FELLOW:
                    stopScan();
                    break;
                default:
                    break;
            }

            mMode = mode;

            // after change mode
            switch (mMode) {
                case NORMAL:
                    // if last connected scale exists, re-connect it
                    if (TextUtils.isEmpty(mLastConnectedBluetoothDeviceAddress)) {
                        Log.e(TAG, "no last connected device");
                    } else {
                        final String address = mLastConnectedBluetoothDeviceAddress;
                        Log.e(TAG, "re-connect: " + mLastConnectedBluetoothDeviceAddress);
                        connect(address);

                        mLastConnectedBluetoothDeviceAddress = null;
                    }
                    break;
                case SETTE:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startScan();
                        }
                    }, 1000);

                    break;
                case FELLOW:
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startScan();
                        }
                    }, 1000);

                    break;
                default:
                    break;
            }
        }
    }

    // Parse Pearl S setting event

    @Subscribe
    public void onEvent(PearlSModeEvent pearlSModeEvent) {
        Log.v("PearlSModeEvent", "Mode=" + String.valueOf(pearlSModeEvent.mode_item.ordinal()) + " " + String.valueOf(pearlSModeEvent.on_off));
        sendCmd(DataOutHelper.setting_chg((short) pearlSModeEvent.mode_item.ordinal(), (short) pearlSModeEvent.on_off));
    }

    @Subscribe
    public void onEvent(RequestPearlSStatueEvent requestPearlSStatueEvent) {
        if (acaiaScale != null) {
            acaiaScale.set_get_status(true);

        }
        scaleGetStatue = true;
    }

    // Parse Brewguide event
    @Subscribe
    public void onEvent(BrewguideCommandEvent event) {
        acaiaScale.set_get_status(false);
        scaleGetStatue = false;

        Log.v("Brewguide event", "Got event:" + String.valueOf(event.getCommandType()));
        if (event.getCommandType() == ScaleProtocol.ECMD.new_cmd_sync_brewguide_s.ordinal()) {
            // Send Brewguide update
            //BrewguideProtocol.new_brewguide_setting brewguide_setting=new BrewguideProtocol.new_brewguide_setting();
            if (acaiaScale != null) {
                acaiaScale.set_get_status(false);
            }
            sendCmd(DataOutHelper.app_command((short) ScaleProtocol.ECMD.new_cmd_sync_brewguide_s.ordinal()));

        }

        if (event.getCommandType() == ScaleProtocol.ECMD.new_cmd_sync_hello_s.ordinal()) {
            // Send Brewguide update
            //BrewguideProtocol.new_brewguide_setting brewguide_setting=new BrewguideProtocol.new_brewguide_setting();
            if (acaiaScale != null) {
                acaiaScale.set_get_status(false);
            }
            sendCmd(DataOutHelper.app_command((short) ScaleProtocol.ECMD.new_cmd_sync_hello_s.ordinal()));

        }

        if (event.getCommandType() == BrewguideProtocol.BREWGUIDE_CMD.brewguide_cmd_app_page_len.ordinal()) {
            // Send Brewguide update
            //BrewguideProtocol.new_brewguide_setting brewguide_setting=new BrewguideProtocol.new_brewguide_setting();
            if (acaiaScale != null) {
                acaiaScale.set_get_status(false);
            }
            sendCmd(DataOutHelper.send_brewguide_cmd(event.getCommandType(), event.getCommandVal()));

        }

        //
        // sendCmd

    }

    @Subscribe
    public void onEvent(BrewguideStringEvent brewguideStringEvent) {
        sendCmd(DataOutHelper.bt2SendBrewguideStr(brewguideStringEvent.brewguide_data_string));
    }

    // Parse Brewguide event
    @Subscribe
    public void onEvent(BrewguideInfoEvent brewguideInfoEvent) {
        sendCmd(DataOutHelper.send_brewguide_info(brewguideInfoEvent.brewguide_data_info));
    }

    @Subscribe
    public void onEvent(BrewguideStepEvent brewguideStepEvent) {
        Log.v("BrewguideStepEvent", "Sendiing BrewguideStepEvent");
        for (int i = 0; i != 12; i++) {
            Log.v("BrewguideStepEvent", "data[%d]" + String.valueOf(i) + " " + String.valueOf(brewguideStepEvent.brewguide_data_step.getByteArray()[i]));
        }
        sendCmd(DataOutHelper.send_brewguide_step(brewguideStepEvent.brewguide_data_step));
    }

    // Parse scale setting command
    @Subscribe
    public void onEvent(ScaleCommandEvent event) {
//        Log.e(TAG, "onEvent, type: " + event.getCommandType() + ", value" + event.getCommandVal());
        if (event.getCommandType() == ScaleCommandType.command_id.GET_MODE.ordinal()) {
            EventBus.getDefault().post(new ModeEvent(mMode.ordinal()));
            return;
        } else if (event.getCommandType() == ScaleCommandType.command_id.SEND_SET_MODE.ordinal()) {
            if (event.getCommandVal() == ScaleCommandType.set_mode.NORMAL.ordinal()) {
                setMode(MODE.NORMAL);
            } else if (event.getCommandVal() == ScaleCommandType.set_mode.SETTE.ordinal()) {
                setMode(MODE.SETTE);
            } else if (event.getCommandVal() == ScaleCommandType.set_mode.FELLOW.ordinal()) {
                setMode(MODE.FELLOW);
            }
            return;
        }

        if (acaiaScale != null) {
            if (event.getCommandType() == ScaleCommandType.command_id.SEND_CHANGE_UNIT.ordinal()) {
                acaiaScale.getScaleCommand().setUnit((short) event.getCommandVal());
            } else if (event.getCommandType() == ScaleCommandType.command_id.SEND_TARE.ordinal()) {
                acaiaScale.getScaleCommand().setTare();
            } else if ((event.getCommandType() == ScaleCommandType.command_id.SEND_SOUND_ONOFF.ordinal())) {
                if (event.getCommandVal() == ScaleCommandType.set_sound_on_off.ON.ordinal()) {
                    acaiaScale.getScaleCommand().setBeep(true);
                } else if (event.getCommandVal() == ScaleCommandType.set_sound_on_off.OFF.ordinal()) {
                    acaiaScale.getScaleCommand().setBeep(false);
                }
            } else if (event.getCommandType() == ScaleCommandType.command_id.SEND_SET_AUTOOFF.ordinal()) {
                acaiaScale.getScaleCommand().setAutoOffTime(event.getCommandVal());
            } else if (event.getCommandType() == ScaleCommandType.command_id.SEND_SET_CAPACITY.ordinal()) {
                CommLogger.logv(TAG, "send set capacity" + String.valueOf(event.getCommandVal()));
                acaiaScale.getScaleCommand().setCapacity((int) event.getCommandVal());
            } else if (event.getCommandType() == ScaleCommandType.command_id.SEND_SET_DISABLE_KEY.ordinal()) {
                acaiaScale.getScaleCommand().setKeyDisabledWithTime(event.getCommandVal());
            } else if (event.getCommandType() == ScaleCommandType.command_id.GET_AUTO_OFF_TIME.ordinal()) {
                acaiaScale.getScaleCommand().getAutoOffTime();
            } else if (event.getCommandType() == ScaleCommandType.command_id.GET_UNIT.ordinal()) {
                // TODO
            } else if (event.getCommandType() == ScaleCommandType.command_id.GET_SOUND_ONOFF.ordinal()) {
                acaiaScale.getScaleCommand().getBeep();
            } else if (event.getCommandType() == ScaleCommandType.command_id.GET_DISABLE_KEY_TIME.ordinal()) {
                // TODO
                acaiaScale.getScaleCommand().getKeyDisabledElapsedTime();
            } else if (event.getCommandType() == ScaleCommandType.command_id.GET_BATT.ordinal()) {
                // TODO
                acaiaScale.getScaleCommand().getBattery();
            } else if (event.getCommandType() == ScaleCommandType.command_id.GET_CONNECTION_STATE.ordinal()) {
                // Get conmnection state async
                if (isConnected()) {
                    EventBus.getDefault().post(new ScaleConnectionEvent(true, mCurrentConnectedDeviceAddr, acaiaScale.getProtocolVersion(), mBluetoothDevice));
                } else {
                    EventBus.getDefault().post(new ScaleConnectionEvent(false));
                }
            } else if (event.getCommandType() == ScaleCommandType.command_id.SEND_TIMER_COMMAND.ordinal()) {
                CommLogger.logv(TAG, "got set timer event " + String.valueOf(event.getCommandVal()));
                int timer_command = event.getCommandVal();
                if (timer_command == ScaleCommandType.set_timer.START.ordinal()) {
                    acaiaScale.getScaleCommand().startTimer();
                } else if (timer_command == ScaleCommandType.set_timer.PAUSE.ordinal()) {
                    acaiaScale.getScaleCommand().pauseTimer();
                } else if (timer_command == ScaleCommandType.set_timer.STOP.ordinal()) {
                    acaiaScale.getScaleCommand().stopTimer();
                }
            } else if (event.getCommandType() == ScaleCommandType.command_id.GET_TIMER.ordinal()) {
                //CommLogger.logv(TAG, "start get timer");
                acaiaScale.getScaleCommand().getTimer();
            } else if (event.getCommandType() == ScaleCommandType.command_id.GET_CAPACITY.ordinal()) {
                CommLogger.logv(TAG, "start get capacity");
                acaiaScale.getScaleCommand().getCapacity();
            } else if (event.getCommandType() == ScaleCommandType.command_id.GET_FIRMWAREV.ordinal()) {
                acaiaScale.getScaleCommand().getFirmwareInfo();
            } else if (event.getCommandType() == ScaleCommandType.command_id.SEND_SET_KETTLE_TARGET_TEMP.ordinal()) {
                acaiaScale.getScaleCommand().setKettleTargetTemp(event.getCommandVal());
            } else if (event.getCommandType() == ScaleCommandType.command_id.SEND_SET_KETTLE_ONOFF.ordinal()) {
                acaiaScale.getScaleCommand().setKettleOnOff(event.getCommandVal() == 1);
            }
        } else if (acaiaScale == null) {
            if (event.getCommandType() == ScaleCommandType.command_id.GET_CONNECTION_STATE.ordinal()) {
                // Get conmnection state async
                if (isConnected()) {
                    EventBus.getDefault().post(new ScaleConnectionEvent(true, mCurrentConnectedDeviceAddr, acaiaScale.getProtocolVersion(), mBluetoothDevice));
                } else {
                    EventBus.getDefault().post(new ScaleConnectionEvent(false));
                }
            }
        }
    }

    // Parse scale connection command
    @Subscribe
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


    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            CommLogger.logv("CINCODEBUG", "onConnectionStateChange  " + String.valueOf(newState));
            try {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    String connection_str = "";
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        mConnectionState = CONNECTION_STATE_CONNECTING;
                        CommLogger.logv(TAG, "Connected to GATT server.");
                        CommLogger.logv(TAG, "Attempting to start service discovery:"
                        );
                        if (gatt != null) {
                            mBluetoothGatt.discoverServices();
                            mCurrentConnectedDeviceAddr = gatt.getDevice().getAddress();
                            mBluetoothDevice = gatt.getDevice();
                            CommLogger.logv(TAG, "current connected addr=" + mCurrentConnectedDeviceAddr);
                            connection_str = ACTION_CONNECTION_STATE_CONNECTED;
                            incomming_msg_counter = 0;
                            if (acaiaScale != null) {
                                acaiaScale = null;
                            }


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
                    Log.d(TAG, "onConnectionStateChange error:" + Integer.toString(status));
                }
            } catch (Exception e) {
                e.printStackTrace();
                EventBus.getDefault().post(new UpdateErrorEvent(UpdateErrorEvent.error_bluetooth, e.getMessage()));

            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            CommLogger.logv("CINCODEBUG", "onServicesDiscovered received: " + status);


            init_sync();

            try {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    EventBus.getDefault().post(new ScaleConnectedEvent());
                    CommLogger.logv(TAG, "Service discover OK");
                    //mBluetoothGatt.beginReliableWrite();


                    //init_sync();
                    // Hanjord 20171213:
                    // Add a hack here...
                    if (acaiaProtocolHelper != null) {
                        acaiaProtocolHelper.shutdown_protocol_helper();
                        acaiaProtocolHelper = null;
                    }


                    new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            acaiaProtocolHelper = new AcaiaProtocolHelper();
                            acaiaProtocolHelper.start();

                        }
                    }.start();
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
            CommLogger.logv("CINCODEBUG", "onCharacteristicRead received: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            CommLogger.logv("CINCODEBUG", "onCharacteristicWrite received: " + Integer.toString(status));
            CommLogger.logv("ack_event_test", "onCharacteristicWrite received: " + Integer.toString(status));
        }

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            CommLogger.logv("CINCODEBUG", "onDescriptorRead received");
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            CommLogger.logv("CINCODEBUG", "onDescriptorWrite received: " + Integer.toString(status));
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
            CommLogger.logv("CINCODEBUG", "onCharacteristicChanged received " + gatt.getDevice().getAddress() + " " + mCurrentConnectedDeviceAddr);

            // Only connect when
            processOnCharaChanged(gatt, characteristic);
            byte[] test_data = characteristic.getValue();
            for (int i = 0; i != test_data.length; i++) {
                Log.v("Input Data2", "[" + String.valueOf(i) + "] " + String.valueOf(test_data[i]));
            }

        }
    };

    private void processOnCharaChanged(BluetoothGatt gatt,
                                       BluetoothGattCharacteristic characteristic) {
        byte[] test_data = characteristic.getValue();
        for (int i = 0; i != test_data.length; i++) {
            Log.v("Input Data", "[" + String.valueOf(i) + "] " + String.valueOf(test_data[i]));
        }
        try {

            if (gatt.getDevice().getAddress().equals(mCurrentConnectedDeviceAddr)) {
                mConnectionState = CONNECTION_STATE_CONNECTED;

                // CommLogger.logv(TAG,"chara len="+String.valueOf(readLen));
//                if (last_received == 0) {
//                    EventBus.getDefault().post(new NewScaleConnectionStateEvent(mCurrentConnectedDeviceAddr));
//                }
                // CommLogger.logv(TAG, "last received ttl=" + String.valueOf((System.nanoTime() - last_received) / 1000000.0));
                send_failed_count = 0;
                if (last_received != 0) {
                    last_received = System.nanoTime();
                }


                // need to know if ISP mode...


                if (!isISPMode()) {
                    if (acaiaScale == null) {

                        if (mMode == MODE.FELLOW) {
                            acaiaScale = AcaiaScaleFactory.createAcaiaScale(AcaiaScaleFactory.version_sette, getApplicationContext(), self, handler, null, false);
                        } else {
                            acaiaScale = ProtocolHelper.getAcaiaScaleFromByte
                                    (pearlDataHelper, characteristic.getValue(), getApplicationContext(), self, handler);
                            Log.v("CINCODEBUG", "acaia scale=" + String.valueOf(acaiaScale.getProtocolVersion()));
                        }

                    } else {
                        //Log.v(TAG, "acaia scale not null");
                        // parse packet
                        acaiaScale.getScaleCommand().parseDataPacket(characteristic.getValue());
                        //  update connection
                        incomming_msg_counter++;
                        if (incomming_msg_counter > 15) {
                            if (last_received == 0) {
                                EventBus.getDefault().post(new NewScaleConnectionStateEvent(mCurrentConnectedDeviceAddr));
                                last_received = System.nanoTime();
                            }

                            EventBus.getDefault().post(new ScaleConnectionEvent(true, mCurrentConnectedDeviceAddr, acaiaScale.getProtocolVersion(), mBluetoothDevice));
                            incomming_msg_counter = 0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            EventBus.getDefault().post(new UpdateErrorEvent(UpdateErrorEvent.error_bluetooth, e.getMessage()));
            e.printStackTrace();
        }
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Subscribe
    public void onEvent(ScaleDataEvent event) {
        if (acaiaProtocolHelper != null) {
            acaiaProtocolHelper.shutdown_protocol_helper();
            acaiaProtocolHelper = null;
        }
    }

    @Subscribe
    public void onEvent(ScaleConnectionEvent event) {

    }

    @Subscribe
    public void onEvent(SendDataEvent sendDataEvent) {
        CommLogger.logv(TAG, "Got send event");
        byte[] debug = sendDataEvent.out_data;
       /* for (int i = 0; i != debug.length; i++) {
            CommLogger.logv(TAG, "Sent [" + String.valueOf(i) + "]" + "=" + String.valueOf(debug[i]));
        }*/
        sendCmd(sendDataEvent.out_data);
        //sendCmdwithResponse(sendDataEvent.out_data);
    }

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

            if (mBluetoothGatt != null) {
                Log.v(TAG, "mBluetoothGatt not null!");
            }
            // We want to directly connect to the device, so we are setting the autoConnect
            // parameter to false.
            stopScan();

            /*mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mGattCallback);
            Log.d(TAG, "Trying to create a new connection.");
            mBluetoothDeviceAddress = targetBtAddress;
            mConnectionState = CONNECTION_STATE_CONNECTING;
*/
            // Add Microchip connect to device here
            if (mBluetoothDevice.getName().contains("CINCO") || mBluetoothDevice.getName().contains("PEARLS")) {
                Log.v(TAG, "Trying to create a new connection. Pearls cinco");
                mBM71Gatt = mBM71GattAdapter.connectGatt(getApplicationContext(), false, mBM71Listener, mBluetoothDevice);
                mBluetoothGatt = null;
            } else {
                mBluetoothGatt = mBluetoothDevice.connectGatt(this, false, mGattCallback);
                //Log.d(TAG, "Trying to create a new connection.");
                mBluetoothDeviceAddress = targetBtAddress;
                mConnectionState = CONNECTION_STATE_CONNECTING;

            }

        }
        return true;
    }

    public void release() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if (acaiaScale != null) {
            acaiaScale.release();
            acaiaScale = null;
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


    }

    public synchronized void disconnect() {

        if (mBM71Gatt == null) {
            Log.w(TAG, "mBluetoothAdapter not initialized");
            return;
        }

        try {
            EventBus.getDefault().post(new ScaleConnectionEvent(false));
            EventBus.getDefault().post(new ConnectionEvent(false));
        } catch (Exception e) {
            CommLogger.logv(TAG, "failed disconnect event post");
        }

        if (acaiaScale != null) {
            acaiaScale.release();
            acaiaScale = null;
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

    @Subscribe
    public void onEvent(ManualDisconnectEvent manualDisconnectEvent) {
        // Disconnect here
        disconnect();
        ;
    }

    /**
     * Distance connect for Brewguide App.
     *
     * @param distanceConnectEvent
     */

    @Subscribe
    public void onEvent(final DistanceConnectEvent distanceConnectEvent) {
        if (mConnectionState == CONNECTION_STATE_CONNECTED) {
            disconnect();
        }
        mMode = MODE.DISTANCE;
        this.distanceConnectHelper = new DistanceConnectHelper();
        startScan();
    }

    /**
     * @param autoConnectEvent
     */

    @Subscribe
    public void onEvent(final AutoConnectEvent autoConnectEvent) {
        CommLogger.logv(TAG, "Auto connect!");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                autoConnect(autoConnectEvent.getScanTimeOut());
            }
        }, 500);

    }

    public boolean autoConnect(final int scanTimeOut) {
        if (mBluetoothAdapter == null) {
            if (!initialize()) {
                CommLogger.logv(TAG, "startScan error, cannot initialize");
                return false;
            }
        }


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothAdapter.stopLeScan(mAutoconnectLeScanCallback);
            }
        }, scanTimeOut);

        UUID[] uuids = {UUID.fromString(ScaleGattAttributes.CSR_JB_UART_RX_PRIMARY_SERVICE_UUID)};
        if (!mBluetoothAdapter.startLeScan(uuids, mAutoconnectLeScanCallback)) {
            Log.d(TAG, "startLeScan Error");
            return false;
        }
        return true;
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mAutoconnectLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    CommLogger.logv(TAG, "onLeScan Auto connect");
                    CommLogger.logv(TAG, "address" + device.getAddress() + ",RSSI=" + String.valueOf(rssi));
                    CommLogger.logv(TAG, "Auto connecting:" + device.getAddress());

                    String deviceName = device.getName();
                    boolean bConnect = false;

                    if (mMode == MODE.FELLOW) {
                        if (!TextUtils.isEmpty(deviceName) && deviceName.startsWith("FELLOW")) {
                            bConnect = true;
                        }
                    } else if (mMode == MODE.SETTE) {
                        if (!TextUtils.isEmpty(deviceName) && deviceName.startsWith("BARW270")) {
//                            Log.e(TAG, "found sette device: " + deviceName);
                            bConnect = true;
                        }
                    } else {
                        if (!TextUtils.isEmpty(deviceName) && (((deviceName.startsWith("ACAIA") || deviceName.startsWith("PROCH") || deviceName.startsWith("CINCO") || deviceName.startsWith("PEARLS"))))) {
                            //if (deviceName.startsWith("CINCO")  ) {
                            bConnect = true;
                        }
                    }


                    if (bConnect) {
                        mBluetoothAdapter.stopLeScan(mAutoconnectLeScanCallback);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new ScaleConnectionCommandEvent(ScaleConnectionCommandEventType.connection_command.CONNECT.ordinal(), device.getAddress()));
                            }
                        }, 2000);

                    }

                }
            };

    public boolean startScan() {

        if (mBluetoothAdapter == null) {
            if (!initialize()) {
                Log.e(TAG, "startScan error, cannot initialize");
                CommLogger.logv(TAG, "startScan error, cannot initialize");
                return false;
            }
        }

        stopScan();
        CommLogger.logv(TAG, "Scan Mode: " +
                Integer.toString(mBluetoothAdapter.getScanMode()) + ", " +
                Boolean.toString(mBluetoothAdapter.isDiscovering()));

        //Log.v("CINCODEBUG","mConnectionState:"+String.valueOf(mConnectionState));

        UUID[] uuids = {UUID.fromString(ScaleGattAttributes.CSR_JB_UART_RX_PRIMARY_SERVICE_UUID), UUID.fromString(ScaleGattAttributes.MICROCHIP_UART_PRIMARY)};
        CommLogger.logv(TAG, "UUIDS: " + String.valueOf(uuids.toString())
        );
        /*if (!mBluetoothAdapter.startLeScan(uuids, mLeScanCallback)) {
                    Log.d(TAG, "startLeScan Error");
        }*/
        if (mConnectionState != CONNECTION_STATE_DISCONNECTED) {
            //return false;
        }

        if (!mBluetoothAdapter.startLeScan(mLeScanCallback)) {
            Log.d(TAG, "startLeScan Error");
        }
        return true;
    }

    public void stopScan() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    // auto connect

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    final String deviceName = device.getName();
                    if (deviceName != null) {

                        /**
                         * Hanjord: Add distance connect
                         */
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

                        } else if (mMode == MODE.FELLOW) {
                            if (!TextUtils.isEmpty(deviceName) && deviceName.startsWith("FELLOW")) {
//                            Log.e(TAG, "found sette device: " + deviceName);
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    EventBus.getDefault().post(new ScaleConnectionCommandEvent(ScaleConnectionCommandEventType.connection_command.CONNECT.ordinal(), device.getAddress()));
//                                }
//                            }, 500);
//                            stopScan();

                                final Intent intent = new Intent(ACTION_DEVICE_FOUND);
                                intent.putExtra(EXTRA_DEVICE, device);
                                intent.putExtra(EXTRA_RSSI, rssi);
                                sendBroadcast(intent);
                            }
                        } else if (mMode == MODE.SETTE) {
                            if (!TextUtils.isEmpty(deviceName) && deviceName.startsWith("BARW270")) {
//                            Log.e(TAG, "found sette device: " + deviceName);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        EventBus.getDefault().post(new ScaleConnectionCommandEvent(ScaleConnectionCommandEventType.connection_command.CONNECT.ordinal(), device.getAddress()));
                                    }
                                }, 500);
                                stopScan();
                            }
                        } else {
                            if (!TextUtils.isEmpty(deviceName) && ((deviceName.startsWith("ACAIA") || (deviceName.startsWith("PROCH") || deviceName.startsWith("CINCO") || deviceName.startsWith("PEARLS"))))) {
                                //if (!TextUtils.isEmpty(deviceName) && (deviceName.startsWith("CINCO" ))) {
                                final Intent intent = new Intent(ACTION_DEVICE_FOUND);
                                intent.putExtra(EXTRA_DEVICE, device);
                                intent.putExtra(EXTRA_RSSI, rssi);
                                sendBroadcast(intent);
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
        CommLogger.logv7(TAG, "onBind");
        // TODO Auto-generated method stub
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new ServiceBindedEvent());
        // return mBinder;
        return mBinder;
    }

    @Override
    public void onCreate() {
        // Handler will get associated with the current thread,
        // which is the main thread.
        handler = new Handler();

        // Microchip BM71
        mBM71Listener = new Bm71GattListener();
        mBM71GattAdapter = new AospGattAdapter(getApplicationContext(), mBM71Listener);

        self = this;

        super.onCreate();
    }

    public void setActivity(Activity activity) {
        parentActivity = activity;
    }

    public class LocalBinder extends Binder {
        public ScaleCommunicationService getService() {
            return ScaleCommunicationService.this;
        }
    }

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
        Log.v("sendCmd", "command len:" + String.valueOf(Command.length));
        for (int i = 0; i != Command.length; i++) {
            Log.v("sendCmd", "command:" + String.valueOf(i) + " " + String.valueOf(Command[i]));
        }
        // todo: more robust error handling.
        if (mBluetoothGatt == null && mBM71Gatt == null) {
            return false;
        }
        if (mBluetoothGatt != null) {
            Log.v("sendCmd", "mBluetoothGatt not null");
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
            Log.v("CINCODEBUG", "bm71 send command, len=%d" + String.valueOf(Command.length));
            for (int i = 0; i != Command.length; i++) {
                Log.v("SENDDATA", String.valueOf(i) + " " + String.valueOf(Command[i]));
            }
            return mBM71Gatt.writeCharacteristic(mTransRx);
            //return false;
        } else {
            if (mBM71Gatt == null) {
                Log.v("sendCmd", "mBM71Gatt null");
            }

            if (mTransRx == null) {
                Log.v("sendCmd", "mTransRx null");
            }
            return false;
        }


    }


    private BluetoothGattCharacteristic findNotifyCharacteristic(BluetoothGattService service, UUID characteristicUUID) {
        BluetoothGattCharacteristic characteristic = null;
        List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
        for (BluetoothGattCharacteristic c : characteristics) {
            if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0
                    && characteristicUUID.equals(c.getUuid())) {
                characteristic = c;
                break;
            }
        }
        if (characteristic != null)
            return characteristic;
        for (BluetoothGattCharacteristic c : characteristics) {
            if ((c.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0
                    && characteristicUUID.equals(c.getUuid())) {
                characteristic = c;
                break;
            }
        }
        return characteristic;
    }

    public boolean getBattery() {

        // TODO
        return true;
       /* return sendCmdwithResponse(AcaiaCommunicationPacketHelper
                .getBatteryCommand());*/

    }

    private boolean init_sync() {

        List<BluetoothGattService> services = mBluetoothGatt.getServices();
        BluetoothGattCharacteristic TX_SEC = null;
        //BluetoothGattCharacteristic TX_SEC2 = null;
        for (int i = 0; i != services.size(); i++) {
            BluetoothGattService service = services.get(i);
            Log.v("CINCODEBUG", service.getUuid().toString());
            if (service.getUuid().toString().equals(ScaleGattAttributes.CSR_JB_UART_TX_PRIMARY_SERVICE_UUID)) {
                Log.v("CINCODEBUG", "Old BT");
                TX_SEC = mBluetoothGatt
                        .getService(
                                UUID.fromString(ScaleGattAttributes.CSR_JB_UART_TX_PRIMARY_SERVICE_UUID))
                        .getCharacteristic(
                                UUID.fromString(ScaleGattAttributes.CSR_JB_UART_TX_SECOND_UUID));
                setCharacteristicNotification(TX_SEC, true);
                break;
            } else if (service.getUuid().toString().equals(ScaleGattAttributes.MICROCHIP_UART_PRIMARY)) {
                Log.v("CINCODEBUG", "new BT");
                List<BluetoothGattCharacteristic> characteristics = mBluetoothGatt
                        .getService(
                                UUID.fromString(ScaleGattAttributes.MICROCHIP_UART_PRIMARY)).getCharacteristics();
                for (int j = 0; j != characteristics.size(); j++) {
                    BluetoothGattCharacteristic characteristic = characteristics.get(j);
                    Log.v("CINCODEBUG", "CHARA=" + characteristic.getUuid().toString());
                }

                BluetoothGattCharacteristic characteristic = findNotifyCharacteristic(service, UUID.fromString(ScaleGattAttributes.MICROCHIP_UART_TX));
                Log.v("hanjord", "tx chara=" + characteristic.getUuid().toString());
                mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                BluetoothGattDescriptor descriptor = characteristic

                        .getDescriptor(uuidFromStr("2902"));
                Log.v("hanjord", "tx descriptor=" + descriptor.getUuid().toString());
                descriptor
                        .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
                break;
            }
        }

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

    public void sendCmdWithLength(final byte[] s_in, final int len) {
        byte[] Command = new byte[len];
        CommLogger.logv4(TAG, "len: " + String.valueOf(len));
        for (int i = 0; i != Command.length; i++) {
            Command[i] = s_in[i];
            CommLogger.logv4(TAG, "send with len: out[i" + String.valueOf(i) + "]=" + String.valueOf(s_in[i]));
        }


        reliableSenderQueue.sendHighPriorityJob(Command, len);

    }

    public void sendCmdFromQueue(final byte[] Command) {
        //Log.v("hanjord","sendCmdFromQueue");
        for (int i = 0; i != Command.length; i++) {
            Log.v("hanjord", "Command " + String.valueOf(i) + " " + String.valueOf(Command[i]));
        }
        try {
            if (mBluetoothGatt != null) {
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
                            if ((System.nanoTime() - last_received) / 1000000.0 > 1500) {

                                if (isConnected()) {
                                    CommLogger.logv(TAG, "manual disconnect!");
                                    disconnect();
                                }
                            }
                        }
                    } catch (Exception e) {
                        send_failed_count++;
                        if (send_failed_count > send_failed_threshold) {
                            disconnect();
                            ;
                        }
                    }


                }
            }
            if (mTransRx != null && mBM71Gatt != null) {

                mTransRx.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                mTransRx.setValue(Command);
                Log.v("CINCODEBUG", "bm71 send command");
                mBM71Gatt.writeCharacteristic(mTransRx);
                //return false;
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
        // if (!isISPMode())
        //   sendCmdwithResponse(DataOutHelper.heartBeat());
        return true;
    }

    public void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        Log.v("CINCODEBUG", String.valueOf(characteristic.getUuid()) + " desc");
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

        if (ScaleGattAttributes.MICROCHIP_UART_TX

                .equals(String.valueOf(characteristic.getUuid()))) {
            List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
            for (int k = 0; k != descriptors.size(); k++) {
                BluetoothGattDescriptor descriptor = descriptors.get(k);
                Log.v("CINCODEBUG", "cripter=" + descriptor.getUuid().toString() + " " + String.valueOf(k));
            }

            if (characteristic.getDescriptors().size() != 0) {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptors().get(0);

                boolean result = false;
                /*if (0 != (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
                    result = descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                    Log.v("CINCODEBUG","PROPERTY_INDICATE 2");
                } else if (0 != (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
                    result = descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    Log.v("CINCODEBUG","ENABLE_NOTIFICATION_VALUE 2");
                }*/
                result = descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                result = descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            } else {
                Log.i("CINCODEBUG", "Set descriptor failed!");
            }
        }

        if (ScaleGattAttributes.MICROCHIP_UART_SECONDARY

                .equals(String.valueOf(characteristic.getUuid()))) {

            if (characteristic.getDescriptors().size() != 0) {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptors().get(0);

                descriptor
                        .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
                Log.v("CINCODEBUG", "ENABLE_NOTIFICATION_VALUE");
            } else {
                Log.i("CINCODEBUG", "Set descriptor failed!");
            }
        }

        if (ScaleGattAttributes.MICROCHIP_UART_SECONDARY3

                .equals(String.valueOf(characteristic.getUuid()))) {

            if (characteristic.getDescriptors().size() != 0) {
                BluetoothGattDescriptor descriptor = characteristic
                        .getDescriptors().get(0);

                descriptor
                        .setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
                Log.v("CINCODEBUG", "ENABLE_NOTIFICATION_VALUE3");
            } else {
                Log.i("CINCODEBUG", "Set descriptor failed!");
            }
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
        Log.v("CINCODEBUG", "calling mService.setCharacteristicNotification:Activity Transperent");

        if (mBM71Gatt != null) {
            GattService proprietary = mBM71Gatt.getService(SERVICE_ISSC_PROPRIETARY);
            List<GattService> services = mBM71Gatt.getServices();
            for (int i = 0; i != services.size(); i++) {
                Log.v("CINCODEBUG", "service uuid=" + String.valueOf(services.get(i).getUuid().toString()));
            }
            if (services.size() == 0) {
                Log.v("CINCODEBUG", "no servive");
                return;
            }

            mConnectionState = CONNECTION_STATE_CONNECTED;

            mTransTx = proprietary.getCharacteristic(CHR_ISSC_TRANS_TX);
            mTransRx = proprietary.getCharacteristic(CHR_ISSC_TRANS_RX);
            mAirPatch = proprietary.getCharacteristic(CHR_AIR_PATCH);
            mTransCtrl = proprietary.getCharacteristic(CHR_ISSC_TRANS_CTRL);

            Log.v("CINCODEBUG", "calling mService.setCharacteristicNotification:Activity Transperent");
            boolean set = mBM71Gatt.setCharacteristicNotification(mTransTx, true);
            Log.v("hanjord", "hanjord mTransTx " + mTransTx.getUuid().toString());
            Log.v("CINCODEBUG", "set notification:" + set);
            GattDescriptor dsc = mTransTx
                    .getDescriptor(DES_CLIENT_CHR_CONFIG);
            dsc.setValue(dsc
                    .getConstantBytes(GattDescriptor.ENABLE_NOTIFICATION_VALUE));

            mBM71Gatt.writeDescriptor(dsc);
            android.util.Log.v("hanjord", "tx descriptor=" + dsc.getUuid().toString());

            if (acaiaProtocolHelper != null) {
                acaiaProtocolHelper.shutdown_protocol_helper();
                acaiaProtocolHelper = null;
            }

            /*new Thread(){
                public void run(){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    acaiaProtocolHelper=new AcaiaProtocolHelper();
                    acaiaProtocolHelper.start();
                }
            }.start();*/

        } else {
            Log.v("CINCODEBUG", "mBM71Gatt null");
        }
    }


    class Bm71GattListener extends Gatt.ListenerHelper {

        Bm71GattListener() {
            super("BM71 GATTListener");
        }

        public void onMtuChanged(Gatt gatt, int mtu, int newState) {
            Log.v("", "onMtuChanged called newState: " + newState);
            Log.v("", "MTU changed MTU " + mtu);
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
            Log.v("CINCODEBUG", "BM71 discovered service");
        }

        @Override
        public void onCharacteristicRead(Gatt gatt, GattCharacteristic charac,
                                         int status) {
            Log.v("CINCODEBUG", "BM71 onCharacteristicRead");
        }

        @Override
        public void onCharacteristicChanged(Gatt gatt, GattCharacteristic chrc) {
            //Log.v("CINCODEBUG","onCharacteristicChanged");
//            if (last_received == 0) {
//                EventBus.getDefault().post(new NewScaleConnectionStateEvent(mCurrentConnectedDeviceAddr));
//            }

            byte[] input_data = chrc.getValue();
            for (int i = 0; i != input_data.length; i++) {
                Log.v("Input data", "[" + String.valueOf(i) + "] " + String.valueOf(input_data[i]));
            }

            send_failed_count = 0;
            if (last_received != 0) {
                last_received = System.nanoTime();
            }


            if (!isISPMode()) {
                if (acaiaScale == null) {
                    acaiaScale = AcaiaScaleFactory.createAcaiaScale(AcaiaScaleFactory.version_20, getApplicationContext(), self, handler, null, false);
                } else {
                    //Log.v(TAG, "acaia scale not null");
                    // parse packet
                    acaiaScale.getScaleCommand().parseDataPacket(chrc.getValue());
                    //  update connection
                    incomming_msg_counter++;

                    if (incomming_msg_counter > 15) {
                        if (last_received == 0) {
                            last_received = System.nanoTime();
                            EventBus.getDefault().post(new NewScaleConnectionStateEvent(mCurrentConnectedDeviceAddr));
                        }
                        EventBus.getDefault().post(new ScaleConnectionEvent(true, mCurrentConnectedDeviceAddr, acaiaScale.getProtocolVersion(), mBluetoothDevice));
                        incomming_msg_counter = 0;
                    }
                }
            }
        }

        @Override
        public void onCharacteristicWrite(Gatt gatt, GattCharacteristic charac,
                                          int status) {


        }

        @Override
        public void onDescriptorWrite(Gatt gatt, GattDescriptor dsc, int status) {
            Log.v("CINCODEBUG", "onDescriptorWrite service here");
            BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) dsc
                    .getCharacteristic().getImpl();

        }
    }


}
