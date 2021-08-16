package com.tamrd.spearsdkexample;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.tamrd.spearsdkexample.commandList.DemoCommandList;
import com.tamrd.spearsdkexample.commandList.LabelCommandList;
import com.tamrd.spearsdkexample.helper.AssetsHelper;
import com.tamrd.spearsdkexample.helper.UIHelper;
import com.thinkamove.spearnative.RegistrationMode;
import com.thinkamove.spearnative.SpearWakeUpResult;
import com.thinkamove.spearnative.TamTranscriptionPair;
import com.thinkamove.spearnative.TamVadPair;
import com.thinkamove.spearnative.recognizer.ProcessMode;
import com.thinkamove.spearnative.spear.api.MissingPermissionException;
import com.thinkamove.spearnative.spear.api.RecognitionListener;
import com.thinkamove.spearnative.spear.api.SpearInitListener;
import com.thinkamove.spearnative.spear.api.SpearSdkApi;
import com.thinkamove.spearnative.spear.api.SpearWakeUp;
import com.thinkamove.spearnative.spear.api.SpearWakeUpListener;
import com.thinkamove.spearnative.spear.api.SpeechRecognizer;
import com.thinkamove.spearnative.spear.api.UnInitializeException;
import com.thinkamove.spearnative.spear.api.VadListener;
import com.thinkamove.spearnative.spearConfig.SpearConfigUpdateListener;
import com.thinkamove.spearnative.speechType.commands.api.CommandList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/*
 * SpearSdkExample
 * This is a simple application to show how to use spearSDK.
 * The workflow of this example is shown below.
 *
 * 1. onCreate(...) is called to initialize all view variables and ask for permission
 * 2. After permission is processed, onRequestPermissionsResult(...) is called to
 * initialize spearSdkApi. (LM packages are copied from assets folder to local HDD, then used to init)
 * 3. After init thread is done, onSpearInitComplete(...) is called to initialize Spear-ASR
 * /Spear-WakeUp and update registration information. Spear-WakeUp is turned on to listen from mic.
 * 4. For initializing Spear-WakeUp, copied WAKEUP_FST from step 2 is used.
 * 5. For initializing Spear-ASR, PRE_COMPILED_GRAMMAR_FILE from asset folder is used along with
 * other two specified grammar(DemoCommandList and LabelCommandList). mapCommands is used in DemoCommandList.
 * 6. While listening to Spear-WakeUp, whenever onCommitResult(SpearWakeUpResult) receives 1,
 * Spear-WakeUp will stop listening and Spear-ASR will start listening.
 * 7. While listening to Spear-ASR, whenever onCommitResult(TamTranscriptionPair[]) receives result,
 * result will either be displayed on the screen or jumped to other tasks.
 * 8. Status will also be updated in between and when onStartWakeUpListening,
 * onStopWakeUpListening, onStartListening, onStopListening are called.
 * 9. If Spear-ASR expires, error will be caught from onRecognitionError. App needs to restart.
 * 10. When Button update_config_button is clicked, onUpdateConfigClicked is called to update
 * spear-ASR settings according to selected spinners.
 */

