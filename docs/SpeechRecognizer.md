# SpeechRecognizer v5.1.0

`SpeechRecognizer.java` interface provides access to the SPEAR Speech Engine's Recognizer. 
Call [`SpearSdkApi.getInstance().createSpeechRecognizer()`](SpearSdkApi.md) to get an instance of
this class. Please note that
`subscribeRecognitionListener(int, com.thinkamove.spearnative.spear.api.RecognitionListener)` must be
called before dispatching any command to the created `SpeechRecognizer` to receive recognition
notifications. In addition, the application must have `Manifest.permission.RECORD_AUDIO`,
`Manifest.permission.READ_EXTERNAL_STORAGE` and `Manifest.permission.WRITE_EXTERNAL_STORAGE` 
permissions to use this class.

## public void subscribeRecognitionListener(int uniqueId, RecognitionListener recognitionListener)

Subscribes RecognitionListener to receive recognition callbacks. Please note that this method
must be called before dispatching any command to the created SpeechRecognizer to receive recognition
notifications.

 * **Parameters:**
    * `uniqueId` — unique ID for the subscribing `RecognitionListener`.
    * `recognitionListener` — will receive recognition callbacks from the created `SpeechRecognizer`.

## public void unsubscribeRecognitionListener(int uniqueId)

Unsubscribes previously subscribed RecognitionListener with its uniqueId.

 * **Parameters:** 
    * `uniqueId` — unique ID of the subscribed `RecognitionListener`.

## public void subscribeVadListener(int uniqueId, VadListener vadListener)

Subscribes VadListener to receive SPEAR VAD results. Please note that this method must be called 
before dispatching any command to the created SpeechRecognizer to receive VAD notifications.

 * **Parameters:**
    * `uniqueId` — unique ID for the `VadListener`.
    * `vadListener` — will receive vad callbacks from the created `SpeechRecognizer`.

## public void unsubscribeVadListener(int uniqueId)

Unsubscribes previously subscribed VadListener with its uniqueId.

 * **Parameters:**
    * `uniqueId` — unique ID of the subscribed `VadListener`.

## public void setFstGrammar(String fstNamePath, boolean shouldLoadFstExternally) throws UnInitializeException

Sets the complied grammar (FST) to the created `SpeechRecognizer`. Please note that
`setGrammar` (this method or `setCommandListGrammar(CommandList)`) must be called before dispatching
any command to the created `SpeechRecognizer`. Please note if `setGrammar` (this method
or `setCommandListGrammar(CommandList)`) is called for multiple `FST` or `CommandList` before hand,
SPEAR ASR will consider the first call to `setGrammar` (this method or
`setCommandListGrammar(CommandList)`) as active grammar when `startListening(boolean)` called until
`changeGrammar` (`changeFstGrammar(String, boolean)` or `changeCommandListGrammar(CommandList)`)
called for other `FST` or `CommandList` to change the active grammar.

 * **Parameters:**
    * `fstNamePath` — Name of the FST or complete path of the FST file depending on the
    shouldLoadFstExternally parameter value.
    * `shouldLoadFstExternally:` —
        * false - Name of the FST (<name>.fst) loaded from the assets folder
          (The FST must be included at `assets/com.think-a-move.spear/models/`).
        * true - Complete path of the FST file.
        
 * **Exceptions:**
    * `UnInitializeException` — If provided fstNamePath is not valid or null or failed to sets the
    grammar successfully.

## public void changeFstGrammar(String fstNamePath, boolean shouldLoadFstExternally) throws UnInitializeException

Changes the active grammar, to be decoded by SPEAR ASR Engine, to the `fstNamePath`. Consider
calling `setFstGrammar(String, boolean)` for the `fstNamePath` before hand.

 * **Parameters:**
    * `fstNamePath` — Name of the FST or complete path of the FST file depending on the
    shouldLoadFstExternally parameter value.
    * `shouldLoadFstExternally:` — 
        * false - Name of the FST (<name>.fst) loaded from the assets folder
          (The FST must be included at `assets/com.think-a-move.spear/models/`).
        * true - Complete path of the FST file.
        
 * **Exceptions:**
    * `UnInitializeException` — If provided fstNamePath is not valid or null or failed to change
    the grammar successfully.

