package co.acaia.acaiaupdater.view;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;

public class FirmwareSelectActivity extends ActionBarActivity {
    private AcaiaDevice currentSelectedDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_firmware_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));
        Log.v("FirmwareSelectActivity","Current device="+currentSelectedDevice.modelName);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
