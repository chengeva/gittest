package co.acaia.acaiaupdater.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.entity.FirmwareEntityHelper;
import co.acaia.acaiaupdater.entity.FirmwareFileEntity;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;

public class FirmwareSelectListActivity extends AppCompatActivity {
    ListView list_firmwares;
    private AcaiaDevice currentSelectedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_select_list);
        list_firmwares=findViewById(R.id.list_firmwares);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));
        ArrayList<FirmwareFileEntity> firmwareFileEntities= FirmwareEntityHelper.obtainFirmwareWithModelName(currentSelectedDevice);

        
    }
}
