# SPEAR-SDK-Java-Android

SPEAR-SDK-Java-Android is SPEAR's software development kit that allows you to integrate SPEAR-ASR and SPEAR-WakeUp to voice-enable your Android applications. The SPEAR-ASR engine will terminate after 10 minutes of processing speech. SPEAR-WakeUp will not terminate after 10 minutes. For continuous operation, contact us at info@think-a-move.com. 

By cloning this repository and downloading SPEAR-SDK library you agree to the [SPEAR-SDK EULA](./LICENSE).

## Contents:

- [SpearSdk-5.1.0.aar](./SpearSdk-5.1.0.aar) - SPEAR-SDK library for Android consisting of SPEAR-ASR and SPEAR-WakeUp.

- SPEAR-ASR in an on-device automatic speech recognition engine that delivers superior recognition accuracy in high noise environments and provides both command recognition and free-from transcription. Developed for use by military medics in the battlefield.

- SPEAR-WakeUp in an on-device lightweight Wake word detection system that has very low power consumption.

- Documentation for the API and app integration is located in the docs directory.   

- SpearSdkExample is an example android app that demonstrates how to integrate our SPEAR-SDK library and language model with Android applications

## Features
- Noise-robust DNN-Based Acoustic Model
- Noise-robust DNN-Based VAD Model
- Domain-specific Language Model
- Works Offline 
- Supports NBest results
- Supports Command and Control (finite state) Applications
- Supports Transcriptions (statistical)
- Speaker Independent
- Grammar switching at runtime
- Dynamic grammar compilation (Grammar on-the-fly)
- Supports 16K Audio Sampling Rate
- Down-sample to 8K or 16K
- Low CPU consumption Wake-up Word 
- Java language support
- Floating point version
- Barge-in support

## Languages
- Chinese - Mandarin
- English
- French
- Spanish
- We can train other languages or augment existing languages. Contact us at info@think-a-move.com.

## Acoustic Models
- Optimized for high-noise
- We can train acoustic models for specific noise environments. Contact us at info@think-a-move.com.
- Accuracy results using WSJ Corpus mixed with various noise samples
![WSJ Accuracy Results](./WSJ_Accuracy_Results.png)

## Services we can provide
- Create customized Acoustic Models
- Create domain Specific Language Models
- Extend additional functionality of SPEAR-ASR to address specific needs
- Voice-enable your application or make one for you
- Contact us at info@think-a-move.com.