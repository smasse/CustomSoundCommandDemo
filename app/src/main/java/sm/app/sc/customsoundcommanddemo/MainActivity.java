/*
 * Copyright (c) 2021 Serge Masse
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used
 * to endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * 4. This software, as well as products derived from it, must not be used for the purpose of
 * killing, harming, harassing, or capturing animals.
 *
 * 5. This software, as well as products derived from it, must be used with free dolphins, and
 * must not be used with captive dolphins kept for exploitation, such as for generating revenues
 * or for research or military purposes; the only ethical use of the app with captive dolphins
 * would be with dolphins that cannot be set free for their own safety AND are kept in a well-
 * managed sanctuary or the equivalent.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package sm.app.sc.customsoundcommanddemo;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.List;
/**
 * receives intents containing soundCommand String, date-time, dc installation id, ...
 * and sends intents containing results
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final boolean LOG = true; //todo false in prod

    /**
     * Value:
     * <p>
     * <code>
     * sm.app.dc.intent.action.SOUND_COMMAND
     * </code></p>
     * <p>old value "sm.app.dc.intent.action.SOUND_COMMAND" is replaced by
     * Intent.ACTION_MAIN and SOUND_COMMAND_INTENT_EXTRA_TYPE</p>
     */
    static final String SOUND_COMMAND_INTENT_ACTION = Intent.ACTION_MAIN;
    //"sm.app.dc.intent.action.SOUND_COMMAND";
    /**
     * values:
     * sound-command-from-x-via-dc
     * sound-command-results-to-dc
     * sound-command-ack-to-dc
     */
    static final String SOUND_COMMAND_INTENT_EXTRA_TYPE = "sm.app.dc.intent.extra.TYPE";
    static final String SOUND_COMMAND_INTENT_TYPE_FROM_X_VIA_DC = "sound-command-from-x-via-dc";
    static final String SOUND_COMMAND_INTENT_TYPE_RESULTS_TO_DC = "sound-command-results-to-dc";
    static final String SOUND_COMMAND_INTENT_TYPE_ACK_TO_DC = "sound-command-ack-to-dc";

    static final String SOUND_COMMAND_INTENT_EXTRA_CMD = "sm.app.dc.intent.extra.SOUND_COMMAND";
    static final String SOUND_COMMAND_INTENT_EXTRA_DATE_STRING =
            "sm.app.dc.intent.extra.SOUND_COMMAND_DATE_STRING";
    static final String SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_TIME_MILLIS";
    static final String SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID =
            "sm.app.dc.intent.extra.SOUND_COMMAND_INSTALLATION_ID";
    static final String SOUND_COMMAND_INTENT_EXTRA_APP_ID =
            "sm.app.dc.intent.extra.SOUND_COMMAND_APP_ID";

    /**
     * designed to be used by the third party app to send results to DC
     * <p>old value replaced by Intent.ACTION_MAIN and SOUND_COMMAND_INTENT_EXTRA_TYPE</p>
     */
    static final String SOUND_COMMAND_INTENT_ACTION_RESULTS = SOUND_COMMAND_INTENT_ACTION;
    //old "sm.app.dc.intent.action.SOUND_COMMAND_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_RESULTS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_RESULTS_SUCCESS =
            "sm.app.dc.intent.extra.SOUND_COMMAND_RESULTS_SUCCESS";

    /**
     * designed to be used by the third party app to send results to DC
     * <p>old value replaced by Intent.ACTION_MAIN and SOUND_COMMAND_INTENT_EXTRA_TYPE</p>
     */
    static final String SOUND_COMMAND_INTENT_ACTION_RECEPTION_NOTIF = SOUND_COMMAND_INTENT_ACTION;
    //old "sm.app.dc.intent.action.SOUND_COMMAND_RECEPTION_NOTIF";

    String soundCommandFromX = "";

