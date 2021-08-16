# SpearSdkApi v5.1.0

`SpearSdkApi.java` class provides access to the SPEAR Speech Engine. 
## public static SpearSdkApi getInstance()

Gets the instance of SpearSdkApi. Please note that `subscribeSpearInitEvent(int, SpearInitListener)` 
must be called before dispatching any command to the created SpearSdkApi to receive notifications.

 * **Returns:** 
    * SpearSdkApi: A `SpearSdkApi` instance.
## public void initialize(Context context, String SpearLmPackage)

Initializes SPEAR ASR Engine. This method will run in a separate thread and generally takes a
longer time to load the engine. `SpearInitListener#onSpearInitComplete(RegistrationMode)` will be called on successful 
initialization. `SpearInitListener#onSpearInitError(Throwable)` will be called when initialization fails.

 * **Parameters:** 
    * `context` — Android application context.
    * `SpearLmPackage` — Absolute path of provided SPEAR LM package on phone HDD.

## public SpeechRecognizer createSpeechRecognizer() throws MissingPermissionException, UnInitializeException

Creates a new `SpeechRecognizer`. Please note that `SpeechRecognizer#subscribeRecognitionListener(int, RecognitionListener)` 
should be called before dispatching any command to the created `SpeechRecognizer`, otherwise no notifications will be received.

 * **Returns:** 
    * `SpeechRecognizer`: a new `SpeechRecognizer`.
 
 * **Exceptions:**
    * `MissingPermissionException` — If an application hasn't granted required `RECORD_AUDIO`, 
                                    `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` permissions.
    * `UnInitializeException` — If called without initializing SPEAR SDK (`initialize(Context, String)`) or
                                an error in creating a new `SpeechRecognizer`.

## public SpearWakeUp createSpearWakeUp() throws MissingPermissionException, UnInitializeException

Creates a new `SpearWakeUp`. Please note that
`SpearWakeUp#subscribeSpearWakeUpListener(int, SpearWakeUpListener)` should be called before
dispatching any command to the created `SpearWakeUp`, otherwise no notifications will be received.

 * **Returns:**
    * `SpearWakeUp`: a new `SpearWakeUp`.

 * **Exceptions:**
   * `MissingPermissionException` — If an application hasn't granted required `RECORD_AUDIO`, 
                                    `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` permissions.
   * `UnInitializeException` — If called without initializing SPEAR SDK (`initialize(Context, String)`) or
                                an error in creating a new `SpearWakeUp`.

## public void subscribeSpearInitEvent(int uniqueId, SpearInitListener spearInitListener)

Subscribes `SpearInitListener` to receive SPEAR ASR Engine's initialization callback.

 * **Parameters:**
    * `uniqueId` — unique ID for the `SpearInitListener`.
    * `spearInitListener` — will receive SPEAR ASR Engine's initialization callbacks.

## public void unsubscribeSpearInitEvent(int uniqueId)

Unsubscribes previously subscribed `SpearInitListener` with its uniqueId.

 * **Parameters:**
    * `uniqueId` — unique ID of the subscribed `SpearInitListener`.
   
## public boolean isSpearInitialized()

Checks whether SPEAR ASR Engine is initialized or not.

 * **Returns:**
    * true: if SPEAR ASR Engine is successfully initialized with `initialize(Context, String)`
    * false: otherwise.

## public void setAudioSourceInput(int audioSourceInput)

Sets AudioSource input value. This value can be one of the AudioSource constants 
defined in https://developer.android.com/reference/android/media/MediaRecorder.AudioSource. 
We recommend to use the default value. SPEAR ASR Engine's performance will vary if 
audio data collected by the selected AudioSource is not compatible with SPEAR.

 * **Parameters:** 
    * `audioSourceInput` — 
        * -1: for the default value of https://developer.android.com/reference/android/media/MediaRecorder.AudioSource.html#MIC 
                on SM-T713 device and https://developer.android.com/reference/android/media/MediaRecorder.AudioSource.html#VOICE_RECOGNITION 
                on all other devices.
        * AudioSource constant: defined in https://developer.android.com/reference/android/media/MediaRecorder.AudioSource.

## public void subscribeMicStateChangeListener(int uniqueId, AudioTask.OnMicStateChangeListener onMicStateChangeListener)

Subscribes `AudioTask.OnMicStateChangeListener` to receive microphone state changes.

 * **Parameters:**
    * `uniqueId` — unique ID for the `AudioTask.OnMicStateChangeListener`.
    * `onMicStateChangeListener` — will receive Mic state changes.

## public void unsubscribeMicStateChangeListener(int uniqueId)

Unsubscribes previously subscribed `AudioTask.OnMicStateChangeListener` with its uniqueId.

 * **Parameters:** 
    * `uniqueId` — unique ID of the subscribed `AudioTask.OnMicStateChangeListener`.

## public int getVolume()

Gets the volume level of the incoming audio.

 * **Returns:** 
    * 0 to 100: volume level.
    * -1: If fails to retrieve volume level.

## public void destroy()

Stops listening for incoming audio and destroys the current instance of SpearSdkApi. 
Call this method when re-initialization of SpearSdkApi is needed before calling 
`initialize(Context, String)` method again.

## public void updateSpearConfig(final String[] commands, SpearConfigUpdateListener spearConfigUpdateListener)
Subscribes `SpearConfigUpdateListener` to receive status message.

Updates the config parameters. This method will run in a separate thread. 
`SpearConfigUpdateListener#updateSpearConfigStatus(String message)` will be called only if there is any warning message that user should know. 

 * **Parameters:**
    * `commands` — Strings array of the updated spear config parameters. i.e new String[]{"--case-preference=lower", "--ignored-symbols=<UNK>,<NOISE>,SPEAR"}
    * `spearConfigUpdateListener` — To recieve warning status message.


# SpearInitListener

## void onSpearInitComplete(RegistrationMode registrationMode)

Called when SPEAR ASR Engine initialized successfully.
 * **Parameters:**
    * `registrationMode`  —
        * UNREGISTERED: when no license file is provided.
        * REGISTERED: when license file is provided and valid.
        * EXPIRED: when license file is provided but expired.

## void onSpearInitError(Throwable throwable)

Called when SPEAR ASR Engine failed on initialization.

 * **Parameters:** 
    * `throwable` — throws error
