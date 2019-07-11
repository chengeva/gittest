package co.acaia.communications.protocol;

import co.acaia.communications.protocol.ver20.ScaleProtocol;
import javolution.io.Struct;


public class FellowEKG {
    static public final byte DATA_STATUS_LEN = 1;
    static public final byte DATA_HOLD_STATUS_LEN = 1;
    static public final byte DATA_TARGET_TEMP_LEN = 2;
    static public final byte DATA_CURRENT_TEMP_LEN = 2;
    static public final byte DATA_HOLD_TIMER_LEN = 2;
    static public final byte DATA_BASE_TIMER_LEN = 2;
    static public final byte DATA_REACH_GOAL_LEN = 1;
    static public final byte DATA_SAFE_MODE_LEN = 1;
    static public final byte DATA_BASE_KETTLE_LEN = 1;
    static public final byte CMD_UNIT_LEN = 1;
    static public final byte CMD_POWER_LEN = 1;
    static public final byte CMD_TARGET_TEMP_LEN = 2;
    static public final byte DATA_ACK_LEN = 1;
    static public final byte DATA_FACTORY_LEN = 7;


    static public final short[] gn_cmd_len = {
            DATA_STATUS_LEN,
            DATA_HOLD_STATUS_LEN,
            DATA_TARGET_TEMP_LEN,
            DATA_CURRENT_TEMP_LEN,
            DATA_HOLD_TIMER_LEN,
            DATA_BASE_TIMER_LEN,
            DATA_REACH_GOAL_LEN,
            DATA_SAFE_MODE_LEN,
            DATA_BASE_KETTLE_LEN,
            1,
            1,
    };




}
