package co.acaia.acaiaupdater.ui;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.entity.FirmwareFileEntity;
import de.greenrobot.event.EventBus;

public class SelectFirmwareActivity extends ActionBarActivity {
    public static final int PICK_FIRMWARE = 1;  // The request code

    public static final String extra_firmware_ver = "ver";
    FirmwareFileEntity firmwareFileEntity;

    private RelativeLayout layout_firmwareversion;
    private TextView tv_firmware_ver;
    private TextView tv_firmware_detail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_firmware);
        setActionBar();
        init_views();

        if(getIntent().hasExtra(extra_firmware_ver)){
            long ver = getIntent().getLongExtra(SelectFirmwareFromListActivity.extra_ver, 0);
            //Log.v("selected version",String.valueOf(ver));
            EventBus.getDefault().post(new SelectVersionEvent(ver));

            // TODO: set detail
            //setDetail(FirmwareFileEntity.findById(FirmwareFileEntity.class, ver));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_firmware, menu);
        return true;
    }


    private void setActionBar() {
        getSupportActionBar().setTitle(getResources().getString(R.string.Version_Detail));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_empty);

    }

    private void init_views() {
        layout_firmwareversion = (RelativeLayout) findViewById(R.id.layout_firmwareversion);
        layout_firmwareversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFirmwareFromList();
            }
        });
        tv_firmware_ver = (TextView) findViewById(R.id.tv_firmware_ver);
        tv_firmware_detail = (TextView) findViewById(R.id.tv_firmware_detail);

    }


    private void selectFirmwareFromList() {
        Intent intent = new Intent();
        intent.setClass(this, SelectFirmwareFromListActivity.class);
        startActivityForResult(intent, PICK_FIRMWARE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_FIRMWARE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                long ver = data.getLongExtra(SelectFirmwareFromListActivity.extra_ver, 0);
                //Log.v("selected version",String.valueOf(ver));
                EventBus.getDefault().post(new SelectVersionEvent(ver));
                // TODO: set detail
                //setDetail(FirmwareFileEntity.findById(FirmwareFileEntity.class, ver));
            }
        }
    }

    private void setDetail(FirmwareFileEntity firmwareFileEntity) {
        tv_firmware_ver.setText(firmwareFileEntity.title);
        tv_firmware_detail.setText(firmwareFileEntity.detail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //Log.v("select firmware ", "opt selected");
        super.onBackPressed();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
