package co.acaia.acaiaupdater.Events;

import co.acaia.acaiaupdater.entity.FirmwareFileEntity;

/**
 * Created by hanjord on 15/4/21.
 */
public class StartFirmwareUpdateEvent {
    public FirmwareFileEntity firmwareFileEntity;
    public StartFirmwareUpdateEvent(FirmwareFileEntity firmwareFileEntity_){
        this.firmwareFileEntity=firmwareFileEntity_;
    }
}
