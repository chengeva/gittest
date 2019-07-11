package co.acaia.brewguide.events;

public class PearlSStatusEvent {
    public int weighingMode;
    public int dualDispMode;
    public int protaMode;
    public int espressoMode;
    public int pourOverMode;
    public int flowRatemode;
    public int beep;
    public int autoOff;
    public int unit;
    public PearlSStatusEvent( co.acaia.communications.protocol.ver20.ScaleProtocol.scale_status scaleStatus)
    {
        weighingMode=scaleStatus.n_setting_weighingmode.get();
        dualDispMode=scaleStatus.n_setting_dual_displaymode.get();
        protaMode=scaleStatus.n_setting_protafilter_mode.get();
        espressoMode=scaleStatus.n_setting_espresso_mode.get();
        pourOverMode=scaleStatus.n_setting_pourover_mode.get();
        flowRatemode=scaleStatus.n_setting_flowrate_mode.get();
        beep=scaleStatus.n_setting_sound.get();
        autoOff=scaleStatus.n_setting_sleep.get();
        unit=scaleStatus.n_unit.get();
    }

}
