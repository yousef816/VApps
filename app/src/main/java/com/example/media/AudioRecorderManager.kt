package com.example.media

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.NoiseSuppressor
import android.os.Build
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.io.File

class AudioRecorderManager(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private var noiseSuppressor: NoiseSuppressor? = null
    private var acousticEchoCanceler: AcousticEchoCanceler? = null

    @SuppressLint("MissingPermission")
    fun startRecording(fileName: String, format: String = "M4A", highQuality: Boolean = true): File? {
        val extension = if (format.uppercase() == "AMR") ".amr" else ".m4a"
        outputFile = File(context.filesDir, "$fileName$extension")
        
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val attrContext = context.createAttributionContext("VoiceRecorder")
            MediaRecorder(attrContext)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            
            if (format.uppercase() == "AMR") {
                setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            } else {
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                if (highQuality) {
                    setAudioSamplingRate(44100)
                    setAudioEncodingBitRate(128000)
                }
            }
            
            setOutputFile(outputFile!!.absolutePath)
            
            try {
                prepare()
                start()
                _isRecording.value = true
                
                // Attempt to apply hardware noise suppression / echo cancellation (تنقية الصوت)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val audioSessionId = getAudioSessionId(this)
                    if (audioSessionId > 0) {
                        if (NoiseSuppressor.isAvailable()) {
                            noiseSuppressor = NoiseSuppressor.create(audioSessionId)
                            noiseSuppressor?.enabled = true
                        }
                        if (AcousticEchoCanceler.isAvailable()) {
                            acousticEchoCanceler = AcousticEchoCanceler.create(audioSessionId)
                            acousticEchoCanceler?.enabled = true
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AudioRecorderManager", "prepare() failed", e)
                release()
                return null
            }
        }
        return outputFile
    }

    private fun getAudioSessionId(recorder: MediaRecorder): Int {
        return try {
            val getAudioSessionIdMethod = MediaRecorder::class.java.getMethod("getAudioSessionId")
            getAudioSessionIdMethod.invoke(recorder) as Int
        } catch (e: Exception) {
            -1
        }
    }

    fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
            noiseSuppressor?.release()
            acousticEchoCanceler?.release()
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "stop() failed", e)
        } finally {
            recorder = null
            noiseSuppressor = null
            acousticEchoCanceler = null
            _isRecording.value = false
        }
    }

    fun cancelRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
            noiseSuppressor?.release()
            acousticEchoCanceler?.release()
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "cancel() failed", e)
        } finally {
            recorder = null
            noiseSuppressor = null
            acousticEchoCanceler = null
            _isRecording.value = false
            outputFile?.let {
                if (it.exists()) it.delete()
            }
            outputFile = null
        }
    }

    fun getAmplitudeFlow(): Flow<Int> = flow {
        while (_isRecording.value) {
            val maxAmp = recorder?.maxAmplitude ?: 0
            emit(maxAmp)
            delay(50) // Update every 50ms for smooth visualizer
        }
    }
}
