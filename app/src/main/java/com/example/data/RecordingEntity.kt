package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recordings")
data class RecordingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val path: String,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val format: String = "M4A",
    val bitRate: Int = 128000
)
