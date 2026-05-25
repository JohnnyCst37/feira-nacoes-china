package com.example.ui

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object SoundSynthesizer {

    private const val SAMPLE_RATE = 22050

    suspend fun playPipaSound() = withContext(Dispatchers.Default) {
        try {
            // Gusty, crisp plucked notes with beautiful decay
            val baseFreqs = doubleArrayOf(440.0, 554.37, 659.25, 880.0) // A Major Chord
            val durationMs = 300
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0) * baseFreqs.size).toInt()
            val samples = ShortArray(numSamples)

            var sampleIdx = 0
            for (freq in baseFreqs) {
                val pluckDuration = 0.08
                val singlePluckSamples = (SAMPLE_RATE * pluckDuration).toInt()
                
                for (i in 0 until singlePluckSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val decay = kotlin.math.exp(-i.toDouble() / (singlePluckSamples * 0.4))
                    val wave = sin(2 * Math.PI * freq * t) + 0.3 * sin(4 * Math.PI * freq * t)
                    val sampleVal = (wave * 32767.0 * 0.4 * decay).toInt().coerceIn(-32768, 32767)
                    if (sampleIdx < samples.size) {
                        samples[sampleIdx++] = sampleVal.toShort()
                    }
                }
            }

            playBuffer(samples)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun playErhuSound() = withContext(Dispatchers.Default) {
        try {
            // Squeaky, bow-drawn wood vibrato string
            val freq = 293.66 // D4 Pitch
            val durationMs = 1200
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)

            val vibratoFreq = 6.0
            val vibratoDepth = 5.0 // Hz deviation

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                // Vibrato LFO effect
                val modFreq = freq + vibratoDepth * sin(2 * Math.PI * vibratoFreq * t)
                val wave = sin(2 * Math.PI * modFreq * t) + 0.2 * sin(4 * Math.PI * modFreq * t)
                
                // Slow Attack phase, long sustain, expressiveness
                val attackDuration = SAMPLE_RATE * 0.2
                val decayBegin = SAMPLE_RATE * 0.9
                val volumeEnvelope = when {
                    i < attackDuration -> i.toDouble() / attackDuration
                    i > decayBegin -> 1.0 - (i - decayBegin).toDouble() / (numSamples - decayBegin)
                    else -> 1.0
                }

                val sampleVal = (wave * 32767.0 * 0.35 * volumeEnvelope).toInt().coerceIn(-32768, 32767)
                samples[i] = sampleVal.toShort()
            }

            playBuffer(samples)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun playGuzhengMelody() = withContext(Dispatchers.Default) {
        try {
            val freqs = doubleArrayOf(261.63, 293.66, 329.63, 392.00, 440.00, 523.25) // Pentatonic scale: C, D, E, G, A, C
            val pluckDuration = 0.16
            val singlePluckSamples = (SAMPLE_RATE * pluckDuration).toInt()
            val numSamples = singlePluckSamples * freqs.size
            val samples = ShortArray(numSamples)

            var sampleIdx = 0
            for (freq in freqs) {
                for (i in 0 until singlePluckSamples) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val decay = kotlin.math.exp(-i.toDouble() / (singlePluckSamples * 0.45))
                    // Harpsichord/Guzheng resonance
                    val wave = sin(2 * Math.PI * freq * t) + 
                               0.4 * sin(4 * Math.PI * freq * t) + 
                               0.25 * sin(6 * Math.PI * freq * t)
                    val sampleVal = (wave * 32767.0 * 0.32 * decay).toInt().coerceIn(-32768, 32767)
                    if (sampleIdx < samples.size) {
                        samples[sampleIdx++] = sampleVal.toShort()
                    }
                }
            }
            playBuffer(samples)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun playTeaPourSound() = withContext(Dispatchers.Default) {
        try {
            val durationMs = 800
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                // Tea filling container frequency raise
                val freq = 550.0 + 250.0 * (i.toDouble() / numSamples) + 40.0 * sin(2 * Math.PI * 18.0 * t)
                val wave = sin(2 * Math.PI * freq * t)
                val popEnvelope = sin(2 * Math.PI * 9.0 * t).coerceIn(0.0, 1.0)
                val sampleVal = (wave * 32767.0 * 0.12 * popEnvelope).toInt().coerceIn(-32768, 32767)
                samples[i] = sampleVal.toShort()
            }
            playBuffer(samples)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun playPastelCrunchSound() = withContext(Dispatchers.Default) {
        try {
            val durationMs = 280
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)

            val random = java.util.Random()
            for (i in 0 until numSamples) {
                val decay = kotlin.math.exp(-i.toDouble() / (numSamples * 0.22))
                val noise = random.nextGaussian()
                val sampleVal = (noise * 32767.0 * 0.14 * decay).toInt().coerceIn(-32768, 32767)
                samples[i] = sampleVal.toShort()
            }
            playBuffer(samples)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun playChineseGreetingSound() = withContext(Dispatchers.Default) {
        try {
            val freq = 523.25
            val durationMs = 950
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val decay = kotlin.math.exp(-i.toDouble() / (numSamples * 0.28))
                val wave = sin(2 * Math.PI * freq * t) + 0.45 * sin(2 * Math.PI * (freq * 1.5) * t)
                val sampleVal = (wave * 32767.0 * 0.24 * decay).toInt().coerceIn(-32768, 32767)
                samples[i] = sampleVal.toShort()
            }
            playBuffer(samples)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playBuffer(buffer: ShortArray) {
        val minBufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(minBufferSize, buffer.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        
        // Let it run and then release resources safely
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 1500)
    }
}
