package co.acaia.acaiaupdater.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import co.acaia.acaiaupdater.R;
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
    }

    private void setActionBar() {
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }
}
