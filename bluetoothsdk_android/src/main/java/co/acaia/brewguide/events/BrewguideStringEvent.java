package co.acaia.brewguide.events;

import co.acaia.communications.protocol.ver20.BrewguideProtocol;

public class BrewguideStringEvent {
    public BrewguideProtocol.new_brewguide_data_string brewguide_data_string;
    public BrewguideStringEvent(BrewguideProtocol.new_brewguide_data_string brewguide_data_string_){
        this.brewguide_data_string=brewguide_data_string_;
    }
}
