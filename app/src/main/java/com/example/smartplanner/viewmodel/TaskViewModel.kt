package com.example.smartplanner.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.smartplanner.data.model.TaskRemote
import com.example.smartplanner.data.remote.ApiClient
import com.example.smartplanner.repo.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {

    private val auth = FirebaseAuth.getInstance()
    private val repo = TaskRepository(app.applicationContext, ApiClient.create())

    private val _tasks = MutableLiveData<List<TaskRemote>>(emptyList())
    val tasks: LiveData<List<TaskRemote>> = _tasks

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun load() {
        val uid = auth.currentUser?.uid
        if (uid == null) { _message.value = "Not logged in"; return }
        viewModelScope.launch {
            val list = repo.getMyTasks(uid)
            _tasks.value = list
            _message.value = "Loaded ${list.size} task(s)"
        }
    }

    fun add(title: String, tag: String, onLocalFallback: (TaskRemote) -> Unit = {}) {
        val uid = auth.currentUser?.uid
        if (uid == null) { _message.value = "Not logged in"; return }
        viewModelScope.launch {
            val created = repo.create(uid, title, tag)
            _tasks.value = (_tasks.value ?: emptyList()) + created
            if (created.id == null) {
                _message.value = "Task saved offline (will sync later)"
                onLocalFallback(created)
            } else {
                _message.value = "Task created"
            }
        }
    }

    fun remove(id: String?) {
        if (id == null) return
        viewModelScope.launch {
            repo.delete(id)
            _tasks.value = _tasks.value?.filterNot { it.id == id }
        }
    }
}
