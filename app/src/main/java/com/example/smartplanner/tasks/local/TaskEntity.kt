package com.example.smartplanner.tasks.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks_local")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val remoteId: String?,            // null while unsynced/offline
    val title: String,
    val tag: String,
    val done: Boolean,
    val createdBy: String,
    val synced: Boolean               // true if mirrored to server
)
