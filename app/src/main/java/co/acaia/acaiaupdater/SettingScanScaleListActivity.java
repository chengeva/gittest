package co.acaia.acaiaupdater;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import co.acaia.androidupdater.R;
import co.acaia.communications.scaleevent.ScaleConnectionEvent;
import de.greenrobot.event.EventBus;


public class SettingScanScaleListActivity extends AppCompatActivity {
    EventBus bus=EventBus.getDefault();
    private boolean mScanning;

    private String currentScaleName="";
    private String currentScaleMac="";
    private BluetoothDevice mCurrentDevice;
    private SettingScanScaleFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_scan_scale_list);
        // Show the Up button in the action bar.
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().containsKey("connected_scale_name"))
                    currentScaleName= getIntent().getExtras().getString("connected_scale_name");
                if (getIntent().getExtras().containsKey("connected_scale_id"))
                    currentScaleMac= (getIntent().getExtras().getString("connected_scale_id"));

            }
        }

        setupActionBar();
        bus.register(this);

        fragment = new SettingScanScaleFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.scan_scale_fragment_container, fragment).commitAllowingStateLoss();


        invalidateOptionsMenu();

    }


    public void onEvent(ScaleConnectionEvent event){

    }
    public void onDestroy(){
        super.onDestroy();
        try {
            bus.unregister(this);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void setupActionBar() {

        getSupportActionBar().setTitle(getResources().getString(R.string._select_sacle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.scan_scale_menu, menu);
        if (!mScanning) {

            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Log.v("Scan scale","item.getItemId()="+String.valueOf(item.getItemId()));

        switch (item.getItemId()){
            case R.id.menu_scan:
            case R.id.menu_refresh:
            case R.id.menu_stop:
                break;
            default:
                super.onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }



}
