package co.acaia.acaiaupdater.ui;

import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import co.acaia.acaiaupdater.R;

public class CheckFirmwareActivity extends ActionBarActivity {
    Typeface type;
    TextView tv_acaia;
    TextView tv_set;
    TextView tv_25;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_firmware);
        setupActionBar();
        type = Typeface.createFromAsset(getAssets(), "7seg.ttf");
        init_text();
    }

    private void init_text(){
        tv_acaia = (TextView) findViewById(R.id.tv_acaia);
        tv_set = (TextView) findViewById(R.id.tv_set);
        tv_25=(TextView)findViewById(R.id.tv_25);
        SegHelper.set7gTextView(tv_acaia,type);
        SegHelper.set7gTextView(tv_set,type);
        SegHelper.set7gTextView(tv_25,type);
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
        getSupportActionBar().setTitle(getResources().getString(R.string.check_firmware_version));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.icon_empty);
    }

}
