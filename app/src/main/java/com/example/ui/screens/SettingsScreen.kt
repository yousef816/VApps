package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.sp
import com.example.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToUpdates: () -> Unit
) {
    var selectedFormat by remember { mutableStateOf("M4A") }
    var highQuality by remember { mutableStateOf(true) }
    var noiseReduction by remember { mutableStateOf(true) }
    var stereo by remember { mutableStateOf(false) }
    var backgroundRecording by remember { mutableStateOf(true) }
    var hardwareAcceleration by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = Color(0xFF0F0A0A),
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.padding(start = 8.dp).background(Color.White.copy(alpha = 0.05f), androidx.compose.foundation.shape.CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFFEF4444))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("جودة التسجيل وتنسيقه", style = MaterialTheme.typography.titleMedium, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("التنسيق", color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedFormat == "M4A",
                        onClick = { 
                            selectedFormat = "M4A"
                            viewModel.setRecordingFormat(selectedFormat, highQuality)
                        },
                        label = { Text("M4A", color = Color.White) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFEF4444))
                    )
                    FilterChip(
                        selected = selectedFormat == "AMR_WB",
                        onClick = { 
                            selectedFormat = "AMR_WB"
                            viewModel.setRecordingFormat(selectedFormat, highQuality)
                        },
                        label = { Text("AMR-WB", color = Color.White) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFEF4444))
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                SettingSwitchRow(
                    title = "جودة عالية (128kbps)",
                    subtitle = "تسجيل الملف بأعلى جودة ممكنة",
                    checked = highQuality,
                    onCheckedChange = { 
                        highQuality = it
                        viewModel.setRecordingFormat(selectedFormat, highQuality)
                    }
                )
                SettingSwitchRow(
                    title = "إلغاء الضوضاء بالذكاء الاصطناعي",
                    subtitle = "إزالة الضوضاء المحيطة أثناء التسجيل",
                    checked = noiseReduction,
                    onCheckedChange = { noiseReduction = it }
                )
            }

            item {
                Text("الصوت والميكروفون", style = MaterialTheme.typography.titleMedium, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                SettingSwitchRow(
                    title = "تسجيل ستيريو",
                    subtitle = "استخدام ميكروفونين للصوت المحيطي (إن وجد)",
                    checked = stereo,
                    onCheckedChange = { stereo = it }
                )
                SettingSwitchRow(
                    title = "التسجيل في الخلفية",
                    subtitle = "متابعة التسجيل عند إغلاق الشاشة",
                    checked = backgroundRecording,
                    onCheckedChange = { backgroundRecording = it }
                )
            }
            
            item {
                Text("ميزات متقدمة", style = MaterialTheme.typography.titleMedium, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                SettingSwitchRow(
                    title = "تسريع الأجهزة",
                    subtitle = "استخدام كرت الصوت لتخفيف العبء عن المعالج",
                    checked = hardwareAcceleration,
                    onCheckedChange = { hardwareAcceleration = it }
                )
            }

            item {
                Divider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("حول التطبيق", style = MaterialTheme.typography.titleMedium, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToUpdates() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("لمحة عن المسجل", color = Color.White, fontSize = 16.sp)
                            Text("الإصدار الحالي: 1.0.4", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                        Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Color.White.copy(alpha = 0.5f))
                    }
                }
                
                Column(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToUpdates() }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("التحقق من وجود تحديثات", color = Color.White, fontSize = 16.sp)
                            Text("البحث عن تحديثات النظام والتطبيق", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                        }
                        Icon(Icons.Outlined.CloudDownload, contentDescription = "Update", tint = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}

@Composable
fun SettingSwitchRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 16.sp)
            Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFEF4444),
                uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}
