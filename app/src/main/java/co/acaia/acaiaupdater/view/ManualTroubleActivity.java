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
        currentSelectedDevice= AcaiaDeviceFactory.acaiaDeviceFromModelName(getIntent().getStringExtra("modelName"));

        tv_fromparse=findViewById(R.id.tv_fromparse);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("AcaiaPlusFirmware");
        query.whereEqualTo("model", currentSelectedDevice.modelName);
        query.addDescendingOrder("releaseDate");
        // hanjord
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> firmwareFileList, ParseException e) {
                if (e == null) {
                    Log.v(TAG,"got n files "+String.valueOf(firmwareFileList.size()));


                } else {
                    onDataRetrieved.doneRetrieved(false,"Parse error "+e.getLocalizedMessage());
                }
            }
        });

    }
}
