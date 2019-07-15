package co.acaia.communications.scaleevent;

public class UpdatedStatusEvent {
    public int mainVersion;
    public int subVersion;
    public int addVersion;

    public UpdatedStatusEvent(int mainV, int subV, int addV){
        this.mainVersion=mainV;
        this.subVersion=subV;
        this.addVersion=addV;
    }
}
