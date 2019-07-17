package co.acaia.acaiaupdater.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import co.acaia.acaiaupdater.R;
import co.acaia.acaiaupdater.entity.FirmwareEntityHelper;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDevice;
import co.acaia.acaiaupdater.entity.acaiaDevice.AcaiaDeviceFactory;
import co.acaia.acaiaupdater.filehelper.OnFileRetrieved;

public class ManualTroubleActivity extends AppCompatActivity {
    TextView tv_fromparse;
    private AcaiaDevice currentSelectedDevice;
    public static final String TAG="ManualTroubleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_trouble);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent().getStringExtra("type").equals("Intro")){

            getSupportActionBar().setTitle("Instructions");
        }else{
            getSupportActionBar().setTitle("Troubleshooting");
        }

        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));

        tv_fromparse=findViewById(R.id.tv_fromparse);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("AcaiaPlusDescriptions");
        query.whereEqualTo("model", currentSelectedDevice.modelName);
        query.whereEqualTo("type",getIntent().getStringExtra("type"));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> InstructionList, ParseException e) {
                if (e == null) {
                    if(InstructionList.size()!=0){
                        final ParseObject informationObject=InstructionList.get(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_fromparse.setText(informationObject.getString("detail"));
                            }
                        });
                    }
                } else {
                    // handle error
                }
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
