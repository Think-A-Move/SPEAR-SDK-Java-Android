# How to integrate SpearSdk library v5.1.0

- Add SpearSdk-<version>.aar to libs folder under app directory.
- Add `flatDir{dirs 'libs'}` to the project's build.gradle file under
  repositories section.

```
    allprojects {
        repositories {
            ...
            flatDir {
              dirs 'libs'
            }
        }
    }
```

- Add spearSdk library dependency in the app's build.gradle file.

```groovy
    dependencies {
        api(name: 'spearSdk-<version>', ext: 'aar')
        // or compile(name: 'spearSdk-<version>', ext: 'aar') if you're using gradle plugin version less than 3.0
    }
```

- SpearSdk supports minimum API version 21. Make sure your app supports
  Android API 21 and above.

- Your application should implement the following interfaces:

  - `SpearInitListener`: To get the callback result when SPEAR ASR
    Engine or SPEAR WakeUp Engine initialization completes and when
    initialization error occurs.
  - `RecognitionListener`: To get the callback results when SPEAR ASR
    Engine returns intermediate or final results, when the engine
    starts or stops listening, and when SPEAR ASR engine return errors.
  - `SpearWakeUpListener`: To get the callback results when SPEAR WakeUp
    Engine returns final results and when the engine starts or stops
    listening.
  - `AudioTask.OnMicStateChangeListener`: To get the callback results
    when incoming audio mic status has changed.

    Check `SpearSdkApi.md`, `SpeechRecognizer.md`, `SpearWakeUp.md` documentation
    for more details on each interface methods.

```java
public class MyClass implements SpearInitListener, RecognitionListener,
SpearWakeUpListener, AudioTask.OnMicStateChangeListener {
   // Initialize SPEAR ASR Engine
   // Implements interface methods
}
```   

- Your app should request permissions `Manifest.permission.RECORD_AUDIO`,
  `Manifest.permission.READ_EXTERNAL_STORAGE` and `Manifest.permission.WRITE_EXTERNAL_STORAGE`
  to work SPEAR ASR Engine as expected if your application supports Android Marshmallow
  and up devices. For reference:
  https://developer.android.com/training/permissions/requesting.html

-  Your application must include provided LM Package, if shared, to any location on HDD.
   SpearSdkApi can then be initialized by calling
  `SpearSdkApi.getInstance().initialize(Context, String)`.
- Get the instance of `SpeechRecognizer` by calling
  `SpearSdkApi.getInstance().createSpeechRecognizer()` to access SPEAR
  Recognizer's functionality. Check `SpearSdkApi.md` for more details.
- Your application must include FST, if shared, to
  `assets/com.think-a-move.spear/models/` and call
  `setFstGrammar("example.fst", false)` on created `SpeechRecognizer`
  object. Or call `setFstGrammar("absolute_path/example.fst", true)` with
  any location on HDD on created `SpeechRecognizer` object.
- Get the instance of `SpearWakeUp` by calling
  `SpearSdkApi.getInstance().createSpearWakeUp()` to access SPEAR
  WakeUp functionality. Check `SpearWakeUp.md` for more details.


#### Example:

