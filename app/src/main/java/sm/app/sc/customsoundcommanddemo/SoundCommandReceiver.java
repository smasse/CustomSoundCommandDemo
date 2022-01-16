package sm.app.sc.customsoundcommanddemo;

import static sm.app.sc.customsoundcommanddemo.MainActivity.SOUND_COMMAND_INTENT_ACTION;
import static sm.app.sc.customsoundcommanddemo.MainActivity.SOUND_COMMAND_INTENT_EXTRA_CMD;

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
            String soundCommandFromX = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
            if(soundCommandFromX==null||soundCommandFromX.length()==0){
                // anomaly
                mainActivity.writeInCommandGui(
                        "The received intent does not contain a sound command.");
                Toast.makeText(context,"The received intent does not contain a sound command.",
                        Toast.LENGTH_SHORT).show();
            }else {
                // 3. gui
                mainActivity.writeInCommandGui(intent, context);
                // 4. ack: notif reception: send reception notif intent to dc
                //todo washere washere disable for this step, and enable when the step is ok
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
     * @param pr BroadcastReceiver.PendingResult from goAsync()
     */
    private void processSoundCommand(final Intent intent,
                                     final BroadcastReceiver.PendingResult pr,
                                     final Context context){
        String soundCommandFromX = intent.getStringExtra(SOUND_COMMAND_INTENT_EXTRA_CMD);
        Toast.makeText(context,"Sound command {"+soundCommandFromX+"} is being processed...",
                Toast.LENGTH_SHORT).show();

        //todo write your code here to process the sound command


        // the results are sent by h-user using the buttons
        // after going to the receiver app manually (this app);
        // this is seen by the h-user after opening this app manually
        MainActivity mainActivity = (MainActivity)mainActivityObject;
        mainActivity.writeInResultsGui("No additional results for sound command {"+
                soundCommandFromX+"}.");
        mainActivity.sendBroadcast(mainActivity.buildIntentForResultsToDC(
                "No additional results for sound command {"+
                soundCommandFromX+"}.",true));

        //mandatory
        pr.finish();
    }

}
