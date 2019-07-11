package co.acaia.brewguide.events;

import co.acaia.communications.protocol.ver20.ScaleProtocol;

public class PearlSModeEvent {
    public ScaleProtocol.ESETTING_ITEM mode_item;
    public int on_off;
    public PearlSModeEvent(ScaleProtocol.ESETTING_ITEM mode_item_, int on_off_)
    {
        this.mode_item=mode_item_;
        this.on_off=on_off_;
    }
}
