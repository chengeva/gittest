package co.acaia.acaiaupdater.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import co.acaia.acaiaupdater.common.ApplicationUtils;
import co.acaia.androidupdater.R;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Support;
import zendesk.support.request.RequestActivity;

public class FeedbackInitActivity extends AppCompatActivity {

    private final int CONTACT_ZENDESK_ACTIVITY_ID = 123;
    private EditText feedback_input_name;
    private EditText feedback_input_email;
    private Button feedback_button_next;

    private String zendeskUrl="https://acaia.zendesk.com/";
    private String applicationId="ac2c14f5468a4acb363049ea3f301bb3ac028967d3ec7b76";
    private String oauthClientId="mobile_sdk_client_322109bd6c70eec4c2e6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_init);

        setupActionBar();
        init_views();
        init_zendesk();
    }

    private void init_views(){
        feedback_input_name=(EditText)findViewById(R.id.feedback_input_name);
        feedback_input_email=(EditText)findViewById(R.id.feedback_input_email);
        feedback_button_next=(Button)findViewById(R.id.feedback_button_next);
        feedback_button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Sets the configuration used by the Contact Zendesk component

                EmailValidator emailvalidator=new EmailValidator();

                if(emailvalidator.validate(feedback_input_email.getText().toString())) {
                    //finish();;
                    Zendesk.INSTANCE.init(getApplicationContext(), zendeskUrl, applicationId, oauthClientId);
                    Identity identity = new AnonymousIdentity.Builder()
                            .withNameIdentifier(feedback_input_name.getText().toString())
                            .withEmailIdentifier(feedback_input_email.getText().toString())
                            .build();

                    Zendesk.INSTANCE.setIdentity(identity);

                    Support.INSTANCE.init(Zendesk.INSTANCE);

                    Intent requestActivityIntent = RequestActivity.builder()
                            .withRequestSubject("Support Ticket for Acaia Updater Android")
                            .withTags("App", "acaiaUpdaterAndroid","Android"+ Build.VERSION.RELEASE.replaceAll("\\s+",""),getDeviceName().replaceAll("\\s+",""), ApplicationUtils.getVersionNumber(getApplicationContext()).replaceAll("\\s+",""))

                            .intent(getApplicationContext());
                    startActivity(requestActivityIntent);

                }else{
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string._EmailIsInvalid), Toast.LENGTH_LONG).show();
                }


            }
        });

    }

    /*
    class SampleFeedbackConfiguration extends BaseZendeskFeedbackConfiguration {

        @Override
        public String getRequestSubject() {
            return "App ticket";
        }

        @Override
        public List<String> getTags(){
            List<String> tags=new ArrayList<>();
            tags.add(getDeviceName().replaceAll("\\s+",""));
            tags.add("Android"+Build.VERSION.RELEASE.replaceAll("\\s+",""));
            tags.add("acaiaPearlUpdaterAndroid");
            tags.add(ApplicationUtils.getVersionNumber(getApplicationContext()).replaceAll("\\s+",""));
            return tags;
        }

    }

     */


    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private void setupActionBar() {

        getSupportActionBar().setTitle(getResources().getString(R.string._Feedback));
        getSupportActionBar().setIcon(R.drawable.iconempty);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init_zendesk(){
        /**
         * Initialises the SDK with authentication
         */




    }
    private void set_zendesk_user(String anonymousEmail, String anonymousName){
        /*
        Identity user;
        user = new AnonymousIdentity.Builder()
                .withEmailIdentifier(anonymousEmail)
                .withNameIdentifier(anonymousName)

                .build();
        ZendeskConfig.INSTANCE.setIdentity(user);
         */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_feedback_init, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Need check
        super.onActivityResult(requestCode,resultCode,data);
        /*
        if(requestCode == CONTACT_ZENDESK_ACTIVITY_ID){
            String LOG_TAG="ZENDESK";
            if(resultCode == Activity.RESULT_OK){
                Logger.d(LOG_TAG, "ContactZendeskActivity - Request submitted");
            } else if(resultCode == Activity.RESULT_CANCELED){

                if(data != null){
                    final String reason = data.getStringExtra(ContactZendeskActivity.RESULT_ERROR_REASON);
                    Logger.d(LOG_TAG, "ContactZendeskActivity - Error: '" + reason + "'");

                } else {
                    Logger.d(LOG_TAG, "ContactZendeskActivity - OnBackPressed User cancelled");

                }
            }
        }
         */
    }
}