## public void setCommandListGrammar(CommandList commandList) throws UnInitializeException

Compiles commands from the passed `CommandList` and sets the compiled grammar to the created
`SpeechRecognizer`. Please note that `setGrammar` (this method or `setFstGrammar(String, boolean)`)
must be called before dispatching any command to the created `SpeechRecognizer`. Please note if
`setGrammar` (this method or `setFstGrammar(String, boolean)`) is called for multiple `CommandList`
or `FSTs` before hand, SPEAR ASR will consider the first call to `setGrammar` (this method or
`setFstGrammar(String, boolean)`) as active grammar when `startListening(boolean)` called until
`changeGrammar` (`changeCommandListGrammar(CommandList)` or `changeFstGrammar(String, boolean)`)
called for other `CommandList` or `FST` to change the active grammar.

 * **Parameters:**
    * `commandList` — `CommandList` object.

 * **Exceptions:**
    * `UnInitializeException` — If provided CommandList is not valid or null or failed to compile
    the grammar successfully.

## void changeCommandListGrammar(CommandList commandList) throws UnInitializeException

Changes the active grammar, to be decoded by SPEAR ASR Engine, to the passed CommandList.
Consider calling `setCommandListGrammar(CommandList)` for the passed `CommandList` before hand.

 * **Parameters:**
    * `commandList` — `CommandList` object.

 * **Exceptions:**
    * `UnInitializeException` — If provided CommandList is not valid or null or failed to compile
    the grammar successfully.

## String[] getPronunciation(CommandList commandList) throws UnInitializeException

Gets the list of pronunciations for the CommandList. Consider calling
`setCommandListGrammar(CommandList)` for the passed `CommandList` before hand.

 * **Parameters:**
    * `commandList` — `CommandList` object.

 * **Returns:**
    * String[]: A list of string where each item contains a word, weight and a corresponding
    pronunciation, each separated by space.

 * **Exceptions:**
    * `UnInitializeException` — If provided CommandList is not valid or null or failed to compile
    the grammar successfully.

## public int startListening(boolean isMicrophoneInput)

Starts listening for incoming audio from the device's microphone or already collected audio
chunks and SPEAR ASR Engine will start decoding them into text. This is an asynchronous call.
`RecognitionListener#onStartListening()` will be called when SPEAR ASR Engine starts listening
for incoming audio. The decoded text output will be returned in
`RecognitionListener#onIntermediateResult(String)` and `RecognitionListener#onCommitResult(String)`
callback methods.

 * **Parameters:** 
    * `isMicrophoneInput` — 
        * true: SPEAR starts listening for incoming audio from the device's microphone.
        * false: SPEAR starts listening for already collected audio data. See
        `transcribeAudioData(byte[])` for more details.
 
 * **Returns:** 
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

## public int stopListening()

Stops listening for incoming audio. This is an asynchronous call.
`RecognitionListener#onStopListening()` will be called when SPEAR ASR Engine stopped.
It is possible that there may still be audio in the decoder input queue when called this method.
SPEAR ASR Engine will decode all enqueued audio first and then stops the engine. If you don't care
about all enqueued audio then call `forceStopListening()`.

 * **Returns:** 
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

## public int forceStopListening()

Force stops listening for incoming audio. SPEAR ASR Engine will throw out all enqueued audio and
stop decoding. `RecognitionListener#onStopListening()` will be called when SPEAR ASR Engine stopped.

 * **Returns:** 
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

## public int transcribeAudioFile(File file)

Transcribes RAW audio file into text. This is an asynchronous call.
`RecognitionListener#onStartListening()` will be called when SPEAR decoder starts transcribing
the file and `RecognitionListener#onStopListening()` will be called when SPEAR engine done
transcribing the file. The transcribed text output will be returned in
`RecognitionListener#onIntermediateResult(String)` and `RecognitionListener#onCommitResult(String)`
callback methods.

 * **Parameters:** 
    * `file` — RAW audio file (*.raw).
    
 * **Returns:**
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

