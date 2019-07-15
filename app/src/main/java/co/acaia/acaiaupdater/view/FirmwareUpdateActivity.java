package co.acaia.acaiaupdater.view;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import co.acaia.acaiaupdater.Events.UpdateEraseProgress;
import co.acaia.acaiaupdater.Events.UpdateProgress;
import co.acaia.acaiaupdater.R;
import de.greenrobot.event.EventBus;

public class FirmwareUpdateActivity extends ActionBarActivity {
    private static int STATE_UPDATE_INIT=0;
    private static int STATE_UPDATE_CHECKING_FW=0;
    private static int STATE_UPDATE_UPDATING=0;
    private static int STATE_UPDATE_COMPLETE=0;

    private int current_updating_state;

    private TextView tv_progress;
    private TextView Update_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_fw);
        current_updating_state=STATE_UPDATE_INIT;
        EventBus.getDefault().register(this);
        tv_progress=(TextView)findViewById(R.id.Updating_progress);
        Update_status=(TextView) findViewById(R.id.Update_status);
    }

    public void onEvent(final UpdateProgress event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Update_status.setText("Updating Firmware...");
                tv_progress.setText(String.valueOf((int)event.process)+"%");
            }
        });
    }

    public void onEvent(final UpdateEraseProgress event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Update_status.setText("Erasing Previous Firmware...");
                tv_progress.setText(String.valueOf((int)event.process)+"%");
            }
        });
    }

}
