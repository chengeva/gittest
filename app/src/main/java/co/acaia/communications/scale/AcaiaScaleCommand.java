package co.acaia.communications.scale;

/**
 * Created by hanjord on 15/3/20.
 */
public interface AcaiaScaleCommand {

    public void parseDataPacket(byte[] data);
    public void parseDataPacketCinco(byte[] data,  AcaiaScale acaiaScale);
    public boolean connect(final String addr);

    public boolean stopScan() ;

    public boolean connectdebug() ;
    public boolean disconnect() ;

    public boolean getAutoOffTime() ;

    public boolean getBattery();
    public boolean getStatus() ;
    public boolean getBeep() ;

    public int getConnectionState() ;

    public boolean getKeyDisabledElapsedTime();

    public boolean getTimer() ;

    public boolean getWeight() ;

    public boolean pauseTimer() ;

    public boolean setAutoOffTime(int time) ;

    public boolean setBeep(boolean on);

    public boolean setKeyDisabledWithTime(int second) ;

    public boolean setLight(boolean on) ;

    public boolean setTare() ;

    public boolean setUnit(short unit) ;

    public boolean startScan();

    public boolean startTimer() ;

    public boolean stopTimer();

    public boolean getCapacity();

    public boolean setCapacity(int capacity);
}
