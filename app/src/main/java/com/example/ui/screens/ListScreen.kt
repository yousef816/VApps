package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.RecordingEntity
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.Info

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingsListScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPlayer: (String) -> Unit
) {
    val recordings by viewModel.allRecordings.collectAsState()

    Scaffold(
        containerColor = Color(0xFF0F0A0A),
        topBar = {
            TopAppBar(
                title = { Text("المكتبة", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (recordings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("لا توجد تسجيلات", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(recordings, key = { it.id }) { recording ->
                    RecordingItem(
                        recording = recording,
                        onPlay = { onNavigateToPlayer(recording.path) },
                        onShare = { viewModel.shareRecording(recording) },
                        onDelete = { viewModel.deleteRecording(recording) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecordingItem(
    recording: RecordingEntity,
    onPlay: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White.copy(alpha = 0.1f), ShapeDefaults.Medium),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(recording.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val date = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).format(Date(recording.timestamp))
                    Text(date, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${recording.durationMs / 1000}s", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(recording.format, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                }
            }
            IconButton(onClick = onPlay) {
                Icon(Icons.Outlined.PlayArrow, contentDescription = "Play", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onShare) {
                Icon(Icons.Outlined.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.secondary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
