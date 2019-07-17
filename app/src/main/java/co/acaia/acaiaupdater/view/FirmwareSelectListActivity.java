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
import co.acaia.acaiaupdater.view.deviceList.CustomAdaptor;
import co.acaia.acaiaupdater.view.firmwarelList.CustomFirmwareAdaptor;
import co.acaia.acaiaupdater.view.firmwarelList.FirmwareModel;

public class FirmwareSelectListActivity extends AppCompatActivity {
    ListView list_firmwares;
    private AcaiaDevice currentSelectedDevice;
    private static CustomFirmwareAdaptor adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_select_list);
        list_firmwares=findViewById(R.id.list_firmwares);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));
        ArrayList<FirmwareFileEntity> firmwareFileEntities= FirmwareEntityHelper.obtainFirmwareWithModelName(currentSelectedDevice);
        ArrayList<FirmwareModel> firmwareModels=new ArrayList<>();
        for (int i=0;i!=firmwareFileEntities.size();i++){
            FirmwareModel firmwareModel=new FirmwareModel();
            firmwareModel.title=firmwareFileEntities.get(i).title;
            firmwareModel.caption=firmwareFileEntities.get(i).shortCap;
        }

;        adapter= new CustomFirmwareAdaptor(firmwareModels,getApplicationContext());
        list_firmwares.setAdapter(adapter);
    }
}
