package sm.app.sc.customsoundcommanddemo;

import static sm.app.sc.customsoundcommanddemo.MainActivity.SOUND_COMMAND_INTENT_ACTION;
import static sm.app.sc.customsoundcommanddemo.MainActivity.SOUND_COMMAND_INTENT_EXTRA_CMD;
import static sm.app.sc.customsoundcommanddemo.MainActivity.SOUND_COMMAND_INTENT_EXTRA_TYPE;
import static sm.app.sc.customsoundcommanddemo.MainActivity.SOUND_COMMAND_INTENT_TYPE_FROM_X_VIA_DC;
import static sm.app.sc.customsoundcommanddemo.MainActivity.SOUND_COMMAND_INTENT_TYPE_RESULTS_TO_DC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SoundCommandReceiver extends BroadcastReceiver {

    Object mainActivityObject = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(SOUND_COMMAND_INTENT_ACTION.equals(action)) {//this is not necessary but safer
            // 1. setup
            MainActivity mainActivity = (MainActivity)mainActivityObject;
            // 2. verify
            String msgType = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_TYPE);
            if( msgType==null || ! SOUND_COMMAND_INTENT_TYPE_FROM_X_VIA_DC.equals(msgType)) {
                Toast.makeText(context, "The msg received" +
                                " does not contain a valid type; type = {"+msgType+"}",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            String soundCommandFromX = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
            if(soundCommandFromX==null||soundCommandFromX.length()==0){
                // anomaly
                mainActivity.writeInCommandGui(
                        "The received msg does not contain a sound command.");
                Toast.makeText(context,"The received msg does not contain a sound command.",
                        Toast.LENGTH_SHORT).show();
            }else {
                // 3. gui
                mainActivity.writeInCommandGui(intent, context);
                // 4. ack: notif reception: send reception notif intent to dc
                //todo washere  disable for this step, and enable when the step is ok
                // mainActivity.sendBroadcast(mainActivity.buildIntentForReceptionNotifToDC());

                //todo washere  see https://developer.android.com/training/snackbar/showing
                // use CoordinatorLayout
//                Snackbar.make(null,"The reception notification was sent to DC.",
//                        Snackbar.LENGTH_SHORT).show();

                // 5. the big work on bg thread
                final BroadcastReceiver.PendingResult pr = goAsync();
                Thread t = new Thread() {
                    public void run() {
                        processSoundCommand(intent,pr,context);
                    }
                };
                t.start();
            }
        }
    }

    /**
     * Must call pr.finish() at the end.
     *
     * <p>Results may include a list of signals to be emitted by dc:
     * !emit signal1 signal2 signal3...
     * </p>
     *
     * @param pr BroadcastReceiver.PendingResult from goAsync()
     */
    private void processSoundCommand(final Intent intent,
                                     final BroadcastReceiver.PendingResult pr,
                                     final Context context){
        try {
            String soundCommandFromX = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
            Toast.makeText(context, "Sound command {" + soundCommandFromX + "} is being processed...",
                    Toast.LENGTH_SHORT).show();

            //todo write your code here to process the sound command
            // and edit the following statements for your own needs

            // the results are sent by h-user using the buttons (success or failure)
            // after going to the receiver app manually (this app);
            // this is seen by the h-user after opening this app manually
            MainActivity mainActivity = (MainActivity) mainActivityObject;
            mainActivity.writeInResultsGui("No additional results for sound command {" +
                    soundCommandFromX + "}.");
            mainActivity.sendBroadcast(mainActivity.buildIntentForResultsToDC(
                    "No additional results for sound command {" +
                            soundCommandFromX + "}.", true));
        }finally {
            //mandatory - keep this here, at the end of the method
            pr.finish();
        }
    }

}
