package co.acaia.acaiaupdater.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import co.acaia.acaiaupdater.R;
import co.acaia.communications.scaleService.DistanceConnectEvent;
import de.greenrobot.event.EventBus;

public class ConnectScaleActivity extends AppCompatActivity {
    private Button connectButton;
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
    }
}
