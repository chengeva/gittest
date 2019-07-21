package co.acaia.communications.scaleService;

import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;

public class DistanceConnectEvent {
    public AcaiaDevice currentConnectingDevice;
    public DistanceConnectEvent(AcaiaDevice currentConnectingDevice_){
        this.currentConnectingDevice=currentConnectingDevice_;
    }
}