```java
public class MyClass implements SpearInitListener, RecognitionListener, VadListener, SpearWakeUpListener, AudioTask.OnMicStateChangeListener, SpearConfigUpdateListener {
    private SpearSdkApi spearSdkApi;
    private SpeechRecognizer speechRecognizer;
    private SpearWakeUp spearWakeUp;
    private static String LM_Package = "/storage/self/primary/SPEAR-DATA-EN";

    // Call this method to load native SPEAR libraries.
    public void initializeSpear() {
        spearSdkApi = SpearSdkApi.getInstance();
        spearSdkApi.subscribeSpearInitEvent(this.hashCode(), this);
        spearSdkApi.initialize(getApplicationContext(), LM_Package);
    }

    // Call this method when permissions are granted and SPEAR ASR is initialized successfully. See `onSpearInitComplete()` implementation.
    public void initializeSpearRecognizer() {
        try {
            speechRecognizer = spearSdkApi.createSpeechRecognizer();
            speechRecognizer.subscribeRecognitionListener(this.hashCode(), this);
            speechRecognizer.subscribeVadListener(this.hashCode(), this);
            speechRecognizer.setFstGrammar("example.fst", false);
            speechRecognizer.saveRecognizerInputAudio(true);
            speechRecognizer.saveRecognizerResult(true);
        } catch (MissingPermissionException e) {
            Log.e(TAG, "Please grant required permissions before initializing SPEAR ASR Engine!", e);
        } catch (UnInitializeException e) {
             Log.e(TAG, "UnInitialized SpearSdkApi error", e);
        }
    }

    // Call this method when permissions are granted and SPEAR WakeUp is initialized successfully. See `onSpearInitComplete()` implementation.
    public void initializeSpearWakeUp() {
        try {
            spearWakeUp = spearSdkApi.createSpearWakeUp();
            speechRecognizer.subscribeRecognitionListener(this.hashCode(), this);
            spearWakeUp.subscribeSpearWakeUpListener(this.hashCode(), this);
            spearWakeUp.initWithFst(WAKEUP_FST, false);
        } catch (MissingPermissionException e) {
            Log.e(TAG, "Please grant required permissions before initializing SPEAR WakeUp Engine!", e);
        } catch (UnInitializeException e) {
             Log.e(TAG, "UnInitialized SpearSdkApi error", e);
        }
    }

    @Override
    public void onSpearInitComplete() {
       // This callback method does not run on main UI thread. Make sure to run UI changes on UI thread.
       if (ContextCompat.checkSelfPermission(this.activity, "android.permission.RECORD_AUDIO") == 0 
            && ContextCompat.checkSelfPermission(this.activity, "android.permission.READ_EXTERNAL_STORAGE") == 0)
             && ContextCompat.checkSelfPermission(this.activity, "android.permission.WRITE_EXTERNAL_STORAGE") == 0){
           initializeSpearWakeUp();
           initializeSpearRecognizer();
       }
    }

    @Override
    public void onSpearInitError(Throwable throwable) {
       // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
       Log.e(TAG, "Error in initializing SPEAR", throwable);
    }

    @Override
    public void onStartListening() {
        // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
        // Do something when SPEAR starts listening for incoming audio after calling
        // speechRecognizer.startListening()
    }
    
    @Override
    public void onStopListening() {
        // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
        // Do something when SPEAR stops listening for incoming audio after calling
        // speechRecognizer.stopListening() or speechRecognizer.forceStopListening()
    }
    
    @Override
    public void onCommitResult(final TamTranscriptionPair[] commitResult) {
        // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
        // Do something with the decoded N-best "commitResults"
    }
    
    @Override
    public void onIntermediateResult(final TamTranscriptionPair[] intermediateResult) {
        // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
        // Do something with the decoded N-best "intermediateResults"
    }

    @Override
    public void onRecognitionError(Throwable throwable) {
            // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
            Log.e(TAG, "Error in Recognizing audio", throwable);
    }

    @Override
    public void onStartWakeUpListening() {
        // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
        // Do something when SPEAR WakeUp starts listening for incoming audio after calling
        // spearWakeUp.startListening()
    }
    
    @Override
    public void onStopWakeUpListening() {
        // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
        // Do something when SPEAR stops listening for incoming audio after calling
        // spearWakeUp.stopListening() or spearWakeUp.forceStopListening()
    }

    @Override
    public void onCommitResult(final SpearWakeUpResult[] spearWakeUpResult) {
        // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
        // Do something with returned "spearWakeUpResult"
    }

    @Override
    public void onVadResult(final TamVadPair vadResult) {
        // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
        // Do something with the vadResult.
    }

    @Override
    public void onStateChange(AudioTask.MicState micState) {
        // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
        // Do something with the current mic status
    }

    @Override
    public void updateSpearConfigStatus(final String status) {
        // This callback method does not run on the main UI thread. Make sure to run UI changes on UI thread.
        // Do something with the status if there is any
    }
}
```
#### Notes:

- SpearSdk contains native code that links LLVM's shared C++ runtime
  (libc++_shared.so) provided by Android NDK r17c. If your application
  includes native code that fulfills one of the following criteria, then
  there is a risk of incompatibility and undefined behavior at runtime.

  - If the native code links the GNU libstdc++ (libstdc++.so or
    libstdc++.a) or STLport (libstlport_shared.so or
    libstlport_static.a) C++ runtimes, which were offered by older NDKs
    but have since been deprecated, then it is almost certainly
    incompatible with the native code inside SpearSdk.
  - If the native code links the LLVM shared C++ runtime
    (libc++_shared.so) provided by a different NDK version, then there
    is a risk of incompatibility because the final APK can only contain
    one version of libc++_shared.so.
  - If the native code links the LLVM static C++ runtime
    (libc++_static.a) then the STL, including its global data and static
    constructors, are duplicated between the SpearSdk and the user's
    native code. This violates the One Definition Rule in the C++
    standard and can lead to undefined behavior.
