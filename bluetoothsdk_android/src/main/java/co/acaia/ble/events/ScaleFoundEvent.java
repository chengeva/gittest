package co.acaia.ble.events;

import android.bluetooth.BluetoothDevice;

public class ScaleFoundEvent {
	public String macId;
	public String scaleName;
	public BluetoothDevice device;
	
	public ScaleFoundEvent(BluetoothDevice device_,String macid,String scalename)
	{
		macId=macid;
		scaleName=scalename;
		device=device_;
	}

}
