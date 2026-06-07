package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AudioVisualizer
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(viewModel: MainViewModel) {
    val recordings by viewModel.allRecordings.collectAsState()
    var selectedRecording by remember { mutableStateOf<com.example.data.RecordingEntity?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val isPlaying by viewModel.isPlaying.collectAsState()
    val amplitude by viewModel.playerAmplitude.collectAsState(initial = 0)

    var speed by remember { mutableStateOf(1f) }
    var pitch by remember { mutableStateOf(1f) }
    var aiEnabled by remember { mutableStateOf(true) }
    var volumeBoost by remember { mutableStateOf(false) }

    // Automatically select the first recording if available
    LaunchedEffect(recordings) {
        if (selectedRecording == null && recordings.isNotEmpty()) {
            selectedRecording = recordings.first()
        }
    }

    LaunchedEffect(speed) {
        if (isPlaying && selectedRecording != null) {
             viewModel.setPlaybackSpeed(speed)
        }
    }

    Scaffold(
        containerColor = Color(0xFF0F0A0A),
        topBar = {
            TopAppBar(
                title = { Text("المحرر الاحترافي", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                actions = {
                    IconButton(onClick = { /* Export */ }) {
                        Icon(Icons.Outlined.SaveAlt, contentDescription = "Export", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        if (recordings.isEmpty()) {
             Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("لا توجد تسجيلات لتحريرها", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.5f))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Selector
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedRecording?.name ?: "اختر التسجيل",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("مساحة العمل", color = Color.White.copy(alpha = 0.5f)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFFEF4444),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedLabelColor = Color(0xFFEF4444),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF1E1E1E))
                    ) {
                        recordings.forEach { rec ->
                            DropdownMenuItem(
                                text = { Text(rec.name, color = Color.White) },
                                onClick = {
                                    selectedRecording = rec
                                    expanded = false
                                    viewModel.stopPlayback()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Waveform Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color.White.copy(alpha = 0.02f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPlaying) {
                        AudioVisualizer(amplitude = amplitude, color = Color(0xFFEF4444))
                    } else {
                        // Static representation when paused
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            val heights = listOf(20.dp, 40.dp, 60.dp, 30.dp, 50.dp, 80.dp, 45.dp, 60.dp, 75.dp, 30.dp, 20.dp)
                            heights.forEachIndexed { index, h ->
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(h)
                                        .background(Color(0xFFEF4444).copy(alpha = 0.3f), CircleShape)
                                )
                            }
                        }
                    }
                    
                    // Center Line Indicator
                    Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color.White.copy(alpha = 0.5f)))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Playback controls for editor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.seekTo(0) }, modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Outlined.Replay, contentDescription = "Restart", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(28.dp))
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                                ),
                                shape = CircleShape
                            )
                            .border(2.dp, Color.Black.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                selectedRecording?.let {
                                    if (isPlaying) {
                                        viewModel.pausePlayback()
                                    } else {
                                        viewModel.playRecording(it.path, speed)
                                    }
                                }
                            },
                            modifier = Modifier.size(64.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { /* Implement trim */ },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.Outlined.ContentCut, contentDescription = "Cut", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(28.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Modifiers
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("سرعة التشغيل", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Slider(
                            value = speed,
                            onValueChange = { speed = it },
                            valueRange = 0.5f..2.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFEF4444),
                                activeTrackColor = Color(0xFFEF4444),
                                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        widthSpacer(16)
                        Text("${String.format("%.1f", speed)}x", color = Color.White, fontSize = 14.sp, modifier = Modifier.width(40.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("تعديل الطبقة الصوتية", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Slider(
                            value = pitch,
                            onValueChange = { pitch = it },
                            valueRange = -5f..5f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White.copy(alpha = 0.5f),
                                inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        widthSpacer(16)
                        Text("${if(pitch > 0) "+" else ""}${pitch.toInt()}st", color = Color.White, fontSize = 14.sp, modifier = Modifier.width(40.dp))
                    }
                }

                // AI Enhancement Tool
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFEF4444).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Outlined.AutoFixHigh, contentDescription = "AI", tint = Color(0xFFF87171))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("الاستوديو الذكي", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text("رفع الجودة وإزالة الصدى بالذكاء الاصطناعي", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                    }
                    Switch(
                        checked = aiEnabled,
                        onCheckedChange = { aiEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFEF4444),
                            uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun widthSpacer(w: Int) {
    Spacer(modifier = Modifier.width(w.dp))
}
