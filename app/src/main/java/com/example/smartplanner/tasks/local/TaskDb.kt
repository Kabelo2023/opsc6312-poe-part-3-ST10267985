package com.example.smartplanner.tasks.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDb : RoomDatabase() {
    abstract fun dao(): TaskDao

    companion object {
        @Volatile private var instance: TaskDb? = null
        fun get(context: Context): TaskDb =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskDb::class.java, "tasks.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
    }
}
