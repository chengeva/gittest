package co.acaia.acaiaupdater.util;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import co.acaia.androidupdater.R;
import co.acaia.acaiaupdater.ui.CheckFirmwareActivity;
import co.acaia.acaiaupdater.ui.ConnectScaleWithAppActivity;
import co.acaia.acaiaupdater.ui.FirmwareUpdateActivity;
import co.acaia.acaiaupdater.ui.StartFirmwareUpdate;
import co.acaia.acaiaupdater.ui.TroubleShootingActivity;

public class ListActivity extends AppCompatActivity {


    public final static String TYPE = "type";

    public final static int ABOUT = 0;
    public final static int GUIDE_STEP = 1;

    private int display_type = 0;
    private String[] arry_list;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                display_type = getIntent().getExtras().getInt(TYPE);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init_listview();
    }

    private void init_listview() {
        //Set the list array
        switch (display_type) {
            case ABOUT:
                arry_list = getResources().getStringArray(R.array.about_arry);
                getSupportActionBar().setTitle(Utils.getResStr(this, R.string._About));
                break;
            case GUIDE_STEP:
                arry_list = getResources().getStringArray(R.array.instruction_step_arry);
                getSupportActionBar().setTitle(Utils.getResStr(this, R.string._InstructionGuide));
                break;
            default:

        }

        //Init adapter
        listview = (ListView) findViewById(R.id.activity_list_lv);
        listview.setAdapter(new ArrayAdapter<String>(this, R.layout.item_textview, arry_list));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (display_type == GUIDE_STEP) {
                    //showInstructionDialog(position);
                    switch (position) {
                        case 0:
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), CheckFirmwareActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent();
                            intent.setClass(getApplicationContext(), FirmwareUpdateActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent();
                            intent.setClass(getApplicationContext(), ConnectScaleWithAppActivity.class);
                            startActivity(intent);
                            break;

                        case 3:
                            intent = new Intent();
                            intent.setClass(getApplicationContext(), StartFirmwareUpdate.class);
                            startActivity(intent);
                            break;
                        case 4:
                            intent = new Intent();
                            intent.setClass(getApplicationContext(), TroubleShootingActivity.class);
                            startActivity(intent);

                    }
                }
            }
        });
    }


    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
