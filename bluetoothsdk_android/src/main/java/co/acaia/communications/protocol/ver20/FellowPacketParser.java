package co.acaia.communications.protocol.ver20;

import android.content.Context;
import android.content.Intent;

import co.acaia.communications.CommLogger;
import co.acaia.communications.scaleService.ScaleCommunicationService;
import javolution.io.Struct;

import static co.acaia.communications.scaleService.ScaleCommunicationService.EXTRA_DATA;
import static co.acaia.communications.scaleService.ScaleCommunicationService.EXTRA_EVENT;
import static co.acaia.communications.scaleService.ScaleCommunicationService.EXTRA_TEMP_TYPE;
import static co.acaia.communications.scaleService.ScaleCommunicationService.EXTRA_UNIT;
import static co.acaia.communications.scaleService.ScaleCommunicationService.TEMP_UNIT_C;
import static co.acaia.communications.scaleService.ScaleCommunicationService.TEMP_UNIT_F;
import static co.acaia.communications.scaleService.ScaleCommunicationService.UNIT_GRAM;
import static co.acaia.communications.scaleService.ScaleCommunicationService.UNIT_OUNCE;

public class FellowPacketParser extends DataPacketParser {
    public static final String TAG="FellowPacketParser";


    public static void app_event(ScaleProtocol.app_prsdata o_data, byte n_cbid, int n_event, Struct.Unsigned8[] s_param, byte[] orig_data, Context context) {
//        CommLogger.logv(TAG, "n_event: " + String.valueOf(n_event));

        if (o_data.has_init.get() == 0) {
            if (n_event==ScaleProtocol.ECMD.e_cmd_info_a.ordinal()){
                DataOutHelper.sent_appid(o_data, "012345678901234".getBytes());
            } else if (n_event == ScaleProtocol.ECMD.e_cmd_status_a.ordinal()){

                o_data.has_init.set((short)1);
                final Intent intent= new Intent(ScaleCommunicationService.ACTION_CONNECTION_STATE_CONNECTED);
                context.sendBroadcast(intent);
            }
        } else {

//            CommLogger.logv(TAG, "s_param[0]: " + String.valueOf(s_param[0].get()));

            if (n_event == FellowProtocol.ECMD_FA.e_data_target_temp_fa.ordinal()
                    ||n_event == FellowProtocol.ECMD_FA.e_data_current_temp_fa.ordinal()
                    ) {
                final Intent intent = new Intent(ScaleCommunicationService.ACTION_TEMP);
                short temp = s_param[0].get();
                short unit = s_param[1].get();

//                CommLogger.logv(TAG, "temp: " + String.valueOf(temp));
//                CommLogger.logv(TAG, "unit: " + String.valueOf(unit));

                if (unit == 0) {
                    intent.putExtra(EXTRA_UNIT, TEMP_UNIT_C);
                } else {
                    intent.putExtra(EXTRA_UNIT, TEMP_UNIT_F);
                }

                intent.putExtra(EXTRA_TEMP_TYPE, n_event);

                intent.putExtra(EXTRA_DATA, temp);

                context.sendBroadcast(intent);

            } else if (n_event == FellowProtocol.ECMD_FA.e_data_status_fa.ordinal()
                    ||n_event == FellowProtocol.ECMD_FA.e_data_hold_status_fa.ordinal()
                    ||n_event == FellowProtocol.ECMD_FA.e_data_reach_goal_fa.ordinal()
                    ||n_event == FellowProtocol.ECMD_FA.e_data_safe_mode_fa.ordinal()
                    ||n_event == FellowProtocol.ECMD_FA.e_data_base_kettle_fa.ordinal()
                    ||n_event == FellowProtocol.ECMD_FA.e_data_hold_timer_fa.ordinal()
                    ||n_event == FellowProtocol.ECMD_FA.e_data_base_timer_fa.ordinal()
                    ) {
                final Intent intent = new Intent(ScaleCommunicationService.ACTION_FELLOW_EVENT);
                if (n_event == FellowProtocol.ECMD_FA.e_data_hold_timer_fa.ordinal()
                        ||n_event == FellowProtocol.ECMD_FA.e_data_base_timer_fa.ordinal()
                        ) {
                    short low = s_param[0].get();
                    short high = s_param[1].get();
                    int timer = high << 8 | low;
                    CommLogger.logv(TAG, "value: " + String.valueOf(timer));
                    intent.putExtra(EXTRA_DATA, timer);
                } else {
                    int value = s_param[0].get();
                    CommLogger.logv(TAG, "value: " + String.valueOf(value));
                    intent.putExtra(EXTRA_DATA, value);
                }

                intent.putExtra(EXTRA_EVENT, n_event);

                context.sendBroadcast(intent);
            }
        }
    }
    private static void sendIntent(Context context,final int type,final float val){
        final Intent intent = new Intent(ScaleCommunicationService.ACTION_DATA_AVAILABLE);
        intent.putExtra(ScaleCommunicationService.EXTRA_DATA_TYPE,type );
        intent.putExtra(EXTRA_DATA,val );
        context.sendBroadcast(intent);
    }
}
