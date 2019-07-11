package co.acaia.acaiaupdater.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import co.acaia.acaiaupdater.R;

public class MainDeviceActivity extends AppCompatActivity {

    private ListView listview_devicelist;
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
    }

    private void setActionBar() {
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }
}
