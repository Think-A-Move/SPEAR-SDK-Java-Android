# SpearWakeUp v5.1.0

`SpearWakeUp.java` interface provides access to the SPEAR WakeUp Engine.
Call [`SpearSdkApi.getInstance().createSpearWakeUp()`](SpearSdkApi.md) to get an instance of
this class. Please note that
`subscribeSpearWakeUpListener(int, com.thinkamove.spearnative.spear.api.SpearWakeUpListener)` must be
called before dispatching any command to the created `SpearWakeUp` to receive recognition
notifications. In addition, the application must have `Manifest.permission.RECORD_AUDIO`,
`Manifest.permission.READ_EXTERNAL_STORAGE` and `Manifest.permission.WRITE_EXTERNAL_STORAGE` 
permissions to use this class.

## public void subscribeSpearWakeUpListener(int uniqueId, SpearWakeUpListener spearWakeUpListener)

Subscribes SpearWakeUpListener to receive SpearWakeUp recognition callbacks. Please note that this
method must be called before dispatching any command to the created SpearWakeUp to receive
SpearWakeUp notifications.

 * **Parameters:**
   * `uniqueId` — unique ID for the `SpearWakeUpListener`.
   * `spearWakeUpListener` — will receive SpearWakeUp callbacks from the created `SpearWakeUp`.

## public void unsubscribeSpearWakeUpListener(int uniqueId)

Unsubscribes previously subscribed SpearWakeUpListener with its uniqueId.

 * **Parameters:**
    * `uniqueId` — unique ID of the subscribed `SpearWakeUpListener`.

## public int initWithFst(String fstNamePath) throws UnInitializeException`

Initializes Spear WakeUp Engine with the compiled WakeUp grammar (FST). Please note that,
this method or `initWithWakeWord(String)` must be called before dispatching any command to the
created `SpearWakeUp`.

 * **Parameters:**
    * `fstNamePath` — Complete path of the FST file on the phone HDD

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful.

 * **Exceptions:**
    * `UnInitializeException` — If provided fstNamePath is not valid or null or failed to sets
    the WakeUp word grammar successfully.

## public int initWithWakeWord(String wakeWord) throws UnInitializeException`

Initializes Spear WakeUp Engine with specified wakeWord. Please note that, this method or
`initWithFst(String)` must be called before dispatching any command to the created
`SpearWakeUp`.

 * **Parameters:**
    * `wakeWord` — A wakeUp word. Long sentences as wakeUp word is not recommended. We
     recommend to use one to three words as WakeUp word.

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful.

 * **Exceptions:**
    * `UnInitializeException` — If provided WakeUp word is not valid or null or failed to
     sets the WakeUp word grammar successfully.

## public int startListening(boolean isMicrophoneInput)`

Starts listening for incoming audio from the device's microphone or already collected audio
chunks and SPEAR WakeUp Engine will start decoding them into text. This is an asynchronous call.
`SpearWakeUpListener#onStartWakeUpListening()` will be called when SPEAR WakeUp Engine starts listening
for incoming audio. The decoded text output will be returned in
`SpearWakeUpListener#onCommitResult(SpearWakeUpResult)` callback method.

 * **Parameters:**
    * `isMicrophoneInput` :
        * true: SPEAR starts listening for incoming audio from the device's microphone.
        * false: SPEAR starts listening for already collected audio data. See
        `transcribeAudioData(byte[])` for more details.

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public int stopListening()

Stops listening for incoming audio. This is an asynchronous call.
`SpearWakeUpListener#onStopWakeUpListening()` will be called when SPEAR WakeUp Engine stopped. It is
possible that there may still be audio in the decoder input queue when called this method.
SPEAR WakeUp Engine will decode all enqueued audio first and then stops the engine. If you don't
care about all enqueued audio then call `forceStopListening()`.

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public int forceStopListening()

Force stops listening for incoming audio. SPEAR WakeUp Engine will throw out all enqueued audio
and stop decoding. `SpearWakeUpListener#onStopWakeUpListening()` will be called when SPEAR WakeUp Engine
stopped.

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public int transcribeAudioFile(File file)

Transcribes RAW audio file into text. This is an asynchronous call.
`SpearWakeUpListener#onStartWakeUpListening()` will be called when SPEAR decoder starts transcribing the
file and `SpearWakeUpListener#onStopWakeUpListening()` will be called when SPEAR WakeUp engine done
transcribing the file. The transcribed text output will be returned in
`SpearWakeUpListener#onCommitResult(SpearWakeUpResult)` callback method.

 * **Parameters:**
    * `file` — RAW audio file (*.raw).

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public int transcribeAudioData(byte[] audioData)

