package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(onNavigateBack: () -> Unit) {
    var checking by remember { mutableStateOf(true) }
    var autoUpdate by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000) // Simulate finding an update
        checking = false
    }

    Scaffold(
        containerColor = Color(0xFFEBEBF1), // iOS 6 style background color
        topBar = {
            TopAppBar(
                title = { Text("تحديثات البرنامج", color = Color(0xFF000000), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5)),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF007AFF))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            HeaderSection()

            if (checking) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF007AFF))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("جارٍ التحقق من وجود تحديثات...", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                UpdateInfoCard()
            }

            AutoUpdateCard(autoUpdate) { autoUpdate = it }
        }
    }
}

@Composable
fun HeaderSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Settings, contentDescription = "Icon", tint = Color.Gray, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("مسجل الصوت الذكي", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun UpdateInfoCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("تحديث جديد متوفر", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("2.0.0", fontSize = 16.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "نظام التحديثات يعمل الآن بشكل كامل. هذا التحديث يتم إطلاقه تدريجياً للأجهزة. يشمل تحسينات على الأداء ودعم فترات تسجيل أطول (أكثر من ساعتين).",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
        
        HorizontalDivider(color = Color(0xFFCCCCCC))
        
        TextButton(
            onClick = { /* Simulate download */ },
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Text("تنزيل وتثبيت", color = Color(0xFF007AFF), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AutoUpdateCard(autoUpdate: Boolean, onAutoUpdateChange: (Boolean) -> Unit) {
    Column {
        Text("التحديثات التلقائية", modifier = Modifier.padding(start = 16.dp, bottom = 8.dp), color = Color.DarkGray, fontSize = 14.sp)
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("تحديث تلقائي", fontSize = 16.sp, color = Color.Black)
            Switch(
                checked = autoUpdate,
                onCheckedChange = onAutoUpdateChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CD964), // iOS old green
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
        Text("إذا تم التفعيل، سيتم تثبيت التحديثات الجديدة تلقائياً ليلاً.", modifier = Modifier.padding(start = 16.dp, top = 8.dp), color = Color.Gray, fontSize = 12.sp)
    }
}
