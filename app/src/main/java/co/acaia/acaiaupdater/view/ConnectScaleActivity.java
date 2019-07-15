package co.acaia.acaiaupdater.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;
import co.acaia.communications.scaleService.DistanceConnectEvent;
import de.greenrobot.event.EventBus;

public class ConnectScaleActivity extends AppCompatActivity {
    private Button connectButton;
    private TextView tv_Updating_progress;
    private TextView tv_Update_status;
    private  TextView tv_current_firmware;
    private  TextView tv_disconnect;

    private AcaiaDevice currentSelectedDevice;

    private static final int STATE_DISCONNECTED=0;
    private static final int STATE_CONNECTED=1;
    private static final int STATE_CONNECTING=2;

    private int current_connection_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_to_device);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));
        init_views();
        current_connection_state=STATE_DISCONNECTED;
        update_view_status();
    }

    private void update_view_status(){
        switch (current_connection_state){
            case STATE_CONNECTED:
                break;
            case STATE_CONNECTING:
                break;
            case STATE_DISCONNECTED:
                tv_disconnect.setVisibility(View.GONE);
                tv_current_firmware.setVisibility(View.GONE);
                tv_Updating_progress.setText("Connect to "+currentSelectedDevice.modelName);
                tv_Update_status.setText("Please place your phone close to "+currentSelectedDevice.modelName);
                break;
            default:
                break;
        }
    }
    private void init_views()
    {
        connectButton=(Button)findViewById(R.id.btn_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new DistanceConnectEvent());
            }
        });
        tv_Updating_progress=(TextView)findViewById(R.id.Updating_progress);
        tv_Update_status=(TextView)findViewById(R.id.Update_status);
        tv_current_firmware=(TextView)findViewById(R.id.tv_current_firmware);
        tv_disconnect=(TextView) findViewById(R.id.tv_disconnect);
    }
    
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