Transcribes audio chunks into text. Call `startListening(boolean)` with `false` to start
listening for incoming audio chunks before calling this method.

 * **Parameters:**
    * `audioData` — Audio chunks in byte[].

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public ProcessMode getSpeechRecognizerState()

Gets the current state of SPEAR WakeUp Engine.

 * **Returns:**
    * UNINITIALIZED: SPEAR WakeUp Engine is not initialized.
    * IDLE: SPEAR WakeUp Engine is initialized and ready to use.
    * LISTENING: SPEAR WakeUp Engine is listening for incoming audio.
    * PROCESSING: SPEAR WakeUp Engine is processing old audio.

## public SpearPerformanceData getSpearPerformanceData()

Gets SPEAR WakeUp Engine's performance data.

 * **Returns:** SpearPerformanceData.

     SpearPerformanceData provides following performance data:

     * realTimeRatio: how fast decoder is processing audio compared to real time.
     * bufferedBlockSize: how many audio chunks are left to process when SPEAR WakeUp Engine
     status is `ProcessMode#LISTENING` or `ProcessMode#PROCESSING`.
     * audioBufferedSeconds: time requires to process buffered audio in seconds.

## public int getPercentComplete()

Gets the percentage of processed buffered audio.

 * **Returns:**
    * 0 to 100: when `getSpeechRecognizerState()` is PROCESSING.
    *  0: when `getSpeechRecognizerState()` is IDLE or LISTENING.
    * -1: when call is unsuccessful or this method is called without calling
    `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public int saveRecognizerInputAudio(boolean shouldSave)

Saves incoming audio to the external storage
("/storage/emulated/0/Android/data/<app_package_name>/files/com.think-a-move.spear-wakeup/Audio/")
if `shouldSave` is true.

 * **Parameters:**
    * `shouldSave` —
        * true: If your application should save incoming audio to the external storage.
        * false: If your application should not save incoming audio.

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public int saveRecognizerResult(boolean shouldSave)

Saves `SpearWakeUpResult` - a value of wakeUp word detection (1 for wakeUp word and 0 for
non-wakeUp word) and deviation score to the external storage
("/storage/emulated/0/Android/data/<app_package_name>/files/com.think-a-move.spear-wakeup/Audio/")
if `shouldSave` is true.

 * **Parameters:**
    * `shouldSave` —
        * true: If your application should save hypothesis to the external storage.
        * false: If your application should not save hypothesis.

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public int setRecognizerResultFileName(String outputFileName)

Sets the file name for the audio input and recognizer result data if
`saveRecognizerInputAudio(boolean)` or `saveRecognizerResult(boolean)` is called with `true`
parameter value.

 * **Parameters:**
    * `outputFileName` — Name of the file. SpearSdk will add unique date/time as a suffix to
     provided outputFileName to each created files.

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public String getRecognizerResultStorageLocation()

Gets the external storage path of the processed audio data if `saveRecognizerInputAudio(boolean)`
or `saveRecognizerResult(boolean)` is called with `true` parameter value.

 * **Returns:**
    * External storage path of the processed input audio.
    * Empty string if no file has been found for the current audio or this method is called without
    calling `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public int flushBuffer()

Flush all the audio data and save the current audio file if `saveRecognizerInputAudio(boolean)`
is called with true parameter value.

 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
     `initWithFst(String)` or `initWithWakeWord(String)` successfully.

## public void onDestroy()

Destroys the current instance and unsubscribe all SpearWakeUpListener callbacks.

# SpearWakeUpListener

## public void onStartWakeUpListening()

Called when SPEAR WakeUp Engine starts listening for incoming audio after calling
`SpearWakeUp#startListening(boolean)`.

## public void onStopWakeUpListening()

Called when SPEAR WakeUp Engine stops listening for incoming audio after calling
`SpearWakeUp#stopListening()` or `SpearWakeUp#forceStopListening()`.

## public void onCommitResult(SpearWakeUpResult spearWakeUpResult)

Called when SPEAR WakeUp Engine has final results for the incoming audio.

 * **Parameters:**
    * `spearWakeUpResult` — a wakeUp word detection value (0 for non-wakeUp word and 1 for
    wakeUp word) and associated deviation score.
