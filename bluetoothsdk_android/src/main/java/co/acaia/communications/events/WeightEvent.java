package co.acaia.communications.events;


/**
 * Created by kenkuan on 8/2/16.
 */
public class WeightEvent {
    public final Weight weight;

    public WeightEvent(double value, int unit) {
        this.weight = new Weight(value, unit);
    }
}
