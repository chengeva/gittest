package co.acaia.acaiaupdater.view;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import co.acaia.acaiaupdater.AcaiaUpdater;
import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.entity.AcaiaFirmware;
import co.acaia.acaiaupdater.entity.FirmwareEntityHelper;
import co.acaia.acaiaupdater.entity.FirmwareFileEntity;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;

public class FirmwareSelectActivity extends ActionBarActivity {
    private AcaiaDevice currentSelectedDevice;
    private TextView firmwareLabel;
    private TextView firmwareRelease;
    private Button btn_next;
    private RelativeLayout layout_available;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_firmware_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init_ui();
        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));
        Log.v("FirmwareSelectActivity","Current device="+currentSelectedDevice.modelName);
        setupViewWithModel();
        AcaiaUpdater.currentAcaiaDevice=currentSelectedDevice;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void init_ui(){
        firmwareLabel=(TextView)findViewById(R.id.tv_firmware_version);
        firmwareRelease=(TextView) findViewById(R.id.tv_firmware_release);
        btn_next=(Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextActivity(currentSelectedDevice.modelName);
            }
        });
        layout_available=findViewById(R.id.layout_available);
        layout_available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FirmwareSelectListActivity.class);
                intent.putExtra("modelName",currentSelectedDevice.modelName);
                startActivity(intent);
            }
        });
    }
    private void nextActivity(String modelName){
        Intent intent = new Intent(getApplicationContext(), ConnectScaleActivity.class);
        intent.putExtra("modelName",modelName);
        startActivity(intent);
    }
    private void setupViewWithModel(){

        if(AcaiaUpdater.currentFirmware==null){
            ArrayList<FirmwareFileEntity> firmwareFileEntities= FirmwareEntityHelper.obtainFirmwareWithModelName(currentSelectedDevice);
            // Improve later
            FirmwareFileEntity firmwareFileEntity=firmwareFileEntities.get(0);
            AcaiaUpdater.currentFirmware=new AcaiaFirmware(firmwareFileEntity);
        }

        firmwareLabel.setText(AcaiaUpdater.currentFirmware.title);
        firmwareRelease.setText(AcaiaUpdater.currentFirmware.detail);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
