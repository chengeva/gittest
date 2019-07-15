package co.acaia.acaiaupdater.view;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import co.acaia.acaiaupdater.R;

public class FirmwareUpdateActivity extends ActionBarActivity {
    private static int STATE_UPDATE_INIT=0;
    private static int STATE_UPDATE_CHECKING_FW=0;
    private static int STATE_UPDATE_UPDATING=0;
    private static int STATE_UPDATE_COMPLETE=0;

    private int current_updating_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_fw);
        current_updating_state=STATE_UPDATE_INIT;
    }
}