//    SoundCommandReceiver soundCommandReceiver = null;
    //IntentFilter intentFilter = null;
    TextView textViewCommandData = null;
    TextView textViewExecResults = null;
    EditText editTextManualResults = null;
    Button buttonSendSuccess = null;
    Button buttonSendFailure = null;
    TextView log = null;
    public static final boolean PROCESS_IMMEDIATELY = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main2);
            textViewCommandData = findViewById(R.id.textViewCommandData);
            textViewExecResults = findViewById(R.id.textViewResults);
            log = findViewById(R.id.cscd_log_tv);
            editTextManualResults = findViewById(R.id.editTextManualResults);
            buttonSendSuccess = findViewById(R.id.buttonSendSuccess);
            buttonSendSuccess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (LOG) {
                            Log.d(TAG, "buttonSendSuccess: onClick, entering...");
                        }
                        //todo washere washere use an explicit intent
                        sendResultsWithExplicitIntent(true);
//                    sendBroadcast(buildIntentForResultsToDC(editTextResults.getText().toString(), true));
//                    //Snackbar.make(null,"The success results were sent to DC.",Snackbar.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(),
//                            "The success results were sent to DC.",
//                            Toast.LENGTH_SHORT).show();
                    } catch (Throwable e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        writeInLog("buttonSendSuccess listener onClick: " + e + "\n" + sw.toString());
                        Log.e(TAG, "buttonSendSuccess listener onClick: " + e + "\n" + sw.toString());
                        appendToResults("buttonSendSuccess listener onClick: " + e
                                + "\n" + sw.toString());
                    }
                }
            });
            buttonSendFailure = findViewById(R.id.buttonSendFailure);
            buttonSendFailure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        sendResultsWithExplicitIntent(false);
                    } catch (Throwable e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        Log.e(TAG, "buttonSendFailure listener onClick: " + e + "\n" + sw.toString());
                        appendToResults("buttonSendFailure listener onClick: " + e
                                + "\n" + sw.toString());
                    }
                }
            });
            //todo washere washere button to manually launch processing of received command
            if(PROCESS_IMMEDIATELY) {
                receiveSoundCommandWithExplicitIntent();
            }
        }catch(Throwable e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            //writeInLog("buttonSendSuccess listener onClick: " + e + "\n" + sw.toString());
            Log.e(TAG, "buttonSendSuccess listener onClick: " + e + "\n" + sw.toString());
//            appendToResults("buttonSendSuccess listener onClick: " + e
//                    + "\n" + sw.toString());
        }
    }

    private void receiveSoundCommandWithExplicitIntent(){
        Intent intentFromDC = getIntent();
        String action = intentFromDC.getAction(); //now this is Intent.ACTION_MAIN
        if(Intent.ACTION_MAIN.equals(action)){
            //normal
        }else{
            //abnormal
            appendToResults("Intent action is unexpected = {"+action+"}");
        }
        String msgType = intentFromDC.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_TYPE);
        if(SOUND_COMMAND_INTENT_TYPE_FROM_X_VIA_DC.equals(msgType)) {
            if(LOG){
                Log.d(TAG,"onReceive: entering with valid Intent action");
            }
            // 1. setup
            //MainActivity mainActivity = (MainActivity)mainActivityObject;
            // 2. verify
//            String msgType = intentFromDC.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_TYPE);
//            if( msgType==null || ! SOUND_COMMAND_INTENT_TYPE_FROM_X_VIA_DC.equals(msgType)) {
//                Toast.makeText(this, "The msg received" +
//                                " does not contain a valid type; type = {"+msgType+"}",
//                        Toast.LENGTH_SHORT).show();
//                return;
//            }
            String soundCommandFromX = intentFromDC.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
            if(soundCommandFromX==null||soundCommandFromX.length()==0){
                // anomaly
                appendToResults("The received msg does not contain a sound command.");
                Toast.makeText(this,"The received msg does not contain a sound command.",
                        Toast.LENGTH_SHORT).show();
            }else {
                // 3. gui
                writeReceivedIntent(intentFromDC, this);
                // 4. ack: notif reception: send reception notif intent to dc
                //todo washere  disable for this step, and enable when the step is ok
                // mainActivity.sendBroadcast(mainActivity.buildIntentForReceptionNotifToDC());

                //todo washere  see https://developer.android.com/training/snackbar/showing
                // use CoordinatorLayout
//                Snackbar.make(null,"The reception notification was sent to DC.",
//                        Snackbar.LENGTH_SHORT).show();
//todo washere washere button to manually launch processing of received command
                if(PROCESS_IMMEDIATELY) {
                    // 5. the big work on bg thread
                    //final BroadcastReceiver.PendingResult pr = goAsync();
                    new Thread() {
                        public void run() {
                            processSoundCommandOnBgThread(intentFromDC);
                        }
                    }.start();
                }
            }
        }else{
            if(LOG) Log.w(TAG,"onReceive: start without an Intent from DC; " +
                    "\n action: {"+action+"}"+ "\n msgType: {"+msgType+"}");
            Toast.makeText(this, "Msg type not from DC: {"+msgType+"}",
                    Toast.LENGTH_SHORT).show();
            appendToResults("Msg type not from DC: {"+msgType+"}");
        }
    }

    /**
     * Designed to be called on a background thread.
     *
     * <p>Results may include a list of signals to be emitted by dc:
     * !emit signal1 signal2 signal3...
     * </p>
     */
    private void processSoundCommandOnBgThread(final Intent intent){
        try {
            String soundCommandFromX = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
            Toast.makeText(getApplicationContext(), "Sound command {" + soundCommandFromX +
                            "} is being processed...", Toast.LENGTH_SHORT).show();
            clearResults();
            appendToResults("Starting the execution of sound command {" +
                    soundCommandFromX + "}.");
            //todo write your code here to process the sound command
            // and edit the following statements for your own needs

            // the results are sent by h-user using the buttons (success or failure)
            // after going to the receiver app manually (this app);
            // this is seen by the h-user after opening this app manually
            appendToResults("The execution of sound command {" +
                    soundCommandFromX + "} has completed.");

//            sendBroadcast(buildIntentForResultsToDC(
//                    "No additional results for sound command {" +
//                            soundCommandFromX + "}.", true));
        }finally {
            //mandatory - keep this here, at the end of the method
            //pr.finish();
        }
    }

    /**
     * Uses an explicit Intent
     */
    private void sendResultsWithExplicitIntent(final boolean success){
        try {
            Intent explicit = new Intent();
            ComponentName cn = ComponentName.unflattenFromString(
                    "sm.app.dc/.CustomSoundCommandResultsReceiver");
            explicit.setComponent(cn);
            String execResults = textViewExecResults.getText().toString();
            String manualResults = editTextManualResults.getText().toString();
            String results = "Automated Results: " + execResults +
                    "\n~~~~~\nManual Results: " + manualResults;
            addExtras(explicit, results, success);
            //todo try queryIntentActivities and queryIntentActivityOptions
            PackageManager pm = getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(explicit, 0);
            if (list.isEmpty()) {
                Toast.makeText(getApplicationContext(), "No app matching intent " + explicit.toString(),
                        Toast.LENGTH_LONG).show();
                return;
            } else {
//            for (ResolveInfo ri : list) {
//                writeInConsoleByApp(ri.activityInfo.name);
//            }
            }
            try {
                startActivity(explicit);
            } catch (ActivityNotFoundException e) {
                //todo writeInConsoleByApp("No activity found for the sound command.");
                Toast.makeText(getApplicationContext(), "Results NOT sent to DC: " + e,
                        Toast.LENGTH_LONG).show();
                return;
            }
            String s = success ? "success" : "failure";
            Toast.makeText(getApplicationContext(), "The " + s + " results were sent to DC.",
                    Toast.LENGTH_LONG).show();
        }catch(Throwable e2){
            Toast.makeText(getApplicationContext(), "Anomaly in sendResultsWithExplicitIntent: " +
                            e2, Toast.LENGTH_LONG).show();
        }
    }

    void addExtras(final Intent intent, final String results, final boolean success){
        // soundCommandString, date-time, dc installation id, ...
//         intent.setAction(SOUND_COMMAND_INTENT_ACTION);
//         intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setType("text/plain");//receivers use "text/*" todo not needed currently
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TYPE,SOUND_COMMAND_INTENT_TYPE_RESULTS_TO_DC);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_RESULTS,results);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_RESULTS_SUCCESS,success);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_CMD,soundCommandFromX);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,System.currentTimeMillis());
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING,""+Calendar.getInstance().getTime());
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID,"todo");
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID,getClass().getName());
    }

    /* *
     * Designed to be called by the button to send results back to DC.
     * Caller should call sendBroadcast(buildIntentForResultsToDC(results,success));
     *
     * @param results String from gui edittext
     * @param success boolean from button
     * @return Intent
     */
