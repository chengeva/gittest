package co.acaia.communications.protocol;

/**
 * Created by hanjord on 15/3/10.
 */

public class ScaleGattAttributes {

    /**
     * The Service UUIDs for the BLE device
     */

    public static String CSR_JB_UART_TX_PRIMARY_SERVICE_UUID = "00001820-0000-1000-8000-00805f9b34fb";
    public static String CSR_JB_UART_TX_SECOND_UUID = "00002a80-0000-1000-8000-00805f9b34fb";
    public static String CSR_JB_UART_RX_PRIMARY_SERVICE_UUID = "00001820-0000-1000-8000-00805f9b34fb";
    public static String CSR_JB_UART_RX_SECOND_UUID = "00002a80-0000-1000-8000-00805f9b34fb";


    /**
     * 	Read weight duration
     */
    public static final int READ_WEIGHT_DURATION = 3000;


    /**
     * The constants to determine the data type of a packet
     * returned from the Acaia scale.
     * @author hanjord@gmail.com
     *
     */
    public class ECMD {
        public static final int e_cmd_none = 0;
        public static final int e_cmd_str = 1;
        public static final int e_cmd_battery = 2;
        public static final int e_cmd_battery_r = 3;
        public static final int e_cmd_weight = 4;
        public static final int e_cmd_weight_r = 5;
        public static final int e_cmd_weight_r2 = 6;
        public static final int e_cmd_tare = 7;
        public static final int e_cmd_sound = 8;
        public static final int e_cmd_sound_on = 9;
        public static final int e_cmd_light_on = 10;
        public static final int e_cmd_file = 11;
        public static final int e_cmd_custom = 12;
        public static final int e_cmd_size = 13;
    }
}
