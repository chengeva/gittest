package co.acaia.acaiaupdater.view;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import co.acaia.acaiaupdater.AcaiaUpdater;
import co.acaia.androidupdater.R;
import co.acaia.acaiaupdater.entity.AcaiaFirmware;
import co.acaia.acaiaupdater.entity.FirmwareEntityHelper;
import co.acaia.acaiaupdater.entity.FirmwareFileEntity;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;
import co.acaia.acaiaupdater.view.deviceList.CustomAdaptor;
import co.acaia.acaiaupdater.view.firmwarelList.CustomFirmwareAdaptor;
import co.acaia.acaiaupdater.view.firmwarelList.FirmwareModel;
import co.acaia.communications.scaleService.gatt.Log;

public class FirmwareSelectListActivity extends AppCompatActivity {
    ListView list_firmwares;
    private AcaiaDevice currentSelectedDevice;
    private static CustomFirmwareAdaptor adapter;
    private   ArrayList<FirmwareFileEntity> firmwareFileEntitiesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_select_list);
        list_firmwares=findViewById(R.id.list_firmwares);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Available Firmware");
        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));
        ArrayList<FirmwareFileEntity> firmwareFileEntities= FirmwareEntityHelper.obtainFirmwareWithModelName(currentSelectedDevice);
        firmwareFileEntitiesList=firmwareFileEntities;
        ArrayList<FirmwareModel> firmwareModels=new ArrayList<>();
        for (int i=0;i!=firmwareFileEntities.size();i++){
            FirmwareModel firmwareModel=new FirmwareModel();
            firmwareModel.title=firmwareFileEntities.get(i).title;
            firmwareModel.caption=firmwareFileEntities.get(i).shortCap;
            //Log.v("FirmwareSelectListActivity","Got fw "+firmwareModel.title);
            firmwareModels.add(firmwareModel);
        }

;       adapter= new CustomFirmwareAdaptor(firmwareModels,getApplicationContext());
        list_firmwares.setAdapter(adapter);
        list_firmwares.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FirmwareFileEntity firmwareFileEntity=firmwareFileEntitiesList.get(i);
                //Log.v("FirmwareSelectListActivity","choose: "+firmwareFileEntity.title);
                AcaiaUpdater.currentFirmware=new AcaiaFirmware(firmwareFileEntity);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
