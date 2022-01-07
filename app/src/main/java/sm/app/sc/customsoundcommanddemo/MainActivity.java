package sm.app.sc.customsoundcommanddemo;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import java.util.Calendar;
import java.util.Date;
/**
 * receives intents containing soundCommandString, date-time, dc installation id, ...
 * and sends intents containing results
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final boolean LOG = true; //todo false in prod

    static final String SOUND_COMMAND_INTENT_ACTION = "sm.app.dc.intent.action.SOUND_COMMAND";
    static final String SOUND_COMMAND_INTENT_EXTRA = "sm.app.dc.intent.extra.SOUND_COMMAND";
    static final String SOUND_COMMAND_INTENT_EXTRA_DATE_STRING =
            "sm.app.dc.intent.extra.SOUND_COMMAND_DATE_STRING";
    static final String SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_TIME_MILLIS";
    static final String SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID =
            "sm.app.dc.intent.extra.SOUND_COMMAND_INSTALLATION_ID";
    static final String SOUND_COMMAND_INTENT_EXTRA_APP_ID =
            "sm.app.dc.intent.extra.SOUND_COMMAND_APP_ID";

    static final String SOUND_COMMAND_INTENT_ACTION_RESULTS =
            "sm.app.dc.intent.action.SOUND_COMMAND_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_RESULTS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_RESULTS_SUCCESS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_RESULTS_SUCCESS";
    static final String SOUND_COMMAND_INTENT_EXTRA_CMD_RESULTS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_CMD_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_DATE_STRING_RESULTS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_DATE_STRING_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS_RESULTS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_TIME_MILLIS_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID_RESULTS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_INSTALLATION_ID_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_APP_ID_RESULTS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_APP_ID_RESULTS";

    String soundCommandFromX = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //textViewCommand
        TextView textViewCommand = findViewById(R.id.textViewCommand);
        //editTextResults
        EditText editTextResults = findViewById(R.id.editTextResults);
        //buttonSendSuccess
        Button buttonSendSuccess = findViewById(R.id.buttonSendSuccess);
        buttonSendSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(buildIntentForResultsToDC(editTextResults.getText().toString(),true));
                Snackbar.make(null,"The success results were sent to DC.",Snackbar.LENGTH_SHORT).show();
            }
        });
        //buttonSendFailure
        Button buttonSendFailure = findViewById(R.id.buttonSendFailure);
        buttonSendFailure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(buildIntentForResultsToDC(editTextResults.getText().toString(),false));
                Snackbar.make(null,"The failure results were sent to DC.",Snackbar.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        if(action==null){
            //todo show error???
        }else {
            if (action.equals(SOUND_COMMAND_INTENT_ACTION)) {
                //receiving a sound command from dolphin or other external participant
                soundCommandFromX = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA);
                String dateString = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING);
                Long timeMillisLong = intent.getLongExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,0L);
                String installationId = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID);
                String appId = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID);

                //show reception of sound command from DC in
                textViewCommand.setText(soundCommandFromX);
            }else{
                // launch without sound command, maybe normal launch
                // normal if action is "android.intent.action.MAIN"
                textViewCommand.setText("The app was launched without a sound command.");
            }
        }
    }

    /**
     * Designed to be called by the button to send results back to DC.
     * Caller should call sendBroadcast(buildIntentForResultsToDC(results,success));
     *
     * @param results String from gui edittext
     * @param success boolean from button
     * @return Intent
     */
    Intent buildIntentForResultsToDC(String results, final boolean success){
        Intent intent = new Intent();
        intent.setAction(SOUND_COMMAND_INTENT_ACTION_RESULTS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_RESULTS,results);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_RESULTS_SUCCESS,success);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_CMD_RESULTS,soundCommandFromX);
        //intent.setType("text/plain");
        //date-time millis
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS_RESULTS,System.currentTimeMillis());
        //date-time string
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING_RESULTS,""+date);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID_RESULTS,getClass().getName());
        //dc installation id
        //todo intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID_RESULTS, Installation.getID());
        return intent;
    }
}