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

    //public static String MICROCHIP_SERVICE_PRIMARY="49535343-FE7D-4AE5-8FA9-9FAFD205E455";

    public static String MICROCHIP_UART_PRIMARY="49535343-fe7d-4ae5-8fa9-9fafd205e455";
    public static String MICROCHIP_UART_SECONDARY="49535343-8841-43f4-a8d4-ecbe34729bb3";
    public static String MICROCHIP_UART_TX="49535343-1e4d-4bd9-ba61-23c647249616";

    public static String MICROCHIP_UART_SECONDARY3="49535343-4c8a-39b3-2f49-511cff073b7e";

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
