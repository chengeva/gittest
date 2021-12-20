package co.acaia.acaiaupdater;

import java.util.UUID;

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
import android.os.IBinder;
import android.util.Log;


import com.acaia.scale.communications.AcaiaCommunicationPacketHelper;
import com.acaia.scale.communications.AcaiaScaleAttributes;

import co.acaia.communications.CommLogger;


public class ScaleService extends Service {
	
	private class ScaleGattAttributes {

		/**
		 * The Service UUIDs for the BLE device
		 */

		public final static String CSR_JB_UART_TX_PRIMARY_SERVICE_UUID_STRING = "00001820-0000-1000-8000-00805f9b34fb";
		public final static String CSR_JB_UART_TX_SECOND_UUID_STRING = "00002a80-0000-1000-8000-00805f9b34fb";
		public final static String CSR_JB_UART_RX_PRIMARY_SERVICE_UUID_STRING = "00001820-0000-1000-8000-00805f9b34fb";
		public final static String CSR_JB_UART_RX_SECOND_UUID_STRING = "00002a80-0000-1000-8000-00805f9b34fb";

		
		/**
		 * 	Read weight duration
		 */
		public static final int READ_WEIGHT_DURATION = 250;
		

		/**
		 * The constants to determine the data type of a packet 
		 * returned from the Acaia scale.
		 * @author hanjord@gmail.com
		 *
		 */
		public class ECMD {
			public static final int e_cmd_none = 0;
			public static final int e_cmd_str = 1;
			public static final int e_cmd_battery = 2;
			public static final int e_cmd_battery_r = 3;
			public static final int e_cmd_weight = 4;
			public static final int e_cmd_weight_r = 5;
			public static final int e_cmd_weight_r2 = 6;
			public static final int e_cmd_tare = 7;
			public static final int e_cmd_sound = 8;
			public static final int e_cmd_sound_on = 9;
			public static final int e_cmd_light_on = 10;
			public static final int e_cmd_file = 11;
			public static final int e_cmd_custom = 12;
			public static final int e_cmd_size = 13;
		}
	}

	private final String TAG = ScaleService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = CONNECTION_STATE_DISCONNECTED;
    private BluetoothDevice mBluetoothDevice;
    private boolean mServiceDiscoverDone = false;


    public final static int UNIT_GRAM = 0;
    public final static int UNIT_OUNCE = 1;
    
    private int _batt_cmd_cnt = 0;
    private int _batt_resp_cnt = 0;
    
    //public final static String ACTION_CONNECTION_STATE_CHANGED = "co.acaia.scale.service.ACTION_CONNECTION_STATE_CHANGED";

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
    
    
    
    //Connection State
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
    //public final static int DATA_TYPE_SOUND = 6;
    
    
    
    
    //public final static int
    //public final static int DATA_TYPE_WEIGHT = 0;

	
    
   
    
    

	// Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        	//CommLogger.logv(TAG, "onLeScan");
        	final Intent intent = new Intent(ACTION_DEVICE_FOUND);
        	intent.putExtra(EXTRA_DEVICE, device);
        	intent.putExtra(EXTRA_RSSI, rssi);
        	sendBroadcast(intent);
        }
    };

    private void broadcastUpdate(final String action) {
		final Intent intent = new Intent(action);
		sendBroadcast(intent);
	}

	private void broadcastUpdate(final String action,
			final BluetoothGattCharacteristic characteristic) {
		final Intent intent = new Intent(action);
		// CommLogger.logv(TAG, "data in");
		if (UUID.fromString(AcaiaScaleAttributes.CSR_JB_UART_RX_SECOND_UUID)
				.equals(characteristic.getUuid())) {
						final byte[] data = characteristic.getValue();
			// AcaiaCommunicationPacketHelper.parseScalePacket(data);

			int type = AcaiaCommunicationPacketHelper
					.getScalePacketDataType(data);

			switch (type) 
			{
			case AcaiaScaleAttributes.ECMD.e_cmd_weight_r:
				float weightVal = AcaiaCommunicationPacketHelper
						.parseScalePacket(data);

				String weightString = "";
				if ((int) AcaiaCommunicationPacketHelper.getweightUnit(data) == 0) {
					// gram
					weightString = String.format("%.1f", weightVal);
					intent.putExtra(ScaleService.EXTRA_UNIT, UNIT_GRAM);

				} else {
					weightString = String.format("%.3f", weightVal);
					intent.putExtra(ScaleService.EXTRA_UNIT, UNIT_OUNCE);
				}
				intent.putExtra(ScaleService.EXTRA_DATA,
						weightString);
				intent.putExtra(EXTRA_DATA_TYPE,
						ScaleService.DATA_TYPE_WEIGHT);
				if (weightVal >= 2050) {
					Log.e(TAG, "ERROR weight >= 2050");
				}

				break;

			case AcaiaScaleAttributes.ECMD.e_cmd_battery_r:
				intent.putExtra(ScaleService.EXTRA_DATA,
						String
						.valueOf(AcaiaCommunicationPacketHelper
								.parseScalePacket(data)));
				intent.putExtra(EXTRA_DATA_TYPE,
						ScaleService.DATA_TYPE_BATTERY);
				break;
			case AcaiaScaleAttributes.ECMD.e_cmd_custom:
				{
					int sub_type = AcaiaCommunicationPacketHelper.getScalePacketSubDataType(data);
					int sub_type_value = AcaiaCommunicationPacketHelper.getSubdataValue(data);

					if (sub_type == 0) {
						intent.putExtra(EXTRA_DATA_TYPE, ScaleService.DATA_TYPE_AUTO_OFF_TIME);
						intent.putExtra(EXTRA_DATA, sub_type_value);
					} else if (sub_type == 1) {
						intent.putExtra(EXTRA_DATA_TYPE, ScaleService.DATA_TYPE_KEY_DISABLED_ELAPSED_TIME);
						intent.putExtra(EXTRA_DATA, sub_type_value);
					} else if (sub_type == 2) {
						intent.putExtra(EXTRA_DATA_TYPE, ScaleService.DATA_TYPE_BEEP);
						intent.putExtra(EXTRA_DATA, sub_type_value);
					} else if (sub_type == 9) {
						int time = (int)AcaiaCommunicationPacketHelper.parseScalePacket(data);
						intent.putExtra(EXTRA_DATA_TYPE, ScaleService.DATA_TYPE_TIMER);
						intent.putExtra(EXTRA_DATA, time);
					}
				}
				break;
			}
			sendBroadcast(intent);

		}

	}
    
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
    	
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        	
        	if (status == BluetoothGatt.GATT_SUCCESS) {
        		String connection_str = "";
	            if (newState == BluetoothProfile.STATE_CONNECTED) {
	                mConnectionState = CONNECTION_STATE_CONNECTED;
	                CommLogger.logv(TAG, "Connected to GATT server.");
	                CommLogger.logv(TAG, "Attempting to start service discovery:" +
	                        mBluetoothGatt.discoverServices());
	                connection_str = ACTION_CONNECTION_STATE_CONNECTED;
	            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
	                mConnectionState = CONNECTION_STATE_DISCONNECTED;
	                mServiceDiscoverDone = false;
	                CommLogger.logv(TAG, "Disconnected from GATT server.");
	                //Mike, 2014/05/09, This is an workaround, because if we just do disconnect and then connect, the time of building connection is very long
	                mBluetoothDeviceAddress = null;
	                if (mBluetoothGatt != null) {
	                	mBluetoothGatt.close();
	                }
	                mBluetoothGatt = null;
	                
	                connection_str = ACTION_CONNECTION_STATE_DISCONNECTED;
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
        		Log.d(TAG, "onConnectionStateChange error:" + Integer.toString(status));
        	}
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        	CommLogger.logv(TAG, "onServicesDiscovered received: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
            	mServiceDiscoverDone = true;
            	//mBtGattChar = getScaleBtGattChar();
            	String st = ScaleService.ACTION_SERVICES_DISCOVERED;
            	broadcastUpdate(st);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        	CommLogger.logv(TAG, "onCharacteristicRead received: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
               broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        	CommLogger.logv(TAG, "onCharacteristicWrite received: " + Integer.toString(status));
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
        	CommLogger.logv(TAG, "onReliableWriteCompleted received");
        }
        
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
        	CommLogger.logv(TAG, "onCharacteristicChanged received");
        	
        	if (UUID.fromString(ScaleGattAttributes.CSR_JB_UART_RX_SECOND_UUID_STRING)
    				.equals(characteristic.getUuid())) {
        		//getWeight();
        		final byte[] data = characteristic.getValue();

        		// todo: if in ISP mode, let ISP.c handle the traffic

        		AcaiaCommunicationPacketHelper.parseScalePacket(data);
        		
        		CommLogger.logv("BLE UPDATE len", String.valueOf(data.length));
    			CommLogger.logv("BLE UPDATE res", String
    					.valueOf(AcaiaCommunicationPacketHelper
    							.parseScalePacket(data)));
    			CommLogger.logv("BLE UPDATE type", String
    					.valueOf(AcaiaCommunicationPacketHelper
    							.getScalePacketDataType(data)));

    			// error here?
    			int type = AcaiaCommunicationPacketHelper
    					.getScalePacketDataType(data);
    			
    			switch (type) {
	    			case AcaiaScaleAttributes.ECMD.e_cmd_weight_r:
	    				//broadcastUpdate(ScaleService.ACTION_DATA_AVAILABLE, characteristic);
	    				break;
	    			case AcaiaScaleAttributes.ECMD.e_cmd_battery_r:
	    				_batt_resp_cnt++;
	    				if (_batt_resp_cnt != _batt_cmd_cnt) {
	    					CommLogger.logv(TAG, "Battery request and response cound not match");
	    					getState();
	    				}
	    				
	    				break;
	    			case AcaiaScaleAttributes.ECMD.e_cmd_custom:
	    				int subDataType = AcaiaCommunicationPacketHelper.getScalePacketSubDataType(data);
	    				
	    				int subDataValue = AcaiaCommunicationPacketHelper.getSubdataValue(data);
	    				
	    				CommLogger.logv(TAG, "subcmd type: "+Integer.toString(subDataType)+", subDataValue: "+Integer.toString(subDataValue));
	    				
	    				break;
    			}
    			broadcastUpdate(ScaleService.ACTION_DATA_AVAILABLE, characteristic);
        	}
            
        }
    };

    public void getState() {
    	CommLogger.logv(TAG, "Service Get Stat _batt_cmd_count:" + Integer.toString(_batt_cmd_cnt));
		CommLogger.logv(TAG, "Service Get Stat _batt_resp_count:" + Integer.toString(_batt_resp_cnt));
    }
    

    
    public void disconnect() {
    	if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
    	
    	//mBluetoothDeviceAddress = null;
    	
        mBluetoothGatt.disconnect();
        
        
    }
    
	public void startScan() {

		if (mBluetoothAdapter == null) {
			if (!initialize()) {
				CommLogger.logv(TAG, "startScan error, cannot initialize");
				return;
			}
		}
		
		stopScan();
		CommLogger.logv(TAG, "Scan Mode: " +
				Integer.toString(mBluetoothAdapter.getScanMode())+", "+
				Boolean.toString(mBluetoothAdapter.isDiscovering()));
		UUID []uuids = {UUID.fromString(ScaleGattAttributes.CSR_JB_UART_RX_PRIMARY_SERVICE_UUID_STRING)};
		if (!mBluetoothAdapter.startLeScan(uuids, mLeScanCallback)) {
			Log.d(TAG, "startLeScan Error");
		}
	}
	
	public void stopScan() {
		mBluetoothAdapter.stopLeScan(mLeScanCallback);
	}
	
	
	public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
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

        mServiceDiscoverDone = false;
        
        if (mBluetoothGatt != null) {
        	mConnectionState = CONNECTION_STATE_DISCONNECTED;
        	mBluetoothGatt.close();
        }
        mBluetoothGatt = null;
        
        
        return true;
    }
	
	
	
	public boolean setUnit(int unit) {
		
		if (!checkConnection()) return false;
		byte[] cmd = null;
		if (unit == UNIT_GRAM) {
			cmd = AcaiaCommunicationPacketHelper.getswitchunitGramcmd();
		} else if (unit == UNIT_OUNCE) {
			cmd = AcaiaCommunicationPacketHelper.getswitchUnitOzCmd();
		}
		
		return sendCmd(cmd);
		//sendCmdwithResponse(cmd);
		
		/*
		mBtGattChar.setValue(cmd);
		setCharacteristicNotification(mBtGattChar, true);
		
		mBtGattChar
				.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
				
		if (mBluetoothGatt != null) {
			boolean result = false;
				result = mBluetoothGatt
						.writeCharacteristic(mBtGattChar);
		}
		*/
	}
	
	public boolean getWeight() {
		if (!checkConnection()) return false;
		return sendCmdwithResponse(AcaiaCommunicationPacketHelper
				.getSendWeightCommand());
	}
	
	public boolean getBattery() {
		if (!checkConnection()) return false;
		_batt_cmd_cnt++;
		return sendCmdwithResponse(AcaiaCommunicationPacketHelper
				.getBatteryCommand());
		
	}
	
	public void setCharacteristicNotification(
			BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (mBluetoothAdapter == null || mBluetoothGatt == null) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			return;
		}
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

		if (AcaiaScaleAttributes.CSR_JB_UART_RX_SECOND_UUID

		.equals(String.valueOf(characteristic.getUuid()))) {

			if (characteristic.getDescriptors().size() != 0) {
				BluetoothGattDescriptor descriptor = characteristic
						.getDescriptors().get(0);

				descriptor
						.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
				mBluetoothGatt.writeDescriptor(descriptor);
			} else {
				CommLogger.logv(TAG, "Set descriptor failed!");
			}
		}

	}
	/*
	BluetoothGattCharacteristic getScaleBtGattChar() {
		if (mBluetoothAdapter == null
				|| mBluetoothGatt == null
				|| mConnectionState != CONNECTION_STATE_CONNECTED) {
			Log.w(TAG, "BluetoothAdapter not initialized");
			// TODO: handle connection failure

			return null;
		}
		List<BluetoothGattService> gattService = mBluetoothGatt
				.getServices();
		CommLogger.logv(TAG, "size=" + String.valueOf(gattService.size()));
		
		if (gattService.size() == 0) {
			//
		}

		
		//BluetoothGattCharacteristic btGattCmd = null;
		
		if (gattService.size() == 5) {
			
			// need to be refractured
			for(int i=0;i!=gattService.size();i++){
				if(gattService.get(i).getUuid().toString().contains("1820")){
					return gattService.get(i).getCharacteristics().get(0);
				}
			}

		} else {
			//TODO:  handle failed to retrieve services, must have 5 services
		}
		return null;
		
	}
	*/
	private boolean sendCmd(byte[] Command) {

		// todo: more robust error handling.

		BluetoothGattCharacteristic TX_SEC = mBluetoothGatt
				.getService(
						UUID.fromString(AcaiaScaleAttributes.CSR_JB_UART_TX_PRIMARY_SERVICE_UUID))
				.getCharacteristic(
						UUID.fromString(AcaiaScaleAttributes.CSR_JB_UART_TX_SECOND_UUID));
		if (TX_SEC != null) {
			TX_SEC.setValue(Command);
			return mBluetoothGatt.writeCharacteristic(TX_SEC);
		} else {
			return false;
		}

	}
	
	private boolean sendCmdwithResponse(byte[] Command) {
		
		//Command[5] = 0x10;
		BluetoothGattCharacteristic TX_SEC = mBluetoothGatt
				.getService(
						UUID.fromString(AcaiaScaleAttributes.CSR_JB_UART_TX_PRIMARY_SERVICE_UUID))
				.getCharacteristic(
						UUID.fromString(AcaiaScaleAttributes.CSR_JB_UART_TX_SECOND_UUID));
		
		if (TX_SEC == null) {
			Log.w(TAG, "Found no Characteristic");
			// TODO: handle connection failure
			return false;
		} else {
			
			if (!mServiceDiscoverDone) {
				//TODO: Error Handling
				return false;
			}
			//mBtGattChar.setValue(Command);
			TX_SEC.setValue(Command);
			//setCharacteristicNotification(mBtGattChar, true);
			setCharacteristicNotification(TX_SEC, true);

			//boolean if_success = mBluetoothGatt
			//		.writeCharacteristic(mBtGattChar);
			return mBluetoothGatt.writeCharacteristic(TX_SEC);
			//TODO:  BluetoothGattService acaiaService=mBl

		}
	}
	
	public class LocalBinder extends Binder  {
		public ScaleService getService() {
            return ScaleService.this;
        }
    }
	
	private final LocalBinder mBinder = new LocalBinder();
	
	@Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }
	
	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;
	}

	/*
	public boolean isScanning() {
		
	}*/

	public int getConnectionState() {
		return this.mConnectionState;
	}
	
	public boolean setLight(boolean on) {
		if (!checkConnection()) return false;
		
		return true;
	}
	
	public boolean setTare() {
		if (!checkConnection()) return false;
		return sendCmd(AcaiaCommunicationPacketHelper
				.getTareCommand());
	}
	
	private boolean checkConnection() {
		if (mBluetoothGatt == null || mBluetoothAdapter == null) {
			CommLogger.logv(TAG, "Bluetooth is not initialized or not connected!");
			return false;
		}
		return true; 
	}
	
	public boolean getKeyDisabledElapsedTime() {
		if (!checkConnection()) return false;
		return sendCmdwithResponse(AcaiaCommunicationPacketHelper
				.getaskdisableKeycmd());
	}
	public boolean getAutoOffTime() {
		
		if (!checkConnection()) return false;
		
		return sendCmdwithResponse(AcaiaCommunicationPacketHelper
				.getaskautooffcmd());
	}
	
	
	public boolean setAutoOffTime(int setting) {
		if (!checkConnection()) return false;
		if (setting < 0 || setting > 3) {
			return false;
		}
		return sendCmd(AcaiaCommunicationPacketHelper.getautooffCmd(setting));
	}
	
	
	public boolean setBeep(boolean on) {
		if (!checkConnection()) return false;
		return sendCmd(AcaiaCommunicationPacketHelper.getBeepONOFFCmd(on));
	}
	
	public boolean getBeep() {
		if (!checkConnection()) return false;
		return sendCmdwithResponse(AcaiaCommunicationPacketHelper
				.getScaleBeepSound());
	}
	
	public boolean setKeyDisabledWithTime(int time) {
		
		if (!checkConnection()) return false;
		
		if (time < 0 || time > 255) return false;
		
		return sendCmdwithResponse(AcaiaCommunicationPacketHelper
				.getdisableKeyCmd(time));
	}
	
	public boolean startTimer() {
		if (!checkConnection()) return false;
		return sendCmd(AcaiaCommunicationPacketHelper.startScaleTimer());
	}

	public boolean stopTimer() {
		if (!checkConnection()) return false;
		return sendCmd(AcaiaCommunicationPacketHelper.stopScaleTimer());
	}
	
	public boolean pauseTimer() {
		if (!checkConnection()) return false;
		return sendCmd(AcaiaCommunicationPacketHelper.pauseScaleTimer());
	}
	
	public boolean getTimer() {
		if (!checkConnection()) return false;
		return sendCmdwithResponse(AcaiaCommunicationPacketHelper.getScaleTimer());
	}

}
