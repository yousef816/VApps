package com.example.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AudioVisualizer
import com.example.viewmodel.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RecordScreen(
    viewModel: MainViewModel,
    onNavigateToList: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val isRecording by viewModel.isRecording.collectAsState()
    val amplitude by viewModel.amplitudeFlow.collectAsState(initial = 0)
    
    val recordPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    var selectedFormat by remember { mutableStateOf("M4A") }

    var secondsElapsed by remember { mutableStateOf(0) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            secondsElapsed = 0
            while (true) {
                delay(1000)
                secondsElapsed++
            }
        }
    }

    val formatTime = { totalSeconds: Int ->
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        String.format("%02d:%02d:%02d", h, m, s)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0A0A)) // bg-[#0F0A0A]
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape)
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
            }
            
            Text("مسجل الصوت الذكي", color = Color.White.copy(alpha = 0.9f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            IconButton(
                onClick = { /* Profile */ },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape)
            ) {
                Icon(Icons.Outlined.Person, contentDescription = "Profile", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
            }
        }

        // Main Recording Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            // Background Glow
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFDC2626).copy(alpha = 0.15f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Waveform Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecording) {
                        AudioVisualizer(amplitude = amplitude, color = Color(0xFFEF4444))
                    } else {
                        // Static fake waveform when not recording to match design
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.height(120.dp)
                        ) {
                            val heights = listOf(48.dp, 80.dp, 112.dp, 64.dp, 96.dp, 120.dp, 80.dp, 96.dp, 112.dp, 56.dp)
                            val alphas = listOf(0.4f, 0.6f, 1f, 0.7f, 1f, 1f, 0.8f, 1f, 0.6f, 0.4f)
                            heights.forEachIndexed { index, h ->
                                Box(
                                    modifier = Modifier
                                        .width(6.dp)
                                        .height(h)
                                        .background(
                                            color = Color(0xFFEF4444).copy(alpha = alphas[index]),
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Timer & Info
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseAlpha"
                )

                if (isRecording) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFFEF4444).copy(alpha = pulseAlpha), CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جارٍ التسجيل...", color = Color(0xFFEF4444).copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Box(modifier = Modifier.size(8.dp).background(Color.White.copy(alpha = 0.3f), CircleShape))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جاهز للتسجيل", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Text(
                    text = formatTime(secondsElapsed),
                    color = Color.White,
                    fontSize = 56.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "48KHZ • 16-BIT • ${selectedFormat}",
                    color = Color.White.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        // Quick Actions & Settings Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // AI Toggle Card
            var aiEnabled by remember { mutableStateOf(true) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
                    .clickable { aiEnabled = !aiEnabled },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Outlined.AutoFixHigh, contentDescription = "AI Cleanup", tint = Color(0xFFF87171))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("تنقية الصوت", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("تقنية AI النشطة", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                    }
                }
                Switch(
                    checked = aiEnabled,
                    onCheckedChange = { aiEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFFDC2626),
                        uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.scale(0.8f)
                )
            }

            // Format Picker
            val formats = listOf("M4A", "AMR_WB")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                formats.forEach { format ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (selectedFormat == format) Color(0xFFDC2626) else Color.White.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (selectedFormat == format) Color.Transparent else Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedFormat = format
                                viewModel.setRecordingFormat(selectedFormat, true)
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = format,
                            color = if (selectedFormat == format) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = if (selectedFormat == format) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Main Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { 
                    if (isRecording) {
                        viewModel.cancelRecording()
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = if (isRecording) 0.15f else 0.05f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.White)
            }

            Box(contentAlignment = Alignment.Center) {
                // Huge Red Button Glow
                if (isRecording) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFDC2626).copy(alpha = 0.3f), Color.Transparent)
                                ),
                                shape = CircleShape
                            )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFDC2626), CircleShape)
                        .border(4.dp, Color.Black.copy(alpha = 0.2f), CircleShape)
                        .clickable {
                            if (recordPermissionState.status.isGranted) {
                                if (isRecording) {
                                    viewModel.stopRecording()
                                } else {
                                    viewModel.startRecording()
                                }
                            } else {
                                recordPermissionState.launchPermissionRequest()
                                if (!recordPermissionState.status.isGranted) {
                                    Toast.makeText(context, "الرجاء منح صلاحية المايكروفون", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isRecording) {
                        Box(modifier = Modifier.size(32.dp).background(Color.White, RoundedCornerShape(4.dp)))
                    } else {
                        // Empty for pure red record button
                    }
                }
            }

            IconButton(
                onClick = { 
                    if (isRecording) {
                        Toast.makeText(context, "تم إضافة علامة بنجاح", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White.copy(alpha = if (isRecording) 0.15f else 0.05f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(if (isRecording) Icons.Outlined.Check else Icons.Outlined.Share, contentDescription = "Action", tint = Color.White)
            }
        }
    }
}
