package com.example.smartplanner.repo

import com.example.smartplanner.data.model.TaskRemote
import com.example.smartplanner.data.remote.ApiService

class TaskRepository(private val api: ApiService) {
    suspend fun getMyTasks(uid: String): List<TaskRemote> = api.getTasks(uid)
    suspend fun create(uid: String, title: String, tag: String): TaskRemote =
        api.createTask(TaskRemote(title = title, tag = tag, done = false, createdBy = uid))
    suspend fun delete(id: String) = api.deleteTask(id)
}