public class MainActivity extends AppCompatActivity implements SpearInitListener, RecognitionListener,
        VadListener, SpearWakeUpListener, SpearConfigUpdateListener{

    // App Variables
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PRE_COMPILED_GRAMMAR_FILE = "aviation_JL16k-NA_v4.fst";
    private static final String WAKEUP_FST = "models/heyspear.fst";
    private static final String LM_PACK_BASE = "SPEAR-DATA-EN";
    private String[] availableCommands = AviationCommands.getAvailableCommands();

    // View Variables
    private TextView currentSpearStatus;
    private TextView transcribedText;
    private TextView infoText;
    private LinearLayout symbLayout, caseLayout;
    private Button updateConfigButton;
    private View updateConfigBarTop;
    private View updateConfigBarBottom;
    private Spinner ignoredSymbolsSpinner;
    private Spinner casePreferenceSpinner;

    // Other variables
    private SpearSdkApi spearSdkApi;
    private SpeechRecognizer speechRecognizer;
    private SpearWakeUp spearWakeUp;
    private CommandList demoCommandList;
    private CommandList labelCommandList;
    private static String LM_Package_ROOT;
    private String ignoredSymbolLabel, casePreferenceLabel = "--";
    private String mode = "spear-wakeup";
    private String registrationStat = "Unregistered";

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.M);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.RECORD_AUDIO"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize view elements
        initView();

        /*
         * Important: After API 23 Android requires permission request for storage read/write and record audio.
         * Just having the permissions set in the manifest is not sufficient.
         */
        if (shouldAskPermissions()) {
            askPermissions();
        }

        // Update UI elements
        updateUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            try {
                initSpearSdk();
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateUI();
        }
    }

    private void initView() {
        currentSpearStatus = findViewById(R.id.current_spear_status);
        transcribedText = findViewById(R.id.spear_transcribed_text);
        infoText = findViewById(R.id.info_text);

        symbLayout = (LinearLayout) findViewById(R.id.ignore_symbole_layout);
        caseLayout = (LinearLayout) findViewById(R.id.case_preference_layout);
        updateConfigButton = (Button) findViewById(R.id.update_config_button);
        updateConfigBarTop = (View) findViewById(R.id.update_config_bar_top);
        updateConfigBarBottom = (View) findViewById(R.id.update_config_bar_bottom);

        ignoredSymbolsSpinner = (Spinner) findViewById(R.id.ignore_symbole_spinner_id);
        ignoredSymbolsSpinner.setOnItemSelectedListener(ignoredSymbolsListener);
        List<String> ignoredSymbolsList = new ArrayList<String>();
        ignoredSymbolsList.add("--");
        ignoredSymbolsList.add("SET");
        ignoredSymbolsList.add("ZOOM");
        ignoredSymbolsList.add("DELTA");
        ArrayAdapter<String> ignoredSymbolsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ignoredSymbolsList);
        ignoredSymbolsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ignoredSymbolsSpinner.setAdapter(ignoredSymbolsAdapter);

        casePreferenceSpinner = (Spinner) findViewById(R.id.case_preference_spinner_id);
        casePreferenceSpinner.setOnItemSelectedListener(casePreferenceListener);
        List<String> casePreferenceList = new ArrayList<String>();
        casePreferenceList.add("--");
        casePreferenceList.add("upper");
        casePreferenceList.add("lower");
        casePreferenceList.add("raw");
        ArrayAdapter<String> casePreferenceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, casePreferenceList);
        casePreferenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        casePreferenceSpinner.setAdapter(casePreferenceAdapter);

        hideUpdateConfigView();
    }

    private void initSpearSdk() throws IOException {
        spearSdkApi = SpearSdkApi.getInstance();
        spearSdkApi.subscribeSpearInitEvent(this.hashCode(), this);

        // Copy LM package from assets to local
        File dir = AssetsHelper.InitLMPackage(this);
        LM_Package_ROOT = dir.getAbsolutePath();

        // Initialize spearSdkApi
        spearSdkApi.initialize(this, LM_Package_ROOT + "/" + LM_PACK_BASE);
    }

    private void initializeSpearWakeUp() {
        try{
            // Initialize spearWakeUp with WAKEUP_FST
            spearWakeUp = spearSdkApi.createSpearWakeUp();
            spearWakeUp.subscribeSpearWakeUpListener(this.hashCode(), this);
            spearWakeUp.initWithFst(LM_Package_ROOT + "/" + WAKEUP_FST);
            spearWakeUp.startListening(true);
        } catch (MissingPermissionException e) {
            Log.e(TAG, "Please grant required permissions before creating SPEAR WakUp", e);
        } catch (UnInitializeException e) {
            Log.e(TAG, "Please Initialize SPEAR SDK before creating SPEAR WakUp", e);
        }
    }
    private void initializeSpearRecognizer() {
        try {
            // Build
            demoCommandList = new DemoCommandList();
            labelCommandList = new LabelCommandList();

            // Initialize SpearRecognizer
            speechRecognizer = spearSdkApi.createSpeechRecognizer();
            speechRecognizer.subscribeRecognitionListener(this.hashCode(), this);
            speechRecognizer.subscribeVadListener(this.hashCode(), this);
            speechRecognizer.setCommandListGrammar(demoCommandList);
            speechRecognizer.setCommandListGrammar(labelCommandList);
            speechRecognizer.setFstGrammar(PRE_COMPILED_GRAMMAR_FILE, false);
        } catch (MissingPermissionException e) {
            Log.e(TAG, "Please grant required permissions before creating SPEAR SpeechRecognizer", e);
        } catch (UnInitializeException e) {
            Log.e(TAG, "Please Initialize SPEAR SDK before creating SPEAR SpeechRecognizer", e);
        }
    }

    private void updateUI() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Set SPEAR status
            ProcessMode processMode = ProcessMode.UNINITIALIZED;

            if (mode.equals("spear-wakeup") && spearWakeUp!= null) {
                processMode = spearWakeUp.getSpeechRecognizerState();
            } else if (mode.equals("spear-asr") && speechRecognizer != null) {
                processMode = speechRecognizer.getSpeechRecognizerState();
            }

            switch (processMode) {
                case UNINITIALIZED:
                    currentSpearStatus.setText(registrationStat+ ", Loading...");
                    break;
                case IDLE:
                    currentSpearStatus.setText(registrationStat+ ", Ready");
                    break;
                case LISTENING:
                    currentSpearStatus.setText(registrationStat+ ", Listening...");
                    break;
                case PROCESSING:
                    currentSpearStatus.setText(registrationStat+ ", Processing... please wait!");
                    break;
                case TERMINATED :
                    currentSpearStatus.setText(registrationStat+ ", Terminated!");
                    break;
            }
        } else {
            currentSpearStatus.setText("Missing Permissions");
        }
    }

    AdapterView.OnItemSelectedListener ignoredSymbolsListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ignoredSymbolLabel = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    AdapterView.OnItemSelectedListener casePreferenceListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            casePreferenceLabel = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void onUpdateConfigClicked(View view) {
        ArrayList<String> commadList = new ArrayList<String>();

        if (!ignoredSymbolLabel.equals("--")) {
            commadList.add("--ignored-symbols=<UNK>,<NOISE>," + ignoredSymbolLabel);
        }
        if (!casePreferenceLabel.equals("--")) {
            commadList.add("--case-preference=" + casePreferenceLabel);
        }

        if (!commadList.isEmpty()) {
            String[] commands = new String[commadList.size()];
            for (int j = 0; j < commadList.size(); j++) {
                commands[j] = commadList.get(j);
            }
            spearSdkApi.updateSpearConfig(commands, this);
        } else {
            Toast.makeText(getApplicationContext(), "No config parameter passed to update config.", Toast.LENGTH_LONG).show();
        }
    }

    private void hideUpdateConfigView() {
        symbLayout.setVisibility(View.INVISIBLE);
        caseLayout.setVisibility(View.INVISIBLE);
        updateConfigButton.setVisibility(View.INVISIBLE);
        updateConfigBarTop.setVisibility(View.INVISIBLE);
        updateConfigBarBottom.setVisibility(View.INVISIBLE);
    }

    private void showUpdateConfigView() {
        symbLayout.setVisibility(View.VISIBLE);
        caseLayout.setVisibility(View.VISIBLE);
        updateConfigButton.setVisibility(View.VISIBLE);
        updateConfigBarTop.setVisibility(View.VISIBLE);
        updateConfigBarBottom.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSpearInitComplete(RegistrationMode registrationMode) {
        Log.d(TAG, "onSpearInitComplete");

        if (registrationMode == RegistrationMode.REGISTERED) {
            registrationStat = "Registered";
        } else if (registrationMode == RegistrationMode.UNREGISTERED){
            registrationStat = "Unregistered";
        } else if (registrationMode == RegistrationMode.EXPIRED){
            registrationStat = "Expired";
        }

        // Initialize SpeechRecognizer
        initializeSpearWakeUp();
        initializeSpearRecognizer();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                infoText.setText(getString(R.string.alert_grammar_text));
            }
        });
    }

    @Override
    public void onSpearInitError(Throwable throwable) {
        Log.e(TAG, "Error in initializing SPEAR", throwable);
    }

    @Override
    public void onStartListening() {
        // started listening for incoming audio
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });
    }

    @Override
    public void onStopListening() {
        // stopped listening for incoming audio
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });

    }

    @Override
    public void onCommitResult(final TamTranscriptionPair[] tamTranscriptionPairs) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onCommitResult: " + tamTranscriptionPairs[0]);

                String result = tamTranscriptionPairs[0].transcription;

                transcribedText.setText(result);

                switch (result.toUpperCase(Locale.US)) {
                    case DemoCommandList.SWITCH_GRAMMAR:
                        try {
                            speechRecognizer.changeFstGrammar(PRE_COMPILED_GRAMMAR_FILE, false);
                            infoText.setText(getString(R.string.stop_grammar_text)
                                    .replace("{stop_spear}", DemoCommandList.STOP_SPEAR)
                                    .replace("{available_commands}", Arrays.toString(availableCommands)));
                        } catch (UnInitializeException e) {
                            Log.e(TAG, "Failed to change grammar.", e);
                        }
                        break;
                    case DemoCommandList.SWITCH_LABEL_GRAMMAR:
                        try {
                            speechRecognizer.changeCommandListGrammar(labelCommandList);
                            infoText.setText(getString(R.string.stop_grammar_text)
                                    .replace("{stop_spear}", DemoCommandList.STOP_SPEAR)
                                    .replace("{available_commands}", UIHelper.getLabelCommands(labelCommandList)));
                        } catch (UnInitializeException e) {
                            Log.e(TAG, "Failed to change grammar.", e);
                        }
                        break;
                    case DemoCommandList.STOP_SPEAR:
                        mode = "spear-wakeup";
                        speechRecognizer.stopListening();
                        spearWakeUp.startListening(true);
                        infoText.setText(getString(R.string.alert_grammar_text));
                        hideUpdateConfigView();
                        break;
                }

                updateUI();
            }
        });
    }

    @Override
    public void onIntermediateResult(final TamTranscriptionPair[] tamTranscriptionPairs) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onIntermediateResult: " + tamTranscriptionPairs[0]);
            }
        });
    }

    @Override
    public void onRecognitionError(Throwable throwable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });
        hideUpdateConfigView();
        if (throwable.getMessage().contains("Trial limit is reached")) {
            infoText.setText(getString(R.string.expiration_terminate_text));
        } else {
            infoText.setText(getString(R.string.other_terminate_text));
        }
        Log.e(TAG, "Error in Recognizing audio", throwable);
        transcribedText.setText("");
    }

    @Override
    public void onStartWakeUpListening() {
        // started listening for incoming audio
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });
    }

    @Override
    public void onStopWakeUpListening() {
        // stopped listening for incoming audio
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });
    }

    @Override
    public void onCommitResult(SpearWakeUpResult spearWakeUpResult) {
        if (spearWakeUpResult.getRetval() == 1) {
            spearWakeUp.stopListening();
            mode = "spear-asr";
            try {
                speechRecognizer.changeCommandListGrammar(demoCommandList);
            } catch (UnInitializeException e) {
                Log.e(TAG, "Failed to change grammar.", e);
            }
            speechRecognizer.startListening(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    infoText.setText(getString(R.string.switch_grammar_text)
                            .replace("{stop_spear}", DemoCommandList.STOP_SPEAR)
                            .replace("{switch_grammar}", DemoCommandList.SWITCH_GRAMMAR)
                            .replace("{switch_label_grammar}", DemoCommandList.SWITCH_LABEL_GRAMMAR)
                            .replace("{available_commands}", Arrays.toString(demoCommandList.getCommandList())));
                    showUpdateConfigView();
                    updateUI();
                }
            });
        }
    }

    @Override
    public void onVadResult(final TamVadPair tamVadPair) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Do something with SPEAR VAD result
                Log.d(TAG, "vadResult: " + tamVadPair);
            }
        });
    }

    @Override
    public void updateSpearConfigStatus(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
            }
        });
    }
}
