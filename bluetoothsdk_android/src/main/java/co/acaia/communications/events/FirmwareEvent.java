package co.acaia.communications.events;

/**
 * Created by hanjord on 2016/3/2.
 */
public class FirmwareEvent {
    public int main;
    public int sub;
    public int info;

    public FirmwareEvent(int main, int sub, int info) {
        this.main = main;
        this.sub = sub;
        this.info = info;
    }
}