//    Intent buildIntentForResultsToDC(String results, final boolean success){
//        Intent intent = new Intent();
//        intent.setAction(SOUND_COMMAND_INTENT_ACTION_RESULTS);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TYPE,SOUND_COMMAND_INTENT_TYPE_RESULTS_TO_DC);
//        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_RESULTS,results);
//        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_RESULTS_SUCCESS,success);
//        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_CMD,soundCommandFromX);
//        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,System.currentTimeMillis());
//        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING,""+Calendar.getInstance().getTime());
//        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID,"todo");
//        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID,getClass().getName());
//        return intent;
//    }

    /**
     * for future use;
     * input: soundCommandFromX
     */
    Intent buildIntentForReceptionNotifToDC(){
        Intent intent = new Intent();
        intent.setAction(SOUND_COMMAND_INTENT_ACTION_RECEPTION_NOTIF);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TYPE,SOUND_COMMAND_INTENT_TYPE_ACK_TO_DC);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_CMD,soundCommandFromX);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,System.currentTimeMillis());
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING,""+Calendar.getInstance().getTime());
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID,"todo");
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID,getClass().getName());
        return intent;
    }

    /**
     * Designed to be called
     * when receiving a potentially valid Intent.
     *
     * @param intent the received Intent
     * @param context from Android when receiving an Intent.
     */
    void writeReceivedIntent(final Intent intent, final Context context){
        String soundCommand = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
        Toast.makeText(context, "Received sound command {" + soundCommand+"}",
                Toast.LENGTH_SHORT).show();
        soundCommandFromX = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
        String dateString = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING);
        long timeMillisLong = intent.getLongExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,0L);
        String installationId = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID);
        String appId = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID);
        //show reception of sound command from DC in gui
        final String s = "Sound command received = {" + soundCommandFromX + "}" +
                "\n\ndateString = {" + dateString + "}" +
                "\n\ntimeMillisLong = " + timeMillisLong +
                "\n\ninstallationId = {" + installationId + "}" +
                "\n\nappId = {" + appId + "}";
        if(textViewCommandData !=null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewCommandData.setText(s);
                }
            });
        }else{
            if(LOG){
                Log.w(TAG,"writeReceivedIntent(2-args): textViewCommandData is null");
            }
        }
    }

