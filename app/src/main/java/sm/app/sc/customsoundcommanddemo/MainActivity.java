package sm.app.sc.customsoundcommanddemo;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import java.util.Calendar;
import java.util.Date;
/**
 * receives intents containing soundCommand String, date-time, dc installation id, ...
 * and sends intents containing results
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final boolean LOG = true; //todo false in prod

    //todo washere  maybe replace by ACTION_DEVICE_OWNER_CHANGED
    static final String SOUND_COMMAND_INTENT_ACTION = "sm.app.dc.intent.action.SOUND_COMMAND";
    static final String SOUND_COMMAND_INTENT_EXTRA_CMD = "sm.app.dc.intent.extra.SOUND_COMMAND";
    static final String SOUND_COMMAND_INTENT_EXTRA_DATE_STRING =
            "sm.app.dc.intent.extra.SOUND_COMMAND_DATE_STRING";
    static final String SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_TIME_MILLIS";
    static final String SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID =
            "sm.app.dc.intent.extra.SOUND_COMMAND_INSTALLATION_ID";
    static final String SOUND_COMMAND_INTENT_EXTRA_APP_ID =
            "sm.app.dc.intent.extra.SOUND_COMMAND_APP_ID";

    //todo washere  maybe replace by ACTION_DEVICE_OWNER_CHANGED
    static final String SOUND_COMMAND_INTENT_ACTION_RESULTS =
            "sm.app.dc.intent.action.SOUND_COMMAND_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_RESULTS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_RESULTS_SUCCESS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_RESULTS_SUCCESS";

    //todo washere  maybe replace by ACTION_DEVICE_OWNER_CHANGED
    static final String SOUND_COMMAND_INTENT_ACTION_RECEPTION_NOTIF =
            "sm.app.dc.intent.action.SOUND_COMMAND_RECEPTION_NOTIF";

    String soundCommandFromX = "";

    SoundCommandReceiver soundCommandReceiver = null;
    //IntentFilter intentFilter = null;
    TextView textViewCommand = null;
    EditText editTextResults = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewCommand = findViewById(R.id.textViewCommand);
        editTextResults = findViewById(R.id.editTextResults);
        Button buttonSendSuccess = findViewById(R.id.buttonSendSuccess);
        buttonSendSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(buildIntentForResultsToDC(editTextResults.getText().toString(),true));
                //Snackbar.make(null,"The success results were sent to DC.",Snackbar.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"The success results were sent to DC.",
                        Toast.LENGTH_SHORT).show();
            }
        });
        Button buttonSendFailure = findViewById(R.id.buttonSendFailure);
        buttonSendFailure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(buildIntentForResultsToDC(editTextResults.getText().toString(),false));
                //Snackbar.make(null,"The failure results were sent to DC.",Snackbar.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"The failure results were sent to DC.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        soundCommandReceiver = new SoundCommandReceiver();
        soundCommandReceiver.mainActivityObject = this;
        IntentFilter intentFilter = new IntentFilter(SOUND_COMMAND_INTENT_ACTION);
        //intentFilter.addAction(SOUND_COMMAND_INTENT_ACTION);
        registerReceiver(soundCommandReceiver,intentFilter);
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
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_CMD,soundCommandFromX);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,System.currentTimeMillis());
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING,""+Calendar.getInstance().getTime());
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID,"todo");
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID,getClass().getName());
        return intent;
    }

    /**
     * input: soundCommandFromX
     */
    Intent buildIntentForReceptionNotifToDC(){
        Intent intent = new Intent();
        intent.setAction(SOUND_COMMAND_INTENT_ACTION_RECEPTION_NOTIF);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_CMD,soundCommandFromX);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,System.currentTimeMillis());
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING,""+Calendar.getInstance().getTime());
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID,"todo");
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID,getClass().getName());
        return intent;
    }

    protected void onDestroy() {
        unregisterReceiver(soundCommandReceiver);
        soundCommandReceiver.mainActivityObject = null;
        super.onDestroy();
    }

    /**
     * Designed to be called by the SoundCommandReceiver class, for example
     * when receiving a potentially valid Intent.
     *
     * @param intent the received Intent
     * @param context from Android when receiving an Intent.
     */
    void writeInCommandGui(final Intent intent, final Context context){
        String soundCommand = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
        Toast.makeText(context, "Received sound command {" + soundCommand+"}",
                Toast.LENGTH_SHORT).show();
        soundCommandFromX = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
        String dateString = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING);
        long timeMillisLong = intent.getLongExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,0L);
        String installationId = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID);
        String appId = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID);
        //show reception of sound command from DC in gui
        textViewCommand.setText("Sound command received = {"+soundCommandFromX+"}"+
                "\n\ndateString = {"+dateString+"}"+
                "\n\ntimeMillisLong = "+timeMillisLong+
                "\n\ninstallationId = {"+installationId+"}"+
                "\n\nappId = {"+appId+"}"
        );
    }

    /**
     * Designed to be called by the SoundCommandReceiver class, for example
     * from an anomaly in the received Intent.
     *
     * @param text
     */
    void writeInCommandGui(String text){
        textViewCommand.setText(text);
    }

    /**
     * Designed to be called by the SoundCommandReceiver class, for example
     * from the bg thread for a long command execution.
     *
     * @param text
     */
    void writeInResultsGui(String text){
        editTextResults.setText(text);
    }
}