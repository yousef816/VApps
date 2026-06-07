package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AudioVisualizer
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: MainViewModel,
    path: String,
    onNavigateBack: () -> Unit
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.playerProgress.collectAsState(initial = 0)
    val amplitude by viewModel.playerAmplitude.collectAsState(initial = 0)
    val duration = 100000 // Just fallback, realistically we get from player

    LaunchedEffect(path) {
        viewModel.playRecording(path)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopPlayback()
        }
    }

    Scaffold(
        containerColor = Color(0xFF0F0A0A),
        topBar = {
            TopAppBar(
                title = { Text("المشغل الذكي", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.padding(start = 8.dp).background(Color.White.copy(alpha = 0.05f), androidx.compose.foundation.shape.CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFFEF4444))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    AudioVisualizer(amplitude = amplitude, color = Color(0xFFEF4444))
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (i in 0..15) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(if (i % 2 == 0) 16.dp else 32.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            )
                        }
                    }
                }
            }
            
            Slider(
                value = progress.toFloat(),
                onValueChange = { viewModel.seekTo(it.toInt()) },
                valueRange = 0f..50000f, // Need actual duration
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFEF4444),
                    activeTrackColor = Color(0xFFEF4444),
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier.padding(vertical = 32.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.seekTo(0) }, modifier = Modifier.size(64.dp)) {
                    Icon(Icons.Outlined.Replay, contentDescription = "Restart", tint = Color.White, modifier = Modifier.size(32.dp))
                }
                
                FloatingActionButton(
                    onClick = {
                        if (isPlaying) viewModel.pausePlayback() else viewModel.playRecording(path)
                    },
                    modifier = Modifier.size(80.dp),
                    containerColor = Color(0xFFEF4444),
                    contentColor = Color.White
                ) {
                    Icon(
                        if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
