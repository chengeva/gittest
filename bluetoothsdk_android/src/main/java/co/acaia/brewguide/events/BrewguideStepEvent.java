package co.acaia.brewguide.events;

import co.acaia.communications.protocol.ver20.BrewguideProtocol;

public class BrewguideStepEvent {
    public BrewguideProtocol.new_brewguide_data_step brewguide_data_step;

    public BrewguideStepEvent(BrewguideProtocol.new_brewguide_data_step brewguide_data_step_){
        this.brewguide_data_step=brewguide_data_step_;
    }
}
