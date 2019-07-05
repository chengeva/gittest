package co.acaia.ble.events;

public class ScaleGotTimerEvent {
	public int minute;
	public int second;
	public ScaleGotTimerEvent(int min, int sec)
	{
		minute=min;
		second=sec;
	}
	
}
