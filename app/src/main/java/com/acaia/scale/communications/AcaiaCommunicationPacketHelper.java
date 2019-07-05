package com.acaia.scale.communications;

/**
 * Created by hanjord on 15/3/19.
 */
public class AcaiaCommunicationPacketHelper {
    static {
        System.loadLibrary("scalecomm-wrapper");
    }

    public static int getSubdataValue(byte[] data){
        return getsubdatavalue(data,data.length);
    }
    public static byte[] getaskautooffcmd(){
        return askautooff();
    }

    public static byte[] getaskdisablekeysecondscmd(){
        return askdisablekeyseconds();
    }
    /**
     * Load native library, the wrapper function for encoding and decoding
     */

    public static float getweightUnit(byte[] buf){
        return getweightunit(buf,buf.length);
    }

    // korean version 20140827
    public static byte [] getSetCapacityCmd(int val){
        return setcapacity(val);
    }


    public static byte[] getaskdisableKeycmd(){
        return getaskdisablekeycmd();
    }

    public static byte[] startScaleTimer(){
        return  startscaletimer();
    }

    public static byte[] pauseScaleTimer(){
        return  pausescaletimer();
    }

    public static byte[] stopScaleTimer(){
        return  stopscaletimer();
    }


    public static byte[] getScaleBeepSound(){
        return askbeepsound();
    }
    public static byte[] getScaleTimer(){
        return  getscaletimer();
    }

    public static byte[] getdisableKeyCmd(int time){
        return getdisablekeycmd( time);
    }

    public static byte[] getswitchunitGramcmd(){
        return getswitchunitgramcmd();
    }

    public static byte[] getswitchUnitOzCmd(){
        return getswitchunitozcmd();
    }

    /**
     *
     * @return The encoded command for getting battery status on the Acaia Scale.
     */
    public static byte[] getBatteryCommand() {
        return getbatterycmd();
    }

    /**
     *
     * @return The encoded command for sending tare command to the Acaia scale.
     */
    public static byte[] getTareCommand() {
        return gettarecmd();
    }

    /**
     *
     */
    public static byte [] getautooffCmd(int time){
        return getautooffcmd(time);
    }

    /**
     *
     * @param if_on Turn beep on/off
     * @return The encoded command for sending beep configuration command to the Acaia scale.
     */
    public static byte[] getBeepONOFFCmd(boolean if_on) {
        return getbeepcmd(if_on);
    }

    /**
     *
     * @return The encoded command for getting updated weight from the Acaia scale.
     */
    public static byte[] getSendWeightCommand() {

        return getweightcmd(1, 100);
    }

    /**
     *
     * @param buf The data of the packet received from the Acaia scale
     * @return The data type of the packet, e.g. Weight data
     */

    public static int getScalePacketDataType(byte[] buf) {
        return getdatatype(buf, buf.length);
    }

    /**
     *
     */
    public static int getScalePacketSubDataType(byte[] buf) {
        return getsubdatatype(buf, buf.length);
    }
    public static byte[] getAskCapacityCmd(){
        return askcapacity();
    }

    /**
     *
     * @param buff The data of the packet received from the Acaia scale
     * @return The decoded data of the packet. e.g. 11.0
     */
    public static float parseScalePacket(byte[] buff) {
        //Log.i("PARSE PACKET", String.valueOf(buff));
        return parsescalepacket(buff, buff.length);
    }

    private native static int getdatatype(byte[] buff, int length);

    private native static int getsubdatatype(byte[] buff, int length);

    private native static float parsescalepacket(byte[] buff, int length);



    private native static byte[] getweightcmd(int n_period, int n_time);

    private native static byte[] getbatterycmd();

    private native static byte[] gettarecmd();

    private native static byte[] getbeepcmd(boolean if_on);

    private native static byte[] getautooffcmd(int time);

    private native static byte[] getswitchunitozcmd();

    private native static byte[] getswitchunitgramcmd();

    private native static byte[] getdisablekeycmd(int time);

    private native static byte [] startscaletimer();

    private native static byte [] pausescaletimer();

    private native static byte [] stopscaletimer();

    private native static byte [] getscaletimer();

    private native static byte [] askbeepsound();

    private native static byte [] askautooff();

    private native static byte [] getaskdisablekeycmd();

    private native static float getweightunit(byte[] buff, int length);

    private native static byte[] askdisablekeyseconds();

    private native static int getsubdatavalue(byte[] buff, int length);;


    private native static byte[] setcapacity(int val);


    private native static byte [] askcapacity();
}
