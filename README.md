# SoundCommandApps for Demo #

Application demo to execute sound commands from dolphins 
or other participants via DC Dolphin Communicator app.

# Application SC Sound Command Demo #

Application *SC Sound Command Demo* is from Android project CustomSoundCommandDemo; 
it includes sm.app.sc.customsoundcommanddemo.MainActivity

SC Sound Command Demo is designed to demonstrate the execution of custom sound commands 
emitted by dolphins (or others) and captured and recognized in real-time 
by the DC Dolphin Communicator app, also for Android. 
The human user can define the frequencies of a sound command in DC, 
a dolphin or other participant emits the sound command, DC recognizes it in real-time, 
sends it to this app, which executes the programmed logic for it, and sends the results back to DC.

Using this app as an example, Android programmers can write their own app 
to process customized sound command in DC.

This app is also designed to demonstrate the effectiveness of DC for implementing sound command 
and develop automated responses to dolphin signals.

For example, you can have dolphins learn a specific command to call for medical assistance,
with DC running on a tablet with a hydrophone and with no staff present,
your sound command execution app could send an alert email to staff
or an http URL to a server which would execute some function.
This may be useful in an authentic sanctuary.


#### How to quickly test the custom sound command feature with DC as-is, without emission: ####

1. This text assumes that you are using DC already.
2. Install the *SC Sound Command Demo* app (free from Google Play, no ads).
3. Run *SC Sound Command Demo* app to ensure all is fine.
4. In DC, select the menu option, 3 dots, and *Sound Command Results*
   this will open CustomSoundCommandResultsReceiver
5. Select button *SEND TEST COMMAND SC-TEST*
   this will send sound command 'sc-test' to the app identified in the setting
   naming the third party app; if the setting has not been edited, then this
   will send 'sc-test' to app CustomSoundCommandDemo.
6. App CustomSoundCommandDemo will open
7. Select button EXECUTE
8. Select button SEND SUCCESS TO DC
   This will send a success message to DC, DC message receiver screen will be shown
9. In DC CustomSoundCommandResultsReceiver,
   inspect the text to ensure that all is well
10. The test is complete and you can use the CLOSE button,
    which will show app CustomSoundCommandDemo, because it is next in the stack;
    this will show the previous screen for DC CustomSoundCommandResultsReceiver,
    because it is next in the stack (the data there is not recent);
    then you can use the CLOSE button or the back key to show DC main screen
    which is the spectrogram and the console,
    which will contain main events and sound command execution results text.
11. You can use buttons SAVE TEXT or SAVE ALL or EXPORT DB
    to save this text to the database, etc.
    You can also do a screenshot for future reference.


#### How to test the custom sound command feature using an actual sound command: ####

1. This text assumes that you are using DC already.
2. Install the *SC Sound Command Demo* app (free from Google Play, no ads).
3. Run *SC Sound Command Demo* app to ensure all is fine.
4. In DC, select the SELF-TEST button
   This will make DC process the whistles that it emits as if they are from a participating
   entity and not from itself, because normally DC does not listen to its own whistles.
5. In the edit text field to the left of the EMIT button, type sc-test  
   ('sc-test' is the name of the hard-coded sound command for testing this new feature,
   all sound command names in DC start with 'sc-')
6. Select the EMIT button;  
   this will play the sc-test whistle which is hard-coded and designed to trigger  
   the transmission of the command (sc-test) to CustomSoundCommandDemo;  
   The name of CustomSoundCommandDemo is defined in a setting that can be edited to use your own  
   app name later.  
   The app CustomSoundCommandDemo is run and is shown by Android.
7. Select the EXECUTE button which will generate a test text in the results field.
8. Select the SEND SUCCESS TO DC button, and this will send the results back to DC.
9. DC's special activity CustomSoundCommandResultsReceiver will show up.  
   The results will also be shown in the main screen text (the chat text, aka. console)
10. Select the CLOSE button.  
    This will close the screen and show the CustomSoundCommandDemo which is behind it in the stack.
11. Use the back key to show DC main screen.  
    The results are in the text in the console (yellow text over the spectrogram).

# How To Make Your Own Sound Command Executor App #

### Option A: Minimal changes ###

- Add your code the this app at line 327 in MainActivity
- Use sound command sc-test as-is
- Use DC as-is
- You cannot publish this version of this app on Google Play because of the duplicate package name 
  but you can use this modified app on your device after uninstalling my Demo app.

### Option B: Your own sound command executor app - detailed below ###

1. your own app (YSC) instead of this one (more below),
2. using DC Dolphin Communicator app (DC), 
   either without changing the source code, or by making your own DC app by forking my DC
   from GitLab. 
   
### Option C: your own DC and SC apps ###

This option is not detailed here but it is a possibility 
as long as the derived app is respecting the license. No more details in this text.

## Option B: 1. Your Sound Command App (YSC) ##

Your sound command app must be installed on the same device as DC and comply with these
requirements.

### Manifest ###

The manifest for YSC must have these two elements:

#### queries clause, in the manifest clause of your executor app ####
<pre><code>
    &lt;queries&gt;
        &lt;android:name="sm.app.dc" /&gt;
    &lt;/queries&gt;
</code></pre>

#### exported="true", in the receiving activity definition clause ####