## public int transcribeAudioData(byte[] audioData)

Transcribes audio chunks into text. Call `startListening(boolean)` with `false` to start listening
for incoming audio chunks before calling this method.

 * **Parameters:** 
    * `audioData` — Audio chunks in byte[].
 
 * **Returns:** 
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

## public ProcessMode getSpeechRecognizerState()

Gets the current state of SPEAR ASR Engine.

 * **Returns:** 
    * UNINITIALIZED: SPEAR ASR Engine is not initialized.
    * IDLE: SPEAR ASR Engine is initialized and ready to use.
    * LISTENING: SPEAR ASR Engine is listening for incoming audio.
    * PROCESSING: SPEAR ASR Engine is processing old audio.
    * TERMINATED: SPEAR ASR Engine is terminated.(especially when allowed processing time is used up according to the license)

## public SpearPerformanceData getSpearPerformanceData()

Gets SPEAR ASR Engine's performance data.

 * **Returns:** SpearPerformanceData.

     SpearPerformanceData provides following performance data:

    * realTimeRatio: how fast decoder is processing audio compared to real time.
    * bufferedBlockSize: how many audio chunks are left to process when SPEAR ASR engine status is
    `ProcessMode#LISTENING` or `ProcessMode#PROCESSING`.
    * audioBufferedSeconds: time requires to process buffered audio in seconds.

## public int getPercentComplete()

Gets the percentage of processed buffered audio.

 * **Returns:** 
    *  0 to 100: when `getSpeechRecognizerState()` is PROCESSING.
    *  0: when `getSpeechRecognizerState()` is IDLE or LISTENING.
    * -1: when call is unsuccessful or this method is called without calling
    `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

## public int saveRecognizerInputAudio(boolean shouldSave)

Saves incoming audio to the external storage
("/storage/emulated/0/Android/data/<app_package_name>/files/com.think-a-move.spear/Audio/")
if `shouldSave` is true.

 * **Parameters:** 
    * `shouldSave` — 
        * true: If your application should save incoming audio to the external storage.
        * false: If your application should not save incoming audio.
        
 * **Returns:** 
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

## public int saveRecognizerResult(boolean shouldSave)

Saves N-best decoded output text (hypothesis) and associated cost to the external storage
("/storage/emulated/0/Android/data/<app_package_name>/files/com.think-a-move.spear/Audio/")
if `shouldSave` is true.

 * **Parameters:** 
    * `shouldSave` — 
        * true: If your application should save hypothesis and associated cost to the external storage.
        * false: If your application should not save hypothesis.
 
 * **Returns:** 
    *  0: when call is successful.
    * -1: when call is unsuccessful or this method is called without calling
    `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

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
    `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

## public String getRecognizerResultStorageLocation()

Gets the external storage path of the processed audio data if `saveRecognizerInputAudio(boolean)`
or `saveRecognizerResult(boolean)` is called with `true` parameter value.

 * **Returns:** 
    * External storage path of the processed input audio.
    * Empty string if no file has been found for the current audio or this method is called without
    calling `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

## public int flushBuffer()

Flush all the audio data and save the current audio file if `saveRecognizerInputAudio(boolean)` is
called with true parameter value.

 * **Returns:** 
      *  0: when call is successful.
      * -1: when call is unsuccessful or this method is called without calling
      `setFstGrammar(String, boolean)` or `setCommandListGrammar(CommandList)` successfully.

## public void onDestroy()

Destroys the current instance and unsubscribe all RecognitionListener callbacks.


# RecognitionListener

## public void onStartListening()

Called when SPEAR ASR Engine starts listening for incoming audio after calling
`SpeechRecognizer#startListening(boolean)`

## public void onStopListening()

Called when SPEAR ASR Engine stops listening for incoming audio after calling
`SpeechRecognizer#stopListening()` or `SpeechRecognizer#forceStopListening()`

## public void onCommitResult(TamTranscriptionPair[] commitResults)

Called when SPEAR ASR Engine has final results for the incoming audio.

 * **Parameters:** 
    * `commitResults` — An array of N-best commit results and associated cost for the incoming audio.
    
