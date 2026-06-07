package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AudioVisualizer(
    modifier: Modifier = Modifier,
    amplitude: Int,
    color: Color = MaterialTheme.colorScheme.primary,
    maxAmplitude: Int = 32767 // Max value for 16-bit PCM (approximate)
) {
    // Keep a history of amplitudes
    val historyRef = remember { mutableListOf<Float>() }
    
    // Normalize and animate
    val normalizedAmp = (amplitude.toFloat() / maxAmplitude.toFloat()).coerceIn(0f, 1f)
    val animatedAmp by animateFloatAsState(targetValue = normalizedAmp, animationSpec = tween(50), label = "visualizer")

    if (historyRef.size > 50) {
        historyRef.removeAt(0)
    }
    historyRef.add(animatedAmp)

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        val barWidth = 10.dp.toPx()
        val spacing = 4.dp.toPx()
        val totalBars = (canvasWidth / (barWidth + spacing)).toInt().coerceIn(1, 50)
        
        val displayHistory = historyRef.takeLast(totalBars)
        
        val startX = (canvasWidth - (displayHistory.size * (barWidth + spacing))) / 2f

        displayHistory.forEachIndexed { index, amp ->
            val barHeight = (amp * canvasHeight).coerceAtLeast(10.dp.toPx())
            val xOffset = startX + index * (barWidth + spacing)
            val yOffset = (canvasHeight - barHeight) / 2f
            
            drawRoundRect(
                color = color.copy(alpha = 0.8f + (amp * 0.2f)),
                topLeft = Offset(xOffset, yOffset),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2)
            )
        }
    }
}
