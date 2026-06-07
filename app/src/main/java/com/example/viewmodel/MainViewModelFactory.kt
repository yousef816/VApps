package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.RecordingRepository
import com.example.media.AudioPlayerManager
import com.example.media.AudioRecorderManager

class MainViewModelFactory(
    private val application: Application,
    private val repository: RecordingRepository,
    private val recorderManager: AudioRecorderManager,
    private val playerManager: AudioPlayerManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application, repository, recorderManager, playerManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
