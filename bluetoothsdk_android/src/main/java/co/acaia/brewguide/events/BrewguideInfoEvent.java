package co.acaia.brewguide.events;

import co.acaia.communications.protocol.ver20.BrewguideProtocol;

public class BrewguideInfoEvent {
    public BrewguideProtocol.new_brewguide_data_info brewguide_data_info;
    public BrewguideInfoEvent(BrewguideProtocol.new_brewguide_data_info brewguide_data_info_){
        this.brewguide_data_info=brewguide_data_info_;
    }
}
