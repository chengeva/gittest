package co.acaia.communications.scaleevent;

import android.bluetooth.BluetoothDevice;

/**
 * Created by hanjord on 15/3/30.
 */
public class ScaleConnectionEvent {
    private int protocol_ver=-1;
    private Boolean connected=false;
    private String addr="";
    private BluetoothDevice bluetoothDevice;
    public ScaleConnectionEvent(Boolean connected_){
        connected=connected_;
    }
    public ScaleConnectionEvent(Boolean connected_,String addr_,int protocol_ver_,BluetoothDevice device_){
        connected=connected_;addr=addr_;
        protocol_ver=protocol_ver_;
        bluetoothDevice=device_;
    }
    public int getProtocolVersion(){
        return protocol_ver;
    }
    public Boolean isConnected(){
        return connected;
    }
    public String getAddr(){
        return addr;
    }
    public BluetoothDevice getBluetoothDevice(){
        return bluetoothDevice;
    }
}
