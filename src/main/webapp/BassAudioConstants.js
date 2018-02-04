/* 
 * Copyright 2018 Robert Steele at bsteele.com
 */


class BassAudioConstants {
}
BassAudioConstants.sampleRate = 44100;
BassAudioConstants.minHertz = 40;
BassAudioConstants.maxHertz = 200;
BassAudioConstants.maxDt = 1 / BassAudioConstants.minHertz;
BassAudioConstants.minDt = 1 / BassAudioConstants.maxHertz;