//    String textViewCommandText = "(not set)";

    /**
     * Designed to be called by the method that executes the sound command, for example
     * for a result or an anomaly in the received Intent.
     *
     * @param text
     */
    void appendToResults(String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textViewExecResults!=null) {
                    String prev = textViewExecResults.getText().toString();
                    if(TextUtils.isEmpty(prev)){
                        textViewExecResults.setText(text);
                    }else {
                        textViewExecResults.setText(prev + "\n~~~~~\n" + text);
                    }
                }else{
                    if(LOG){
                        Log.w(TAG,"appendToResults(1-arg): editTextResults is null");
                    }
                }
            }
        });
    }

    void clearResults(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textViewExecResults!=null) {
                    textViewExecResults.setText("");
                }else{
                    if(LOG){
                        Log.w(TAG,"clearResults(1-arg): textViewExecResults is null");
                    }
                }
            }
        });
    }



    private void writeInLog(final String text){
        //DolphinMainActivity.writeLog(text);
        if(log==null)return;
        CharSequence l = log.getText();
        if(l==null) l = "";
        String s = l.toString()+"\n"+getDateTimePrefixForLog()+": "+text;
        log.setText(s);
    }
    private String getDateTimePrefixForLog() {
        final Calendar cal = Calendar.getInstance();// TimeZone.getTimeZone("GMT"));
//        final String millisFormat = AcousticLibConfig.getIt().isShowMillisInTimeInConsoleEnabled()
//                ? ".SSS" : "";
        final String millisFormat = "";
        CharSequence time = DateFormat.format("yyyy-MM-dd hh:mm:ss" + millisFormat,
                cal.getTimeInMillis());// +" GMT";
        return time.toString();
    }

}