package com.example.smartplanner.repo

import android.content.Context
import com.example.smartplanner.data.model.TaskRemote
import com.example.smartplanner.data.remote.ApiService
import com.example.smartplanner.tasks.local.TaskDb
import com.example.smartplanner.tasks.local.TaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(
    private val context: Context,
    private val api: ApiService
) {
    private val dao = TaskDb.get(context).dao()

    private fun TaskRemote.toEntity() = TaskEntity(
        remoteId = id, title = title, tag = tag, done = done,
        createdBy = createdBy, synced = true
    )

    private fun entityToRemote(e: TaskEntity) = TaskRemote(
        id = e.remoteId, title = e.title, tag = e.tag, done = e.done, createdBy = e.createdBy
    )

    /** Returns freshest available tasks; attempts network, falls back to local. */
    suspend fun getMyTasks(uid: String): List<TaskRemote> = withContext(Dispatchers.IO) {
        runCatching {
            val remote = api.getTasks(uid)
            dao.deleteAllFor(uid)
            dao.insertAll(remote.map { it.toEntity() })
            remote
        }.getOrElse {
            dao.getAll(uid).map { entityToRemote(it) }
        }
    }

    /** Creates remotely; if offline, stores a local unsynced copy. */
    suspend fun create(uid: String, title: String, tag: String): TaskRemote =
        withContext(Dispatchers.IO) {
            runCatching {
                val created = api.createTask(TaskRemote(id = null, title = title, tag = tag, done = false, createdBy = uid))
                dao.insert(created.toEntity())
                created
            }.getOrElse {
                val local = TaskEntity(
                    remoteId = null, title = title, tag = tag, done = false,
                    createdBy = uid, synced = false
                )
                dao.insert(local)
                entityToRemote(local)
            }
        }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        runCatching { api.deleteTask(id) }
        dao.deleteByRemote(id)
    }
}
