package com.example.media

import android.media.MediaPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class AudioPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition.asStateFlow()

    fun setSpeed(speed: Float) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        val params = player.playbackParams
                        params.speed = speed
                        player.playbackParams = params
                    } else {
                        // Apply immediately if not playing is not possible directly without start unless prepared
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun play(path: String, speed: Float = 1.0f, onCompletion: () -> Unit) {
        if (_isPlaying.value && mediaPlayer != null) {
            mediaPlayer?.pause()
            _isPlaying.value = false
            return
        } else if (mediaPlayer != null) {
             mediaPlayer?.start()
             setSpeed(speed)
             _isPlaying.value = true
             return
        }

        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(path)
                prepare()
                start()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    val params = playbackParams
                    params.speed = speed
                    playbackParams = params
                }
                _isPlaying.value = true
                
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentPosition.value = it.duration
                    onCompletion()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun pause() {
        mediaPlayer?.pause()
        _isPlaying.value = false
    }

    fun seekTo(position: Int) {
         mediaPlayer?.seekTo(position)
         _currentPosition.value = position
    }

    fun stop() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null
        _isPlaying.value = false
        _currentPosition.value = 0
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    /** Emits player progress smoothly */
    fun getProgressFlow(): Flow<Int> = flow {
        while (true) {
            if (_isPlaying.value) {
                val pos = mediaPlayer?.currentPosition ?: 0
                _currentPosition.value = pos
                emit(pos)
            }
            delay(100)
        }
    }

    fun getPlayerAmplitudeFlow(): Flow<Int> = flow {
        while (true) {
            if (_isPlaying.value) {
                emit((1000..30000).random())
            } else {
                emit(0)
            }
            delay(100)
        }
    }
}
