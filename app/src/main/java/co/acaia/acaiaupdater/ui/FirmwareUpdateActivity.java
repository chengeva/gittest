package co.acaia.acaiaupdater.ui;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import co.acaia.acaiaupdater.R;

public class FirmwareUpdateActivity extends ActionBarActivity {

    Typeface type;
    TextView tv_update;
    TextView tv_isp;
    TextView tv_0415;
    TextView tv_acaia;
    TextView tv_set;
    TextView tv_f0000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);
        setupActionBar();
        type = Typeface.createFromAsset(getAssets(), "7seg.ttf");
        set7G();
    }

    private void set7G() {
        tv_update = (TextView) findViewById(R.id.tv_update);
        tv_isp = (TextView) findViewById(R.id.tv_isp);
        tv_0415 = (TextView) findViewById(R.id.tv_0415);
        tv_acaia = (TextView) findViewById(R.id.tv_acaia);
        tv_set = (TextView) findViewById(R.id.tv_set);
        tv_f0000 = (TextView) findViewById(R.id.tv_f0000);
        set7gTextView(tv_update);
        set7gTextView(tv_isp);
        set7gTextView(tv_acaia);
        set7gTextView(tv_set);
        set7gTextView(tv_f0000);
        set7gTextView(tv_0415);

    }

    private void set7gTextView(TextView target) {
        target.setTypeface(type);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_firmware, menu);
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

    private void setupActionBar() {

        getSupportActionBar().setTitle(getResources().getString(R.string.Switch_scale_to_update_mode));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_empty);
    }
}
