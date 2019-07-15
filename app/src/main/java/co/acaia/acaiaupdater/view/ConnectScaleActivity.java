package co.acaia.acaiaupdater.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import co.acaia.acaiaupdater.R;
import co.acaia.communications.scaleService.DistanceConnectEvent;
import de.greenrobot.event.EventBus;

public class ConnectScaleActivity extends AppCompatActivity {
    private Button connectButton;
    private TextView tv_Updating_progress;
    private TextView tv_Update_status;
    private  TextView tv_current_firmware;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_to_device);
        init_views();
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
        tv_current_firmware.setVisibility(View.GONE);
    }
}