## public void onIntermediateResult(TamTranscriptionPair[] intermediateResults)

Called when SPEAR ASR Engine has intermediate results while decoding and processing incoming audio.

 * **Parameters:** 
    * `intermediateResults` — An array of N-best intermediate results and associated cost for the
    incoming audio.

## public void onRecognitionError(Throwable throwable)

Called when SPEAR ASR Engine throw exceptions while decoding and processing incoming audio.

 * **Parameters:** 
     * `throwable` — throws error (When Engine used up allowed processing time, "Trial limit is reached." Error message will be passed from here.)

# VadListener

## public void onVadResult(TamVadPair vadResult)

Called for speech vs non-speech data detected by SPEAR ASR Engine's Voice Activity Detector.

 * **Parameters:**
    * `vadResult` — A tuple of a string ('SP' for speech data, 'UN' for unknown or 'NS' for
    non-speech data) and the duration as a float.

# TamTranscriptionPair

A tuple of two objects: decoded output (String) and associated cost (float).

# TamVadPair

A tuple of two objects: a flag to indicate speech('SP'), unknown('UN') or non-speech('NS')
data (string) and the duration (float).

# CommandList

This class is responsible for providing active commands to SPEAR ASR Engine. Your custom
CommandList class should implement following methods.

## public abstract String[] getCommandList()

Gets a list of active commands.

The list should include all the target commands with interleaved words
and labels.
1. Each command can be separated by pipe "|" or newline character "\n".
   It is recommended to put each command in parentheses to correctly
   parse the grammar.
2. Words/labels inside a command can be separated by a dot(".") or
  unlimited times of space(" "), tabulator("\t"), carriage return("\r").
3. Each word can start with alphabet and be followed by unlimited times
  of alphabet, dash, and apostrophe. (For example, john's).
4. Word can also be a number represented in numerical digit format. Sign
  and decimal points are also supported (For example, -15.46). This
  number will be interpolated as label `$integer` or `$real` based on
  the value of this number.

Please check `GrammarSyntax.md` for more details on accepted grammar
rules by SPEAR ASR Engine.

 * **Returns:**
    * A list of active commands.

## public abstract Map<String, String> mapCommands()

* Key: An input command via voice.
* Value: A command that SpearSdk should return as a result in
`RecognitionListener#onCommitResult(TamTranscriptionPair[])`.

As an example, if your application would like to receive "HELLO" command for the input commands 
"HELLO", "HELL LOW", and "ELMO" then the application should create a map where the value 
"HELLO" should be defined for keys "HELL LOW" and "ELMO".

 * **Returns:**
    * An empty Map or Map of commands to a resulting command.

## public abstract CommandsFileData getCommandsFileData()

Gets the active commands from file.

Use this method if you would like to load commands regex from the file.
This method has precedence over `getCommandList()`. Please check
`GrammarSyntax.md` for more details on accepted grammar rules by SPEAR
ASR Engine.

 * **Returns:** A new `CommandsFileData` object with following parameters.
    * `commandsFileNamePath` — Name of the commands regex file or complete path of the Commands
                                regex file depending on the `shouldLoadFileExternally` parameter value.
    * `shouldLoadFileExternally` —
        * true: if SPEAR ASR Engine should load grammar from any location other than the assets folder.
        * false: if SPEAR ASR Engine should load grammar from the assets folder
          (The grammar file must be included at
          `assets/com.think-a-move.spear/models/`).

## public abstract Map<String, String[]> getGrammarLabels()

Gets a map of labels and assigned commands to it.

* Key: A label name must start with an alphabet and can be followed by
  unlimited times of alphabet, numeric digit, dash, underscore, and
  apostrophe.
* Value: A list of commands. Please check `getCommandList()` for
  the format of the commands.

There are three pre-defined labels available:

1. `$digit`: single numeric digit
2. `$integer`: any integer number(sign is optional)
3. `$real`: any real number(sign is optional)

Please check `GrammarSyntax.md` for accepted grammar rules by SPEAR ASR
Engine.

 * **Returns:**
    * A map of labels and assigned commands.
