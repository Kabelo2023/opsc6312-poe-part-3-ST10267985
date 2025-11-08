package com.example.smartplanner.feature

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import androidx.core.app.NotificationManagerCompat

class ReminderWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "Task due"
        val tag = inputData.getString("tag") ?: ""
        val note = Notifier.build(applicationContext, title, tag)
        NotificationManagerCompat.from(applicationContext).notify(title.hashCode(), note)
        return Result.success()
    }

    companion object {
        fun data(title: String, tag: String) = workDataOf("title" to title, "tag" to tag)
    }
}
