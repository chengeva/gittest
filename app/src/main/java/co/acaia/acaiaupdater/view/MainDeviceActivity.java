package co.acaia.acaiaupdater.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.view.deviceList.CustomAdaptor;
import co.acaia.acaiaupdater.view.deviceList.DeviceModel;

public class MainDeviceActivity extends AppCompatActivity {

    private ListView listview_devicelist;
    ArrayList<DeviceModel> dataModels;
    private static CustomAdaptor adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_device);
        setActionBar();
        init_view();
    }

    private void init_view()
    {
        listview_devicelist=(ListView)findViewById(R.id.listview_devicelist);
        dataModels=new ArrayList<>();
        DeviceModel deviceModel=new DeviceModel("Pearl S");
        dataModels.add(deviceModel);
        deviceModel=new DeviceModel("Lunar");
        dataModels.add(deviceModel);
        deviceModel=new DeviceModel("Orion");
        dataModels.add(deviceModel);
        deviceModel=new DeviceModel("Cinco");
        dataModels.add(deviceModel);
        adapter= new CustomAdaptor(dataModels,getApplicationContext());
        listview_devicelist.setAdapter(adapter);
        listview_devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceModel dataModel=dataModels.get(i);
                Log.v("MainDevice",dataModel.modelName);
                Intent intent = new Intent(getApplicationContext(), FirmwareSelectActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setActionBar() {
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }
}
