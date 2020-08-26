package co.acaia.acaiaupdater.ui;

import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import co.acaia.androidupdater.R;
import co.acaia.acaiaupdater.entity.FirmwareFileEntity;

public class SelectFirmwareFromListActivity extends AppCompatActivity {
    ListView firmwareListView;
    List<FirmwareFileEntity> firmwareFileEntities;
    public static final int result_ver = 2;
    public static final String extra_ver = "ver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_firmware_from_list);
        setupActionBar();
        init_firmware_list();
    }


    private void setupActionBar() {
        getSupportActionBar().setTitle(getResources().getString(R.string.Select_Firmware_Version));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_empty);
    }

    private void init_firmware_list() {
        firmwareListView = (ListView) findViewById(R.id.list_firmware);
        // TODO: get download firmware
        //firmwareFileEntities = FirmwareFileEntityHelper.getDownloadedFirmware();
        final String[] firmwareList = new String[firmwareFileEntities.size()];
        for (int i = 0; i != firmwareFileEntities.size(); i++) {
            FirmwareFileEntity firmwareFileEntity = firmwareFileEntities.get(i);
           // String display="Lunar-"+String.valueOf(firmwareFileEntity.majorversion)+"."+String.valueOf(firmwareFileEntity.minorversion);
            firmwareList[i] = firmwareFileEntity.title;
        }
        firmwareListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.firmware_item, firmwareList));
        firmwareListView.invalidate();
        firmwareListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                // todo: firmware id
                //intent.putExtra(extra_ver, (long)firmwareFileEntities.get(position).getId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_firmware_from_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
