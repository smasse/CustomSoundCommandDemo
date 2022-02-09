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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * This app is designed to demonstrate the execution of a custom sound command from
 * a dolphin (or x-user).
 * It receives from DC messages (Intent instances) containing soundCommand String, date-time, etc.,
 * and sends messages containing results to DC.
 * @since 2022-2-9
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static final boolean LOG = false; //todo false in prod

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
    Button buttonExecute = null;
    ToggleButton toggleButtonExecuteImmediately = null;
    ToggleButton toggleButtonSendImmediately = null;
    public static final boolean EXECUTE_IMMEDIATELY_DEFAULT = false;
    public static final boolean SEND_IMMEDIATELY_DEFAULT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main2);
            textViewCommandData = findViewById(R.id.textViewCommandData);
            textViewExecResults = findViewById(R.id.textViewResults);
            log = findViewById(R.id.cscd_log_tv);
            editTextManualResults = findViewById(R.id.editTextManualResults);
            if(editTextManualResults!=null){
                editTextManualResults.setSelected(false);
                // to prevent keyboard to show at startup:
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                editTextManualResults.setFocusableInTouchMode(true);
                editTextManualResults.setEnabled(true);
            }
            buttonSendSuccess = findViewById(R.id.buttonSendSuccess);
            buttonSendSuccess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (LOG) {
                            Log.d(TAG, "buttonSendSuccess: onClick, entering...");
                        }
                        sendResultsWithExplicitIntent(true);
                    } catch (Throwable e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        writeInLog("buttonSendSuccess listener onClick: " + e + "\n" + sw.toString());
                        Log.e(TAG, "buttonSendSuccess listener onClick: " + e + "\n" + sw.toString());
                        appendToResults("buttonSendSuccess listener onClick: anomaly = " + e
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
                        appendToResults("buttonSendFailure listener onClick: anomaly = " + e
                                + "\n" + sw.toString());
                    }
                }
            });
            buttonExecute = findViewById(R.id.buttonExecute);
            buttonExecute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executeSoundCommand(intentFromDC);
                }
            });
            toggleButtonExecuteImmediately = findViewById(R.id.toggleButtonExecuteImmediately);
            toggleButtonSendImmediately = findViewById(R.id.toggleButtonSendImmediately);
        }catch(Throwable e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            writeInLog("buttonSendSuccess listener onClick: " + e + "\n" + sw);
            Log.e(TAG, "buttonSendSuccess listener onClick: " + e + "\n" + sw);
        }
    }

    Intent intentFromDC = null;

    private void receiveSoundCommandWithExplicitIntent(){
        intentFromDC = getIntent();
        String action = intentFromDC.getAction(); //now this is Intent.ACTION_MAIN
        if(Intent.ACTION_MAIN.equals(action)){
            //normal
        }else{
            //abnormal
            appendToResults("Intent action is unexpected = {"+action+"}");
        }
        String msgType = intentFromDC.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_TYPE);
        writeInLog("receiveSoundCommandWithExplicitIntent" +
                ": entering with msgType = {"+msgType+"}");
        if(SOUND_COMMAND_INTENT_TYPE_FROM_X_VIA_DC.equals(msgType)) {
            if(LOG){
                Log.d(TAG,"receiveSoundCommandWithExplicitIntent" +
                        ": entering with valid Intent action");
            }
            soundCommandFromX = intentFromDC.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
            writeInLog("receiveSoundCommandWithExplicitIntent" +
                    ": soundCommandFromX = {"+soundCommandFromX+"}");
            if(soundCommandFromX==null||soundCommandFromX.length()==0){
                // anomaly
                appendToResults("The received msg does not contain a sound command.");
//                Toast.makeText(this,"The received msg does not contain a sound command.",
//                        Toast.LENGTH_SHORT).show();
            }else {
                // ok
                writeReceivedIntent(intentFromDC, this);
                if(toggleButtonExecuteImmediately.isChecked()) {
                    //Immediate: the big work on bg thread
                    executeSoundCommand(intentFromDC);
                }
            }
        }else{
            if(LOG) Log.w(TAG,"receiveSoundCommandWithExplicitIntent" +
                    ": start without an Intent from DC; " +
                    "\n action: {"+action+"}"+ "\n msgType: {"+msgType+"}");
//            Toast.makeText(this, "Msg type not from DC: {"+msgType+"}",
//                    Toast.LENGTH_SHORT).show();
            appendToResults("Msg type not from DC: {"+msgType+"}");
        }
    }

    private void executeSoundCommand(final Intent intentFromDC){
        writeInLog("executeSoundCommand: entering with intentFromDC = {"+intentFromDC+"}");
        //if(intentFromDC==null)return;
        if(toggleButtonSendImmediately==null){
            writeInLog("defect detected in executeSoundCommand: " +
                    "toggleButtonSendImmediately is null; exiting");
            return;
        }
        if(textViewExecResults!=null) {
            final boolean SEND_IMMEDIATE = toggleButtonSendImmediately.isChecked();
            textViewExecResults.setText("");
            new Thread() {
                public void run() {
                    try {
                        executeSoundCommandOnBgThread(intentFromDC,SEND_IMMEDIATE);
                    }catch(Throwable e){
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        String s = "executeSoundCommand: " +
                                "in thread calling executeSoundCommandOnBgThread: " + e +
                                "\n" + sw;
                        Log.e(TAG, s);
                        writeInLog(s);
                        appendToResults(s);
                    }
                }
            }.start();
        }else{
            writeInLog("defect detected in executeSoundCommand: " +
                    "textViewExecResults is null; exiting");
        }
    }

    /**
     * Designed to be called on a background thread.
     *
     * <p>Results may include a list of signals to be emitted by dc:
     * !emit signal1 signal2 signal3...
     * </p>
     */
    private void executeSoundCommandOnBgThread(final Intent intent, final boolean sendImmediately){
        try {
            String soundCommandFromX = "sc-test";
            writeInLog("executeSoundCommandOnBgThread: entering with no Intent and " +
                    "soundCommandFromX = {"+ soundCommandFromX+"}");
            if(intent!=null) {
                soundCommandFromX = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
                writeInLog("executeSoundCommandOnBgThread: entering with Intent containing " +
                        "soundCommandFromX = {"+ soundCommandFromX+"}");
            }

            //todo write your code here to process the sound command
            // and edit the following statements for your own needs

            appendToResults("Starting the execution of sound command {" +
                    soundCommandFromX + "}.");

            appendToResults("This is demonstration; nothing else is done here." +
                    "The implementer would normally add the custom code here." +
                    " This could be, for example, to execute a program" +
                    ", or to send an http request to a web address" +
                    ", or to send an email to staff.");

            boolean success = true;

            // the results are sent by h-user using the buttons (success or failure)
            // after going to the receiver app manually (this app);
            // this is seen by the h-user after opening this app manually
            appendToResults("The execution of sound command {" +
                    soundCommandFromX + "} has completed.");

            if(sendImmediately) {
                writeInLog("executeSoundCommandOnBgThread: sendImmediately is checked, " +
                        "so sending now");
                //============================================
                sendResultsWithExplicitIntent(success);
                //============================================
            }else{
                writeInLog("executeSoundCommandOnBgThread: sendImmediately is not checked, " +
                        "so not sending now and waiting for the manual action");
            }
        }finally {
            writeInLog("executeSoundCommandOnBgThread: exiting");
        }
    }

    public static final String DC_ACTIVITY = "sm.app.dc/.CustomSoundCommandResultsReceiver";

    public static final String AUTOMATED_RESULTS_LABEL = "Automated Results";
    public static final String MANUAL_RESULTS_LABEL = "Manual Results";
    /**
     * Uses an explicit Intent
     */
    private void sendResultsWithExplicitIntent(final boolean success){
        try {
            Intent explicit = new Intent();
            ComponentName cn = ComponentName.unflattenFromString(DC_ACTIVITY);
            explicit.setComponent(cn);
            String execResults = textViewExecResults.getText().toString();
            if(TextUtils.isEmpty(execResults))execResults = "(empty)";
            String manualResults = editTextManualResults.getText().toString();
            if(TextUtils.isEmpty(manualResults))manualResults = "(empty)";
            String results = "[\n"+AUTOMATED_RESULTS_LABEL+": " + execResults +
                    "\n]\n["+MANUAL_RESULTS_LABEL+": " + manualResults+"\n]";
            addExtras(explicit, results, success);
            //todo try queryIntentActivities and queryIntentActivityOptions
            PackageManager pm = getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(explicit, 0);
            writeInLog("sendResultsWithExplicitIntent: List<ResolveInfo> list {"+list+"}"+
                " for DC_ACTIVITY = "+DC_ACTIVITY);
            if (list.isEmpty()) {
//                Toast.makeText(getApplicationContext(), "No app matching intent " + explicit,
//                        Toast.LENGTH_LONG).show();
                return;
            } else {
//            for (ResolveInfo ri : list) {
//                writeInConsoleByApp(ri.activityInfo.name);
//            }
            }
            try {
                startActivity(explicit);
            } catch (ActivityNotFoundException e) {
//                Toast.makeText(getApplicationContext(), "Results NOT sent to DC: " + e,
//                        Toast.LENGTH_LONG).show();
                writeInLog("Results NOT sent to DC: " + e);
                return;
            }
            String s = success ? "success" : "failure";
//            Toast.makeText(getApplicationContext(), "The " + s + " results were sent to DC.",
//                    Toast.LENGTH_LONG).show();
            writeInLog("The " + s + " results were sent to DC.");
        }catch(Throwable e2){
//            Toast.makeText(getApplicationContext(), "Anomaly in sendResultsWithExplicitIntent: " +
//                            e2, Toast.LENGTH_LONG).show();
            writeInLog("Anomaly in sendResultsWithExplicitIntent: " + e2);
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
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_RESULTS_SUCCESS,""+success);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_CMD,soundCommandFromX);
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,System.currentTimeMillis());
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING,""+Calendar.getInstance().getTime());
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_INSTALLATION_ID,"todo");
        intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID,getClass().getName());
    }

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
     * Designed to be called when receiving a potentially valid Intent.
     *
     * @param intent the received Intent
     * @param context from Android when receiving an Intent.
     */
    void writeReceivedIntent(final Intent intent, final Context context){
        soundCommandFromX = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
//        Toast.makeText(context, "Received sound command {" + soundCommand+"}",
//                Toast.LENGTH_SHORT).show();
        writeInLog("Received sound command {" + soundCommandFromX+"}");
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
            textViewCommandData.post(new Runnable() {
                @Override
                public void run() {
                    textViewCommandData.setText(s);
                }
            });
        }else{
//            if(LOG){
//                Log.w(TAG,"writeReceivedIntent(2-args): textViewCommandData is null");
//            }
            writeInLog("writeReceivedIntent(2-args): textViewCommandData is null");
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
//                    if(LOG){
//                        Log.w(TAG,"appendToResults(1-arg): editTextResults is null");
//                    }
                    writeInLog("appendToResults(1-arg): editTextResults is null");
                }
            }
        });
    }


    /**
     * works also when called on a bg thread.
     *
     * @param text
     */
    private void writeInLog(final String text){
        if(log==null)return;
        log.post(new Runnable() {
            @Override
            public void run() {
                CharSequence l = log.getText();
                if(l==null) l = "";
                String s = "";
                if(TextUtils.isEmpty(l)){
                    s = getDateTimePrefixForLog()+": "+text;
                }else{
                    s = l +"\n"+getDateTimePrefixForLog()+": "+text;
                }
                log.setText(s);
            }
        });
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


    //============== preferences ==============


    /**
     * Designed to be called after the GUI is created.
     */
    void restorePreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean exec = preferences.getBoolean(TOGGLE_BUTTON_EXECUTE_IMMEDIATELY_KEY,
                EXECUTE_IMMEDIATELY_DEFAULT);
        if(toggleButtonExecuteImmediately!=null) {
            toggleButtonExecuteImmediately.setChecked(exec);
        }
        boolean send = preferences.getBoolean(TOGGLE_BUTTON_SEND_IMMEDIATELY_KEY,
                SEND_IMMEDIATELY_DEFAULT);
        if(toggleButtonSendImmediately!=null){
            toggleButtonSendImmediately.setChecked(send);
        }
    }

    public static final String TOGGLE_BUTTON_EXECUTE_IMMEDIATELY_KEY =
            "toggleButtonExecuteImmediatelyKey";
    public static final String TOGGLE_BUTTON_SEND_IMMEDIATELY_KEY =
            "toggleButtonSendImmediatelyKey";

    void savePreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        boolean exec = toggleButtonExecuteImmediately==null?EXECUTE_IMMEDIATELY_DEFAULT:
                toggleButtonExecuteImmediately.isChecked();
        editor.putBoolean(TOGGLE_BUTTON_EXECUTE_IMMEDIATELY_KEY,exec);

        boolean send = toggleButtonSendImmediately==null?SEND_IMMEDIATELY_DEFAULT:
                toggleButtonSendImmediately.isChecked();
        editor.putBoolean(TOGGLE_BUTTON_SEND_IMMEDIATELY_KEY,send);

        editor.commit();
    }
    protected void onResume(){
        super.onResume();
        writeInLog("onResume: starting with: " +
                        "\n EXECUTE_IMMEDIATELY_DEFAULT = "+ EXECUTE_IMMEDIATELY_DEFAULT+
                        "\n SEND_IMMEDIATELY_DEFAULT = "+SEND_IMMEDIATELY_DEFAULT
//                "\n toggleButtonExecuteImmediately.isChecked() = "+toggleButtonExecuteImmediately.isChecked()+
//                "\n toggleButtonSendImmediately.isChecked() = "+toggleButtonSendImmediately.isChecked()
        );
        restorePreferences();
        writeInLog("onResume: restored preferences:"+
                "\n toggleButtonExecuteImmediately.isChecked() = "+
                toggleButtonExecuteImmediately.isChecked()+
                "\n toggleButtonSendImmediately.isChecked() = " +
                toggleButtonSendImmediately.isChecked()
        );
        receiveSoundCommandWithExplicitIntent();
    }
    protected void onPause(){
        savePreferences();
        super.onPause();
    }
    protected void onStop(){
        savePreferences();
        super.onStop();
    }
    protected void onDestroy(){
        savePreferences();
        super.onDestroy();
    }

}