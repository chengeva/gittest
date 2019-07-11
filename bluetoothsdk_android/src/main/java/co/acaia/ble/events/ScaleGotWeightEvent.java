package co.acaia.ble.events;

public class ScaleGotWeightEvent {
	public String weight;
	public String unit;
	public ScaleGotWeightEvent(String w, String u){
		weight=w;
		unit=u;
	}

}
