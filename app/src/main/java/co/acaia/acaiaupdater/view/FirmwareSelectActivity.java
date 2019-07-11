package co.acaia.acaiaupdater.view;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.entity.FirmwareEntityHelper;
import co.acaia.acaiaupdater.entity.FirmwareFileEntity;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;

public class FirmwareSelectActivity extends ActionBarActivity {
    private AcaiaDevice currentSelectedDevice;
    private TextView firmwareLabel;
    private TextView firmwareRelease;
    private Button btn_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_firmware_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init_ui();
        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));
        Log.v("FirmwareSelectActivity","Current device="+currentSelectedDevice.modelName);
        setupViewWithModel();
    }

    private void init_ui(){
        firmwareLabel=(TextView)findViewById(R.id.tv_firmware_version);
        firmwareRelease=(TextView) findViewById(R.id.tv_firmware_release);
        btn_next=(Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setupViewWithModel(){
        ArrayList<FirmwareFileEntity> firmwareFileEntities= FirmwareEntityHelper.obtainFirmwareWithModelName(currentSelectedDevice);
        // Improve later
        FirmwareFileEntity firmwareFileEntity=firmwareFileEntities.get(0);
        firmwareLabel.setText(firmwareFileEntity.title);
        firmwareRelease.setText(firmwareFileEntity.detail);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
