package co.acaia.communications.events;

/**
 * Created by dimple on 03/12/2017.
 */

public class ModeEvent {
    public int mode;

    public ModeEvent(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "mode: " + mode;
    }
}