Example:<pre><code>
    &lt;activity
        android:name=".MainActivity"
        android:label="@string/app_name"
        android:exported="true"
    &gt;
        ...
    &lt;/activity&gt;
</code></pre>

### Data Sent By DC To YSC ###

DC sends this data to YSC and YSC is expected to use the elements that it needs. 
The most important element is the name of the sound command that was emitted by 
a participant and recognized by DC.

The data is in an Intent object sent by DC to the receiving class which is defined in a setting
in DC (see below).

The Intent sent by DC:<pre><code>
    ComponentName cn = ComponentName.unflattenFromString(appFlattenString);
    Intent intent = new Intent();
    intent.setComponent(cn);
    intent.setAction(Intent.ACTION_MAIN);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TYPE,SOUND_COMMAND_INTENT_TYPE_FROM_X_VIA_DC);
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_CMD,soundCommandString);
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_SC_ID,soundCommandId);
    //date-time millis
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,System.currentTimeMillis());
    //date-time string
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING,""+Calendar.getInstance().getTime());
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID, appId);
    </code></pre>

The permanent fields:<pre><code>
    static final String SOUND_COMMAND_INTENT_EXTRA_TYPE = "sm.app.dc.intent.extra.TYPE";
    static final String SOUND_COMMAND_INTENT_TYPE_FROM_X_VIA_DC = "sound-command-from-x-via-dc";
    static final String SOUND_COMMAND_INTENT_EXTRA_CMD = "sm.app.dc.intent.extra.SOUND_COMMAND";
    static final String SOUND_COMMAND_INTENT_EXTRA_SC_ID ="sm.app.dc.intent.extra.SOUND_COMMAND_ID";
    static final String SOUND_COMMAND_INTENT_EXTRA_DATE_STRING = "sm.app.dc.intent.extra.SOUND_COMMAND_DATE_STRING";
    static final String SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS = "sm.app.dc.intent.extra.SOUND_COMMAND_TIME_MILLIS";
    static final String SOUND_COMMAND_INTENT_EXTRA_APP_ID = "sm.app.dc.intent.extra.SOUND_COMMAND_APP_ID";
</code></pre>

The demo app contains code example to process the Intent received from DC.

### Optional Data To Be Sent To DC By YSC ###

If your app sends data to DC, then DC will write it in the console (chat) text and this will eventually 
be saved in DC database. This may be advantageous. The data set should be similar to this example.
<pre><code>
    intent.setAction(Intent.ACTION_VIEW);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TYPE,SOUND_COMMAND_INTENT_TYPE_RESULTS_TO_DC);
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_RESULTS,results);
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_RESULTS_SUCCESS,""+success);
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_CMD,soundCommandFromX);
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_SC_ID,soundCommandId);
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_TIME_MILLIS,System.currentTimeMillis());
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_DATE_STRING,""+Calendar.getInstance().getTime());
    intent.putExtra(SOUND_COMMAND_INTENT_EXTRA_APP_ID,getClass().getName());
</code></pre>

The permanent fields:<pre><code>
    static final String SOUND_COMMAND_INTENT_EXTRA_RESULTS = "sm.app.dc.intent.extra.SOUND_COMMAND_RESULTS";
    static final String SOUND_COMMAND_INTENT_EXTRA_RESULTS_SUCCESS = "sm.app.dc.intent.extra.SOUND_COMMAND_RESULTS_SUCCESS";
    static final String SOUND_COMMAND_INTENT_TYPE_RESULTS_TO_DC = "sound-command-results-to-dc";
</code></pre>

## Option B: 2. Use of DC Without Code Change ##

 If you use the same package name for your sound command executor app, 
 or if you don't send any data to DC, 
 then you don't need to change the code in DC.

 The current sound command executor demo app package used in DC is in line 43 of the DC manifest:
 "sm.app.sc.customsoundcommanddemo".
 DC also defines the package of another sound command executor app in the manifest:
 "dc.sc.ex.jcl".
 The current version of DC will only accept data from apps using one these two package names.
 Future versions of DC could use package names of other apps if such are requested.
 
 Recent versions of Android seem to require full package names in the queries clause 
 and therefore customized 
 executor apps with unique package names will require additional queries clauses in DC manifest.

### Set the Receiver Class Name in DC ###

Use setting "SettingCustomSoundCommandExecutorApp" in DC 
 to enter the name of the class in your SC to receive the data.
 It's the first one in the Settings screen.

The format is &lt;path&gt;/.&lt;classname&gt;

Example: "dc.sc.ex.jcl/.MainActivity"

#### Set the Custom Sound Command(s) in DC ####

Use the Whistle Menu Item to create one or more sound commands:
1. the name must start with "sc-"
2. the frequency pattern must _not_ be similar to other sound commands, either hard-coded or not.


## YSC Design Option: Automatic versus Supervised ##

You can design YSC to wait for human action before executing the sound command received from DC 
or YSC can execute it immediately (after verifications by the app), 
so that the device can act entirely without a human presence 
because DC does it's part automatically, without human interaction.

The demo app includes an example of controls to switch the same app from
automated to supervised, and vice-versa, by using some toggle buttons.

# Releases #
    
### Release 1 published 2022-2-12 ###
### Release 2 published 2022-2-16 ###
### Release 3 published 2022-3-7 ###
Minor improvements such as improved UI, added documentation, code cleanup.
