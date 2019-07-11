package co.acaia.acaiaupdater.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import co.acaia.acaiaupdater.R;

public class MainDeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_device);
        setActionBar();
    }

    private void setActionBar() {
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }
}
