package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class AudioEngine {

    private val sampleRate = 22050
    private var isPlaying = AtomicBoolean(false)
    private var currentMood = "mystery"
    private var musicVolume = 0.7f
    private var sfxVolume = 0.8f
    private var ambienceVolume = 0.6f

    // Dynamic stealth & action audio states
    var keeperTensionLevel = 0.0f // 0.0f (calm) to 1.0f (chase/danger)
    var isPlayerMoving = false
    var isPlayerRunning = false
    var isPlayerCrouching = false
    var isRadioActive = false

    private var audioTrack: AudioTrack? = null
    private var audioThread: Thread? = null

    fun start() {
        if (isPlaying.getAndSet(true)) return

        audioThread = thread(name = "UnsavedAudioEngine", isDaemon = true) {
            runAudioLoop()
        }
    }

    fun setMood(mood: String) {
        currentMood = mood
    }

    fun updateVolumes(music: Float, sfx: Float, ambience: Float) {
        musicVolume = music.coerceIn(0f, 1f)
        sfxVolume = sfx.coerceIn(0f, 1f)
        ambienceVolume = ambience.coerceIn(0f, 1f)
    }

    private fun runAudioLoop() {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = if (minBufferSize > 0) (minBufferSize * 2).coerceAtLeast(2048) else 4096

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            track.play()

            val shortBuffer = ShortArray(bufferSize / 2)
            var sampleIndex = 0L
            var rainState = 0f
            var chordTimer = 0L
            var currentChordIndex = 0
            var footstepTimer = 0L

            val mysteryChords = listOf(
                floatArrayOf(110.0f, 164.81f, 220.0f),
                floatArrayOf(98.0f, 146.83f, 196.0f),
                floatArrayOf(87.31f, 130.81f, 174.61f),
                floatArrayOf(103.83f, 155.56f, 207.65f)
            )

            val emotionalChords = listOf(
                floatArrayOf(174.61f, 220.0f, 261.63f, 329.63f),
                floatArrayOf(130.81f, 196.0f, 261.63f, 329.63f),
                floatArrayOf(110.0f, 164.81f, 220.0f, 261.63f),
                floatArrayOf(146.83f, 220.0f, 293.66f, 349.23f)
            )

            val suspenseChords = listOf(
                floatArrayOf(55.0f, 82.41f, 116.54f),
                floatArrayOf(49.0f, 73.42f, 103.83f),
                floatArrayOf(51.91f, 77.78f, 110.0f),
                floatArrayOf(43.65f, 65.41f, 92.50f)
            )

            val endingChords = listOf(
                floatArrayOf(130.81f, 196.0f, 261.63f, 329.63f, 392.0f),
                floatArrayOf(146.83f, 220.0f, 293.66f, 369.99f),
                floatArrayOf(164.81f, 246.94f, 329.63f, 392.0f),
                floatArrayOf(174.61f, 220.0f, 261.63f, 329.63f, 523.25f)
            )

            while (isPlaying.get()) {
                for (i in shortBuffer.indices) {
                    val t = sampleIndex.toDouble() / sampleRate
                    sampleIndex++

                    // 1. Rain & Wind Ambience
                    val whiteNoise = (Random.nextFloat() * 2f - 1f)
                    rainState = (rainState + (0.04f * whiteNoise)) / 1.04f
                    val rainSample = rainState * 0.18f * ambienceVolume

                    // 2. Harmonic Synth Pad
                    val chordDuration = (sampleRate * 4.5).toLong()
                    if (sampleIndex - chordTimer > chordDuration) {
                        chordTimer = sampleIndex
                        currentChordIndex = (currentChordIndex + 1) % 4
                    }

                    val activeChords = when {
                        keeperTensionLevel > 0.4f -> suspenseChords
                        currentMood == "emotional" || currentMood == "romance" -> emotionalChords
                        currentMood == "suspense" || currentMood == "danger" -> suspenseChords
                        currentMood == "ending" -> endingChords
                        else -> mysteryChords
                    }
                    val chord = activeChords[currentChordIndex % activeChords.size]

                    var musicSample = 0.0
                    for (freq in chord) {
                        val phase = 2.0 * PI * freq * t
                        val harmonic = sin(phase) + 0.25 * sin(phase * 2.0) + 0.1 * sin(phase * 3.0)
                        musicSample += harmonic
                    }
                    musicSample = (musicSample / chord.size) * 0.22 * musicVolume

                    // 3. Keeper Tension Heartbeat
                    var tensionSample = 0.0
                    if (keeperTensionLevel > 0.05f) {
                        val pulseRate = 0.9f + (keeperTensionLevel * 1.8f)
                        val pulseEnvelope = (sin(2.0 * PI * pulseRate * t).coerceAtLeast(0.0)).let { it * it * it }
                        val subBassSine = sin(2.0 * PI * 48.0 * t)
                        tensionSample = subBassSine * pulseEnvelope * (0.35f * keeperTensionLevel) * sfxVolume
                    }

                    // 4. Movement Footsteps
                    var footstepSample = 0.0
                    if (isPlayerMoving && !isPlayerCrouching) {
                        val stepInterval = if (isPlayerRunning) (sampleRate * 0.32).toLong() else (sampleRate * 0.52).toLong()
                        if (sampleIndex - footstepTimer > stepInterval) {
                            footstepTimer = sampleIndex
                        }
                        val stepPhase = ((sampleIndex - footstepTimer).toFloat() / (sampleRate * 0.12f)).coerceIn(0f, 1f)
                        if (stepPhase < 0.9f) {
                            val stepEnv = (1f - stepPhase) * (1f - stepPhase)
                            val thud = sin(2.0 * PI * 85.0 * t) * stepEnv
                            val crunch = (Random.nextFloat() * 2f - 1f) * 0.15f * stepEnv
                            footstepSample = (thud * 0.4 + crunch) * (if (isPlayerRunning) 0.35 else 0.18) * sfxVolume
                        }
                    }

                    // 5. Radio Static SFX
                    var radioSample = 0.0
                    if (isRadioActive) {
                        val hiss = (Random.nextFloat() * 2f - 1f) * 0.12
                        val whine = sin(2.0 * PI * 880.0 * t) * 0.04
                        radioSample = (hiss + whine) * sfxVolume
                    }

                    val mixed = (rainSample + musicSample + tensionSample + footstepSample + radioSample).coerceIn(-1.0, 1.0)
                    shortBuffer[i] = (mixed * 32767.0).toInt().toShort()
                }

                if (isPlaying.get() && track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    track.write(shortBuffer, 0, shortBuffer.size)
                }
            }
        } catch (e: Throwable) {
            // Safe fallback if audio hardware is absent
        } finally {
            try {
                audioTrack?.stop()
                audioTrack?.release()
            } catch (e: Throwable) {
                // Ignore
            }
            audioTrack = null
        }
    }

    fun stop() {
        isPlaying.set(false)
        audioThread?.interrupt()
        audioThread = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Throwable) {
            // Ignore
        }
        audioTrack = null
    }
}
