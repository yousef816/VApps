package com.example.viewmodel

import android.app.Application
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RecordingEntity
import com.example.data.RecordingRepository
import com.example.media.AudioPlayerManager
import com.example.media.AudioRecorderManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
    application: Application,
    private val repository: RecordingRepository,
    private val recorderManager: AudioRecorderManager,
    private val playerManager: AudioPlayerManager
) : AndroidViewModel(application) {

    val allRecordings: StateFlow<List<RecordingEntity>> = repository.allRecordings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val isRecording = recorderManager.isRecording
    val amplitudeFlow = recorderManager.getAmplitudeFlow()

    val isPlaying = playerManager.isPlaying
    val playerProgress = playerManager.getProgressFlow()
    val playingPosition = playerManager.currentPosition
    val playerAmplitude = playerManager.getPlayerAmplitudeFlow()

    private var currentRecordStart: Long = 0
    private var currentFormat = "M4A" // Default
    private var isHighQuality = true

    fun setRecordingFormat(format: String, highQuality: Boolean) {
        currentFormat = format
        isHighQuality = highQuality
    }

    fun startRecording() {
        val fileName = "REC_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}"
        recorderManager.startRecording(fileName, currentFormat, isHighQuality)
        currentRecordStart = System.currentTimeMillis()
    }

    fun stopRecording() {
        recorderManager.stopRecording()
        val durationMs = System.currentTimeMillis() - currentRecordStart
        val fileName = "REC_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date(currentRecordStart))}"
        val extension = if (currentFormat == "AMR") ".amr" else ".m4a"
        val path = File(getApplication<Application>().filesDir, "$fileName$extension").absolutePath

        viewModelScope.launch {
            repository.insert(
                RecordingEntity(
                    name = fileName,
                    path = path,
                    durationMs = durationMs,
                    format = currentFormat,
                    bitRate = if (isHighQuality) 128000 else 64000
                )
            )
        }
    }

    fun cancelRecording() {
        recorderManager.cancelRecording()
    }

    fun playRecording(path: String, speed: Float = 1.0f) {
        playerManager.play(path, speed) {
            // on completion
        }
    }

    fun setPlaybackSpeed(speed: Float) {
        playerManager.setSpeed(speed)
    }
    
    fun pausePlayback() {
        playerManager.pause()
    }
    
    fun seekTo(position: Int) {
        playerManager.seekTo(position)
    }

    fun stopPlayback() {
        playerManager.stop()
    }

    fun deleteRecording(recording: RecordingEntity) {
        viewModelScope.launch {
            repository.deleteById(recording.id)
            val file = File(recording.path)
            if (file.exists()) {
                file.delete()
            }
        }
    }

    fun shareRecording(recording: RecordingEntity) {
        val file = File(recording.path)
        if (!file.exists()) return

        val uri = FileProvider.getUriForFile(
            getApplication(),
            "${getApplication<Application>().packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share Recording")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(chooser)
    }
}
